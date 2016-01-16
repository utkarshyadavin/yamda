package isel.pdm.yamda.ui.fragment.common;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import isel.pdm.yamda.R;
import isel.pdm.yamda.model.Movie;
import isel.pdm.yamda.ui.activity.MovieActivity;
import isel.pdm.yamda.ui.adapter.MovieRecyclerAdapter;
import isel.pdm.yamda.ui.fragment.base.LoadDataFragment;

/**
 * Common code for the movies list fragments
 */
public abstract class MovieListableFragment extends LoadDataFragment<List<Movie>> {

    public static final String RETRY_VIEW = "retry_view";
    protected RecyclerView listView;
    protected MovieRecyclerAdapter adapter;

    List<Movie> data;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View viewContainer = super.onCreateView(inflater, container, savedInstanceState);

        this.listView = (RecyclerView) this.mainView;

        this.setupListView();
        this.setListViewAdapter();

        // if no connection, retry_view retains state on rotate
        if(savedInstanceState != null && savedInstanceState.getBoolean(RETRY_VIEW)) {
            showNoConnection();
        } else {
            //first time creating the fragment? ask for data
            if(data == null) {
                Log.d(TAG, "onCreateView: execute presenter!");
                this.presenter.execute();
            } else {
                //there is already data? screen must be rotating or tab switching
                this.setData(data);
            }
        }

        return viewContainer;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        //save state if retry_view is active
        outState.putBoolean(RETRY_VIEW, data == null);
    }

    /**
     * Setup the RecyclerView
     */
    private void setupListView() {
        listView.setHasFixedSize(true);

        // use a linear layout manager
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        listView.setLayoutManager(mLayoutManager);
    }

    /**
     * Setup the adapter
     */
    private void setListViewAdapter() {
         this.adapter = new MovieRecyclerAdapter(this.getActivity());

        adapter.setListener(new MovieRecyclerAdapter.IClickListener() {
            @Override
            public void onItemClick(Movie movie) {
                Intent i = MovieActivity.createIntent(getActivity(), movie.getId());
                getActivity().startActivity(i);
            }
        });

        this.listView.setAdapter(adapter);
    }

    @Override
    public void setData(List<Movie> data) {
        Log.d(TAG, "setData: data! size: " + data.size());
        this.showResults();
        this.data = data;
        this.adapter.setData(data);
    }

    @Override
    protected int getLayout() {
        return R.layout.list_movies_layout;
    }

    @Override
    public void showResults() {
        this.frameLayout.removeAllViews();
        this.frameLayout.addView(listView);
    }
}
