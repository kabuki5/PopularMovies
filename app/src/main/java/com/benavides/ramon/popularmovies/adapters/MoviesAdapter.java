package com.benavides.ramon.popularmovies.adapters;

/**
 * Adapter class to show each movie item in the grid view
 */


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.benavides.ramon.popularmovies.R;
import com.benavides.ramon.popularmovies.data.Movie;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class MoviesAdapter extends BaseAdapter {

    private Context mContext;

    class ViewHolder {
        ImageView imageView;
    }

    private ArrayList<Movie> items;

    public MoviesAdapter(Context context) {
        this.mContext = context;
        items = new ArrayList<>();
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder vh;
        if (convertView == null) {
            vh = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.movie_item, parent, false);
            vh.imageView = (ImageView) convertView.findViewById(R.id.movie_item_imv);
            convertView.setTag(vh);
        } else {
            vh = (ViewHolder) convertView.getTag();
        }

        Movie movie = items.get(position);

        Picasso.with(mContext).load(movie.getPoster()).into(vh.imageView);

        return convertView;
    }

    public ArrayList<Movie> getItems() {
        return items;
    }

    public void setItems(ArrayList<Movie> items) {
        this.items = items;
        notifyDataSetChanged();
    }
}