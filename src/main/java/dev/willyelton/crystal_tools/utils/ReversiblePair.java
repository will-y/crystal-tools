package dev.willyelton.crystal_tools.utils;

import java.util.Objects;

/**
 * Pair implementation where the order of the objects doesn't matter for equality and hashcodes
 * @param <T>
 */
public class ReversiblePair<T> {
    private final T o1;
    private final T o2;

    public ReversiblePair(T o1, T o2) {
        this.o1 = o1;
        this.o2 = o2;
    }

    public T first() {
        return o1;
    }

    public T second() {
        return o2;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ReversiblePair<?> that = (ReversiblePair<?>) o;
        return (Objects.equals(o1, that.o1) && Objects.equals(o2, that.o2)) || (Objects.equals(o1, that.o2) && Objects.equals(o2, that.o1));
    }

    /**
     * Hashcode implementation where order of the pair doesn't matter
     * @return Sum of the hashcodes of the two objects
     */
    @Override
    public int hashCode() {
        return o1.hashCode() + o2.hashCode();
    }
}
