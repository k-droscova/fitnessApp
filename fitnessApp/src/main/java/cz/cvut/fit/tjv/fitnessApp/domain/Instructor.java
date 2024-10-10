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
    private final Set<ClassType> specializations = new HashSet<>();
    @OneToMany(mappedBy = "instructor")
    private final Set<FitnessClass> classes = new HashSet<>();

    public Instructor(String name, String surname, LocalDate birthDate) {
        this.name = name;
        this.surname = surname;
        this.birthDate = birthDate;
    }

    public Instructor() {}

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

    public Set<ClassType> specalizationsCopy() {
        return new HashSet<>(specializations);
    }

    public void addSpecialization(ClassType specialization) {
        if (!this.specializations.contains((specialization))) {
            this.specializations.add(specialization);
            specialization.addInstructor(this);  // Synchronize the relationship on the ClassType side
        }
    }

    public void removeSpecialization(ClassType specialization) {
        if (this.specializations.contains((specialization))) {
            this.specializations.remove(specialization);
            specialization.removeInstructor(this);
        }
    }
    public Set<FitnessClass> classesCopy() {
        return new HashSet<>(classes);
    }

    public void addFitnessClass(FitnessClass fitnessClass) {
        if (!this.classes.contains(fitnessClass)) {  // Avoid circular call
            this.classes.add(fitnessClass);
            fitnessClass.setInstructor(this);  // Synchronize the relationship
        }
    }

    public void removeFitnessClass(FitnessClass fitnessClass) {
        if (this.classes.contains(fitnessClass)) {  // Avoid circular call
            this.classes.remove(fitnessClass);
            fitnessClass.removeInstructor();  // Break the relationship
        }
    }
}