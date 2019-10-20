package com.github.skjolber.stcsv.databinder.column.tri;

import java.util.function.BiConsumer;

/**
 * Represents an operation that accepts an object-valued and a
 * {@code boolean}-valued argument, and returns no result.  This is the
 * {@code (reference, boolean)} specialization of {@link BiConsumer}.
 * Unlike most other functional interfaces, {@code ObjBooleanConsumer} is
 * expected to operate via side-effects.
 *
 * <p>This is a <a href="package-summary.html">functional interface</a>
 * whose functional method is {@link #accept(Object, Object, boolean)}.
 *
 * @param <T> the type of the object argument to the operation
 * @param <I> intermediate helper / processor
 *
 */
@FunctionalInterface
public interface ObjBooleanTriConsumer<T, I> {

    /**
     * Performs this operation on the given arguments.
     *
     * @param t the first input argument
	 * @param intermediate intermediate helper / processor
     * @param value the second input argument
     */
    void accept(T t, I intermediate, boolean value);
}
