package com.example.android.popularmovies;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.popularmovies.utilities.ReviewData;

public class ReviewsAdapter extends RecyclerView.Adapter<ReviewsAdapter.ReviewViewHolder> {

    private Cursor mCursor;
    private Context mContext;
    private ReviewData[] reviews;

    public ReviewsAdapter(Context mContext) {
        this.mContext = mContext;
    }

    @Override
    public ReviewViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Inflate the review to a view
        View view = LayoutInflater.from(mContext).inflate(R.layout.review, parent, false);
        return new ReviewsAdapter.ReviewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ReviewViewHolder holder, int position) {
        holder.author.setText(reviews[position].author);
        holder.content.setText(reviews[position].content);
    }

    @Override
    public int getItemCount() {
        if (reviews == null) return 0;
        else return reviews.length;
    }

    public void setReviewsData(ReviewData[] reviewsData) {
        reviews = reviewsData;
        notifyDataSetChanged();
    }

    class ReviewViewHolder extends RecyclerView.ViewHolder {
        // Class variables for the author and content TextViews
        TextView author;
        TextView content;

        public ReviewViewHolder(View itemView) {
            super(itemView);
            author = (TextView) itemView.findViewById(R.id.tv_review_author);
            content = (TextView) itemView.findViewById(R.id.tv_review_content);
        }
    }
}
