package cz.cvut.fit.tjv.fitnessApp.domain;

import java.util.Objects;

public abstract class IdentifiableImpl<ID> implements Identifiable<ID> {

    protected ID id;

    @Override
    public ID getId() {
        return id;
    }

    public void setId(ID id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        IdentifiableImpl<?> that = (IdentifiableImpl<?>) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
