package za.ac.sun.cs.providentia.domain;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.UUID;

@SuppressWarnings("unused")
@Entity
@Table(name = "city")
public class City {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String id;

    @NotNull
    @Column(name = "name", nullable = false)
    private String state;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getState() {
        return state;
    }

    public City setState(String state) {
        this.state = state;
        return this;
    }
}
