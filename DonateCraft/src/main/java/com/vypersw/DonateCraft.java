package com.vypersw;

import com.vypersw.listeners.PlayerListener;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class DonateCraft extends JavaPlugin {

    public static final int TICKS_TO_SECONDS = 20;

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(new PlayerListener(), this);
        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new ReanimationProtocol(getServer()), 5 * TICKS_TO_SECONDS, 10 * TICKS_TO_SECONDS);
    }
}
