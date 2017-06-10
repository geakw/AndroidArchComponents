package com.geakw.arch.components;

import android.app.Fragment;
import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;

/**
 * Created by wlq on 11/6/17.
 */

public class SeekBarFragment extends Fragment {
    private SeekBar mSeekBar;

    private SeekBarViewModel mSeekBarViewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_seekbar, container, false);
        mSeekBar = (SeekBar) root.findViewById(R.id.seekBar);

        mSeekBarViewModel = ViewModelProviders.of((FragmentActivity) getActivity()).get(SeekBarViewModel.class);

        subscribeSeekBar();

        return root;
    }

    private void subscribeSeekBar() {

        // Update the ViewModel when the SeekBar is changed.
        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    mSeekBarViewModel.seekbarValue.setValue(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        // Update the SeekBar when the ViewModel is changed.
        mSeekBarViewModel.seekbarValue.observe(
                (LifecycleOwner) getActivity(),
                new Observer<Integer>() {
                    @Override
                    public void onChanged(@Nullable Integer value) {
                        if (value != null) {
                            mSeekBar.setProgress(value);
                        }
                    }
                });
    }
}
