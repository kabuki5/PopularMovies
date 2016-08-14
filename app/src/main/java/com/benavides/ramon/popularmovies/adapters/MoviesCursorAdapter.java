package com.benavides.ramon.popularmovies.adapters;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;

import com.benavides.ramon.popularmovies.R;
import com.benavides.ramon.popularmovies.cviews.StateImageView;
import com.benavides.ramon.popularmovies.database.MoviesContract;
import com.squareup.picasso.Picasso;

/**
 * Created by ramon on 23/7/16.
 */
public class MoviesCursorAdapter extends CursorAdapter {

    private Context mContext;

    public MoviesCursorAdapter(Context context, Cursor c, boolean autoRequery) {
        super(context, c, autoRequery);
        this.mContext = context;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = (LayoutInflater.from(mContext).inflate(R.layout.movie_item, parent, false));
        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder vh = ((ViewHolder) view.getTag());
        StateImageView movieImv = vh.imageView;

        String posterUrl = cursor.getString(MoviesContract.MovieEntry.MOVIES_COLUMN_POSTER);

        Picasso.with(mContext).load(posterUrl).placeholder(R.drawable.ic_movie).into(movieImv);

    }

    public static class ViewHolder {
        public final StateImageView imageView;

        public ViewHolder(View view) {
            imageView = (StateImageView) view.findViewById(R.id.movie_item_imv);
        }

    }
}
