package com.vypersw.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class PlayerListener implements Listener {

    @EventHandler
    void onPlayerDeath(PlayerDeathEvent event) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8000/death"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString("{\"uuid\": \"" + event.getEntity().getUniqueId().toString() + "\", \"name\": \"" + event.getEntity().getDisplayName() + "\"}"))
                .build();
        HttpClient client = HttpClient.newBuilder().build();
        client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body);
    }
}
