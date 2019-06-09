package za.ac.sun.cs.providentia.domain;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.time.LocalDateTime;

public class User {

    private String userId;
    private String name;
    private double averageStars;
    private int reviewCount;
    private LocalDateTime yelpingSince;
    private int cool;
    private int funny;
    private int useful;
    private int fans;
    //    private int complimentHot;
//    private int complimentMore;
//    private int complimentProfile;
//    private int complimentCute;
//    private int complimentList;
//    private int complimentNote;
//    private int complimentPlain;
//    private int complimentCool;
//    private int complimentFunny;
//    private int complimentWriter;
//    private int complimentPhotos;
    private String[] friends;

    public User() {
    }

    public double getAverageStars() {
        return averageStars;
    }

    public void setAverageStars(double averageStars) {
        this.averageStars = averageStars;
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

    public LocalDateTime getYelpingSince() {
        return yelpingSince;
    }

    public void setYelpingSince(LocalDateTime yelpingSince) {
        this.yelpingSince = yelpingSince;
    }

    public int getReviewCount() {
        return reviewCount;
    }

    public void setReviewCount(int reviewCount) {
        this.reviewCount = reviewCount;
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

//    public int getComplimentHot() {
//        return complimentHot;
//    }
//
//    public void setComplimentHot(int complimentHot) {
//        this.complimentHot = complimentHot;
//    }
//
//    public int getComplimentMore() {
//        return complimentMore;
//    }
//
//    public void setComplimentMore(int complimentMore) {
//        this.complimentMore = complimentMore;
//    }
//
//    public int getComplimentProfile() {
//        return complimentProfile;
//    }
//
//    public void setComplimentProfile(int complimentProfile) {
//        this.complimentProfile = complimentProfile;
//    }
//
//    public int getComplimentCute() {
//        return complimentCute;
//    }
//
//    public void setComplimentCute(int complimentCute) {
//        this.complimentCute = complimentCute;
//    }
//
//    public int getComplimentList() {
//        return complimentList;
//    }
//
//    public void setComplimentList(int complimentList) {
//        this.complimentList = complimentList;
//    }
//
//    public int getComplimentNote() {
//        return complimentNote;
//    }
//
//    public void setComplimentNote(int complimentNote) {
//        this.complimentNote = complimentNote;
//    }
//
//    public int getComplimentPlain() {
//        return complimentPlain;
//    }
//
//    public void setComplimentPlain(int complimentPlain) {
//        this.complimentPlain = complimentPlain;
//    }
//
//    public int getComplimentCool() {
//        return complimentCool;
//    }
//
//    public void setComplimentCool(int complimentCool) {
//        this.complimentCool = complimentCool;
//    }
//
//    public int getComplimentFunny() {
//        return complimentFunny;
//    }
//
//    public void setComplimentFunny(int complimentFunny) {
//        this.complimentFunny = complimentFunny;
//    }
//
//    public int getComplimentWriter() {
//        return complimentWriter;
//    }
//
//    public void setComplimentWriter(int complimentWriter) {
//        this.complimentWriter = complimentWriter;
//    }
//
//    public int getComplimentPhotos() {
//        return complimentPhotos;
//    }
//
//    public void setComplimentPhotos(int complimentPhotos) {
//        this.complimentPhotos = complimentPhotos;
//    }

    @Override
    public String toString() {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.writeValueAsString(this);
        } catch (IOException e) {
            final StringBuilder sb = new StringBuilder("User{");
            sb.append("averageStars=").append(averageStars);
            sb.append(", userId='").append(userId).append('\'');
            sb.append(", name='").append(name).append('\'');
            sb.append(", cool=").append(cool);
            sb.append(", yelpingSince=").append(yelpingSince);
            sb.append(", reviewCount=").append(reviewCount);
            sb.append(", funny=").append(funny);
            sb.append(", friends=[");
            for (String friend : friends)
                sb.append(friend).append(" ");
            sb.append("]");
            sb.append(", fans=").append(fans);
            sb.append(", useful=").append(useful);
//            sb.append(", complimentHot=").append(complimentHot);
//            sb.append(", complimentMore=").append(complimentMore);
//            sb.append(", complimentProfile=").append(complimentProfile);
//            sb.append(", complimentCute=").append(complimentCute);
//            sb.append(", complimentList=").append(complimentList);
//            sb.append(", complimentNote=").append(complimentNote);
//            sb.append(", complimentPlain=").append(complimentPlain);
//            sb.append(", complimentCool=").append(complimentCool);
//            sb.append(", complimentFunny=").append(complimentFunny);
//            sb.append(", complimentWriter=").append(complimentWriter);
//            sb.append(", complimentPhotos=").append(complimentPhotos);
            sb.append('}');
            return sb.toString();
        }
    }
}
