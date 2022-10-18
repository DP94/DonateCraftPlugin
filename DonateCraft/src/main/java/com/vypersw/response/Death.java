package com.vypersw.response;

import com.fasterxml.jackson.annotation.JsonRootName;

import java.util.Objects;
import java.util.UUID;

@JsonRootName(value = "death")
public class Death {
    private UUID playerId;
    private String playerName;
    private String reason;

    public UUID getPlayerId() {
        return playerId;
    }

    public void setPlayerId(UUID playerId) {
        this.playerId = playerId;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Death death = (Death) o;
        return Objects.equals(playerId, death.playerId) && Objects.equals(playerName, death.playerName) && Objects.equals(reason, death.reason);
    }
}
