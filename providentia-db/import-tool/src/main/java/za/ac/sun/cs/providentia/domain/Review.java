package za.ac.sun.cs.providentia.domain;

import java.time.Instant;

public class Review {

    private String reviewId;
    private String businessId;
    private String userId;
    private int cool;
    private int funny;
    private int useful;
    private Instant date;
    private double stars;
    private String text;

    public Review() {
    }

    public String getBusinessId() {
        return businessId;
    }

    public void setBusinessId(final String businessId) {
        this.businessId = businessId;
    }

    public int getCool() {
        return cool;
    }

    public void setCool(final int cool) {
        this.cool = cool;
    }

    public Instant getDate() {
        return date;
    }

    public void setDate(final Instant date) {
        this.date = date;
    }

    public int getFunny() {
        return funny;
    }

    public void setFunny(final int funny) {
        this.funny = funny;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(final String userId) {
        this.userId = userId;
    }

    public int getUseful() {
        return useful;
    }

    public void setUseful(final int useful) {
        this.useful = useful;
    }

    public String getReviewId() {
        return reviewId;
    }

    public void setReviewId(final String reviewId) {
        this.reviewId = reviewId;
    }

    public double getStars() {
        return stars;
    }

    public void setStars(final double stars) {
        this.stars = stars;
    }

    public String getText() {
        return text;
    }

    public void setText(final String text) {
        this.text = text;
    }

    public String toEsString() {
        return this.toString();
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("{");
        sb.append("\"businessId\": \"").append(businessId).append('\"');
        sb.append(", \"cool\": ").append(cool);
        sb.append(", \"date\": \"").append(date.toString()).append('\"');
        sb.append(", \"funny\": ").append(funny);
        sb.append(", \"userId\": \"").append(userId).append('\"');
        sb.append(", \"useful\": ").append(useful);
        sb.append(", \"reviewId\": \"").append(reviewId).append('\"');
        sb.append(", \"stars\": ").append(stars);
        sb.append(", \"text\": \"").append(text.replaceAll("[^a-zA-Z0-9 ]+", "")).append('\"');
        sb.append("}");
        return sb.toString();
    }
}
