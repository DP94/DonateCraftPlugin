package com.vypersw;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vypersw.network.HttpHelper;
import com.vypersw.response.Revival;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.json.JSONObject;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class ReanimationProtocol implements Runnable {

    private final Server server;
    private final String serverURL;
    private BlockingQueue<UUID> toRevive = new LinkedBlockingQueue<>();
    private final HttpHelper httpHelper;

    public ReanimationProtocol(Server server, String serverURL) {
        this.server = server;
        this.serverURL = serverURL;
        this.httpHelper = new HttpHelper(serverURL);
    }

    @Override
    public void run() {
        reanimateEligiblePlayers();
    }

    private void reanimateEligiblePlayers() {
        HttpRequest request = httpHelper.buildGETHttpRequest("/unlocked");
        HttpClient client = HttpClient.newBuilder().build();
        client.sendAsync(request, HttpResponse.BodyHandlers.ofString()).thenAccept(asyncResponse -> {
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                RevivalResponse response = objectMapper.readerFor(RevivalResponse.class).readValue(asyncResponse.body());
                if (response.getRevivals() != null && !response.getRevivals().isEmpty()) {
                    for (Revival revival : response.getRevivals()) {
                        UUID uuid = UUID.fromString(revival.getKey());
                        Player player = server.getPlayer(uuid);
                        if (player != null && player.getGameMode() == GameMode.SPECTATOR) {
                            Bukkit.broadcastMessage(ChatColor.GOLD + player.getName() + ChatColor.WHITE + " just donated"
                                    + ChatColor.GREEN + " £" + revival.getDonation().getAmount() + ChatColor.WHITE + " to "
                                    + ChatColor.GOLD + revival.getDonation().getCharityName() + ChatColor.WHITE
                                    +"! They will be revived shortly (if they are online)");
                            toRevive.offer(uuid);
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

    private void reanimatePlayer(UUID uuid) {
        Player player = server.getPlayer(uuid);
        //Extra checks just in case Minecraft has pinged the server again before our async call has come back
        JSONObject deathObject = new JSONObject();
        deathObject.put("uuid", uuid.toString());
        if (player != null && player.isOnline() && (player.isDead() || player.getGameMode() == GameMode.SPECTATOR)) {
            Bukkit.getLogger().info("Attempting to revive " + player.getName());
            World currentPlayerWorld = player.getWorld();
            currentPlayerWorld.strikeLightningEffect(player.getLocation());
            currentPlayerWorld.playEffect(player.getLocation(), Effect.SMOKE, 0, 100);
            server.broadcastMessage(ChatColor.GOLD + player.getName() + " " + ChatColor.GREEN + "has been revived!");
            player.setGameMode(GameMode.SURVIVAL);
            httpHelper.fireAsyncPostRequestToServer("/revived", deathObject);
        } else if (player != null && player.isOnline() && !player.isDead()) {
            httpHelper.fireAsyncPostRequestToServer("/revived", deathObject);
        }
    }
}
