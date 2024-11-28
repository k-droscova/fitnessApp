package cz.cvut.fit.tjv.fitnessApp.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor
@Getter
@Setter
public class ClassType extends IdentifiableImpl<Long> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_class_type")
    private Long id;
    @Column(nullable = false, unique = true)
    private String name;
    @ManyToMany(mappedBy = "specializations")
    private List<Instructor> instructors = new ArrayList<>();
    @ManyToMany(mappedBy = "classTypes")
    private List<Room> rooms = new ArrayList<>();
    @OneToMany(mappedBy = "classType")
    private List<FitnessClass> classes = new ArrayList<>();
}