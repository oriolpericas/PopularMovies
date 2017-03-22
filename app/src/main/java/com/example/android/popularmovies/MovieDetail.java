package com.example.android.popularmovies;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.popularmovies.data.MovieContract;
import com.example.android.popularmovies.utilities.JsonUtils;
import com.example.android.popularmovies.utilities.MovieData;
import com.example.android.popularmovies.utilities.NetworkUtils;
import com.example.android.popularmovies.utilities.ReviewData;
import com.example.android.popularmovies.utilities.TrailerData;
import com.squareup.picasso.Picasso;

import java.net.URL;

public class MovieDetail extends AppCompatActivity {

    MovieData movie;
    TrailerData trailer;

    TextView mTitleTextView;
    ImageView mImageView;
    TextView mReleaseDateTextView;
    TextView mVoteAverageTextView;
    TextView mDescriptionTextView;
    ProgressBar mLoadingIndicatorTrailers;
    Button mButtonPlayTrailer;
    private RecyclerView mRecyclerView;
    private ReviewsAdapter mReviewsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);

        // Find views
        mTitleTextView = (TextView) findViewById(R.id.tv_details_title);
        mImageView = (ImageView) findViewById(R.id.iv_details_poster);
        mReleaseDateTextView = (TextView) findViewById(R.id.tv_details_release_date);
        mVoteAverageTextView = (TextView) findViewById(R.id.tv_details_vote_average);
        mDescriptionTextView = (TextView) findViewById(R.id.tv_details_description);
        mLoadingIndicatorTrailers = (ProgressBar) findViewById(R.id.pb_loading_indicator_trailers);
        mButtonPlayTrailer = (Button) findViewById(R.id.bt_play_trailer);

        // Create the recycler view used for the reviews
        mRecyclerView = (RecyclerView) findViewById(R.id.rv_reviews);
        GridLayoutManager layoutManager = new GridLayoutManager(this, 1);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);
        mReviewsAdapter = new ReviewsAdapter(this);
        mRecyclerView.setAdapter(mReviewsAdapter);


        // Get movie data from intent
        Intent intent = getIntent();
        if (intent != null) {
            if (intent.hasExtra("movie")) {
                movie = intent.getExtras().getParcelable("movie");
                // Show title
                mTitleTextView.setText(movie.original_title);
                // Load poster thumbnail
                String posterUrl = NetworkUtils.buildPosterUrl(movie);
                Picasso.with(this).load(posterUrl).resize(360,540).into(mImageView);
                // Show release date and user rating
                mReleaseDateTextView.setText(getString(R.string.release_date, movie.release_date));
                mVoteAverageTextView.setText(getString(R.string.user_rating, movie.vote_average));
                // Show description
                mDescriptionTextView.setText(movie.overview);
            }
        }

        // Request movie trailers
        new FetchMovieTrailersTask().execute(movie.id);
        // Request movie reviews
        new FetchMovieReviewsTask().execute(movie.id);
    }

    private class FetchMovieTrailersTask extends AsyncTask<Integer, Void, TrailerData[]> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Show loading indicator at start
            mLoadingIndicatorTrailers.setVisibility(View.VISIBLE);
        }

        @Override
        protected TrailerData[] doInBackground(Integer... params) {
            if (params.length == 0) return null;
            int id = params[0];
            Context mContext = getApplicationContext();

            // Get the trailers data
            URL trailersURL = NetworkUtils.buildUrlVideos(id);
            try {
                // Get the response and parse
                String jsonResponse = NetworkUtils.getResponseFromHttpUrl(trailersURL);
                Log.d("DEBUG", jsonResponse);
                return JsonUtils.getTrailerDataFromJson(mContext, jsonResponse);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(TrailerData[] trailerDatas) {
            // Hide the loading indicator and show the button
            mLoadingIndicatorTrailers.setVisibility(View.GONE);
            mButtonPlayTrailer.setVisibility(View.VISIBLE);
            // Save the first trailer
            trailer = trailerDatas[0];
        }
    }


    private class FetchMovieReviewsTask extends AsyncTask<Integer, Void, ReviewData[]> {

        @Override
        protected ReviewData[] doInBackground(Integer... params) {
            if (params.length == 0) return null;
            int id = params[0];
            Context mContext = getApplicationContext();

            // Get the reviews data
            URL reviewsURL = NetworkUtils.buildUrlReviews(id);
            try {
                // Get the response and parse
                String jsonResponse = NetworkUtils.getResponseFromHttpUrl(reviewsURL);
                Log.d("DEBUG", jsonResponse);
                return JsonUtils.getReviewDataFromJson(mContext, jsonResponse);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(ReviewData[] reviewDatas) {
            if (reviewDatas != null) {
                mRecyclerView.setVisibility(View.VISIBLE);
                mReviewsAdapter.setReviewsData(reviewDatas);
            }
        }
    }

    public void markFavorite(View view) {
        // Check if this movie is already favorite
        Cursor c = getContentResolver().query(MovieContract.MovieEntry.CONTENT_URI,
                null,
                MovieContract.MovieEntry.COLUMN_MOVIE_ID + "=?",
                new String[]{String.valueOf(movie.id)},
                MovieContract.MovieEntry.COLUMN_MOVIE_ID);
        if (c == null) return;
        if (c.getCount() == 0) {
            ContentValues contentValues = new ContentValues();
            // Put the movie ID and title
            contentValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_ID, movie.id);
            contentValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_TITLE, movie.original_title);
            // Insert the content values
            getContentResolver().insert(MovieContract.MovieEntry.CONTENT_URI, contentValues);
            // Show a toast
            Toast.makeText(this, "Movie saved to favorites", Toast.LENGTH_SHORT).show();
        } else {
            // Show a toast saying the movie is already favorite
            Toast.makeText(this, "Movie is already favorite", Toast.LENGTH_SHORT).show();
        }
        c.close();
    }

    public void playTrailer(View view) {
        String link = "http://www.youtube.com/watch?v=" + trailer.key;
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(link)));
    }
}
