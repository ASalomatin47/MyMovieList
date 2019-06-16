package salomatin.com.movielist.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

import salomatin.com.movielist.database.DatabaseHelper;
import salomatin.com.movielist.database.MovieModel;
import salomatin.com.movielist.R;

// This class is responsible for all actions in our main page (with Movie List).

public class MainActivity extends AppCompatActivity {

    private ListView mListView;

    private ArrayList<MovieModel> mMovieList;
    private ArrayAdapter<MovieModel> mListViewAdapter;
    private DatabaseHelper mDatabaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initObjects();

    }

    @Override
    protected void onResume() {
        super.onResume();
        addMovieAsyncTask();
    }

    // Menu options

    @Override
    public boolean onCreateOptionsMenu (Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.eraseDB:
                Toast.makeText(this, getString(R.string.dbdrop_toast_text), Toast.LENGTH_SHORT).show();
                mDatabaseHelper.deleteAllMovies();
                recreate();
                break;
            case R.id.exitApp:
                finish();
                break;
            case R.id.addManually:
                Intent addManually = new Intent(this, EditActivity.class);
                addManually.putExtra(EditActivity.ACTION_KEY, EditActivity.ACTION_ADD);
                startActivity(addManually);
                break;
            case R.id.addWeb:
                Intent addWebSearch = new Intent(this, SearchActivity.class);
                startActivity(addWebSearch);
                break;
        }

        return true;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.context_menu, menu);
    }

    // Movie List interactions
    @Override
    public boolean onContextItemSelected (MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        int position = info.position;
        MovieModel movie = mMovieList.get(position);
        Log.d("Tag", "onContextItemSelected position " + position);
        switch (item.getItemId()) {
            case R.id.edit:
                Intent addManually = new Intent(this, EditActivity.class);
                addManually.putExtra(EditActivity.ACTION_KEY, EditActivity.ACTION_EDIT);
                addManually.putExtra(DatabaseHelper.MOVIE_ID, movie.getId());
                addManually.putExtra(DatabaseHelper.MOVIE_TITLE, movie.getTitle());
                addManually.putExtra(DatabaseHelper.MOVIE_SUMMARY, movie.getOverview());
                addManually.putExtra(DatabaseHelper.MOVIE_POSTER, movie.getPoster_path());
                startActivity(addManually);
                return true;
            case R.id.remove:
                mDatabaseHelper.deleteMovie(movie.getId());
                mMovieList.remove(position);
                mListViewAdapter.notifyDataSetChanged();
                Toast.makeText(this, getString(R.string.delete_movie_toast_text), Toast.LENGTH_SHORT).show();
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    //Movie List AsyncTask

    @SuppressLint("StaticFieldLeak")
    private void addMovieAsyncTask() {
        new AsyncTask<Void, Void, ArrayList<MovieModel>>(){

            @Override
            protected ArrayList<MovieModel> doInBackground(Void... voids) {
                return mDatabaseHelper.getAllMoviesAsList();
            }

            @Override
            protected void onPostExecute(ArrayList<MovieModel> movieList) {
                super.onPostExecute(movieList);
                if (!movieList.isEmpty()) {
                    Log.d("Tag", "Movie List is not empty");
                    mListViewAdapter = new ArrayAdapter<MovieModel>(MainActivity.this, android.R.layout.simple_list_item_1, movieList);
                    Log.d("Tag", "Set Adapter");
                    mListView.setAdapter(mListViewAdapter);
                    mMovieList = movieList;
                } else {
                    mMovieList = new ArrayList<>();;
                }
            }

        }.execute();
    }

    private void initObjects() {
        mListView = findViewById(R.id.movieList);
        registerForContextMenu(mListView);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Log.d("Tag", "position " + i);
            }
        });
        mDatabaseHelper = new DatabaseHelper(this);
    }
}
