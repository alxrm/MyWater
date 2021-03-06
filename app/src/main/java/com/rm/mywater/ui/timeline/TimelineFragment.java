package com.rm.mywater.ui.timeline;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.github.brnunes.swipeablerecyclerview.SwipeableRecyclerViewTouchListener;
import com.rm.mywater.R;
import com.rm.mywater.adapter.OnItemClickListener;
import com.rm.mywater.adapter.TimelineAdapter;
import com.rm.mywater.database.DrinkHistoryDatabase;
import com.rm.mywater.database.OnDataRetrievedListener;
import com.rm.mywater.model.Day;
import com.rm.mywater.model.Drink;
import com.rm.mywater.util.base.BaseFragment;

import java.util.ArrayList;
import java.util.Collection;

/**
 * A simple {@link Fragment} subclass.
 */
public class TimelineFragment extends BaseFragment implements
        OnItemClickListener,
        SwipeableRecyclerViewTouchListener.SwipeListener {

    private static final String TAG = "TimelineFragment";

    private Day              mSourceDay;
    private ArrayList<Drink> mDrinks = new ArrayList<>();

    private RelativeLayout mEmptyView;
    private RecyclerView mDrinkList;
    private TimelineAdapter mDrinkListAdapter;
    private LinearLayoutManager mLayoutManager;

    public TimelineFragment() {
        // Required empty public constructor
    }

    public static TimelineFragment newInstance(Day day) {

        TimelineFragment newFragment = new TimelineFragment();

        newFragment.setSourceDay(day);

        return newFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_timeline, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (mToolbar != null) {
            mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    onBackPressed();
                }
            });
        }

        mLayoutManager = new LinearLayoutManager(
                getActivity(),
                LinearLayoutManager.VERTICAL,
                false
        );

        mEmptyView = (RelativeLayout) view.findViewById(R.id.empty_box);
        mDrinkList = (RecyclerView) view.findViewById(R.id.timeline_drinks);
        mDrinkList.setLayoutManager(mLayoutManager);

        SwipeableRecyclerViewTouchListener swipeTouchListener =
                new SwipeableRecyclerViewTouchListener(mDrinkList, this);

        mDrinkList.addOnItemTouchListener(swipeTouchListener);

        DrinkHistoryDatabase.retrieveTimeline(getActivity(), mSourceDay,
                new OnDataRetrievedListener() {

                    @SuppressWarnings("unchecked")
                    @Override
                    public void onDataReceived(Collection<?> data) {

                        Log.d(TAG, "onDataReceived");

                        mDrinks = (ArrayList<Drink>) data;
                        mDrinkListAdapter = new TimelineAdapter(
                                getActivity(),
                                mDrinks,
                                TimelineFragment.this
                        );

                        mDrinkList.setAdapter(mDrinkListAdapter);
                    }

                    @Override
                    public void onError(String err) {

                        Log.w(TAG, "onError: " + err);

                        mDrinkList.setVisibility(View.GONE);
                        mEmptyView.setVisibility(View.VISIBLE);
                    }
                });
    }

    public void setSourceDay(Day sourceDay) {
        mSourceDay = sourceDay;
    }

    @Override
    public void onItemClick(int position) {

    }

    @Override
    public boolean canSwipe(int position) {
        return true;
    }

    @Override
    public void onDismissedBySwipeLeft(RecyclerView recyclerView,
                                       int[] reverseSortedPositions) {

        for (int position : reverseSortedPositions) {

            Log.d(TAG, "SwipeRight: pos: " + position);

            mDrinkListAdapter.removeAt(position);
        }
    }

    @Override
    public void onDismissedBySwipeRight(RecyclerView recyclerView,
                                        int[] reverseSortedPositions) {

        for (int position : reverseSortedPositions) {

            Log.d(TAG, "SwipeLeft: pos: " + position);

            mDrinkListAdapter.removeAt(position);
        }
    }
}
