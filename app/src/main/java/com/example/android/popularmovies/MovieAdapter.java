package com.example.android.popularmovies;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.android.popularmovies.utilities.MovieData;
import com.example.android.popularmovies.utilities.NetworkUtils;
import com.squareup.picasso.Picasso;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieViewHolder>{

    private static final String TAG = MovieAdapter.class.getSimpleName();

    private MovieData[] posters;
    private Context mContext;
    private MovieAdapterOnClickHandler mClickHandler;

    public MovieAdapter(Context context, MovieAdapterOnClickHandler onClickHandler) {
        mContext = context;
        mClickHandler = onClickHandler;
    }

    @Override
    public MovieViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.poster, parent, false);
        return new MovieViewHolder(v);
    }

    @Override
    public void onBindViewHolder(MovieViewHolder holder, int position) {
        String posterUrl = NetworkUtils.buildPosterUrl(posters[position]);
        Log.d(TAG, posterUrl);
        Picasso.with(mContext).load(posterUrl).into(holder.mImageView);
    }

    @Override
    public int getItemCount() {
        if (posters == null) return 0;
        else return posters.length;
    }

    public void setMovieData(MovieData[] postersData) {
        posters = postersData;
        notifyDataSetChanged();
    }

    public class MovieViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private ImageView mImageView;
        public MovieViewHolder(View view) {
            super(view);
            mImageView = (ImageView) view.findViewById(R.id.iv_poster);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            MovieData movie = posters[adapterPosition];
            mClickHandler.onClick(movie);
        }
    }

    public interface MovieAdapterOnClickHandler {
        void onClick(MovieData movie);
    }

}
