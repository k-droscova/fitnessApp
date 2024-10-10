package cz.cvut.fit.tjv.fitnessApp.domain;

import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
public class Room extends IdentifiableImpl<Integer> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_room")
    private Integer id;

    @Column(name = "max_capacity", nullable = false)
    private int maxCapacity;

    /**
     * Represents the one-to-many relationship between Room and FitnessClass.
     *
     * A Room can host multiple FitnessClass entities, and this relationship is managed
     * on the FitnessClass side (i.e., FitnessClass owns the foreign key to Room).
     *
     * CascadeType.ALL:
     * -----------------
     * Cascade operations allow automatic propagation of changes from the Room entity to
     * the related FitnessClass entities. With CascadeType.ALL, the following operations
     * will be cascaded:
     *
     * - Persist: When a Room is saved, all associated FitnessClass entities will also be saved.
     * - Merge: When a Room is updated, all associated FitnessClass entities will also be updated.
     * - Remove: When a Room is deleted, all associated FitnessClass entities will also be deleted.
     * - Refresh: When a Room is refreshed, all associated FitnessClass entities will also be refreshed.
     * - Detach: When a Room is detached from the persistence context, all associated FitnessClass
     *   entities will also be detached.
     *
     * orphanRemoval = true:
     * ----------------------
     * Orphan removal ensures that if a FitnessClass is removed from the Room's 'classes' collection,
     * it will be automatically deleted from the database. Essentially, the removed FitnessClass becomes
     * an "orphan" entity and is deleted.
     *
     * Example scenarios:
     * - If a FitnessClass is removed from the 'classes' collection in the Room entity, the FitnessClass
     *   entity will also be removed from the database.
     * - If a Room is deleted, all associated FitnessClass entities will be deleted because of
     *   CascadeType.ALL and orphan removal.
     *
     */
    @OneToMany(mappedBy = "room", cascade = CascadeType.ALL, orphanRemoval = true)
    private final Set<FitnessClass> classes = new HashSet<>();

    @ManyToMany
    @JoinTable(
            name = "room_possible_class_types",  // The join table name
            joinColumns = @JoinColumn(name = "id_room"),  // Foreign key to Room
            inverseJoinColumns = @JoinColumn(name = "id_class_type")  // Foreign key to ClassType
    )
    private final Set<ClassType> classTypes = new HashSet<>();

    public Room() {}

    public Room(int maxCapacity) {
        this.maxCapacity = maxCapacity;
    }

    public int getMaxCapacity() {
        return maxCapacity;
    }

    public void setMaxCapacity(int maxCapacity) {
        this.maxCapacity = maxCapacity;
    }

    public Set<FitnessClass> classesCopy() { return new HashSet<>(classes); }

    public void addFitnessClass(FitnessClass fitnessClass) {
        if (!this.classes.contains(fitnessClass)) {
            this.classes.add(fitnessClass);
            fitnessClass.setRoom(this);
        }
    }

    public void removeFitnessClass(FitnessClass fitnessClass) {
        if (this.classes.contains(fitnessClass)) {
            this.classes.remove(fitnessClass);
            fitnessClass.setRoom(null);
        }
    }

    public void addClassType(ClassType classType) {
        if (!this.classTypes.contains(classType)) {  // Avoid circular call
            this.classTypes.add(classType);
            classType.addRoom(this);
        }
    }
    public void removeClassType(ClassType classType) {
        if (this.classTypes.contains(classType)) {
            this.classTypes.remove(classType);
            classType.removeRoom(this);
        }
    }
}