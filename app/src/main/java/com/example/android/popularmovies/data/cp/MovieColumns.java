package com.example.android.popularmovies.data.cp;

import net.simonvt.schematic.annotation.AutoIncrement;
import net.simonvt.schematic.annotation.DataType;
import net.simonvt.schematic.annotation.DataType.Type;
import net.simonvt.schematic.annotation.NotNull;
import net.simonvt.schematic.annotation.PrimaryKey;


/**
 * Created by Vamsi on 2/8/2017.
 */

public interface MovieColumns {
    @DataType(Type.INTEGER) @PrimaryKey
    @AutoIncrement
    String _ID = "_id";

    @DataType(Type.TEXT) @NotNull
    String TITLE = "title";
}
