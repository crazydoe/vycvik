package com.example.michal.vycvik.Fragments;
import android.app.Fragment;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.Toast;

import com.example.michal.vycvik.API.ApiUtils;
import com.example.michal.vycvik.API.Models.ModelEvent;
import com.example.michal.vycvik.Fragments.Adapters.SignedEventsAdapter;
import com.example.michal.vycvik.HidingScrollListener;
import com.example.michal.vycvik.R;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * Created by michal on 13.06.2017.
 */

public abstract class SignedEventsFragment extends Fragment{
    int userId;
    private View view = null;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private RecyclerView.Adapter mAdapter;
    private EventsListFragment.OnFragmentInteractionListener mListener;
    private Call<List<ModelEvent>> call;


    public SignedEventsFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_signed_events_list, container, false);
        //userId = getArguments().getInt("id");
        userId = 1;


        recyclerView = (RecyclerView) view.findViewById(R.id.signed_events_recycler);
        recyclerView.setHasFixedSize(true);
        recyclerView.setOnScrollListener(new HidingScrollListener() {
            @Override
            public void onHide() {
                hideViews();
            }

            @Override
            public void onShow() {
                showViews();
            }
        });

        layoutManager = new LinearLayoutManager(view.getContext());
        recyclerView.setLayoutManager(layoutManager);

        call = ApiUtils.getApiService().getUserEvents(userId);
        call.enqueue(new Callback<List<ModelEvent>>() {
            @Override
            public void onResponse(Call<List<ModelEvent>> call, Response<List<ModelEvent>> response) {

                mAdapter = new SignedEventsAdapter(response.body(), new SignedEventsAdapter.Callback() {
                    @RequiresApi(api = Build.VERSION_CODES.M)
                    @Override
                    public void onItemClick(String text, int position) {
                        startPostActivity(position);

                    }
                });

                recyclerView.setAdapter(mAdapter);
                Toast.makeText(getContext(), userId + " ", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(Call<List<ModelEvent>> call, Throwable t) {
                Toast.makeText(getContext(), "Błąd połączenia "+ t.getMessage(), Toast.LENGTH_SHORT ).show();
            }
        });

        return view;
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    private void hideViews() {
        Toolbar mToolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        mToolbar.animate().translationY(-mToolbar.getHeight()).setInterpolator(new AccelerateInterpolator(2));
    }

    private void showViews() {
        Toolbar mToolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        mToolbar.animate().translationY(0).setInterpolator(new DecelerateInterpolator(2));
    }

    public abstract void startPostActivity(int id);
}
