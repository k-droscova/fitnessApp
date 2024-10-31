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
public class Room extends IdentifiableImpl<Integer> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_room")
    private Integer id;

    @Column(name = "max_capacity", nullable = false)
    private int maxCapacity;

    /**
     * One-to-many relationship between Room and FitnessClass.
     * CascadeType.ALL and orphanRemoval ensure that when a Room is deleted,
     * associated FitnessClasses are also deleted.
     */
    @OneToMany(mappedBy = "room", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<FitnessClass> classes = new HashSet<>();

    @ManyToMany
    @JoinTable(
            name = "room_possible_class_types",  // Join table name
            joinColumns = @JoinColumn(name = "id_room"),  // Foreign key to Room
            inverseJoinColumns = @JoinColumn(name = "id_class_type")  // Foreign key to ClassType
    )
    private Set<ClassType> classTypes = new HashSet<>();

    public Room(Integer id, int maxCapacity, Set<FitnessClass> classes, Set<ClassType> classTypes) {
        this.id = id;
        this.maxCapacity = maxCapacity;
        this.classes = classes != null ? classes : new HashSet<>();
        this.classTypes = classTypes != null ? classTypes : new HashSet<>();
    }
}