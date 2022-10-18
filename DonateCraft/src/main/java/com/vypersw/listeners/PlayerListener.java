package com.vypersw.listeners;

import com.vypersw.MessageHelper;
import com.vypersw.network.HttpHelper;
import com.vypersw.response.DCPlayer;
import com.vypersw.response.Death;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PlayerListener implements Listener {

    private final MessageHelper messageHelper;
    private final HttpHelper httpHelper;

    private List<String> playerCache;

    public PlayerListener(MessageHelper messageHelper, HttpHelper httpHelper) {
        this.messageHelper = messageHelper;
        this.httpHelper = httpHelper;
        this.playerCache = new ArrayList<>();
    }

    @EventHandler
    void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        Death death = new Death();
        death.setPlayerId(player.getUniqueId());
        death.setPlayerName(player.getName());
        death.setReason(event.getDeathMessage());
        httpHelper.fireAsyncPostRequestToServer("Death", death, () -> messageHelper.sendDeathURL(player));

        dropSkull(player, event.getDeathMessage());
    }

    @EventHandler
    void onLogin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (!playerCache.contains(player.getUniqueId().toString())) {
            DCPlayer dcPlayer = new DCPlayer();
            dcPlayer.setId(player.getUniqueId());
            dcPlayer.setName(player.getName());
            httpHelper.fireAsyncPostRequestToServer("Player", dcPlayer);
            playerCache.add(player.getUniqueId().toString());
        }
        if (player.getGameMode() == GameMode.SPECTATOR) {
            messageHelper.sendDeathURL(player);
        }
    }

    private void dropSkull(Player player, String reason) {
        ItemStack skull = new ItemStack(Material.PLAYER_HEAD, 1);
        SkullMeta skullMeta = (SkullMeta) skull.getItemMeta();
        skullMeta.setOwningPlayer(player);
        skullMeta.setLore(Collections.singletonList(reason));
        skull.setItemMeta(skullMeta);
        player.getWorld().dropItem(player.getLocation(), skull);
    }
}
