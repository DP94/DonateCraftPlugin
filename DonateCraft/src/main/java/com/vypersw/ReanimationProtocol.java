package com.vypersw;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vypersw.network.HttpHelper;
import com.vypersw.response.DCPlayer;
import com.vypersw.response.Revival;
import org.bukkit.*;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class ReanimationProtocol implements Runnable {

    private final Server server;
    private BlockingQueue<UUID> toRevive = new LinkedBlockingQueue<>();
    private final MessageHelper messageHelper;
    private final HttpHelper httpHelper;

    public ReanimationProtocol(Server server, MessageHelper messageHelper, HttpHelper httpHelper) {
        this.server = server;
        this.messageHelper = messageHelper;
        this.httpHelper = httpHelper;
    }

    @Override
    public void run() {
        if (server.getOnlinePlayers().size() > 0) {
            reanimateEligiblePlayers();
        }
    }

    private void reanimateEligiblePlayers() {
        HttpRequest request = httpHelper.buildGETHttpRequest("Lock?" + this.getQueryStringForPlayerIds());
        HttpClient client = HttpClient.newBuilder().build();
        client.sendAsync(request, HttpResponse.BodyHandlers.ofString()).thenAccept(asyncResponse -> {
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                List<Revival> response = objectMapper.readValue(asyncResponse.body(), new TypeReference<>() {
                    @Override
                    public Type getType() {
                        return super.getType();
                    }
                });
                if (response != null && !response.isEmpty()) {
                    for (Revival revival : response) {
                        if (revival.isUnlocked() == true) {
                            UUID uuid = UUID.fromString(revival.getId());
                            Player player = server.getPlayer(uuid);
                            if (player != null && player.getGameMode() == GameMode.SPECTATOR) {
                                server.broadcastMessage(messageHelper.getDonationMessageFromRevival(player, revival));
                                toRevive.offer(uuid);
                            }
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        Set<UUID> uuids = new HashSet<>();
        toRevive.drainTo(uuids);
        for (UUID uuid : uuids) {
            reanimatePlayer(uuid);
        }
    }

    private String getQueryStringForPlayerIds() {
        StringBuilder builder = new StringBuilder();
        for (Player player : server.getOnlinePlayers()) {
            builder.append("playerIds=" + player.getUniqueId() + "&");
        }
        return builder.toString();
    }

    public void reanimatePlayer(UUID uuid) {
        Player player = server.getPlayer(uuid);
        Revival revival = new Revival();
        revival.setId(uuid.toString());
        //Extra checks just in case Minecraft has pinged the server again before our async call has come back
        if (player != null && player.isOnline() && (player.isDead() || player.getGameMode() == GameMode.SPECTATOR)) {
            server.getLogger().info("Attempting to revive " + player.getName());
            World currentPlayerWorld = player.getWorld();
            if (player.getBedSpawnLocation() == null) {
                player.teleport(currentPlayerWorld.getSpawnLocation());
            } else {
                player.teleport(player.getBedSpawnLocation());
            }
            player.setGameMode(GameMode.SURVIVAL);
            currentPlayerWorld.strikeLightningEffect(player.getLocation());
            currentPlayerWorld.playEffect(player.getLocation(), Effect.DRAGON_BREATH, 0);
            currentPlayerWorld.spawnEntity(player.getLocation(), EntityType.FIREWORK);
            addRespawnPotionEffects(player);
            server.broadcastMessage(ChatColor.GOLD + player.getName() + " " + ChatColor.GREEN + "has been revived!");
            httpHelper.fireAsyncDeleteRequestToServer("Lock/" + uuid);
        } else if (player != null && player.isOnline() && !player.isDead()) {
            httpHelper.fireAsyncDeleteRequestToServer("Lock/" + uuid);
        }
    }

    public void addRespawnPotionEffects(Player player) {
        player.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 200, 100, true, true));
        player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 200, 10, true, true));
        player.addPotionEffect(new PotionEffect(PotionEffectType.HEAL, 200, 10, true, true));
        player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 200, 10, true, true));
    }
}
