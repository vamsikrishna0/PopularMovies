package com.example.android.popularmovies.data;

import java.io.Serializable;

/**
 * Created by Vamsi on 2/7/2017.
 */

public class Trailer implements Serializable{
    String id;
    String name;

    public String getId() {
        return id;
    }

    public Trailer(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
