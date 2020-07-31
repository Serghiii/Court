package com.krrtvl.court;


import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.paging.PagedList;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.krrtvl.court.domain.Visits;


/**
 * A simple {@link Fragment} subclass.
 */
public class GraphFragment extends Fragment {

    public GraphFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_graph, container, false);
        final RecyclerView recyclerView = rootView.findViewById(R.id.rv_visits);
//        recyclerView.addItemDecoration(new DividerItemDecoration(container.getContext(), DividerItemDecoration.VERTICAL));

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setHasFixedSize(true);
        VisitsViewModel newsViewModel = ViewModelProviders.of(getActivity()).get(VisitsViewModel.class);
        final VisitsAdapter visitsAdapter = new VisitsAdapter();
        newsViewModel.visitsPagedList.observe(getActivity(), new Observer<PagedList<Visits>>() {
            @Override
            public void onChanged(@Nullable PagedList<Visits> visits) {
                visitsAdapter.submitList(visits);
            }
        });
        recyclerView.setAdapter(visitsAdapter);

        visitsAdapter.setOnItemClickListener(new VisitsAdapter.OnItemClickListener() {
            @Override
            public void onItemClicked(Long id) {
                Intent intent = new Intent(getContext(), VisitsActivity.class);
                intent.putExtra("id", id);
                getContext().startActivity(intent);
            }
        });

        FloatingActionButton add = rootView.findViewById(R.id.Add);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), VisitsActivity.class);
                getContext().startActivity(intent);
            }
        });

        return rootView;
    }

}
