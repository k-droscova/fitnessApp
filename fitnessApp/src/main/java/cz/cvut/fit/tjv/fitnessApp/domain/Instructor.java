package cz.cvut.fit.tjv.fitnessApp.domain;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
public class Instructor extends IdentifiableImpl<Integer> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_employee")
    private Integer id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String surname;

    @Column(name = "birth_date", nullable = false)
    private LocalDate birthDate;

    @ManyToMany
    @JoinTable(
            name = "instructor_specializations",  // The join table name
            joinColumns = @JoinColumn(name = "id_employee"),  // Instructor's foreign key column
            inverseJoinColumns = @JoinColumn(name = "id_class_type")  // ClassType's foreign key column
    )
    private Set<ClassType> specializations = new HashSet<>();

    @OneToMany(mappedBy = "instructor")
    private Set<FitnessClass> classes = new HashSet<>();

    // Default constructor for JPA
    public Instructor() {}

    // Constructor for testing
    public Instructor(Integer id, String name, String surname, LocalDate birthDate, Set<ClassType> specializations, Set<FitnessClass> classes) {
        this.id = id;
        this.name = name;
        this.surname = surname;
        this.birthDate = birthDate;
        this.specializations = specializations != null ? specializations : new HashSet<>();
        this.classes = classes != null ? classes : new HashSet<>();
    }

    public void setId(Integer id) {
        this.id = id;
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
        return specializations;
    }

    public void setSpecializations(Set<ClassType> specializations) {
        this.specializations = specializations;
    }

    public Set<FitnessClass> getClasses() {
        return classes;
    }

    public void setClasses(Set<FitnessClass> classes) {
        this.classes = classes;
    }
}