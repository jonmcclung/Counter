package com.lerenard.counter3.helper;

/**
 * Created by mc on 02-Dec-16.
 */
public interface Consumer<T> {
    public void accept(T item);
}
