package salomatin.com.movielist.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

import static android.content.ContentValues.TAG;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String MOVIE_LIST_NAME = "MOVIE";
    public static final String MOVIE_ID = "_ID";
    public static final String MOVIE_TITLE = "title";
    public static final String MOVIE_SUMMARY = "summary";
    public static final String MOVIE_POSTER = "poster_path";
    public static final String MOVIE_RELEASE_DATE = "release_date";


    public DatabaseHelper(Context context) {
        super(context, MOVIE_LIST_NAME, null, 1);
    }

    // This class is responsible for database handling.

    // Create database
    @Override
    public void onCreate(SQLiteDatabase db) {

        final String CREATE_TABLE = "CREATE TABLE " + MOVIE_LIST_NAME + " (" +
                MOVIE_ID + " INTEGER PRIMARY KEY, " +
                MOVIE_TITLE+ " TEXT, " +
                MOVIE_SUMMARY + " TEXT, " +
                MOVIE_POSTER + " REAL, "+
                MOVIE_RELEASE_DATE + " INTEGER" + ")";
        try {
            db.execSQL(CREATE_TABLE);
        } catch (SQLException e) {
            Log.d("SQLiteException", e.getMessage());
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS " + MOVIE_LIST_NAME);
        onCreate(db);
    }

    // Add a new movie
    public void addMovie(MovieModel movie) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(MOVIE_TITLE, movie.getTitle());
        contentValues.put(MOVIE_SUMMARY, movie.getOverview());
        contentValues.put(MOVIE_POSTER, movie.getPoster_path());
        try {
           long id = db.insertOrThrow(MOVIE_LIST_NAME, null, contentValues);
           Log.e("MovieDB", "Insert movie with new ID: " + id);
           movie.setId(id);
        } catch (SQLException e) {
            Log.e(TAG, "Create table exception: " + e.getMessage());
        }
        finally {
            db.close();
        }
    }

    public void updateMovieInfo(MovieModel movie) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(MOVIE_ID, movie.getId());
        contentValues.put(MOVIE_TITLE, movie.getTitle());
        contentValues.put(MOVIE_SUMMARY, movie.getOverview());
        contentValues.put(MOVIE_POSTER, movie.getPoster_path());
        try {
            int rows = db.update(MOVIE_LIST_NAME, contentValues, "MOVIE_ID =" + movie.getId(), null);
           if(rows == 0)
           {
               Log.d("Tag", "No movie rows error ");
           }
           if(rows == 1 )
           {
               Log.d("Tag", "Movie rows ");
           }
        } catch (SQLiteException e) {
            Log.d("DatabaseHelper ", e.getMessage());
        } finally {
            db.close();
        }
    }

    // Delete movie
    public void deleteMovie(long id) {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            db.delete(MOVIE_LIST_NAME,MOVIE_ID + "=" + id, null);
        } catch (SQLiteException e) {
            Log.d("DatabaseHelper ", e.getMessage());
        } finally {
            db.close();
        }
    }

    // Erase all movie info
    public void deleteAllMovies() {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            db.delete(MOVIE_LIST_NAME,null, null);
        } catch (SQLiteException e) {
            Log.d("DatabaseHelper ", e.getMessage());
        } finally {
            db.close();
        }
    }


    public Cursor getAllMovies() {
        SQLiteDatabase db = getReadableDatabase();
        return db.query(MOVIE_LIST_NAME, null, null, null, null, null, null);
    }

    public ArrayList<MovieModel> getAllMoviesAsList()
    {

        SQLiteDatabase db = getReadableDatabase();

        ArrayList<MovieModel> movieList = new ArrayList<>();
        Cursor cursor = getAllMovies();
        while (cursor.moveToNext())
        {
            int id = cursor.getInt(cursor.getColumnIndex(MOVIE_ID));
            String title = cursor.getString(cursor.getColumnIndex(MOVIE_TITLE));
            String summary = cursor.getString(cursor.getColumnIndex(MOVIE_SUMMARY));
            String posterURL = cursor.getString(cursor.getColumnIndex(MOVIE_POSTER));
            MovieModel movie = new MovieModel (id, title, summary, posterURL);
            movieList.add(movie);
        }
        cursor.close();
        db.close();

        return movieList;
    }
}