package com.android.konnek2.utils.listeners.simple;

import android.view.View;

import com.android.konnek2.utils.listeners.OnRecycleItemClickListener;


public class SimpleOnRecycleItemClickListener<T> implements OnRecycleItemClickListener<T> {

    @Override
    public void onItemClicked(View view, T entity, int position) {
    }

    @Override
    public void onItemLongClicked(View view, T entity, int position) {
    }
}