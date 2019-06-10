package za.ac.sun.cs.providentia.domain;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.UUID;

@SuppressWarnings("unused")
@Entity
@Table(name = "bus_by_cat")
public class Category {

    @EmbeddedId
    private CategoryId id;

    public CategoryId getId() {
        return id;
    }

    public Category setId(CategoryId id) {
        this.id = id;
        return this;
    }

}
