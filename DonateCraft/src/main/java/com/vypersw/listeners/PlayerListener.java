package com.vypersw.listeners;

import com.vypersw.network.HttpHelper;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.json.JSONObject;

public class PlayerListener implements Listener {

    private final String serverURL;
    private final HttpHelper httpHelper;

    public PlayerListener(String serverURL, HttpHelper httpHelper) {
        this.serverURL = serverURL;
        this.httpHelper = httpHelper;
    }


    @EventHandler
    void onPlayerDeath(PlayerDeathEvent event) {

        JSONObject deathProperties = new JSONObject();
        deathProperties.put("uuid", event.getEntity().getUniqueId().toString());
        deathProperties.put("name", event.getEntity().getDisplayName());
        deathProperties.put("lastdeathreason", event.getDeathMessage());
        JSONObject deathObject = new JSONObject();
        deathObject.put("death", deathProperties);

        final String deathURL = serverURL + "#donate?key=" + event.getEntity().getUniqueId().toString();
        httpHelper.fireAsyncPostRequestToServer("/lock", deathObject, () -> sendDeathURL(event.getEntity(), deathURL));
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
