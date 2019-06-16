package salomatin.com.movielist.activities;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import salomatin.com.movielist.R;
import salomatin.com.movielist.database.DatabaseHelper;
import salomatin.com.movielist.database.MovieModel;

public class SearchActivity extends AppCompatActivity {

    // This class is responsible for all actions in Movie Search page.

    private static final String BASE_MOVIES_URL = "https://api.themoviedb.org/3/search/movie?api_key=7f19a16c765fdf76ac0f74a04961f8a0&query=";

    private EditText mSearchEditText;
    private ListView mSearchMoviesListView;
    private ProgressDialog mProgressDialog;

    private ArrayList<MovieModel> mMoviesList;
    private String searchContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(!connectionCheck(SearchActivity.this)) buildDialog(SearchActivity.this).show();
        else {
            setContentView(R.layout.activity_search);
            initObjects();
        }
    }


    //Check network connection
    public boolean connectionCheck(Context context) {

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netinfo = cm.getActiveNetworkInfo();

        if (netinfo != null && netinfo.isConnectedOrConnecting()) {
            android.net.NetworkInfo wifi = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            android.net.NetworkInfo mobile = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

            if((mobile != null && mobile.isConnectedOrConnecting()) || (wifi != null && wifi.isConnectedOrConnecting())) return true;
            else return false;
        } else
            return false;
    }

    public AlertDialog.Builder buildDialog(Context c) {

        AlertDialog.Builder builder = new AlertDialog.Builder(c);
        builder.setTitle(getString(R.string.warning_title_text));
        builder.setMessage(getString(R.string.network_error_text));

        builder.setPositiveButton(getString(R.string.cancel_search_text), new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                finish();
            }
        });

        return builder;
    }

    private void initObjects() {
        mSearchEditText = findViewById(R.id.movNameSearch);
        mSearchMoviesListView = findViewById(R.id.searchList);
        mProgressDialog = new ProgressDialog(this);


        // Search button listener
        Button searchButton = findViewById(R.id.searchButton);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchContent = mSearchEditText.getText().toString();
                if (("").equals(searchContent)) {
                    Toast.makeText(SearchActivity.this, getString(R.string.empty_search_toast_text), Toast.LENGTH_SHORT).show();
                    return;
                }
                mProgressDialog.setMessage(getString(R.string.search_toast_text));
                mProgressDialog.show();
                GetMoviesAsyncTask getMoviesAsyncTask = new GetMoviesAsyncTask();
                getMoviesAsyncTask.execute(BASE_MOVIES_URL + searchContent);
            }
        });
        mSearchMoviesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                MovieModel movie = mMoviesList.get(i);
                Intent viewMovieIntent = new Intent(SearchActivity.this, EditActivity.class);
                viewMovieIntent.putExtra(EditActivity.ACTION_KEY, EditActivity.ACTION_EDIT);
                viewMovieIntent.putExtra(DatabaseHelper.MOVIE_TITLE, movie.getTitle());
                viewMovieIntent.putExtra(DatabaseHelper.MOVIE_SUMMARY, movie.getOverview());
                viewMovieIntent.putExtra(DatabaseHelper.MOVIE_POSTER, movie.getPoster_path());
                startActivity(viewMovieIntent);
            }
        });
    }

    // Go back
    public void onClickCancel(View v) {
        setResult(0, getIntent());
        finish();
    }

    // Json AsyncTask
    public class GetMoviesAsyncTask extends AsyncTask<String, Integer, ArrayList<MovieModel>> {

        @Override
        protected ArrayList<MovieModel> doInBackground(String... urls) {
            OkHttpClient client = new OkHttpClient();
            String urlQuery = urls [0];
            Request request = new Request.Builder()
                    .url(urlQuery)
                    .build();

            Response response = null;
            try {
                response = client.newCall(request).execute();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (!response.isSuccessful()) try {
                throw new IOException("Unexpected code " + response);
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                return  getMoviesListFromJson(response.body().string());
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;

        }

        //Show search result
        @Override
        protected void onPostExecute(ArrayList<MovieModel> moviesList) {
            mProgressDialog.dismiss();
            mMoviesList = moviesList;
            ArrayAdapter<MovieModel> adapter = new ArrayAdapter<MovieModel>(SearchActivity.this, android.R.layout.simple_list_item_1, mMoviesList);
            mSearchMoviesListView.setAdapter(adapter);
        }

        public ArrayList<MovieModel> getMoviesListFromJson(String jsonResponse) {
            List<MovieModel> stubMovieData = new ArrayList<MovieModel>();
            Gson gson = new GsonBuilder().create();
            MovieResponse response = gson.fromJson(jsonResponse, MovieResponse.class);
            stubMovieData = response.results;
            ArrayList<MovieModel> arrList = new ArrayList<>();
            arrList.addAll(stubMovieData);
            return arrList;
        }

        public class MovieResponse {

            private List<MovieModel> results;

            // public constructor is necessary for collections
            public MovieResponse() {
                results = new ArrayList<MovieModel>();
            }

        }
    }
}