package com.mydevice.device_stats.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.mydevice.device_stats.FileDetailsElement;
import com.mydevice.device_stats.R;

import java.util.List;

/**
 * Adapter for about file details recycler view.
 */
public class FileDetailsAdapter extends RecyclerView.Adapter<FileDetailsAdapter.FileDetailsViewHolder> {

    /**
     * FileDetailsElement.
     */
    private final List<FileDetailsElement> fileDetailsElementList;

    /**
     * Constructor.
     *
     * @param aboutElementList
     */
    public FileDetailsAdapter(List<FileDetailsElement> aboutElementList) {
        this.fileDetailsElementList = aboutElementList;
    }

    @Override
    public int getItemCount() {
        return fileDetailsElementList.size();
    }

    @Override
    public FileDetailsViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {

        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.file_details_item, viewGroup, false);
        return new FileDetailsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(FileDetailsViewHolder fileDetailsViewHolder, int position) {

        fileDetailsViewHolder.filePropertyTitle.setText(fileDetailsElementList.get(position).getTitle());
        fileDetailsViewHolder.filePropertyTitle.setVisibility(View.VISIBLE);
        fileDetailsViewHolder.filePropertyValue.setText(String.valueOf(fileDetailsElementList.get(position).getvalue()));
    }

    /**
     * File Details View Holder.
     */
    public static class FileDetailsViewHolder extends RecyclerView.ViewHolder {

        /**
         * About the file property title.
         */
        private final TextView filePropertyTitle;

        /**
         * About the file property value.
         */
        private final TextView filePropertyValue;

        /**
         * Constructor.
         *
         * @param itemView
         */
        public FileDetailsViewHolder(View itemView) {
            super(itemView);
            filePropertyTitle = (TextView) itemView.findViewById(R.id.file_property_title);
            filePropertyValue = (TextView) itemView.findViewById(R.id.file_property_value);
        }
    }
}
