package cz.cvut.fit.tjv.fitnessApp.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor
@Getter
@Setter
public class ClassType implements Identifiable<Long> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_class_type")
    private Long id;
    @NotNull(message = "Name should not be null")
    @Column(nullable = false, unique = true)
    private String name;
    @ManyToMany(mappedBy = "specializations")
    private List<Instructor> instructors = new ArrayList<>();
    @ManyToMany(mappedBy = "classTypes")
    private List<Room> rooms = new ArrayList<>();
    @OneToMany(mappedBy = "classType", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FitnessClass> classes = new ArrayList<>();

    @PreRemove
    private void preRemove() {
        for (Instructor instructor : instructors) {
            instructor.getSpecializations().remove(this);
        }
        for (Room room : rooms) {
            room.getClassTypes().remove(this);
        }
    }
}