package com.vypersw.vote;

public enum VoteAnswer {
    YES("YES"), NO("NO"), ABSTAIN("ABSTAIN"), TIE("TIE");

    private String value;

    VoteAnswer(String value) {
        this.value = value;
    }
}