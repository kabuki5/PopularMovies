package com.benavides.ramon.popularmovies.fragments;

import android.app.Activity;
import android.app.Dialog;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import com.benavides.ramon.popularmovies.MovieDetailActivity;
import com.benavides.ramon.popularmovies.R;
import com.benavides.ramon.popularmovies.adapters.MoviesAdapter;
import com.benavides.ramon.popularmovies.data.Movie;
import com.benavides.ramon.popularmovies.interfaces.MovieSelectorInterface;
import com.benavides.ramon.popularmovies.interfaces.TmdbApiTaskListener;
import com.benavides.ramon.popularmovies.network.TmdbApiTask;
import com.benavides.ramon.popularmovies.utils.Utils;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Fragment class to show the movies into a grid view
 */
public class MoviesFragment extends Fragment implements TmdbApiTaskListener, AdapterView.OnItemClickListener {

    private static final String TAG = MoviesFragment.class.getSimpleName();

    @BindView(R.id.movies_grv)
    GridView mGridView;
    private MovieSelectorInterface mCallback;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mCallback = (MovieSelectorInterface) context;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mCallback = (MovieSelectorInterface) activity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.movies_fragment_layout, container, false);
        ButterKnife.bind(this, view);

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mGridView.setAdapter(new MoviesAdapter(getActivity()));
        mGridView.setOnItemClickListener(this);

        new TmdbApiTask(this, getActivity()).execute(Utils.readSortByPreference(getActivity()));//pass popular/top_rated parameter
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.action_bar, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_sort_by:

                final Dialog dialog = new Dialog(getActivity());
                dialog.setContentView(R.layout.choose_sort_by_dialog);
                dialog.show();

                View.OnClickListener clickListener = new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String selection;

                        if (v.getId() == R.id.popular_dialog_button) {
                            selection = getString(R.string.popular_low);
                        } else {
                            selection = getString(R.string.top_rated_low);
                        }

                        Utils.writeSortByPreference(getActivity(), selection);
                        new TmdbApiTask(MoviesFragment.this, getActivity()).execute(selection);//pass popular/top_rated parameter

                        dialog.dismiss();
                    }
                };

                dialog.findViewById(R.id.popular_dialog_button).setOnClickListener(clickListener);
                dialog.findViewById(R.id.top_rated_dialog_button).setOnClickListener(clickListener);

                break;
        }


        return super.onOptionsItemSelected(item);
    }

    //    APIDataTaskListener callback methods

    @Override
    public void onRequestSuccess(ArrayList<Movie> movies) {
        /*for (Movie movie : movies) {
            Log.v(TAG, movie.toString());
        }*/
        updateGridView(movies);
    }

    @Override
    public void onRequestError() {
        //TODO => Manage request API error. Maybe show a dialog with info.
    }


    //Gridview methods

    private void updateGridView(ArrayList<Movie> movies) {
        ((MoviesAdapter) mGridView.getAdapter()).setItems(movies);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Log.d("RBM", "onItemClick");
        Movie movie = (Movie) mGridView.getAdapter().getItem(position);
        //If it's tablet and landscape orientation
        if (Utils.isTablet(getActivity()) && getActivity().getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            if (mCallback != null) {
                mCallback.onMovieSelected(movie);
            }
        } else {        //If it's mobile
            Intent detailIntent = new Intent(getActivity(), MovieDetailActivity.class);
            detailIntent.putExtra(getString(R.string.movie_intent_tag), movie);
            startActivity(detailIntent);
        }


    }




}
