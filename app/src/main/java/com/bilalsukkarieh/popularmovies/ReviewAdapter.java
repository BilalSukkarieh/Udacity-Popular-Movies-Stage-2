package com.bilalsukkarieh.popularmovies;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.File;

class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewHolder>{

    private Context context;
    private Cursor reviewCursor;


    //constructor of class
    public ReviewAdapter(Context c){
        this.context = c;
    }

    @Override
    public ReviewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //inflate the layout with the specified corresponding xml

        LayoutInflater li = LayoutInflater.from(context);
        View view = li.inflate(R.layout.grid_review_layout,parent, false);

        return new ReviewHolder(view);

    }

    @Override
    public void onBindViewHolder(ReviewHolder holder, int position) {
        reviewCursor.moveToPosition(position);

        String author = reviewCursor.getString(MovieReviews.INDEX_REVIEW_AUTHOR);
        String review = reviewCursor.getString(MovieReviews.INDEX_REVIEW_CONTENT);

        holder.tv_author.setText(author);
        holder.tv_review.setText(review);

    }

    @Override
    public int getItemCount() {
        //return the items count
        return reviewCursor.getCount();
    }

    void swapCursor(Cursor newCursor) {
        reviewCursor = newCursor;
        notifyDataSetChanged();
    }

    class ReviewHolder extends RecyclerView.ViewHolder{
        //declare required layout variables
        TextView tv_author;
        TextView tv_review;

        ReviewHolder(View itemView) {
            //class constructor and refrence variables
            super(itemView);
            tv_author = itemView.findViewById(R.id.review_author);
            tv_review = itemView.findViewById(R.id.review_content);
        }


    }



}
