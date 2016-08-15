package com.mydevice.device_stats.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mydevice.device_stats.FileDetailsElement;
import com.mydevice.device_stats.R;
import com.mydevice.device_stats.adapter.FileDetailsAdapter;

import java.io.Serializable;
import java.util.List;

/**
 * Fragment used to display recycler view with file details selected option.
 */
public class FileDetailsFragment extends Fragment {


    private static final String KEY_FILE_DETAILS_ARGS = "key_file_details_args";
    private static final boolean HAS_FIXED_SIZE = true;

    /**
     * Creates a new instance of this class.
     *
     * @return an instance of FileDetailsFragment
     */
    public static FileDetailsFragment newInstance(List<FileDetailsElement> aboutElementList) {
        FileDetailsFragment fragment = new FileDetailsFragment();
        Bundle args = new Bundle();
        args.putSerializable(KEY_FILE_DETAILS_ARGS, (Serializable) aboutElementList);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View view = inflater.inflate(R.layout.file_details, container, false);

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.file_details_recycler_view);
        recyclerView.setHasFixedSize(HAS_FIXED_SIZE);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this.getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);
        FileDetailsAdapter adapter = new FileDetailsAdapter((List<FileDetailsElement>) getArguments().getSerializable(KEY_FILE_DETAILS_ARGS));
        recyclerView.setAdapter(adapter);

        return view;
    }
}
