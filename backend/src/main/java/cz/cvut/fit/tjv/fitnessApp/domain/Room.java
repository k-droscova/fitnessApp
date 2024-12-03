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
public class Room implements Identifiable<Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_room")
    private Long id;

    @Column(name = "max_capacity", nullable = false)
    private int maxCapacity;

    /**
     * One-to-many relationship between Room and FitnessClass.
     * CascadeType.ALL and orphanRemoval ensure that when a Room is deleted,
     * associated FitnessClasses are also deleted.
     */
    @OneToMany(mappedBy = "room", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<FitnessClass> classes = new ArrayList<>();

    @ManyToMany
    @JoinTable(
            name = "room_possible_class_types",  // Join table name
            joinColumns = @JoinColumn(name = "id_room"),  // Foreign key to Room
            inverseJoinColumns = @JoinColumn(name = "id_class_type")  // Foreign key to ClassType
    )
    private List<ClassType> classTypes = new ArrayList<>();

    @PreRemove
    private void preRemove() {
        for (ClassType classType : classTypes) {
            classType.getRooms().remove(this);
        }
    }
}