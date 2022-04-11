package com.vypersw;

import com.vypersw.command.DonateCraftCommands;
import com.vypersw.command.VoteCommands;
import com.vypersw.listeners.PlayerListener;
import com.vypersw.network.HttpHelper;
import com.vypersw.vote.VotePoller;
import org.bukkit.plugin.java.JavaPlugin;

public class DonateCraft extends JavaPlugin {

    public static final int TICKS_TO_SECONDS = 20;

    @Override
    public void onEnable() {
        this.saveDefaultConfig();

        final String backendServerURL = this.getConfig().getString("backendUrl");
        final String frontendServerURL = this.getConfig().getString("frontendUrl");

        HttpHelper httpHelper = new HttpHelper(backendServerURL);
        MessageHelper messageHelper = new MessageHelper(frontendServerURL);
        getServer().getPluginManager().registerEvents(new PlayerListener(messageHelper, httpHelper), this);
        this.getCommand("dc").setExecutor(new DonateCraftCommands(messageHelper));
        this.getCommand("vote").setExecutor(new VoteCommands(getServer(), httpHelper));
        ReanimationProtocol reanimationProtocol = new ReanimationProtocol(getServer(), messageHelper, httpHelper);
        getServer().getScheduler().scheduleSyncRepeatingTask(this, reanimationProtocol, 5 * TICKS_TO_SECONDS, 10 * TICKS_TO_SECONDS);
        getServer().getScheduler().scheduleSyncRepeatingTask(this, new VotePoller(getServer(), httpHelper),
                5 * TICKS_TO_SECONDS, 15 * TICKS_TO_SECONDS);
    }
}
