package com.krrtvl.court;

import androidx.lifecycle.MutableLiveData;
import androidx.paging.DataSource;
import androidx.paging.PageKeyedDataSource;

import com.krrtvl.court.domain.Visits;

public class VisitsDataSourceFactory extends DataSource.Factory {

    private MutableLiveData<PageKeyedDataSource<Integer, Visits>> VisitsLiveDataSource = new MutableLiveData<>();

    @Override
    public DataSource create() {
        VisitsDataSource visitsDataSource = new VisitsDataSource();
        VisitsLiveDataSource.postValue(visitsDataSource);
        return visitsDataSource;
    }

    public MutableLiveData<PageKeyedDataSource<Integer, Visits>> getVisitsLiveDataSource() {
        return VisitsLiveDataSource;
    }
}