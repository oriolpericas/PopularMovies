package com.example.android.popularmovies;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.android.popularmovies.data.MovieContract;
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
    // Toggle display or hide favorites
    private boolean showFavorites = false;
    private Menu mMenu;
    // Adapter to display the favorite movies
    private FavoriteMoviesAdapter mFavoriteMoviesAdapter;
    // Item touch helper used to swipe off favorited movies
    private ItemTouchHelper mItemTouchHelper;

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
        // Create and set the favorite movies adapter
        mFavoriteMoviesAdapter = new FavoriteMoviesAdapter(this);

        // Default sorting mode is by popularity
        sortingMode = NetworkUtils.PATH_POPULAR;
        // Now load the data in a background task
        loadData();

        // Create a touch helper which will be used to delete favorited movies
        mItemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }
            // Called when a user swipes left or right on a ViewHolder
            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                int id = (int) viewHolder.itemView.getTag();
                // Build appropriate uri with String row id appended
                String stringId = Integer.toString(id);
                Uri uri = MovieContract.MovieEntry.CONTENT_URI;
                uri = uri.buildUpon().appendPath(stringId).build();
                // Delete the single row from the DB
                getContentResolver().delete(uri, null, null);
                // Reload the adapter's data
                reloadFavoriteMoviesData();
            }
        });
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
                String jsonResponse = NetworkUtils.getResponseFromHttpUrl(movieRequestUrl);
                Log.d("DEBUG", jsonResponse);
                MovieData[] parsedData = JsonUtils.getMovieDataFromJson(MainActivity.this, jsonResponse);
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
        mMenu = menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.menu_refresh:
                mAdapter.setMovieData(null);
                loadData();
                break;
            case R.id.menu_sort_popularity:
                if (!sortingMode.equals(NetworkUtils.PATH_POPULAR)) {
                    // Reload only if the mode changed
                    sortingMode = NetworkUtils.PATH_POPULAR;
                    loadData();
                }
                break;
            case R.id.menu_sort_rating:
                if (!sortingMode.equals(NetworkUtils.PATH_RATING)) {
                    // Reload only if the mode changed
                    sortingMode = NetworkUtils.PATH_RATING;
                    loadData();
                }
                break;
            case R.id.menu_toggle_favorites:
                if (showFavorites) {
                    // Toggle the icon state and text
                    showFavorites = false;
                    mMenu.findItem(R.id.menu_toggle_favorites).setTitle("Show favorites");
                    mMenu.findItem(R.id.menu_toggle_favorites).setIcon(R.drawable.ic_star_empty);
                    // Display all the movies postesr
                    showAllMovies();
                } else {
                    // Toggle the icon state and text
                    showFavorites = true;
                    mMenu.findItem(R.id.menu_toggle_favorites).setTitle("Show all");
                    mMenu.findItem(R.id.menu_toggle_favorites).setIcon(R.drawable.ic_star_fill);
                    // Display the favorite movies list
                    showFavoriteMovies();
                }
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    private void showFavoriteMovies(){
        // Load the favorite movies list
        reloadFavoriteMoviesData();
        // Switch the favorite movies adapter into the recycler view
        mRecyclerView.setAdapter(mFavoriteMoviesAdapter);
        // Set grid size to 1
        GridLayoutManager layoutManager = new GridLayoutManager(this, 1);
        mRecyclerView.setLayoutManager(layoutManager);
        // Attach the touch helper to the recycler view
        mItemTouchHelper.attachToRecyclerView(mRecyclerView);
    }

    private void reloadFavoriteMoviesData() {
        // Load the favorite movies data from the DB
        Cursor c =  getContentResolver().query(MovieContract.MovieEntry.CONTENT_URI,
                null,
                null,
                null,
                MovieContract.MovieEntry.COLUMN_MOVIE_ID);
        mFavoriteMoviesAdapter.swapCursor(c);
    }

    private void showAllMovies() {
        // Show the movie posters adapter
        mRecyclerView.setAdapter(mAdapter);
        // Restore grid size to 2
        GridLayoutManager layoutManager = new GridLayoutManager(this, 2);
        mRecyclerView.setLayoutManager(layoutManager);
        // Detach the touch helper
        mItemTouchHelper.attachToRecyclerView(null);
    }

    @Override
    public void onClick(MovieData movie) {
        Intent intent = new Intent(this, MovieDetail.class);
        intent.putExtra("movie", movie);
        startActivity(intent);
    }

}
