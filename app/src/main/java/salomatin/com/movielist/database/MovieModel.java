package salomatin.com.movielist.database;


import android.text.TextUtils;
import android.util.Log;

import java.io.Serializable;

// This class represents our Movie object, with all it's attributes and methods to get them.

public class MovieModel implements Serializable {

    private static final String SMALL_POSTER_SIZE = "/w154";
    private static final String BIG_POSTER_SIZE = "/original";

    private String title = "title";
    private String release_date;
    private String poster_path;
    private String overview;
    private String backdrop_path;
    private boolean adult;
    private long id;
    private float popularity;
    private boolean video;
    private float vote_average;
    private int vote_count;

    public MovieModel(int id, String title, String plot, String posterPic) {
        this.id = id;
        this.title = title;
        this.overview = plot;
        this.poster_path = posterPic;
    }

    public MovieModel(String title, String plot, String posterPic) {
        this.title = title;
        this.overview = plot;
        this.poster_path = posterPic;
    }

    public boolean isAdult() {
        return adult;
    }

    public void setAdult(boolean adult) {
        this.adult = adult;
    }

    public String getBackdrop_path() {
        return backdrop_path;
    }

    public void setBackdrop_path(String backdrop_path) {
        this.backdrop_path = backdrop_path;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getRelease_date() {
        return release_date;
    }

    public void setRelease_date(String release_date) {
        this.release_date = release_date;
    }

    public String getPoster_path() {
        return poster_path;
    }

    public void setPoster_path(String poster_path) {
        this.poster_path = poster_path;
    }

    public float getPopularity() {
        return popularity;
    }

    public void setPopularity(float popularity) {
        this.popularity = popularity;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isVideo() {
        return video;
    }

    public void setVideo(boolean video) {
        this.video = video;
    }

    public float getVote_average() {
        return vote_average;
    }

    public void setVote_average(float vote_average) {
        this.vote_average = vote_average;
    }

    public int getVote_count() {
        return vote_count;
    }

    public void setVote_count(int vote_count) {
        this.vote_count = vote_count;
    }


    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }


    public String getYearOfRelease() {
        if (!TextUtils.isEmpty(release_date)) {
            return release_date.substring(0, 4);
        } else {
            return "";
        }
    }

    public String toString() {
        Log.d("Tag", "MovieModel toString");
        String theTitle = title.toString();
        return theTitle;
    }
}


