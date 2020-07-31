package com.krrtvl.court;

import androidx.annotation.NonNull;
import androidx.paging.PageKeyedDataSource;

import com.krrtvl.court.domain.Visits;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VisitsDataSource extends PageKeyedDataSource<Integer, Visits> {

    public static final int FirstPage = 1;
    public static final int PageSize = 20;

    public VisitsDataSource() {
    }

    @Override
    public void loadInitial(@NonNull LoadInitialParams<Integer> params, @NonNull final LoadInitialCallback<Integer, Visits> callback) {
        NetworkService.getInstance()
                .getJSONApi()
                .getVisitsPage(FirstPage, PageSize)
                .enqueue(new Callback<List<Visits>>() {
                    @Override
                    public void onResponse(@NonNull Call<List<Visits>> call, @NonNull Response<List<Visits>> response) {
                        if (!response.body().isEmpty()) {
                            callback.onResult(response.body(), null, FirstPage + 1);
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<List<Visits>> call, @NonNull Throwable t) {
                        t.printStackTrace();
                    }
                });
    }

    @Override
    public void loadBefore(@NonNull LoadParams<Integer> params, @NonNull LoadCallback<Integer, Visits> callback) {

    }

    @Override
    public void loadAfter(@NonNull final LoadParams<Integer> params, @NonNull final LoadCallback<Integer, Visits> callback) {
        NetworkService.getInstance()
                .getJSONApi()
                .getVisitsPage(params.key, PageSize)
                .enqueue(new Callback<List<Visits>>() {
                    @Override
                    public void onResponse(@NonNull Call<List<Visits>> call, @NonNull Response<List<Visits>> response) {
                        if (!response.body().isEmpty()) {
                            callback.onResult(response.body(),  params.key + 1);
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<List<Visits>> call, @NonNull Throwable t) {
                        t.printStackTrace();
                    }
                });
    }
}

