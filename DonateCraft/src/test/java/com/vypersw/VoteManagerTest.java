package com.vypersw;

import com.vypersw.vote.VoteRecord;
import org.bukkit.entity.Player;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class VoteManagerTest {

    @Mock
    private Player player;

    @Mock
    private Player secondPlayer;

    @After
    public void after() {
        VoteManager.getInstance().end();
    }

    @Test
    public void testIsVoteActiveReturnsTrueWhenVoteIsNotNull() {
        VoteManager voteManager = VoteManager.getInstance();
        voteManager.startVote("", player);
        assertTrue(voteManager.isVoteActive());
    }

    @Test
    public void testIsVoteActiveReturnsFalseWhenVoteIsNotNull() {
        VoteManager voteManager = VoteManager.getInstance();
        voteManager.startVote("", player);
        voteManager.end();
        assertFalse(voteManager.isVoteActive());
    }

    @Test
    public void testThatIsVotedFinishedReturnsTrueWhenTotalOnlinePlayersHaveNotVoted() {
        VoteManager voteManager = VoteManager.getInstance();
        voteManager.startVote("", player);
        voteManager.answer(player, VoteRecord.NO);
        assertFalse(voteManager.isVoteFinished(2));
    }

    @Test
    public void testThatIsVotedFinishedReturnsFalseWhenTotalOnlinePlayersHaveVoted() {
        VoteManager voteManager = VoteManager.getInstance();
        voteManager.startVote("", player);
        voteManager.answer(player, VoteRecord.NO);
        voteManager.answer(secondPlayer, VoteRecord.NO);
        assertTrue(voteManager.isVoteFinished(2));
    }

    @Test
    public void testThatCalculateWinningVoteReturnsYESWhenYesHasMoreVotes() {
        VoteManager voteManager = VoteManager.getInstance();
        voteManager.startVote("", player);
        voteManager.answer(player, VoteRecord.YES);
        assertEquals(VoteRecord.YES, voteManager.calculateWinningVote());
    }

    @Test
    public void testThatCalculateWinningVoteReturnsNOWhenNoHasMoreVotes() {
        VoteManager voteManager = VoteManager.getInstance();
        voteManager.startVote("", player);
        voteManager.answer(player, VoteRecord.NO);
        assertEquals(VoteRecord.NO, voteManager.calculateWinningVote());
    }

    @Test
    public void testThatCalculateWinningVoteReturnsTIEWhenNoConsensus() {
        VoteManager voteManager = VoteManager.getInstance();
        voteManager.startVote("", player);
        voteManager.answer(player, VoteRecord.YES);
        voteManager.answer(secondPlayer, VoteRecord.NO);
        assertEquals(VoteRecord.TIE, voteManager.calculateWinningVote());
    }

    @Test
    public void testThatAnsweringAVoteCorrectlyRecordsIt() {
        VoteManager voteManager = VoteManager.getInstance();
        voteManager.startVote("", player);
        voteManager.answer(player, VoteRecord.YES);
        assertEquals(VoteRecord.YES, voteManager.getActiveVote().getVoteRecords().get(player));
    }
}
