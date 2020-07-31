package com.krrtvl.court;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import androidx.paging.LivePagedListBuilder;
import androidx.paging.PageKeyedDataSource;
import androidx.paging.PagedList;

import com.krrtvl.court.domain.Visits;

public class VisitsViewModel extends ViewModel {

    LiveData<PagedList<Visits>> visitsPagedList;
    LiveData<PageKeyedDataSource<Integer, Visits>> liveDataSource;

    public VisitsViewModel(){
        VisitsDataSourceFactory visitsDataSourceFactory = new VisitsDataSourceFactory();
        liveDataSource = visitsDataSourceFactory.getVisitsLiveDataSource();

        PagedList.Config config = new PagedList.Config.Builder()
                .setEnablePlaceholders(false)
                .setPageSize(VisitsDataSource.PageSize)
                .build();
        visitsPagedList = (new LivePagedListBuilder(visitsDataSourceFactory, config)).build();
    }
}
