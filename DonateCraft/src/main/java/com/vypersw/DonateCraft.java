package com.vypersw;

import com.vypersw.listeners.PlayerListener;
import org.bukkit.plugin.java.JavaPlugin;

public class DonateCraft extends JavaPlugin {

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(new PlayerListener(), this);
    }
}
