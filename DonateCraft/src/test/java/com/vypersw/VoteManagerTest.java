package com.vypersw;

import com.vypersw.vote.VoteAnswer;
import org.bukkit.entity.Player;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.UUID;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class VoteManagerTest {

    @Mock
    private Player player;

    @Mock
    private Player secondPlayer;

    @Before
    public void before() {
        when(player.getUniqueId()).thenReturn(UUID.randomUUID());
        when(secondPlayer.getUniqueId()).thenReturn(UUID.randomUUID());
    }

    @After
    public void after() {
        VoteManager.getInstance().end();
    }

    @Test
    public void testIsVoteActiveReturnsTrueWhenVoteIsNotNull() {
        VoteManager voteManager = VoteManager.getInstance();
        voteManager.startVote("", player.getUniqueId());
        assertTrue(voteManager.isVoteActive());
    }

    @Test
    public void testIsVoteActiveReturnsFalseWhenVoteIsNotNull() {
        VoteManager voteManager = VoteManager.getInstance();
        voteManager.startVote("", player.getUniqueId());
        voteManager.end();
        assertFalse(voteManager.isVoteActive());
    }

    @Test
    public void testThatIsVotedFinishedReturnsTrueWhenTotalOnlinePlayersHaveNotVoted() {
        VoteManager voteManager = VoteManager.getInstance();
        voteManager.startVote("", player.getUniqueId());
        voteManager.answer(player, VoteAnswer.NO);
        assertFalse(voteManager.isVoteFinished(2));
    }

    @Test
    public void testThatIsVotedFinishedReturnsFalseWhenTotalOnlinePlayersHaveVoted() {
        VoteManager voteManager = VoteManager.getInstance();
        voteManager.startVote("", player.getUniqueId());
        voteManager.answer(player, VoteAnswer.NO);
        voteManager.answer(secondPlayer, VoteAnswer.NO);
        assertTrue(voteManager.isVoteFinished(2));
    }

    @Test
    public void testThatCalculateWinningVoteReturnsYESWhenYesHasMoreVotes() {
        VoteManager voteManager = VoteManager.getInstance();
        voteManager.startVote("", player.getUniqueId());
        voteManager.answer(player, VoteAnswer.YES);
        assertEquals(VoteAnswer.YES, voteManager.calculateWinningVote());
    }

    @Test
    public void testThatCalculateWinningVoteReturnsNOWhenNoHasMoreVotes() {
        VoteManager voteManager = VoteManager.getInstance();
        voteManager.startVote("", player.getUniqueId());
        voteManager.answer(player, VoteAnswer.NO);
        assertEquals(VoteAnswer.NO, voteManager.calculateWinningVote());
    }

    @Test
    public void testThatCalculateWinningVoteReturnsTIEWhenNoConsensus() {
        VoteManager voteManager = VoteManager.getInstance();
        voteManager.startVote("", player.getUniqueId());
        voteManager.answer(player, VoteAnswer.YES);
        voteManager.answer(secondPlayer, VoteAnswer.NO);
        assertEquals(VoteAnswer.TIE, voteManager.calculateWinningVote());
    }

    @Test
    public void testThatAnsweringAVoteCorrectlyRecordsIt() {
        VoteManager voteManager = VoteManager.getInstance();
        voteManager.startVote("", player.getUniqueId());
        voteManager.answer(player, VoteAnswer.YES);
        assertEquals(VoteAnswer.YES, voteManager.getActiveVote().findByUUID(player.getUniqueId()).getDecision());
    }
}
