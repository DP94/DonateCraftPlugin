package com.vypersw;

import com.vypersw.listeners.PlayerListener;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ResourceBundle;

public class DonateCraft extends JavaPlugin {

    public static final int TICKS_TO_SECONDS = 20;

    @Override
    public void onEnable() {
        ResourceBundle bundle = ResourceBundle.getBundle("env");
        final String serverUrl = bundle.getString("SERVER_URL");
        getServer().getPluginManager().registerEvents(new PlayerListener(serverUrl), this);
        ReanimationProtocol reanimationProtocol = new ReanimationProtocol(getServer(), serverUrl);
        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, reanimationProtocol, 5 * TICKS_TO_SECONDS, 10 * TICKS_TO_SECONDS);
    }
}
