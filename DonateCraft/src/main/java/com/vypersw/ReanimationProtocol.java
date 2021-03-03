package com.vypersw;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vypersw.network.HttpHelper;
import com.vypersw.response.Revival;
import org.bukkit.*;
import org.bukkit.entity.Player;

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
    private final MessageHelper messageHelper = new MessageHelper();

    public ReanimationProtocol(Server server, String serverURL, HttpHelper httpHelper) {
        this.server = server;
        this.serverURL = serverURL;
        this.httpHelper = httpHelper;
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
                            server.broadcastMessage(messageHelper.getDonationMessageFromRevival(player, revival));
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

    public void reanimatePlayer(UUID uuid) {
        Player player = server.getPlayer(uuid);
        Revival revival = new Revival();
        revival.setKey(uuid.toString());
        //Extra checks just in case Minecraft has pinged the server again before our async call has come back
        if (player != null && player.isOnline() && (player.isDead() || player.getGameMode() == GameMode.SPECTATOR)) {
            server.getLogger().info("Attempting to revive " + player.getName());
            World currentPlayerWorld = player.getWorld();
            if (player.getBedSpawnLocation() == null) {
                player.teleport(currentPlayerWorld.getSpawnLocation());
            } else {
                player.teleport(player.getBedSpawnLocation());
            }
            currentPlayerWorld.strikeLightningEffect(player.getLocation());
            currentPlayerWorld.playEffect(player.getLocation(), Effect.SMOKE, 0, 100);
            server.broadcastMessage(ChatColor.GOLD + player.getName() + " " + ChatColor.GREEN + "has been revived!");
            player.setGameMode(GameMode.SURVIVAL);
            httpHelper.fireAsyncPostRequestToServer("/revived", revival);
        } else if (player != null && player.isOnline() && !player.isDead()) {
            httpHelper.fireAsyncPostRequestToServer("/revived", revival);
        }
    }
}
