package cz.cvut.fit.tjv.fitnessApp.domain;

import java.util.HashSet;
import java.util.Set;

public class Room extends IdentifiableImpl<Integer> {

    private int maxCapacity;
    private final Set<FitnessClass> classes = new HashSet<>();

    public Room(Integer id, int maxCapacity) {
        this.id = id;
        this.maxCapacity = maxCapacity;
    }

    public int getMaxCapacity() {
        return maxCapacity;
    }

    public void setMaxCapacity(int maxCapacity) {
        this.maxCapacity = maxCapacity;
    }

    public Set<FitnessClass> getClasses() {
        return new HashSet<>(classes);  // Defensive copy
    }

    public void addFitnessClass(FitnessClass fitnessClass) {
        this.classes.add(fitnessClass);
    }

    public void removeFitnessClass(FitnessClass fitnessClass) {
        this.classes.remove(fitnessClass);
    }
}