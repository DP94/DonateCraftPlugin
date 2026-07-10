package com.vypersw.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Objects;
import java.util.UUID;

@JsonIgnoreProperties(value = {"playerId", "createdDate"}, ignoreUnknown = true)
public class Death {
    private UUID id;
    private String playerName;
    private String reason;
    private boolean autoRevived;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public boolean isAutoRevived() {
        return autoRevived;
    }

    public void setAutoRevived(boolean autoRevived) {
        this.autoRevived = autoRevived;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Death death = (Death) o;
        return autoRevived == death.autoRevived && Objects.equals(id, death.id) && Objects.equals(playerName, death.playerName) && Objects.equals(reason, death.reason);
    }
}
