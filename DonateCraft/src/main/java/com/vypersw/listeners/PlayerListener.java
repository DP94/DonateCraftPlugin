package com.vypersw.listeners;

import com.vypersw.MessageHelper;
import com.vypersw.network.HttpHelper;
import com.vypersw.response.Death;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerListener implements Listener {

    private final MessageHelper messageHelper;
    private final HttpHelper httpHelper;

    public PlayerListener(MessageHelper messageHelper, HttpHelper httpHelper) {
        this.messageHelper = messageHelper;
        this.httpHelper = httpHelper;
    }

    @EventHandler
    void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        Death death = new Death();
        death.setUuid(player.getUniqueId());
        death.setName(player.getName());
        death.setLastDeathReason(event.getDeathMessage());
        httpHelper.fireAsyncPostRequestToServer("/lock", death, () -> messageHelper.sendDeathURL(player));
    }

    @EventHandler
    void onLogin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (player.getGameMode() == GameMode.SPECTATOR) {
            messageHelper.sendDeathURL(player);
        }
    }
}
