package isel.pdm.yamda.presentation.presenter;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.view.View;

import isel.pdm.yamda.data.handlers.MovieDetailsService;
import isel.pdm.yamda.model.entity.MovieDetails;
import isel.pdm.yamda.presentation.presenter.base.IPresenter;
import isel.pdm.yamda.presentation.view.activity.MovieActivity;
import isel.pdm.yamda.presentation.view.contract.ILoadDataView;

/**
 * Movie view details presenter
 */
public class MovieViewPresenter implements IPresenter, ILoadDataView<MovieDetails> {


    private final BroadcastReceiver receiver;
    private int id;
    private MovieActivity activity;

    public MovieViewPresenter(MovieActivity activity, int movieId) {
        this.activity = activity;
        this.id = movieId;

        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                setData((MovieDetails) intent.getParcelableExtra(MovieDetailsService.MOVIE_PARAM));
            }
        };

        this.askForData();
    }

    private void askForData() {
        this.showLoading();

        Intent intent = new Intent(this.activity, MovieDetailsService.class);
        intent.putExtra(MovieDetailsService.ID_PARAM, id);

        activity.startService(intent);
    }

    /*
    |--------------------------------------------------------------------------
    | DataView Methods
    |--------------------------------------------------------------------------
    */
    @Override
    public void showLoading() {
        this.activity.getMovieView().setVisibility(View.INVISIBLE);
        this.activity.getLoadingView().setVisibility(View.VISIBLE);
    }

    @Override
    public void hideLoading() {
        this.activity.getLoadingView().setVisibility(View.INVISIBLE);
        this.activity.getMovieView().setVisibility(View.VISIBLE);
    }

    @Override
    public void showError(String message) {
        this.hideLoading();
    }

    @Override
    public void setData(MovieDetails data) {
        this.hideLoading();
        this.activity.updateView(data);
    }

    /*
    |--------------------------------------------------------------------------
    | Presenter Lifecycle
    |--------------------------------------------------------------------------
    */
    @Override
    public void onResume() {
        activity.registerReceiver(receiver, new IntentFilter(MovieDetailsService.NOTIFICATION));
    }

    @Override
    public void onPause() {
        activity.unregisterReceiver(receiver);
    }

    @Override
    public void onDestroy() {

    }

}
