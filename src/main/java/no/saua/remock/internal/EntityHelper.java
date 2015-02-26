package no.saua.remock.internal;

public abstract class EntityHelper<T extends EntityHelper> {
    @Override
    public boolean equals(Object other) {
        if (getClass().equals(other.getClass())) {
            return equals((T) other);
        }

        return false;
    }

    public abstract boolean equals(T other);
}
