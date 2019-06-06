package za.ac.sun.cs.providentia.domain;

import com.fasterxml.jackson.databind.ObjectMapper;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.IOException;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@SuppressWarnings("unused")
@Entity
@Table(name = "business")
public class Business implements Serializable {

    @Id
    @Size(max = 22)
    @Column(length = 22)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String id;

    @NotNull
    @Column(name = "address", nullable = false)
    private String address;

    @NotNull
    @Column(name = "postalCode", nullable = false)
    private String postalCode;

    @NotNull
    @Column(name = "is_open", nullable = false)
    private boolean isOpen;

    @ManyToMany
    @JoinTable(
            name = "bus_cat",
            joinColumns = @JoinColumn(name = "business_id"),
            inverseJoinColumns = @JoinColumn(name = "category_id"))
    private Set<Category> categories = new HashSet<>();

    @ManyToOne()
    private City city;

    @NotNull
    @Column(name = "review_count", nullable = false)
    private int reviewCount;

    @NotNull
    @Column(name = "name", nullable = false)
    private String name;

    @NotNull
    @Column(name = "longitude", nullable = false)
    private double longitude;

    @NotNull
    @Column(name = "latitude", nullable = false)
    private double latitude;

    @NotNull
    @Column(name = "stars", nullable = false)
    private double stars;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAddress() {
        return address;
    }

    public Business setAddress(String address) {
        this.address = address;
        return this;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public Business setPostalCode(String postalCode) {
        this.postalCode = postalCode;
        return this;
    }

    public boolean isOpen() {
        return isOpen;
    }

    public Business setIsOpen(boolean isOpen) {
        this.isOpen = isOpen;
        return this;
    }

    public Set<Category> getCategories() {
        return categories;
    }

    public Business setCategories(Set<Category> categories) {
        this.categories = categories;
        return this;
    }

    public City getCity() {
        return city;
    }

    public Business setCity(City city) {
        this.city = city;
        return this;
    }

    public int getReviewCount() {
        return reviewCount;
    }

    public Business setReviewCount(int reviewCount) {
        this.reviewCount = reviewCount;
        return this;
    }

    public String getName() {
        return name;
    }

    public Business setName(String name) {
        this.name = name;
        return this;
    }

    public double getLongitude() {
        return longitude;
    }

    public Business setLongitude(double longitude) {
        this.longitude = longitude;
        return this;
    }

    public double getLatitude() {
        return latitude;
    }

    public Business setLatitude(double latitude) {
        this.latitude = latitude;
        return this;
    }

    public double getStars() {
        return stars;
    }

    public Business setStars(double stars) {
        this.stars = stars;
        return this;
    }

    @Override
    public String toString() {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.writeValueAsString(this);
        } catch (IOException e) {
            final StringBuilder sb = new StringBuilder("Business{");
            sb.append("id='").append(id).append('\'');
            sb.append(", address='").append(address).append('\'');
            sb.append(", postalCode='").append(postalCode).append('\'');
            sb.append(", isOpen=").append(isOpen);
            sb.append(", categories=").append(categories);
            sb.append(", city='").append(city).append('\'');
            sb.append(", reviewCount=").append(reviewCount);
            sb.append(", name='").append(name).append('\'');
            sb.append(", longitude=").append(longitude);
            sb.append(", latitude=").append(latitude);
            sb.append(", stars=").append(stars);
            sb.append('}');
            return sb.toString();
        }
    }
}
