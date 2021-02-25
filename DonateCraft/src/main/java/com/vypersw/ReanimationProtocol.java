package com.vypersw;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.UUID;

public class ReanimationProtocol implements Runnable {

    private Server server;

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
                reanimatePlayer(player);
            }
        });
    }

    private HttpRequest getReanimationListRequest() {
        return HttpRequest.newBuilder()
                .uri(URI.create("http://changeme/unlocked"))
                .header("Content-Type", "application/json")
                .GET()
                .build();
    }

    private void reanimatePlayer(JSONObject jsonObject) {
        String uuid = jsonObject.getString("key");
        Player player = Bukkit.getPlayer(UUID.fromString(uuid));
        if (player != null && player.isOnline()) {
            server.broadcastMessage("Revived " + player.getName());
            player.setGameMode(GameMode.SURVIVAL);
            fireRevivedRequest(uuid);
        }
    }

    private void fireRevivedRequest(String uuid) {
        JSONObject deathObject = new JSONObject();
        deathObject.put("uuid", uuid);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://changeme/revived"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(deathObject.toString()))
                .build();
        HttpClient client = HttpClient.newBuilder().build();
        client.sendAsync(request, HttpResponse.BodyHandlers.ofString());
    }
}
