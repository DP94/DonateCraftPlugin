package com.vypersw.response;

import com.fasterxml.jackson.annotation.JsonRootName;

import java.util.Objects;
import java.util.UUID;

@JsonRootName(value = "death")
public class Death {
    private UUID uuid;
    private String name;
    private String reason;

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
        return Objects.equals(uuid, death.uuid) && Objects.equals(name, death.name) && Objects.equals(reason, death.reason);
    }
}
