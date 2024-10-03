package cz.cvut.fit.tjv.fitnessApp.domain;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

public class Instructor extends IdentifiableImpl<Integer> {

    private String name;
    private String surname;
    private LocalDate birthDate;
    private final Set<ClassType> specializations = new HashSet<>();
    private final Set<FitnessClass> classes = new HashSet<>();

    public Instructor(Integer id, String name, String surname, LocalDate birthDate) {
        this.id = id;
        this.name = name;
        this.surname = surname;
        this.birthDate = birthDate;
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

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    public Set<ClassType> getSpecializations() {
        return new HashSet<>(specializations);
    }

    public void addSpecialization(ClassType specialization) {
        this.specializations.add(specialization);
    }

    public void removeSpecialization(ClassType specialization) {
        this.specializations.remove(specialization);
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