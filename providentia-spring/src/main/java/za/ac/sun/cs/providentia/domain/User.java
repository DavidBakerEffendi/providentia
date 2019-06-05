package za.ac.sun.cs.providentia.domain;

import com.fasterxml.jackson.databind.ObjectMapper;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Table(name = "users")
public class User {

    @Id
    @Size(max = 22)
    @Column(length = 22)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String id;

    @NotNull
    @Column(name = "name", nullable = false)
    private String name;

    @NotNull
    @Column(name = "average_stars", nullable = false)
    private double averageStars;

    @NotNull
    @Column(name = "review_count", nullable = false)
    private int reviewCount;

    @NotNull
    @Column(name = "yelping_since", nullable = false)
    private LocalDateTime yelpingSince;

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
    @Column(name = "fans", nullable = false)
    private int fans;

    @ManyToMany
    @JoinTable(name = "friends",
            joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "friend_id", referencedColumnName = "id"))
    private Set<User> friends;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public double getAverageStars() {
        return averageStars;
    }

    public User setAverageStars(double averageStars) {
        this.averageStars = averageStars;
        return this;
    }

    public String getName() {
        return name;
    }

    public User setName(String name) {
        this.name = name;
        return this;
    }

    public int getCool() {
        return cool;
    }

    public User setCool(int cool) {
        this.cool = cool;
        return this;
    }

    public LocalDateTime getYelpingSince() {
        return yelpingSince;
    }

    public User setYelpingSince(LocalDateTime yelpingSince) {
        this.yelpingSince = yelpingSince;
        return this;
    }

    public int getReviewCount() {
        return reviewCount;
    }

    public User setReviewCount(int reviewCount) {
        this.reviewCount = reviewCount;
        return this;
    }

    public int getFunny() {
        return funny;
    }

    public User setFunny(int funny) {
        this.funny = funny;
        return this;
    }

    public Set<User> getFriends() {
        return friends;
    }

    public User setFriends(Set<User> friends) {
        this.friends = friends;
        return this;
    }

    public int getFans() {
        return fans;
    }

    public User setFans(int fans) {
        this.fans = fans;
        return this;
    }

    public int getUseful() {
        return useful;
    }

    public User setUseful(int useful) {
        this.useful = useful;
        return this;
    }

    @Override
    public String toString() {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.writeValueAsString(this);
        } catch (IOException e) {
            final StringBuilder sb = new StringBuilder("User{");
            sb.append("averageStars=").append(averageStars);
            sb.append(", id='").append(id).append('\'');
            sb.append(", name='").append(name).append('\'');
            sb.append(", cool=").append(cool);
            sb.append(", yelpingSince=").append(yelpingSince);
            sb.append(", reviewCount=").append(reviewCount);
            sb.append(", funny=").append(funny);
            sb.append(", friends=[");
            for (User friend : friends)
                sb.append(friend.getId()).append(" ");
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
