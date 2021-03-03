package com.vypersw.command;

import com.vypersw.MessageHelper;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class DonateCraftCommands implements CommandExecutor {

  private final MessageHelper messageHelper;
  public DonateCraftCommands(MessageHelper messageHelper) {
    this.messageHelper = messageHelper;
  }

  @Override
  public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
    if (sender instanceof Player) {
      Player player = (Player) sender;

      if (player.getGameMode() == GameMode.SPECTATOR) {
        messageHelper.sendDeathURL(player);
      } else {
        sender.sendMessage("You seem to be alive to me? You must be dead to get your revival URL.");
      }

      return true;
    } else {
      sender.sendMessage("Command must be used in game");
    }

    // If the player (or console) uses our command correct, we can return true
    return true;
  }
}
