package com.vypersw;

import com.vypersw.listeners.PlayerListener;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class DonateCraft extends JavaPlugin {

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(new PlayerListener(), this);
        Bukkit.getScheduler().scheduleAsyncRepeatingTask(this, new ReanimationProtocol(getServer().getWorld("")), 1200, 600);
    }
}
