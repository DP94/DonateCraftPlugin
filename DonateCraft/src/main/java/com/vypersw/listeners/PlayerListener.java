package com.vypersw.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.json.JSONObject;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class PlayerListener implements Listener {

    @EventHandler
    void onPlayerDeath(PlayerDeathEvent event) {

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("uuid", event.getEntity().getUniqueId().toString());
        jsonObject.put("name", event.getEntity().getDisplayName());
        jsonObject.put("lastdeathreason", event.getDeathMessage());

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8000/death"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonObject.toString()))
                .build();
        HttpClient client = HttpClient.newBuilder().build();
        client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body);
    }
}
