package cz.cvut.fit.tjv.fitnessApp.domain;

import java.util.HashSet;
import java.util.Set;

public class Trainee extends IdentifiableImpl<Integer> {
    private String email;
    private String name;
    private String surname;
    private final Set<FitnessClass> classes = new HashSet<>();

    public Trainee(Integer id, String email, String name, String surname) {
        this.id = id;
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

    public Set<FitnessClass> getClasses() {
        return new HashSet<>(classes);
    }

    public void addFitnessClass(FitnessClass fitnessClass) {
        this.classes.add(fitnessClass);
    }

    public void removeFitnessClass(FitnessClass fitnessClass) {
        this.classes.remove(fitnessClass);
    }
}