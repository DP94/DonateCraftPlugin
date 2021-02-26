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

    private Server server;
    private BlockingQueue<UUID> toRevive = new LinkedBlockingQueue<>();

    public ReanimationProtocol(Server server) {
        this.server = server;
    }

    @Override
    public void run() {
        reanimateEligiblePlayers();
    }

    private void reanimateEligiblePlayers() {

        HttpRequest request = getReanimationListRequest();
        HttpClient client = HttpClient.newBuilder().build();
        client.sendAsync(request, HttpResponse.BodyHandlers.ofString()).thenAccept(asyncResponse -> {
            JSONObject object = new JSONObject(asyncResponse.body());
            JSONArray array = object.getJSONArray("revivals");
            for (int i = 0; i < array.length(); i++) {
                JSONObject player = array.getJSONObject(i);
                String uuid = player.getString("key");
                System.out.println("Received UUID which is eligible to be revived! " + uuid);
                toRevive.offer(UUID.fromString(uuid));
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
                .uri(URI.create("http://86.168.247.27:8000/unlocked"))
                .header("Content-Type", "application/json")
                .GET()
                .build();
    }

    private void reanimatePlayer(UUID uuid) {
        Player player = Bukkit.getPlayer(uuid);
        System.out.println("In reanimate player: " + uuid.toString());
        //Extra checks just in case Minecraft has pinged the server again before our async call has come back
        if (player != null && player.isOnline() && (player.isDead() || player.getGameMode() == GameMode.SPECTATOR)) {
            System.out.println("Attempting to revive " + player.getName());
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
                .uri(URI.create("http://localhost:8000/revived"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(deathObject.toString()))
                .build();
        HttpClient client = HttpClient.newBuilder().build();
        client.sendAsync(request, HttpResponse.BodyHandlers.ofString());
    }
}
