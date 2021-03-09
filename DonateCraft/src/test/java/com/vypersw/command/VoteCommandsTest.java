package com.vypersw.command;

import com.vypersw.network.HttpHelper;
import com.vypersw.vote.VoteAnswer;
import com.vypersw.vote.VoteRecord;
import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.beans.Transient;
import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class VoteCommandsTest {

    @Mock
    private CommandSender consoleCommandSender;

    @Mock
    private Player playerCommandSender;

    @Mock
    private Server server;

    @Mock
    private Command command;

    @Mock
    private ScoreboardManager scoreboardManager;

    @Mock
    private Scoreboard scoreboard;

    @Mock
    private Objective objective;

    @Mock
    private Score score;

    @Mock
    private HttpHelper httpHelper;

    private VoteCommands voteCommands;

    @Before
    public void before() {
        voteCommands = new VoteCommands(server, httpHelper);
        when(server.getScoreboardManager()).thenReturn(scoreboardManager);
        when(scoreboardManager.getNewScoreboard()).thenReturn(scoreboard);
        when(scoreboard.registerNewObjective(anyString(), anyString(), anyString())).thenReturn(objective);
        when(playerCommandSender.getName()).thenReturn("Test");
        when(objective.getScore(playerCommandSender.getName())).thenReturn(score);
        when(playerCommandSender.getUniqueId()).thenReturn(UUID.randomUUID());
    }

    @After
    public void after() {
        voteCommands.onCommand(playerCommandSender, command, "vote", new String[] {"end"});
    }

    @Test
    public void testOnCommandNotAPlayer() {
        voteCommands.onCommand(consoleCommandSender, null, null, null);
        verify(consoleCommandSender).sendMessage("Command must be used in game");
        verifyNoMoreInteractions(consoleCommandSender);
    }

    @Test
    public void testThat0ArgsSendsCorrectMessage() {
        voteCommands.onCommand(playerCommandSender, command, "vote", new String[0]);
        verify(playerCommandSender).sendMessage("Not enough arguments");
    }

    @Test
    public void testThat2ArgsCommandButNotRecognisedSendsUsageMessage() {
        voteCommands.onCommand(playerCommandSender, command, "vote", new String[] {"test", "test"});
        verify(playerCommandSender).sendMessage("Second argument must be either <ask> or <answer>!");
    }

    @Test
    public void testThatASKStartsVoteIfNoVoteActive() {
        voteCommands.onCommand(playerCommandSender, command, "vote", new String[] {"ask", "Testing a vote"});
        voteCommands.onCommand(playerCommandSender, command, "vote", new String[] {"end"});
        verify(server).broadcastMessage("§6Test§f has started a vote! §6Testing a vote");
    }

    @Test
    public void testThatScoreboardIsSetupWhenNoVoteActive() {
        doReturn(Collections.singletonList(playerCommandSender)).when(server).getOnlinePlayers();
        voteCommands.onCommand(playerCommandSender, command, "vote", new String[] {"ask", "Testing a vote"});
        verify(server).broadcastMessage("§6Test§f has started a vote! §6Testing a vote");
        verify(objective).setDisplaySlot(DisplaySlot.SIDEBAR);
        verify(objective).setDisplayName("§6Testing a vote");
        verify(objective).getScore(playerCommandSender.getName());
        verify(playerCommandSender).setScoreboard(scoreboard);
    }

    @Test
    public void testThatAskCommandWhenVoteIsActiveSendsCorrectMessage() {
        voteCommands.onCommand(playerCommandSender, command, "vote", new String[] {"ask", "Testing a vote"});
        voteCommands.onCommand(playerCommandSender, command, "vote", new String[] {"ask", "Testing a vote"});
        verify(playerCommandSender).sendMessage("There is already a vote active!");
    }

    @Test
    public void testThatAnswerCommandWithInvalidValueReturnsCorrectMessage() {
        voteCommands.onCommand(playerCommandSender, command, "vote", new String[] {"answer", "invalid"});
        verify(playerCommandSender).sendMessage("§cinvalid§f is not a valid input. Try §aYES§f or §cNO");
    }

    @Test
    public void testThatAnswerCommandWithNoVoteActiveReturnsCorrectMessage() {
        voteCommands.onCommand(playerCommandSender, command, "vote", new String[] {"answer", "yes"});
        verify(playerCommandSender).sendMessage("There is not a vote active!");
    }

    @Test
    public void testThatAnswerCommandBroadcastsThatPlayerVoted() {
        Player secondPlayer = mock(Player.class);
        when(secondPlayer.getName()).thenReturn("Test2");

        Score secondScore = mock(Score.class);
        when(objective.getScore(secondPlayer.getName())).thenReturn(secondScore);

        List<? extends Player> players = Arrays.asList(playerCommandSender, secondPlayer);
        doReturn(players).when(server).getOnlinePlayers();
        voteCommands.onCommand(playerCommandSender, command, "vote", new String[] {"ask", "Testing a vote"});
        voteCommands.onCommand(playerCommandSender, command, "vote", new String[] {"answer", "yes"});
        verify(server).broadcastMessage("§6Test§f just voted!");
    }

    @Test
    public void testThatAnswerCommandBroadcastsMultiplePlayersAndEndsVoteIfAllPlayersVoted() {
        Player secondPlayer = mock(Player.class);
        when(secondPlayer.getName()).thenReturn("Test2");

        Score secondScore = mock(Score.class);
        when(objective.getScore(secondPlayer.getName())).thenReturn(secondScore);

        List<? extends Player> players = Arrays.asList(playerCommandSender, secondPlayer);
        doReturn(players).when(server).getOnlinePlayers();


        //First player asks and votes
        voteCommands.onCommand(playerCommandSender, command, "vote", new String[] {"ask", "Testing a vote"});
        voteCommands.onCommand(playerCommandSender, command, "vote", new String[] {"answer", "yes"});
        verify(server).broadcastMessage("§6Test§f just voted!");

        //Second player votes, but this time with just /vote yes
        voteCommands.onCommand(secondPlayer, command, "vote", new String[] {"yes"});
        verify(server).broadcastMessage("§6Test2§f just voted!");
        verify(server).broadcastMessage("All players have voted! The result is §aYES!");
        verify(server).broadcastMessage("The vote has now ended.");
    }

    @Test
    public void testThatNoConsensusIsInRed() {
        doReturn(Collections.singletonList(playerCommandSender)).when(server).getOnlinePlayers();
        voteCommands.onCommand(playerCommandSender, command, "vote", new String[] {"ask", "Testing a vote"});
        voteCommands.onCommand(playerCommandSender, command, "vote", new String[] {"answer", "no"});
        verify(server).broadcastMessage("§6Test§f just voted!");
        verify(server).broadcastMessage("All players have voted! The result is §cNO!");
        verify(server).broadcastMessage("The vote has now ended.");
    }

    @Test
    public void testThatEndCommandEndsVoteIfOriginalAuthorSends() {
        voteCommands.onCommand(playerCommandSender, command, "vote", new String[] {"ask", "Testing a vote"});
        voteCommands.onCommand(playerCommandSender, command, "vote", new String[] {"end"});
        verify(server).broadcastMessage("§6Test§f cancelled the vote!");
    }

    @Test
    public void testThatEndCommandWithNoActiveVoteSendsCorrectMessage() {
        voteCommands.onCommand(playerCommandSender, command, "vote", new String[] {"ask", "Testing a vote"});
        Player player = mock(Player.class);
        when(player.getUniqueId()).thenReturn(UUID.randomUUID());
        voteCommands.onCommand(player, command, "vote", new String[] {"end"});
        verify(player).sendMessage("There is not a vote active currently, or you are not the original author of the vote");
    }

    @Test
    public void testThatEndCommandWithActiveVoteButDifferentUserSendsCorrectMessage() {
        voteCommands.onCommand(playerCommandSender, command, "vote", new String[] {"ask", "Testing a vote"});
        voteCommands.onCommand(playerCommandSender, command, "vote", new String[] {"end"});
        voteCommands.onCommand(playerCommandSender, command, "vote", new String[] {"end"});
        verify(playerCommandSender).sendMessage("There is not a vote active currently, or you are not the original author of the vote");
    }


    @Test
    public void testThatNullInputReturnsNull() {
        assertNull(voteCommands.getVoteRecordForInput(null));
    }

    @Test
    public void testThat1IsRecognisedAsYES() {
        assertEquals(VoteAnswer.YES, voteCommands.getVoteRecordForInput("1"));
    }

    @Test
    public void testThatYesIsRecognisedAsYES() {
        assertEquals(VoteAnswer.YES, voteCommands.getVoteRecordForInput("yes"));
    }

    @Test
    public void testThatYeIsRecognisedAsYES() {
        assertEquals(VoteAnswer.YES, voteCommands.getVoteRecordForInput("ye"));
    }

    @Test
    public void testThatYIsRecognisedAsYES() {
        assertEquals(VoteAnswer.YES, voteCommands.getVoteRecordForInput("y"));
    }

    @Test
    public void testThatTrueIsRecognisedAsYES() {
        assertEquals(VoteAnswer.YES, voteCommands.getVoteRecordForInput("true"));
    }

    @Test
    public void testThat0IsRecognisedAsNO() {
        assertEquals(VoteAnswer.NO, voteCommands.getVoteRecordForInput("0"));
    }

    @Test
    public void testThatNoIsRecognisedAsNO() {
        assertEquals(VoteAnswer.NO, voteCommands.getVoteRecordForInput("no"));
    }

    @Test
    public void testThatNIsRecognisedAsNO() {
        assertEquals(VoteAnswer.NO, voteCommands.getVoteRecordForInput("n"));
    }

    @Test
    public void testThatFalseIsRecognisedAsNO() {
        assertEquals(VoteAnswer.NO, voteCommands.getVoteRecordForInput("false"));
    }

    @Test
    public void testThatUnrecognisedInputReturnsNull() {
        assertNull(voteCommands.getVoteRecordForInput("unrecognised"));
    }
}
