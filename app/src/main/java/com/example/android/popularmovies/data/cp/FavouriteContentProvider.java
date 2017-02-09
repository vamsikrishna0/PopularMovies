package com.example.android.popularmovies.data.cp;

import android.net.Uri;

import net.simonvt.schematic.annotation.ContentProvider;
import net.simonvt.schematic.annotation.ContentUri;
import net.simonvt.schematic.annotation.InexactContentUri;
import net.simonvt.schematic.annotation.TableEndpoint;

/**
 * Created by Vamsi on 2/8/2017.
 */
@ContentProvider(authority = FavouriteContentProvider.AUTHORITY, database = FavoritesDatabase.class)
public class FavouriteContentProvider {

    public static final String AUTHORITY = "com.example.android.popularmovies.data.cp.FavouriteContentProvider";

    interface Path{
        String LISTS = "lists";
    }
    @TableEndpoint(table = FavoritesDatabase.LISTS) public static class Lists {

        @ContentUri(
                path = "lists",
                type = "vnd.android.cursor.dir/list",
                defaultSort = MovieColumns.TITLE + " ASC")
        public static final Uri LISTS = Uri.parse("content://" + AUTHORITY + "/lists");

        @InexactContentUri(
                path = Path.LISTS + "/#",
                name = "LIST_ID",
                type = "vnd.android.cursor.item/list",
                whereColumn = MovieColumns._ID,
                pathSegment = 1)
        public static Uri withId(long id) {
            return Uri.parse("content://" + AUTHORITY + "/lists/" + id);
        }
    }
}
