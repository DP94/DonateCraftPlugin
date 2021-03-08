package com.vypersw;

import com.vypersw.vote.Vote;
import com.vypersw.vote.VoteRecord;
import org.bukkit.entity.Player;

public class VoteManager {

    private static VoteManager INSTANCE;
    private Vote activeVote;

    public boolean isVoteActive() {
        return activeVote != null;
    }

    public boolean isVoteFinished(int totalOnlinePlayers) {
        return totalOnlinePlayers == activeVote.getVoteRecords().size();
    }

    public VoteRecord calculateWinningVote() {
        long yesCount = activeVote.getVoteRecords().values().stream().filter(v -> v == VoteRecord.YES).count();
        long noCount = activeVote.getVoteRecords().values().stream().filter(v -> v == VoteRecord.NO).count();
        if (yesCount > noCount) {
            return VoteRecord.YES;
        } else if (noCount > yesCount) {
            return VoteRecord.NO;
        } else {
            return VoteRecord.TIE;
        }
    }

    public void end() {
        if (activeVote != null) {
            activeVote.setHasFinished(true);
        }
        activeVote = null;
    }

    public void startVote(String question, Player author) {
        activeVote = new Vote(question, author);
    }

    public void answer(Player voter, VoteRecord voteRecord) {
        if (activeVote != null) {
            activeVote.getVoteRecords().put(voter, voteRecord);
        }
    }

    public Vote getActiveVote() {
        return activeVote;
    }

    public static VoteManager getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new VoteManager();
        }
        return INSTANCE;
    }
}
