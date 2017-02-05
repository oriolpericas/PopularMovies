package com.example.android.popularmovies;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.popularmovies.utilities.MovieData;
import com.example.android.popularmovies.utilities.NetworkUtils;
import com.squareup.picasso.Picasso;

public class MovieDetail extends AppCompatActivity {

    MovieData movie;

    TextView mTitleTextView;
    ImageView mImageView;
    TextView mReleaseDateTextView;
    TextView mVoteAverageTextView;
    TextView mDescriptionTextView;

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
    }
}
