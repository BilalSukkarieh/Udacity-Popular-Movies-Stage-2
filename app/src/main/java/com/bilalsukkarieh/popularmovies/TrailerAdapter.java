package com.bilalsukkarieh.popularmovies;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.io.File;

class TrailerAdapter extends RecyclerView.Adapter<TrailerAdapter.TrailerHolder>{

    private Context context;
    private ItemClickListener mItemClickListener;
    private Cursor trailerCursor;

    //click interface
    interface ItemClickListener{
        //pass the movie id and the thumnail url to the click to be used in intent
        void onItemClick(Cursor itemCursor);
    }
    //constructor of class
    public TrailerAdapter(Context c, ItemClickListener listItemClickListener){
        this.context = c;
        mItemClickListener = listItemClickListener;
    }

    @Override
    public TrailerHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //inflate the layout with the specified corresponding xml
        LayoutInflater li = LayoutInflater.from(context);
        View v = li.inflate(R.layout.grid_trailer_layout, parent, false);
        return new TrailerHolder(v);
    }

    @Override
    public void onBindViewHolder(TrailerHolder holder, int position) {
        trailerCursor.moveToPosition(position);

        String trailerimg = trailerCursor.getString(MovieDetails.INDEX_TRAILER_THUMBNAIL);
        File f = new File(trailerimg);
        Log.i("service", "picasso " + trailerimg);
        Picasso.with(context).load(f).into(holder.getTrailerThumbnail());
    }

    @Override
    public int getItemCount() {
        //return the items count
        return trailerCursor.getCount();
    }

    void swapCursor(Cursor newCursor) {
        trailerCursor = newCursor;
        notifyDataSetChanged();
    }

    class TrailerHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        //declare required layout variables
        ImageView trailerThumbnail;

        TrailerHolder(View itemView) {
            //class constructor and refrence variables
            super(itemView);
            trailerThumbnail = itemView.findViewById(R.id.trailer_thumbnail);
            itemView.setOnClickListener(this);
        }

        public ImageView getTrailerThumbnail(){
            //return object of the view elemnts
            return trailerThumbnail;
        }


        @Override
        public void onClick(View view) {
            //pass the data onto the click to be used in intent
            trailerCursor.moveToPosition(getAdapterPosition());

            mItemClickListener.onItemClick(trailerCursor);
        }
    }



}
