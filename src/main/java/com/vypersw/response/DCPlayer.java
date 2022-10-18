package com.vypersw.response;

import java.util.UUID;

public class DCPlayer {
    private UUID uuid;
    private String name;

    public DCPlayer() {}

    public DCPlayer(UUID uuid, String name) {
        this.uuid = uuid;
        this.name = name;
    }

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
}
