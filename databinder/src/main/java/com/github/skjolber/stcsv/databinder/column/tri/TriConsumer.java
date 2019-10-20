package com.github.skjolber.stcsv.databinder.column.tri;

@FunctionalInterface
public interface TriConsumer<S, T, U> {

    /**
     * Performs this operation on the given arguments.
     *
     * @param s the first function argument
     * @param t the second function argument
     * @param u the third function argument
     */
    void accept(S s, T t, U u);
    
}
