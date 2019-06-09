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
    private UUID id;

    @NotNull
    @Column(name = "name", nullable = false)
    private String name;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(unique = true)
    private State state;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public City setName(String name) {
        this.name = name;
        return this;
    }

    public State getState() {
        return state;
    }

    public City setState(State state) {
        this.state = state;
        return this;
    }
}
