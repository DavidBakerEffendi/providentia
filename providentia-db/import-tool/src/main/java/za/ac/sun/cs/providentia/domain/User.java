package za.ac.sun.cs.providentia.domain;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;

public class User {

    private String userId;
    private String name;
    private Instant yelpingSince;
    private int cool;
    private int funny;
    private int useful;
    private int fans;
    private String[] friends;

    public User() {
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCool() {
        return cool;
    }

    public void setCool(int cool) {
        this.cool = cool;
    }

    public Instant getYelpingSince() {
        return yelpingSince;
    }

    public void setYelpingSince(Instant yelpingSince) {
        this.yelpingSince = yelpingSince;
    }

    public int getFunny() {
        return funny;
    }

    public void setFunny(int funny) {
        this.funny = funny;
    }

    public String[] getFriends() {
        return friends;
    }

    public void setFriends(String[] friends) {
        this.friends = friends;
    }

    public int getFans() {
        return fans;
    }

    public void setFans(int fans) {
        this.fans = fans;
    }

    public int getUseful() {
        return useful;
    }

    public void setUseful(int useful) {
        this.useful = useful;
    }

    public String toEsString() {
        return this.toString();
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("{");
        sb.append("\"userId\": \"").append(userId).append('\"');
        sb.append(", \"name\": \"").append(name).append('\"');
        sb.append(", \"cool\": ").append(cool);
        sb.append(", \"yelpingSince\": \"").append(yelpingSince.toString()).append('\"');
        sb.append(", \"funny\": ").append(funny);
        sb.append(", \"friends\": [");
        for (int i = 0; i < friends.length; i++) {
            sb.append('\"').append(friends[i]).append('\"');
            if (i < friends.length - 1) sb.append(", ");
        }
        sb.append("]");
        sb.append(", \"fans\": ").append(fans);
        sb.append(", \"useful\": ").append(useful);
        sb.append('}');
        return sb.toString();
    }
}