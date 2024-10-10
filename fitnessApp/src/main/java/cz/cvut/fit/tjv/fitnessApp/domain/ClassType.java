package cz.cvut.fit.tjv.fitnessApp.domain;

import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
public class ClassType extends IdentifiableImpl<Integer> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_class_type")
    private Integer id;
    @Column(nullable = false, unique = true)
    private String name;
    @ManyToMany(mappedBy = "specializations")
    private final Set<Instructor> instructors = new HashSet<>();
    @ManyToMany(mappedBy = "classTypes")
    private final Set<Room> rooms = new HashSet<>();

    @OneToMany(mappedBy = "classType")
    private final Set<FitnessClass> classes = new HashSet<>();

    public ClassType(String name) {
        this.name = name;
    }
    public ClassType() {}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<Instructor> instructorsCopy() { return new HashSet<>(instructors); }

    public void addInstructor(Instructor instructor) {
        this.instructors.add(instructor);
        instructor.addSpecialization(this);
    }

    public void removeInstructor(Instructor instructor) {
        this.instructors.remove(instructor);
        instructor.removeSpecialization(this);
    }

    public Set<Room> roomsCopy() {
        return new HashSet<>(rooms);
    }

    public void addRoom(Room room) {
        this.rooms.add(room);
        room.addClassType(this);
    }

    public void removeRoom(Room room) {
        this.rooms.remove(room);
        room.removeClassType(this);
    }

    public Set<FitnessClass> classesCopy() { return new HashSet<>(classes); }

    public void addFitnessClass(FitnessClass fitnessClass) {
        this.classes.add(fitnessClass);
        fitnessClass.setClassType(this);
    }

    public void removeFitnessClass(FitnessClass fitnessClass) {
        this.classes.remove(fitnessClass);
        fitnessClass.setClassType(null);
    }
}