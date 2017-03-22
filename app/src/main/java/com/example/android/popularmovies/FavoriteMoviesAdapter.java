package com.example.android.popularmovies;


import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.popularmovies.data.MovieContract;

public class FavoriteMoviesAdapter extends RecyclerView.Adapter<FavoriteMoviesAdapter.MovieViewHolder> {

    private Cursor mCursor;
    private Context mContext;

    public FavoriteMoviesAdapter(Context mContext) {
        this.mContext = mContext;
    }

    @Override
    public MovieViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Inflate the favorite movie to a view
        View view = LayoutInflater.from(mContext)
                .inflate(R.layout.favorite_movie, parent, false);
        return new MovieViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MovieViewHolder holder, int position) {
        // Indices for the _id, movie ID and title columns
        int idIndex = mCursor.getColumnIndex(MovieContract.MovieEntry._ID);
        int movieIdIndex = mCursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_MOVIE_ID);
        int titleIndex = mCursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_MOVIE_TITLE);

        // Move cursor to the right position
        mCursor.moveToPosition(position);

        // Get the values from the cursor
        final int id = mCursor.getInt(idIndex);
        String movieId = mCursor.getString(movieIdIndex);
        String title = mCursor.getString(titleIndex);

        Log.d("DEBUG", "ID " + movieId + " Title " + title);

        //Set values
        holder.itemView.setTag(id);
        holder.movieId.setText(movieId);
        holder.movieTitle.setText(title);
    }

    @Override
    public int getItemCount() {
        if (mCursor == null)return 0;
        return mCursor.getCount();
    }

    public void swapCursor(Cursor c) {
        // If the cursor has not changed, do nothing
        if (mCursor == c) {
            return;
        }
        // Assign the new cursor
        this.mCursor = c;
        // If this is a valid cursor, update
        if (c != null)
            this.notifyDataSetChanged();
    }

    // Inner class for creating ViewHolders
    class MovieViewHolder extends RecyclerView.ViewHolder {

        // Class variables for the task description and priority TextViews
        TextView movieId;
        TextView movieTitle;

        public MovieViewHolder(View itemView) {
            super(itemView);
            movieId = (TextView) itemView.findViewById(R.id.favorite_movie_id);
            movieTitle = (TextView) itemView.findViewById(R.id.favorite_movie_title);
        }
    }
}
