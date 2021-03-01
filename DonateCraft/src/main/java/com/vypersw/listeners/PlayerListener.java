package com.vypersw.listeners;

import com.vypersw.network.HttpHelper;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.json.JSONObject;

public class PlayerListener implements Listener {

    private final String serverURL;

    public PlayerListener(String serverURL) {
        this.serverURL = serverURL;
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
        HttpHelper httpHelper = new HttpHelper(serverURL);
        httpHelper.fireAsyncPostRequestToServer("/lock", deathObject, () -> sendDeathURL(event.getEntity(), deathURL));
    }

    private void sendDeathURL(Player player, String deathURL) {
        player.spigot().sendMessage(new ComponentBuilder("You died! Please click this link to donate to a charity to buy back in!")
                .event(new ClickEvent(ClickEvent.Action.OPEN_URL, deathURL))
                .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(deathURL).create()))
                .create());
    }
}
