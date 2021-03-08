package com.vypersw.vote;

public enum VoteRecord {
    YES("YES"), NO("NO"), TIE("TIE");

    private String value;

    VoteRecord(String value) {
        this.value = value;
    }
}
