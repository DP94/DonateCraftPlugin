package com.vypersw;

import com.vypersw.response.Revival;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import java.text.DecimalFormat;

public class MessageHelper {

    private final String serverURL;
    private final DecimalFormat donationAmountFormat;

    public MessageHelper(String serverURL) {
        this.serverURL = serverURL;
        this.donationAmountFormat = new DecimalFormat("##.00");
    }

    public void sendDeathURL(Player player) {
        String deathURL = serverURL + "#donate;key=" + player.getUniqueId().toString();
        TextComponent textComponent = new TextComponent("You died! Please click ");
        textComponent.setColor(net.md_5.bungee.api.ChatColor.RED);
        TextComponent thisTextComp = new TextComponent("this link");
        thisTextComp.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, deathURL));
        thisTextComp.setUnderlined(true);
        thisTextComp.setBold(true);
        thisTextComp.setColor(net.md_5.bungee.api.ChatColor.GOLD);
        TextComponent moreTextComp = new TextComponent(" to donate to a charity to buy back in!");
        moreTextComp.setColor(net.md_5.bungee.api.ChatColor.RED);
        player.spigot().sendMessage(textComponent, thisTextComp, moreTextComp);
    }

    public String getDonationMessageFromRevival(Player player, Revival revival) {
        StringBuilder builder = new StringBuilder();

        builder.append(ChatColor.GOLD);

        if (revival.getDonation().getPaidForBy() != null) {
             builder.append(revival.getDonation().getPaidForBy().getName())
                    .append(ChatColor.WHITE)
                    .append(" just donated")
                    .append(getDonationAmountMessage(revival))
                     .append(ChatColor.WHITE)
                     .append(" on behalf of ")
                    .append(ChatColor.GOLD)
                    .append(player.getName());
        } else {
              builder.append(player.getName())
                     .append(ChatColor.WHITE)
                     .append(" just donated")
                     .append(getDonationAmountMessage(revival));
        }
         builder.append(ChatColor.WHITE)
                .append(" to ")
                .append(ChatColor.GOLD)
                .append(revival.getDonation().getCharityName())
                .append(ChatColor.WHITE)
                .append("! They will be revived shortly (if they are online)");
        return builder.toString();
    }

    private String getDonationAmountMessage(Revival revival) {
        StringBuilder builder = new StringBuilder();
        if (!revival.getDonation().isPrivate()) {
            builder.append(ChatColor.GREEN);
            builder.append(" £");
            builder.append(donationAmountFormat.format(revival.getDonation().getAmount()));
        }
        return builder.toString();
    }
}
