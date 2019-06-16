package salomatin.com.movielist.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import salomatin.com.movielist.database.DatabaseHelper;
import salomatin.com.movielist.database.MovieModel;
import salomatin.com.movielist.R;


// This class is responsible for all actions in the Movie Info Edit page.

public class EditActivity extends AppCompatActivity {

    private EditText mEditTitle, mEditSummary, mEditPosterURL;
    private ImageView mPosterView;
    private int movieId = -1;

    public static final String ACTION_EDIT = "action_edit";
    public static final String ACTION_VIEW = "action_view";
    public static final String ACTION_ADD = "action_add";
    public static final String ACTION_KEY = "action_key";
    public static final String MOVIE_BASE_URL="https://image.tmdb.org/t/p/w185";

    //Get movie info
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie);
        initObjects();
        Intent dataIntent = getIntent();
        switch (dataIntent.getStringExtra(ACTION_KEY)) {
            case ACTION_EDIT:
                movieId = dataIntent.getIntExtra(DatabaseHelper.MOVIE_ID, -1);
                displayContent(dataIntent);
                break;
            case ACTION_VIEW:
                displayContent(dataIntent);
                break;
        }
    }

    //Show movie info
    private void displayContent(Intent dataIntent) {
        mEditTitle.setText(dataIntent.getStringExtra(DatabaseHelper.MOVIE_TITLE));
        mEditSummary.setText(dataIntent.getStringExtra(DatabaseHelper.MOVIE_SUMMARY));
        mEditPosterURL.setText(dataIntent.getStringExtra(DatabaseHelper.MOVIE_POSTER));
    }

    private void initObjects() {
        mEditTitle = findViewById(R.id.movieName);
        mEditSummary = findViewById(R.id.plotSummary);
        mEditPosterURL = findViewById(R.id.posterURL);

        final DatabaseHelper databaseHelper = new DatabaseHelper(this);

        // Add movie to database (our list)
        final Button addMov = findViewById(R.id.addButton);
        addMov.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("Tag", "Add movie button pressed");
                String title = mEditTitle.getText().toString();
                String summary = mEditSummary.getText().toString();
                String posterURL = mEditPosterURL.getText().toString();
                MovieModel movie = new MovieModel(title, summary, posterURL);
                if (movieId != -1) {
                    databaseHelper.updateMovieInfo(movie);
                } else {
                    databaseHelper.addMovie(movie);
                }
                Intent showAdded = new Intent(EditActivity.this, MainActivity.class);
                ActivityCompat.finishAffinity(EditActivity.this);
                startActivity(showAdded);

            }
        });

        // Cancel
        Button cancelAdd = findViewById(R.id.returnToDB);
        cancelAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //Show movie poster
        Button showPoster = findViewById(R.id.posterShow);
        mPosterView = findViewById(R.id.posterView);
        final String picURLString = (MOVIE_BASE_URL + mEditPosterURL.getText().toString());
        showPoster.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Picasso.get()
                        .load(picURLString).
                        into(mPosterView);
            }
        });
    }
}
