package za.ac.sun.cs.providentia.domain;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
public class CategoryId implements Serializable {

    @Column(name = "name")
    private String name;

    @Column(name = "business_id")
    private String business_id;

    public String getBusiness_id() {
        return business_id;
    }

    public CategoryId setBusiness_id(String business_id) {
        this.business_id = business_id;
        return this;
    }

    public String getName() {
        return name;
    }

    public CategoryId setName(String name) {
        this.name = name;
        return this;
    }
}
