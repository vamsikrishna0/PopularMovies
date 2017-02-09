package com.example.android.popularmovies.data.cp;

import net.simonvt.schematic.annotation.Database;
import net.simonvt.schematic.annotation.Table;

/**
 * Created by Vamsi on 2/8/2017.
 */

@Database(version = FavoritesDatabase.VERSION)
public class FavoritesDatabase {
    public static final int VERSION = 1;
    @Table(MovieColumns.class) public static final String LISTS = "lists";
}
