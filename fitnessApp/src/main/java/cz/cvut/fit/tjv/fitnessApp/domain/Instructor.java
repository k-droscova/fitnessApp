package cz.cvut.fit.tjv.fitnessApp.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor
@Getter
@Setter
public class Instructor extends IdentifiableImpl<Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_employee")
    private Long id;

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
    private List<ClassType> specializations = new ArrayList<>();

    @OneToMany(mappedBy = "instructor")
    private List<FitnessClass> classes = new ArrayList<>();
}