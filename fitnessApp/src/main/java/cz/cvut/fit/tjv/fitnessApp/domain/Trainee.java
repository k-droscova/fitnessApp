package cz.cvut.fit.tjv.fitnessApp.domain;

import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
public class Trainee extends IdentifiableImpl<Integer> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_trainee")
    private Integer id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String surname;

    @ManyToMany(mappedBy = "trainees")
    private Set<FitnessClass> classes = new HashSet<>();

    // Default constructor for JPA
    public Trainee() {}

    // Constructor for testing or creating Trainee instances
    public Trainee(Integer id, String email, String name, String surname, Set<FitnessClass> classes) {
        this.id = id;
        this.email = email;
        this.name = name;
        this.surname = surname;
        this.classes = classes != null ? classes : new HashSet<>();
    }

    // Getters and setters

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public Set<FitnessClass> getClasses() {
        return classes;
    }

    public void setClasses(Set<FitnessClass> classes) {
        this.classes = classes;
    }
}