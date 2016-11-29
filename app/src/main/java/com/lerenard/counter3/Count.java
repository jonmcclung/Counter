package com.lerenard.counter3;

import java.io.Serializable;

/**
 * Created by lerenard on 16-Aug-16.
 */
public class Count implements Serializable {
    private String name;
    private int count;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    Count(String n, int c) {
        name = n;
        count = c;
    }

    Count(String n) {
        this(n, 0);
    }

    Count() {this("");}

    public String toString() {
        return "[Count, name: " + name + ", count: " + count + "]";
    }

}
