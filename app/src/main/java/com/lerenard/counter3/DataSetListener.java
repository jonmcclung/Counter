package com.lerenard.counter3;

/**
 * Created by mc on 07-Dec-16.
 */
public interface DataSetListener<T> {
    void onDelete(T t);

    void onUpdate(T t);

    void onClick(T t);

    void onDrag(T t, int start, int end);

    void onLongPress(T t);
}
