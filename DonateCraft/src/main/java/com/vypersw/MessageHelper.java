package com.vypersw;

import com.vypersw.response.Revival;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class MessageHelper {

    public String getDonationMessageFromRevival(Player player, Revival revival) {
        StringBuilder builder = new StringBuilder();
        builder.append(ChatColor.GOLD)
                .append(player.getName())
                .append(ChatColor.WHITE)
                .append(" just donated");

        if (!revival.getDonation().isPrivate()) {
            builder.append(ChatColor.GREEN);
            builder.append(" £");
            builder.append(revival.getDonation().getAmount());
        }
        builder.append(ChatColor.WHITE)
                .append(" to ")
                .append(ChatColor.GOLD)
                .append(revival.getDonation().getCharityName())
                .append(ChatColor.WHITE)
                .append("! They will be revived shortly (if they are online)");
        return builder.toString();
    }
}
