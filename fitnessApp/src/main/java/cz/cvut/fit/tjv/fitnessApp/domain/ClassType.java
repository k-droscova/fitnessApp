package cz.cvut.fit.tjv.fitnessApp.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Entity
@NoArgsConstructor
@Getter
@Setter
public class ClassType extends IdentifiableImpl<Integer> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_class_type")
    private Integer id;
    @Column(nullable = false, unique = true)
    private String name;
    @ManyToMany(mappedBy = "specializations")
    private Set<Instructor> instructors = new HashSet<>();
    @ManyToMany(mappedBy = "classTypes")
    private Set<Room> rooms = new HashSet<>();
    @OneToMany(mappedBy = "classType")
    private Set<FitnessClass> classes = new HashSet<>();

    public ClassType(String name) {
        this.name = name;
    }
    protected ClassType(Integer id, String name, Set<Instructor> instructors, Set<Room> rooms, Set<FitnessClass> classes) {
        this.id = id;
        this.name = name;
        this.instructors = instructors;
        this.rooms = rooms;
        this.classes = classes;
    }
}