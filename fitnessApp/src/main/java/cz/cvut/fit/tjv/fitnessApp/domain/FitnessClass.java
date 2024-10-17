package cz.cvut.fit.tjv.fitnessApp.domain;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.Set;

@Entity
public class FitnessClass extends IdentifiableImpl<Integer> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_class")
    private Integer id;

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
    private Set<Trainee> trainees = new HashSet<>();

    // Default constructor for JPA
    public FitnessClass() {}

    // Constructor for testing
    protected FitnessClass(Integer id, int capacity, LocalDate date, LocalTime time, Instructor instructor, Room room, ClassType classType, Set<Trainee> trainees) {
        this.id = id;
        this.capacity = capacity;
        this.date = date;
        this.time = time;
        this.instructor = instructor;
        this.room = room;
        this.classType = classType;
        this.trainees = trainees != null ? trainees : new HashSet<>();
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public LocalTime getTime() {
        return time;
    }

    public void setTime(LocalTime time) {
        this.time = time;
    }

    public Instructor getInstructor() {
        return instructor;
    }

    public void setInstructor(Instructor instructor) {
        this.instructor = instructor;
    }

    public Room getRoom() {
        return room;
    }

    public void setRoom(Room room) {
        this.room = room;
    }

    public ClassType getClassType() {
        return classType;
    }

    public void setClassType(ClassType classType) {
        this.classType = classType;
    }

    public Set<Trainee> getTrainees() {
        return trainees;
    }

    public void setTrainees(Set<Trainee> trainees) {
        this.trainees = trainees;
    }
}