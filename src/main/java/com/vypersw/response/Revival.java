package com.vypersw.response;

import com.fasterxml.jackson.annotation.JsonRootName;

import java.util.Objects;

@JsonRootName(value = "revival")
public class Revival {
    private long id;
    private String key;
    private boolean unlocked;
    private Donation donation;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public boolean isUnlocked() {
        return unlocked;
    }

    public void setUnlocked(boolean unlocked) {
        this.unlocked = unlocked;
    }

    public Donation getDonation() {
        return donation;
    }

    public void setDonation(Donation donation) {
        this.donation = donation;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Revival revival = (Revival) o;
        return Objects.equals(key, revival.key);
    }
}
