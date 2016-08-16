package com.mydevice.device_stats.fragments;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.util.Log;

import com.mydevice.device_stats.Constants;
import com.mydevice.device_stats.DirectoryUIHelper;
import com.mydevice.device_stats.FileDetailsElement;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class TasksFragment extends Fragment {

    /**
     * Callback interface through which the fragment will report the
     * task's progress and results back to the Activity.
     */
    public interface TasksCallbacks {

        void onPreExecute();

        void onCancelled();

        void onPostExecute(DirectoryUIHelper file);

        void onProgressUpdate(Integer... progress);
    }

    private final String TAG = TasksFragment.class.getSimpleName();
    private scanFilesAsyncTask myAsyncTask;
    private DirectoryUIHelper mDirectoryObject;
    private HashMap<String, Integer> mExtFrequencyMap;
    private int mProgressCount = 0;
    private TasksCallbacks mTaskCallBack;
    private List<FileDetailsElement> mFileDetailsElementsList;


    public TasksFragment() {
        mFileDetailsElementsList = new ArrayList<>();
        mExtFrequencyMap = new HashMap<>();
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        Log.d(TAG, "onAttach");

        if (context instanceof TasksCallbacks) {
            mTaskCallBack = (TasksCallbacks) context;
        } else {
            // If activity trying to access this fragment without implementing TasksCallbacks, throw an exception
            throw new IllegalStateException("Parent activity must implement TasksCallbacks");
        }
    }

    /**
     * This method will only be called once when the retained
     * Fragment is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Retain this fragment across configuration changes.
        setRetainInstance(true);

        Log.d(TAG, "inside ---on Create");
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // If the AsyncTask finished when we didn't have a listener we can
        // deliver the result here
        if ((mDirectoryObject != null) && (mTaskCallBack != null)) {
            mTaskCallBack.onPostExecute(mDirectoryObject);
            mDirectoryObject = null;
        }
    }

    /**
     * Set the callback to null so we don't accidentally leak the
     * Activity instance.
     */
    @Override
    public void onDetach() {
        super.onDetach();
        mTaskCallBack = null;
        Log.d(TAG, "on Detach");

    }

    /**
     * used to launch async task on button click.
     */
    public void launchTask() {
        Log.d(TAG, "inside ---launchTask");

        File sdcard;
        String sdCardState = Environment.getExternalStorageState();

        if (!sdCardState.equals(Environment.MEDIA_MOUNTED)) {
            return;
        } else {
            sdcard = Environment.getExternalStorageDirectory();
        }
        //If start scan button click should make call only once, then remove scanFilesAsyncTask.getStatus() check.
        if ((myAsyncTask == null || myAsyncTask.getStatus() != AsyncTask.Status.PENDING || myAsyncTask.getStatus() != AsyncTask.Status.RUNNING)) {
            myAsyncTask = new scanFilesAsyncTask();
            myAsyncTask.execute(sdcard);
        }
    }

    /**
     * used to cancel async task.
     */
    public void cancelTask() {
        if ((myAsyncTask != null)) {
            myAsyncTask.cancel(true);
        }
    }

    /**
     * async task used to process file info and update UI. UI is updated only if reference of task listener is not null.
     * Else directory ui helper object is updated and UI is updated on re attach of the activity.
     */
    private class scanFilesAsyncTask extends AsyncTask<File, Integer, DirectoryUIHelper> {

        int percentDone = 1;
        int latestPercentDone;

        protected void onPreExecute() {
            if (mTaskCallBack != null) {
                mTaskCallBack.onPreExecute();
            }
        }

        private void updateProgress(int value) {
            //Not a very correct way to calculate progress without knowing total size. Using approximate
            latestPercentDone = value > 300 ? value / 20 : value / 5;

            if ((latestPercentDone - percentDone) > 10) {
                percentDone = latestPercentDone;
                publishProgress(percentDone);
            }
        }

        protected DirectoryUIHelper doInBackground(File... dir) {

            DirectoryUIHelper directoryUIHelper = new DirectoryUIHelper();
            List<FileDetailsElement> fileDetailsElement = null;
            try {
                Log.d(TAG, "Do in backg");
                mFileDetailsElementsList.clear();
                mExtFrequencyMap.clear();
                mProgressCount = 0;

                fileDetailsElement = getListFiles(dir[0]);

                publishProgress(95);
                directoryUIHelper.setCompleteFileSizeList(fileDetailsElement);
                directoryUIHelper.setUnsortedExtensionFrequencyMap(mExtFrequencyMap);

                directoryUIHelper.evaluateTopBiggestFiles(Constants.NUMBER_OF_FILES);
                directoryUIHelper.evaluateSortedExtensionFrequencyList(Constants.NUMBER_OF_FILES);
                directoryUIHelper.evaluateAverageFileSize();

                publishProgress(100);
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.getMessage();
            }
            return directoryUIHelper;
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            if (mTaskCallBack != null) {
                mTaskCallBack.onProgressUpdate(progress[0]);
            }
        }

        protected void onPostExecute(DirectoryUIHelper result) {
            if (mTaskCallBack != null) {
                mTaskCallBack.onPostExecute(result);
            } else {
                mDirectoryObject = result;
            }
        }
    }

    /**
     * Recursive method to read files in directory and add it in the List
     *
     * @param parentDir
     *
     * @return List<FileDetailsElement>
     */
    private List<FileDetailsElement> getListFiles(File parentDir) {

        Log.d(TAG, "parent dir>> " + parentDir);

        File[] files = new File[0];
        if (parentDir.exists() && parentDir.isDirectory()) {
            files = parentDir.listFiles();
        }

        if (files == null) {
            Log.d(TAG, "no file");
            return mFileDetailsElementsList;
        }
        for (File file : files) {
            if (file.isDirectory()) {
                getListFiles(file);
            } else {
                FileDetailsElement fileModel = new FileDetailsElement();
                fileModel.setTitle(file.getName());
                fileModel.setValue(file.length());

                mFileDetailsElementsList.add(fileModel);
                updateExtensionFrequencyMap(file.getName());

                myAsyncTask.updateProgress(this.mProgressCount++);

                Log.d(TAG, file.getName() + " file absolute path: " + file.getAbsolutePath() + " file length: " + file.length());
            }
        }
        return mFileDetailsElementsList;
    }

    /**
     * Used to update extension frequency unsorted map
     *
     * @param fileName
     */
    public void updateExtensionFrequencyMap(String fileName) {

        int indexOfPerion = fileName.lastIndexOf(".");
        String fileExtension = fileName.substring(indexOfPerion + 1);
        int count = 1;
        if (mExtFrequencyMap.containsKey(fileExtension)) {
            count = count + mExtFrequencyMap.get(fileExtension);
        }
        mExtFrequencyMap.put(fileExtension, count);
    }
}

