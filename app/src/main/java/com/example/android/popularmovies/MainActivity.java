package com.example.android.popularmovies;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.android.popularmovies.utilities.JsonUtils;
import com.example.android.popularmovies.utilities.MovieData;
import com.example.android.popularmovies.utilities.NetworkUtils;

import java.net.URL;

public class MainActivity extends AppCompatActivity implements MovieAdapter.MovieAdapterOnClickHandler{

    private RecyclerView mRecyclerView;
    private MovieAdapter mAdapter;
    private ProgressBar mLoadingIndicator;
    private TextView mErrorMessageDisplay;
    // Hold the current sorting mode
    private String sortingMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Set up views
        mLoadingIndicator = (ProgressBar) findViewById(R.id.pb_loading_indicator);
        mErrorMessageDisplay = (TextView) findViewById(R.id.tv_error_message);
        mRecyclerView = (RecyclerView) findViewById(R.id.rv_discovery);

        // Create and set layout  manager
        GridLayoutManager layoutManager = new GridLayoutManager(this, 2);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);
        // Create and set the movies adapter
        mAdapter = new MovieAdapter(this, this);
        mRecyclerView.setAdapter(mAdapter);

        // Default sorting mode is by popularity
        sortingMode = NetworkUtils.PATH_POPULAR;
        // Now load the data in a background task
        loadData();
    }

    private void loadData() {
        new FetchMovieDataTask().execute(sortingMode);
    }

    public class FetchMovieDataTask extends AsyncTask<String, Void, MovieData[]> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Show loading indicator at start
            mLoadingIndicator.setVisibility(View.VISIBLE);
        }

        @Override
        protected MovieData[] doInBackground(String... params) {
            // Exit if no parameter was passed
            if (params.length == 0) {
                return null;
            }
            // Take the sorting mode from the only parameter
            String sorting = params[0];
            // Build the URL
            URL movieRequestUrl = NetworkUtils.buildUrl(sorting);
            Log.d("DEBUG", movieRequestUrl.toString());
            try {
                // Get the response and parse
                String jsonWeatherResponse = NetworkUtils.getResponseFromHttpUrl(movieRequestUrl);
                MovieData[] parsedData = JsonUtils.getMovieDataFromJson(MainActivity.this, jsonWeatherResponse);
                return parsedData;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(MovieData[] data) {
            // Hide loading indicator
            mLoadingIndicator.setVisibility(View.INVISIBLE);
            if (data != null) {
                // Show the posters view
                showMoviePostersView();
                // Assign the movie data to the adapter
                mAdapter.setMovieData(data);
            } else {
                showErrorMessage();
            }
        }
    }

    private void showMoviePostersView() {
        // Show the movie posters and hide the error text view
        mErrorMessageDisplay.setVisibility(View.INVISIBLE);
        mRecyclerView.setVisibility(View.VISIBLE);
    }

    private void showErrorMessage() {
        // Hide the movie posters and show the error text view
        mRecyclerView.setVisibility(View.INVISIBLE);
        mErrorMessageDisplay.setVisibility(View.VISIBLE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_activity_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.menu_refresh) {
            mAdapter.setMovieData(null);
            loadData();
        }

        if (id == R.id.menu_sort_popularity) {
            if (sortingMode != NetworkUtils.PATH_POPULAR) {
                // Reload only if the mode changed
                sortingMode = NetworkUtils.PATH_POPULAR;
                loadData();
            }
        }

        if (id == R.id.menu_sort_rating) {
            if (sortingMode != NetworkUtils.PATH_RATING) {
                // Reload only if the mode changed
                sortingMode = NetworkUtils.PATH_RATING;
                loadData();
            }
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(MovieData movie) {
        Intent intent = new Intent(this, MovieDetail.class);
        intent.putExtra("movie", movie);
        startActivity(intent);
    }
}
