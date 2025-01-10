package com.trocandgo.trocandgo.model;

import org.springframework.data.redis.core.RedisHash;

@RedisHash("RevokedToken")
public class RevokedToken {
    private String token;

    // Constructeurs, getters et setters

    public RevokedToken(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
