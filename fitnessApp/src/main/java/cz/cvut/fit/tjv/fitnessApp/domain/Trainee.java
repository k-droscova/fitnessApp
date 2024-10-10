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
    private final Set<FitnessClass> classes = new HashSet<>();

    public Trainee() {}

    public Trainee(String email, String name, String surname) {
        this.email = email;
        this.name = name;
        this.surname = surname;
    }

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

    public Set<FitnessClass> classesCopy() {
        return new HashSet<>(classes);
    }

    public void addFitnessClass(FitnessClass fitnessClass) {
        if (!this.classes.contains(fitnessClass)) {
            this.classes.add(fitnessClass);
            fitnessClass.addTrainee(this);
        }
    }

    public void removeFitnessClass(FitnessClass fitnessClass) {
        if (this.classes.contains(fitnessClass)) {
            this.classes.remove(fitnessClass);
            fitnessClass.removeTrainee(this);
        }
    }
}