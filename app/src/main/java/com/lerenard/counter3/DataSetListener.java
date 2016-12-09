package com.lerenard.counter3;

/**
 * Created by mc on 07-Dec-16.
 */
public interface DataSetListener<T> {
    void onAdd(final T t, int index);

    void onDelete(final T t, int position);

    void onUpdate(final T t);

    void onClick(final T t, int position);

    void onDrag(final T t, int start, int end);

    void onLongPress(final T t, int position);
}
