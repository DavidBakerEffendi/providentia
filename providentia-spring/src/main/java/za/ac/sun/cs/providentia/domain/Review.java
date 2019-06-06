package za.ac.sun.cs.providentia.domain;

import com.fasterxml.jackson.databind.ObjectMapper;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.IOException;
import java.time.LocalDateTime;

@SuppressWarnings("unused")
@Entity
@Table(name = "review")
public class Review {

    @Id
    @Size(max = 22)
    @Column(length = 22)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String id;

    @OneToOne
    private Business business;

    @OneToOne
    private User user;

    @NotNull
    @Column(name = "cool", nullable = false)
    private int cool;

    @NotNull
    @Column(name = "funny", nullable = false)
    private int funny;

    @NotNull
    @Column(name = "useful", nullable = false)
    private int useful;

    @NotNull
    @Column(name = "date", nullable = false)
    private LocalDateTime date;

    @NotNull
    @Column(name = "stars", nullable = false)
    private double stars;

    @NotNull
    @Column(name = "text", nullable = false)
    private String text;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Business getBusiness() {
        return business;
    }

    public Review setBusiness(Business business) {
        this.business = business;
        return this;
    }

    public int getCool() {
        return cool;
    }

    public Review setCool(int cool) {
        this.cool = cool;
        return this;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public Review setDate(LocalDateTime date) {
        this.date = date;
        return this;
    }

    public int getFunny() {
        return funny;
    }

    public Review setFunny(int funny) {
        this.funny = funny;
        return this;
    }

    public User getUser() {
        return user;
    }

    public Review setUser(User user) {
        this.user = user;
        return this;
    }

    public int getUseful() {
        return useful;
    }

    public Review setUseful(int useful) {
        this.useful = useful;
        return this;
    }

    public double getStars() {
        return stars;
    }

    public Review setStars(double stars) {
        this.stars = stars;
        return this;
    }

    public String getText() {
        return text;
    }

    public Review setText(String text) {
        this.text = text;
        return this;
    }

    @Override
    public String toString() {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.writeValueAsString(this);
        } catch (IOException e) {
            final StringBuilder sb = new StringBuilder("Review{");
            sb.append("business='").append(business).append('\'');
            sb.append(", cool=").append(cool);
            sb.append(", date=").append(date);
            sb.append(", funny=").append(funny);
            sb.append(", user='").append(user).append('\'');
            sb.append(", useful=").append(useful);
            sb.append(", id='").append(id).append('\'');
            sb.append(", stars=").append(stars);
            sb.append(", text='").append(text).append('\'');
            sb.append('}');
            return sb.toString();
        }
    }
}
