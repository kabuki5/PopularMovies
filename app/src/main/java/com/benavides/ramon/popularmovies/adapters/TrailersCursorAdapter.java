package com.benavides.ramon.popularmovies.adapters;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.benavides.ramon.popularmovies.R;
import com.benavides.ramon.popularmovies.database.MoviesContract;

/**
 * Cursor adapter for Reviews Fragment
 */
public class TrailersCursorAdapter extends CursorAdapter {
    private Context mContext;

    public TrailersCursorAdapter(Context context, Cursor c, boolean autoRequery) {
        super(context, c, autoRequery);
        mContext = context;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_trailer_layout, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder vh = ((ViewHolder) view.getTag());
        vh.name.setText(cursor.getString(MoviesContract.TrailerEntry.TRAILER_COLUMN_NAME));
    }

    static class ViewHolder {

        public final TextView name;

        public ViewHolder(View view) {
            name = (TextView) view.findViewById(R.id.item_trailer_tev);
        }
    }
}
