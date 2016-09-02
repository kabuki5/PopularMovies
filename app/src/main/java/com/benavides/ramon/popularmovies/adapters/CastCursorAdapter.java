package com.benavides.ramon.popularmovies.adapters;

import android.content.Context;
import android.database.Cursor;
import android.speech.tts.TextToSpeech;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.benavides.ramon.popularmovies.R;
import com.benavides.ramon.popularmovies.cviews.RoundedImageView;
import com.benavides.ramon.popularmovies.database.MoviesContract;
import com.squareup.picasso.Picasso;

/**
 * Cursor adapter for Reviews Fragment
 */
public class CastCursorAdapter extends CursorAdapter {
    private Context mContext;

    public CastCursorAdapter(Context context, Cursor c, boolean autoRequery) {
        super(context, c, autoRequery);
        mContext = context;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_actor, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder vh = ((ViewHolder) view.getTag());
        Picasso.with(context).load(cursor.getString(MoviesContract.CastEntry.CAST_COLUMN_PICTURE)).error(R.mipmap.ic_launcher).into(vh.picture);
        vh.name.setText(cursor.getString(MoviesContract.ReviewEntry.REVIEWS_COLUMN_AUTHOR));
        vh.character.setText(cursor.getString(MoviesContract.ReviewEntry.REVIEWS_COLUMN_CONTENT));
    }

    static class ViewHolder {

        public final RoundedImageView picture;
        public final TextView name;
        public final TextView character;

        public ViewHolder(View view) {
            picture = (RoundedImageView) view.findViewById(R.id.item_actor_thumb);
            name = (TextView) view.findViewById(R.id.item_actor_name_tev);
            character = (TextView)view.findViewById(R.id.item_actor_character_tev);
        }
    }
}
