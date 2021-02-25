package com.vypersw;

import com.vypersw.listeners.PlayerListener;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class DonateCraft extends JavaPlugin {

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(new PlayerListener(), this);
        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new ReanimationProtocol(getServer()), 100, 300);
    }
}
