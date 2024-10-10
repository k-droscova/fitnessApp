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
            name = "attended_trainees",  // Join table name
            joinColumns = @JoinColumn(name = "id_class"),
            inverseJoinColumns = @JoinColumn(name = "id_trainee")
    )
    private final Set<Trainee> trainees = new HashSet<>();

    public FitnessClass(int capacity, LocalDate date, LocalTime time, Instructor instructor, Room room, ClassType classType) {
        this.capacity = capacity;
        this.date = date;
        this.time = time;
        this.instructor = instructor;
        this.room = room;
        this.classType = classType;
    }
    public FitnessClass() {}

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

    public Room getRoom() {
        return room;
    }

    public void setRoom(Room room) {
        this.room = room;
        if (room == null) {
            room.removeFitnessClass(this);
        } else {
            room.addFitnessClass(this);
        }
    }

    public ClassType getClassType() {
        return classType;
    }

    public void setClassType(ClassType classType) {
        this.classType = classType;
        if (classType == null) {
            classType.removeFitnessClass(this);
        } else {
            classType.addFitnessClass(this);
        }
    }

    public Instructor getInstructor() {
        return instructor;
    }

    public void setInstructor(Instructor instructor) {
        if (this.instructor != instructor) {  // Prevent circular call
            this.instructor = instructor;
            if (instructor != null) {
                instructor.addFitnessClass(this);
            }
        }
    }

    public void removeInstructor() {
        if (this.instructor != null) {
            Instructor temp = this.instructor;
            this.instructor = null;  // Break the current relationship
            temp.removeFitnessClass(this);  // Synchronize the other side
        }
    }

    public Set<Trainee> traineesCopy() {
        return new HashSet<>(trainees);
    }

    public void addTrainee(Trainee trainee) {
        if (!this.trainees.contains(trainee)) {
            this.trainees.add(trainee);
            trainee.addFitnessClass(this);
        }
    }

    public void removeTrainee(Trainee trainee) {
        if (this.trainees.contains(trainee)) {
            this.trainees.remove(trainee);
            trainee.removeFitnessClass(this);
        }
    }
}