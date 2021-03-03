package com.vypersw;

import com.vypersw.command.DonateCraftCommands;
import com.vypersw.listeners.PlayerListener;
import com.vypersw.network.HttpHelper;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ResourceBundle;

public class DonateCraft extends JavaPlugin {

    public static final int TICKS_TO_SECONDS = 20;

    @Override
    public void onEnable() {
        ResourceBundle bundle = ResourceBundle.getBundle("env");
        final String serverUrl = bundle.getString("SERVER_URL");
        HttpHelper httpHelper = new HttpHelper(serverUrl);
        MessageHelper messageHelper = new MessageHelper(serverUrl);
        getServer().getPluginManager().registerEvents(new PlayerListener(messageHelper, httpHelper), this);
        this.getCommand("dc").setExecutor(new DonateCraftCommands(messageHelper));
        ReanimationProtocol reanimationProtocol = new ReanimationProtocol(getServer(), messageHelper, httpHelper);
        getServer().getScheduler().scheduleSyncRepeatingTask(this, reanimationProtocol, 5 * TICKS_TO_SECONDS, 10 * TICKS_TO_SECONDS);
    }
}
