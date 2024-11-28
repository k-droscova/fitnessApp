package cz.cvut.fit.tjv.fitnessApp.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor
@Getter
@Setter
public class FitnessClass extends IdentifiableImpl<Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_class")
    private Long id;

    @Column(nullable = false)
    private int capacity;

    @Column(nullable = false)
    private LocalDate date;

    @Column(nullable = false)
    private LocalTime time;

    @ManyToOne
    @JoinColumn(name = "id_employee", nullable = false)
    private Instructor instructor;

    @ManyToOne
    @JoinColumn(name = "id_room", nullable = false)
    private Room room;

    @ManyToOne
    @JoinColumn(name = "id_class_type", nullable = false)
    private ClassType classType;

    @ManyToMany
    @JoinTable(
            name = "attended_trainees",
            joinColumns = @JoinColumn(name = "id_class"),
            inverseJoinColumns = @JoinColumn(name = "id_trainee")
    )
    private List<Trainee> trainees = new ArrayList<>();
}