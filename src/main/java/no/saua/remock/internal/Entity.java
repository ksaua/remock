package no.saua.remock.internal;

public abstract class Entity<T extends Entity> {
    @Override
    public boolean equals(Object other) {
        return getClass().equals(other.getClass()) && equals((T) other);

    }

    public abstract boolean equals(T other);
}
