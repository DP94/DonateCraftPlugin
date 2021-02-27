package com.vypersw;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URI;
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

    public ReanimationProtocol(Server server, String serverURL) {
        this.server = server;
        this.serverURL = serverURL;
    }

    @Override
    public void run() {
        Bukkit.getLogger().info("Running reanimation protocol");
        reanimateEligiblePlayers();
    }

    private void reanimateEligiblePlayers() {

        HttpRequest request = getReanimationListRequest();
        HttpClient client = HttpClient.newBuilder().build();
        client.sendAsync(request, HttpResponse.BodyHandlers.ofString()).thenAccept(asyncResponse -> {
            JSONObject object = new JSONObject(asyncResponse.body());
            if (object.has("revivals")) {
                JSONArray array = object.getJSONArray("revivals");
                for (int i = 0; i < array.length(); i++) {
                    JSONObject player = array.getJSONObject(i);
                    if (player.has("key")) {
                        String uuid = player.getString("key");
                        Bukkit.getLogger().info("Received UUID which is eligible to be revived! " + uuid);
                        toRevive.offer(UUID.fromString(uuid));
                    } else {
                        Bukkit.getLogger().warning("Could not find player object in revivals JSON response - has the server response changed?");
                    }
                }
            } else {
                Bukkit.getLogger().warning("Could not find revivals array in JSON response - has the server response changed?");
            }
        });

        Set<UUID> uuids = new HashSet<>();
        toRevive.drainTo(uuids);
        for (UUID uuid : uuids) {
            reanimatePlayer(uuid);
        }
    }

    private HttpRequest getReanimationListRequest() {
        return HttpRequest.newBuilder()
                .uri(URI.create(serverURL + "/unlocked"))
                .header("Content-Type", "application/json")
                .GET()
                .build();
    }

    private void reanimatePlayer(UUID uuid) {
        Player player = Bukkit.getPlayer(uuid);
        //Extra checks just in case Minecraft has pinged the server again before our async call has come back
        if (player != null && player.isOnline() && (player.isDead() || player.getGameMode() == GameMode.SPECTATOR)) {
            Bukkit.getLogger().info("Attempting to revive " + player.getName());
            server.broadcastMessage("Revived " + player.getName());
            player.setGameMode(GameMode.SURVIVAL);
            fireRevivedRequest(uuid);
        }
    }

    private void fireRevivedRequest(UUID uuid) {
        JSONObject deathObject = new JSONObject();
        deathObject.put("uuid", uuid.toString());
        System.out.println("Firing revived request");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(serverURL + "/revived"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(deathObject.toString()))
                .build();
        HttpClient client = HttpClient.newBuilder().build();
        client.sendAsync(request, HttpResponse.BodyHandlers.ofString());
    }
}
