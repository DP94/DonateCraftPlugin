package com.vypersw.response;

import java.util.Objects;
import java.util.UUID;

public class Death {
    private UUID id;
    private String playerName;
    private String reason;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Death death = (Death) o;
        return Objects.equals(id, death.id) && Objects.equals(playerName, death.playerName) && Objects.equals(reason, death.reason);
    }
}
