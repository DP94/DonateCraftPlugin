package com.vypersw;

import com.vypersw.vote.Vote;
import com.vypersw.vote.VoteAnswer;
import com.vypersw.vote.VoteRecord;
import org.bukkit.entity.Player;

import java.util.Date;
import java.util.UUID;

public class VoteManager {

    private static VoteManager INSTANCE;
    private Vote activeVote;

    public boolean isVoteActive() {
        return activeVote != null;
    }

    public boolean isVoteFinished(int totalOnlinePlayers) {
        return activeVote.getVoteRecords().size() >= totalOnlinePlayers;
    }

    public VoteAnswer calculateWinningVote() {
        long yesCount = activeVote.getVoteRecords().stream().filter(v -> v.getDecision() == VoteAnswer.YES).count();
        long noCount = activeVote.getVoteRecords().stream().filter(v -> v.getDecision() == VoteAnswer.NO).count();
        if (yesCount > noCount) {
            return VoteAnswer.YES;
        } else if (noCount > yesCount) {
            return VoteAnswer.NO;
        } else {
            return VoteAnswer.TIE;
        }
    }

    public void end() {
        activeVote = null;
    }

    public void startVote(String question, UUID author) {
        activeVote = new Vote(question, author);
    }

    public void answer(Player voter, VoteAnswer voteAnswer) {
        if (activeVote != null) {
            VoteRecord voteRecord = new VoteRecord(voter.getUniqueId(), voteAnswer);
            voteRecord.setTimeVoted(new Date());
            activeVote.getVoteRecords().add(voteRecord);
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
