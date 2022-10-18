package com.vypersw.vote;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonRootName;

import java.util.*;

@JsonRootName(value = "vote")
public class Vote {

    private String question;
    private UUID author;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZ")
    private Date dateCalled;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZ")
    private Date dateFinished;
    private final Set<VoteRecord> voteRecords;

    public Vote(String question, UUID author) {
        voteRecords = new HashSet<>();
        this.question = question;
        this.author = author;
        this.dateCalled = new Date();
    }

    public Set<VoteRecord> getVoteRecords() {
        return voteRecords;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public UUID getAuthor() {
        return author;
    }

    public void setAuthor(UUID author) {
        this.author = author;
    }

    public Date getDateCalled() {
        return dateCalled;
    }

    public void setDateCalled(Date dateCalled) {
        this.dateCalled = dateCalled;
    }

    public Date getDateFinished() {
        return dateFinished;
    }

    public void setDateFinished(Date dateFinished) {
        this.dateFinished = dateFinished;
    }

    public VoteRecord findByUUID(UUID uuid) {
        Optional<VoteRecord> voteRecord = voteRecords.stream().filter(v -> v.getVoter().equals(uuid)).findFirst();
        return voteRecord.orElse(null);
    }
}
