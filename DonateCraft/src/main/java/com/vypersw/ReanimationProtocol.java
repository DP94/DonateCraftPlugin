package com.vypersw;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class ReanimationProtocol extends BukkitRunnable {

    private World world;

    public ReanimationProtocol(World world) {
        this.world = world;
    }

    @Override
    public void run() {
        reanimateEligiblePlayers();
    }

    private void reanimateEligiblePlayers() {
        HttpRequest request = getReanimationListRequest();
        HttpClient client = HttpClient.newBuilder().build();
        client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body).thenAccept(this::reanimatePlayer);
    }

    private HttpRequest getReanimationListRequest(){
        return HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8000/reanimation"))
                .header("Content-Type", "application/json")
                .GET()
                .build();
    }

    private void reanimatePlayer(String deadPlayer) {
        Player player = Bukkit.getPlayer(deadPlayer);
        if (player != null && player.isOnline()) {
            player.setGameMode(GameMode.SURVIVAL);
        }
    }
}
