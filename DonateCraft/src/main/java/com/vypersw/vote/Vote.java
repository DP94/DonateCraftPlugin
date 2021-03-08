package com.vypersw.vote;

import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class Vote {

    private String question;
    private Player author;
    private boolean hasStarted;
    private boolean hasFinished;
    private final Map<Player, VoteRecord> voteRecords;

    public Vote(String question, Player author) {
        voteRecords = new HashMap<>();
        this.question = question;
        this.author = author;
    }

    public Map<Player, VoteRecord> getVoteRecords() {
        return voteRecords;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public Player getAuthor() {
        return author;
    }

    public void setAuthor(Player author) {
        this.author = author;
    }

    public boolean isHasStarted() {
        return hasStarted;
    }

    public void setHasStarted(boolean hasStarted) {
        this.hasStarted = hasStarted;
    }

    public boolean isHasFinished() {
        return hasFinished;
    }

    public void setHasFinished(boolean hasFinished) {
        this.hasFinished = hasFinished;
        this.hasStarted = !hasFinished;
    }
}
