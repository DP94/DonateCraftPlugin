package com.vypersw.vote;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;
import java.util.Objects;
import java.util.UUID;

public class VoteRecord {
    private UUID voter;
    private VoteAnswer decision;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZ")
    private Date timeVoted;

    public VoteRecord(UUID voter, VoteAnswer decision) {
        this.voter = voter;
        this.decision = decision;
    }

    public UUID getVoter() {
        return voter;
    }

    public void setVoter(UUID voter) {
        this.voter = voter;
    }

    public VoteAnswer getDecision() {
        return decision;
    }

    public void setDecision(VoteAnswer decision) {
        this.decision = decision;
    }

    public Date getTimeVoted() {
        return timeVoted;
    }

    public void setTimeVoted(Date timeVoted) {
        this.timeVoted = timeVoted;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        VoteRecord that = (VoteRecord) o;
        return Objects.equals(voter, that.voter);
    }

    @Override
    public int hashCode() {
        return Objects.hash(voter, decision, timeVoted);
    }
}