package cz.cvut.fit.tjv.fitnessApp.domain;

public interface Identifiable<ID> {
    ID getId();
    void setId(ID id);
}