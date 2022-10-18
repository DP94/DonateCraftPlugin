package com.vypersw.command;

import com.vypersw.VoteManager;
import com.vypersw.network.HttpHelper;
import com.vypersw.vote.VoteAnswer;
import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class VoteCommands implements CommandExecutor {

    private final Server server;
    private Scoreboard scoreboard;
    private Objective objective;
    private final Map<Player, Score> scores = new HashMap<>();

    private final String ASK = "ask";
    private final String ANSWER = "answer";
    private final String END = "end";

    private final HttpHelper httpHelper;

    public VoteCommands(Server server, HttpHelper httpHelper) {
        this.server = server;
        this.httpHelper = httpHelper;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player commandSender = (Player) sender;
            VoteManager voteManager = VoteManager.getInstance();
            if (args.length >= 2) {
                String type = args[0];
                if (type != null && type.equalsIgnoreCase(ASK)) {
                    processQuestion(args, voteManager, commandSender);
                } else if (type != null && type.equalsIgnoreCase(ANSWER)) {
                    processAnswer(args[1], voteManager, commandSender);
                } else if (type != null) {
                    commandSender.sendMessage("Second argument must be either <ask> or <answer>!");
                }
            } else if (args.length == 1) {
                String type = args[0];
                if (type.equalsIgnoreCase(END)) {
                    processEnd(voteManager, commandSender);
                } else if (getVoteRecordForInput(type) != null) {
                    processAnswer(type, voteManager, commandSender);
                }
            } else {
                commandSender.sendMessage("Not enough arguments");
            }
        } else {
            sender.sendMessage("Command must be used in game");
        }
        return true;
    }

    private void processQuestion(String[] args, VoteManager voteManager, Player commandSender) {
        StringBuilder question = new StringBuilder();
        for (int i = 1; i < args.length; i++) {
            question.append(args[i]);
            if (i != args.length - 1) {
                question.append(" ");
            }
        }
        if (!voteManager.isVoteActive()) {
            voteManager.startVote(question.toString(), commandSender.getUniqueId());
            createScoreboardForVote(question.toString(), server.getOnlinePlayers());
            server.broadcastMessage(ChatColor.GOLD + commandSender.getName() + ChatColor.WHITE + " has started a vote! " + ChatColor.GOLD + question.toString());
        } else {
            commandSender.sendMessage("There is already a vote active!");
        }
    }

    private void processAnswer(String answer, VoteManager voteManager, Player commandSender) {
        VoteAnswer record = getVoteRecordForInput(answer);
        if (record == null) {
            commandSender.sendMessage(ChatColor.RED + answer + ChatColor.WHITE + " is not a valid input. Try " + ChatColor.GREEN +
                    "YES" + ChatColor.WHITE + " or " + ChatColor.RED +  "NO");
        } else if (voteManager.isVoteActive()) {
            voteManager.answer(commandSender, record);
            Score score = scores.get(commandSender);
            score.setScore(1);
            server.broadcastMessage(ChatColor.GOLD + commandSender.getName() + ChatColor.WHITE + " just voted!");
            if (voteManager.isVoteFinished(server.getOnlinePlayers().size())) {
                VoteAnswer result = voteManager.calculateWinningVote();
                ChatColor color = ChatColor.WHITE;
                if (result == VoteAnswer.YES) {
                    color = ChatColor.GREEN;
                } else if (result == VoteAnswer.NO) {
                    color = ChatColor.RED;
                }
                server.broadcastMessage("All players have voted! The result is " + color + voteManager.calculateWinningVote() + "!");
                server.broadcastMessage("The vote has now ended.");
                voteManager.getActiveVote().setDateFinished(new Date());
                httpHelper.fireAsyncPostRequestToServer("/vote", voteManager.getActiveVote());
                voteManager.end();
                for (Player player : server.getOnlinePlayers()) {
                    player.setScoreboard(server.getScoreboardManager().getNewScoreboard());
                }
            }
        } else {
            commandSender.sendMessage("There is not a vote active!");
        }
    }

    private void processEnd(VoteManager voteManager, Player commandSender) {
        if (voteManager.isVoteActive() && voteManager.getActiveVote().getAuthor().equals(commandSender.getUniqueId())) {
            voteManager.getActiveVote().setDateFinished(new Date());
            httpHelper.fireAsyncPostRequestToServer("/vote", voteManager.getActiveVote());
            voteManager.end();
            for (Player player : server.getOnlinePlayers()) {
                player.setScoreboard(server.getScoreboardManager().getNewScoreboard());
            }
            scores.clear();
            server.broadcastMessage(ChatColor.GOLD + commandSender.getName() + ChatColor.WHITE + " cancelled the vote!");
        } else {
            commandSender.sendMessage("There is not a vote active currently, or you are not the original author of the vote");
        }
    }

    private void createScoreboardForVote(String question, Collection<? extends Player> onlinePlayers) {
        ScoreboardManager scoreboardManager = server.getScoreboardManager();
        scoreboard = scoreboardManager.getNewScoreboard();
        objective = scoreboard.registerNewObjective(question, "", question);
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        objective.setDisplayName(ChatColor.GOLD + question);
        scoreboard.registerNewObjective("Name", question, question);

        for (Player player : server.getOnlinePlayers()) {
            Score score = objective.getScore(player.getName());
            score.setScore(0);
            scores.put(player, score);
            player.setScoreboard(scoreboard);
        }
    }

    public VoteAnswer getVoteRecordForInput(String input) {
        if (input == null) {
            return null;
        }
        switch (input.toLowerCase()) {
            case "1":
            case "yes":
            case "ye":
            case "y":
            case "true":
                return VoteAnswer.YES;
            case "0":
            case "no":
            case "n":
            case "false":
                return VoteAnswer.NO;
            default:
                return null;
        }
    }
}
