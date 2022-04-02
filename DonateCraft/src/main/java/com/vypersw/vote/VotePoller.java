package com.vypersw.vote;

import com.vypersw.VoteManager;
import com.vypersw.network.HttpHelper;
import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.entity.Player;

import java.util.Calendar;
import java.util.Date;

public class VotePoller implements Runnable {

    private final Server server;
    private final HttpHelper httpHelper;

    public VotePoller(Server server, HttpHelper httpHelper) {
        this.server = server;
        this.httpHelper = httpHelper;
    }

    @Override
    public void run() {
        VoteManager voteManager = VoteManager.getInstance();
        try {
            if (voteManager.isVoteActive()) {
                Vote activeVote = voteManager.getActiveVote();
                if (activeVote.getDateFinished() == null) {
                    System.out.println("Found a vote which is stale! Trying to end it");
                    Date now = new Date();
                    Date started = activeVote.getDateCalled();
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(started);
                    cal.add(Calendar.SECOND, 30);
                    System.out.println("Date started 2 mins in the future: " + cal.getTime().toString());
                    System.out.println("Now: " + now.toString());
                    if (now.after(cal.getTime())) {
                        System.out.println("Attempting to end!");
                        server.broadcastMessage("The vote has been inactive for more than 30 seconds - calculating " + "the consensus!");
                        VoteAnswer result = voteManager.calculateWinningVote();
                        ChatColor color = ChatColor.WHITE;
                        if (result == VoteAnswer.YES) {
                            color = ChatColor.GREEN;
                        } else if (result == VoteAnswer.NO) {
                            color = ChatColor.RED;
                        }
                        server.broadcastMessage("All players have voted! The result for " +
                                ChatColor.GOLD + activeVote.getQuestion() + ChatColor.WHITE + " is " +
                                color + voteManager.calculateWinningVote() + "!");
                        voteManager.getActiveVote().setDateFinished(new Date());
                        httpHelper.fireAsyncPostRequestToServer("/vote", activeVote);
                        voteManager.end();
                        for (Player player : server.getOnlinePlayers()) {
                            player.setScoreboard(server.getScoreboardManager().getNewScoreboard());
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
