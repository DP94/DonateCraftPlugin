package com.vypersw.listeners;

import com.vypersw.network.HttpHelper;
import com.vypersw.response.Death;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public class PlayerListener implements Listener {

    private final String serverURL;
    private final HttpHelper httpHelper;

    public PlayerListener(String serverURL, HttpHelper httpHelper) {
        this.serverURL = serverURL;
        this.httpHelper = httpHelper;
    }


    @EventHandler
    void onPlayerDeath(PlayerDeathEvent event) {
        Death death = new Death();
        death.setUuid(event.getEntity().getUniqueId());
        death.setName(event.getEntity().getName());
        death.setLastDeathReason(event.getDeathMessage());
        final String deathURL = serverURL + "#donate;key=" + event.getEntity().getUniqueId().toString();
        httpHelper.fireAsyncPostRequestToServer("/lock", death, () -> sendDeathURL(event.getEntity(), deathURL));

    }

    private void sendDeathURL(Player player, String deathURL) {
        TextComponent textComponent = new TextComponent("You died! Please click ");
        textComponent.setColor(ChatColor.RED);
        TextComponent thisTextComp = new TextComponent("this link");
        thisTextComp.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, deathURL));
        thisTextComp.setUnderlined(true);
        thisTextComp.setBold(true);
        thisTextComp.setColor(ChatColor.GOLD);
        TextComponent moreTextComp = new TextComponent(" to donate to a charity to buy back in!");
        moreTextComp.setColor(ChatColor.RED);
        player.spigot().sendMessage(textComponent, thisTextComp, moreTextComp);
    }
}
