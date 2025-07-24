package me.iexception.knfly.managers.user;

import java.util.UUID;

public class User {

    private String name;
    private UUID uuid;
    private Integer time;

    public User(String name, UUID uuid, Integer time){
        this.name = name;
        this.uuid = uuid;
        this.time = time;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public Integer getTime() {
        return time;
    }

    public void setTime(Integer time) {
        this.time = time;
    }
}
