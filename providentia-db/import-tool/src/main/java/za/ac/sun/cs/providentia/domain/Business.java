package za.ac.sun.cs.providentia.domain;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;

public class Business implements Serializable {

    private String businessId;
    private String address;
    private String postalCode;
    private boolean isOpen;
    private List<String> categories;
    private String city;
    private int reviewCount;
    private String name;
    private double longitude;
    private double latitude;
    private String state;
    private double stars;

    public Business() {
    }

    public String getBusinessId() {
        return businessId;
    }

    public void setBusinessId(String businessId) {
        this.businessId = businessId;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public boolean isOpen() {
        return isOpen;
    }

    public void setIsOpen(boolean isOpen) {
        this.isOpen = isOpen;
    }

    public List<String> getCategories() {
        return categories;
    }

    public void setCategories(List<String> categories) {
        this.categories = categories;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public int getReviewCount() {
        return reviewCount;
    }

    public void setReviewCount(int reviewCount) {
        this.reviewCount = reviewCount;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public double getStars() {
        return stars;
    }

    public void setStars(double stars) {
        this.stars = stars;
    }

    @Override
    public String toString() {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.writeValueAsString(this);
        } catch (IOException e) {
            final StringBuilder sb = new StringBuilder("Business{");
            sb.append("businessId='").append(businessId).append('\'');
            sb.append(", address='").append(address).append('\'');
            sb.append(", postalCode='").append(postalCode).append('\'');
            sb.append(", isOpen=").append(isOpen);
            sb.append(", categories=").append(categories);
            sb.append(", city='").append(city).append('\'');
            sb.append(", reviewCount=").append(reviewCount);
            sb.append(", name='").append(name).append('\'');
            sb.append(", longitude=").append(longitude);
            sb.append(", latitude=").append(latitude);
            sb.append(", state='").append(state).append('\'');
            sb.append(", stars=").append(stars);
            sb.append('}');
            return sb.toString();
        }
    }
}
