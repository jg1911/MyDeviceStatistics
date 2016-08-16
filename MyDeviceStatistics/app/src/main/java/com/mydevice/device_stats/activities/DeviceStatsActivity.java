package com.mydevice.device_stats.activities;

import android.Manifest;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.TextView;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationCompat.Builder;

import com.mydevice.device_stats.DirectoryUIHelper;
import com.mydevice.device_stats.R;
import com.mydevice.device_stats.fragments.FileDetailsFragment;
import com.mydevice.device_stats.fragments.TasksFragment;

import java.io.Serializable;

public class DeviceStatsActivity extends FragmentActivity implements TasksFragment.TasksCallbacks, View.OnClickListener {

    private final int RESULT_CODE = 1;

    private TasksFragment mTaskFragment;
    private static final String TAG_TASK_FRAGMENT = "task_fragment";
    private static final String TAG_FILE_DETAIL_SIZE_FRAGMENT = "file_detail_fragment";
    private static final String TAG_FILE_EXTENSION_DETAIL_FRAGMENT = "file_extension_detail_fragment";

    private final String TAG = DeviceStatsActivity.class.getSimpleName();
    private TextView fileSizeHeaderTextView;
    private TextView fileExtHeaderTextView;
    private TextView averageFileSizeTextView;
    private RadioButton startScanButton;
    private RadioButton stopScanButton;
    private ProgressBar progressBar;
    private ProgressBar secondaryProgressBar;
    private Button shareButton;
    private DirectoryUIHelper directoryUIHelper;
    private LinearLayout parentLayout;
    private FrameLayout frameLayout;

    private FragmentManager fragmentManager;
    private NotificationManager mNotifyManager;

    private Builder mBuilder;
    private int id = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_stats);

        parentLayout = (LinearLayout) findViewById(R.id.parent_layout);
        frameLayout = (FrameLayout) findViewById(R.id.fragment_container);
        fileSizeHeaderTextView = (TextView) findViewById(R.id.fileSizeHeader);
        averageFileSizeTextView = (TextView) findViewById(R.id.averageFileSize);
        fileExtHeaderTextView = (TextView) findViewById(R.id.fileExtensionHeader);

        startScanButton = (RadioButton) findViewById(R.id.start_scan_button);
        stopScanButton = (RadioButton) findViewById(R.id.stop_scan_button);
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        secondaryProgressBar = (ProgressBar) findViewById(R.id.secondary_progress_bar);
        shareButton = (Button) findViewById(R.id.share_button);

        fragmentManager = getSupportFragmentManager(); ;
        mTaskFragment = (TasksFragment) fragmentManager.findFragmentByTag(TAG_TASK_FRAGMENT);

        // If the Fragment is non-null, then it is currently being
        // retained across a configuration change.
        if (mTaskFragment == null) {
            mTaskFragment = new TasksFragment();
            fragmentManager.beginTransaction().add(mTaskFragment, TAG_TASK_FRAGMENT).commit();
        }

        statusBarNotification();
        startScanButton.setOnClickListener(this);
        stopScanButton.setOnClickListener(this);
        shareButton.setOnClickListener(this);
        fileSizeHeaderTextView.setOnClickListener(this);
        fileExtHeaderTextView.setOnClickListener(this);
    }

    /**
     * Check if device has required permission to read external storage before starting scan.
     * Applicable for marshmallow and above.
     *
     * @return boolean
     */
    private boolean isExternalStoragePermissionGranted() {

        if (Build.VERSION.SDK_INT >= 23 &&
                checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            return false;
        }
        return true;
    }

    /**
     * Once permission recived, start scanning directory.
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == RESULT_CODE && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            //resume tasks needing this permission
            //     statusBarNotification();
            mTaskFragment.launchTask();
        }
    }

    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.start_scan_button:

                fileSizeHeaderTextView.setVisibility(View.GONE);
                averageFileSizeTextView.setVisibility(View.GONE);
                fileExtHeaderTextView.setVisibility(View.GONE);
                shareButton.setVisibility(View.GONE);
                frameLayout.setVisibility(View.GONE);

                startScanButton.setChecked(true);

                // If read external storage permission is not granted on Marshmallow and above devices, request permission
                if (!isExternalStoragePermissionGranted())
                    ActivityCompat.requestPermissions(DeviceStatsActivity.this, new String[] {Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
                else {
                    //    statusBarNotification();
                    mTaskFragment.launchTask();
                }
                break;

            case R.id.stop_scan_button:

                progressBar.setVisibility(View.GONE);
                secondaryProgressBar.setVisibility(View.GONE);
                stopScanButton.setChecked(true);

                mTaskFragment.cancelTask();
                break;

            case R.id.share_button:

                String dataToShare = directoryUIHelper.getSharableData();
                Intent intent = new Intent(); intent.setAction(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.share_subject));
                intent.putExtra(Intent.EXTRA_TEXT, dataToShare);
                startActivity(Intent.createChooser(intent, getString(R.string.share_through)));
                break;

            case R.id.fileSizeHeader:

                Fragment fileSizeFragment = FileDetailsFragment.newInstance(directoryUIHelper.getSortedSmallFileSizeList());
                frameLayout.setVisibility(View.VISIBLE);
                fragmentManager.beginTransaction().replace(R.id.fragment_container, fileSizeFragment, TAG_FILE_DETAIL_SIZE_FRAGMENT).commit();
                break;

            case R.id.fileExtensionHeader:

                Fragment fileExtensionFragment = FileDetailsFragment.newInstance(directoryUIHelper.getSortedExtensionFrequencyList());
                frameLayout.setVisibility(View.VISIBLE);
                fragmentManager.beginTransaction()
                               .replace(R.id.fragment_container, fileExtensionFragment, TAG_FILE_EXTENSION_DETAIL_FRAGMENT)
                               .commit();
                break;

            default:
                break;

        }
    }

    private void statusBarNotification() {
        mNotifyManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mBuilder = new NotificationCompat.Builder(DeviceStatsActivity.this);
        mBuilder.setContentTitle("File Scanner")
                .setContentText("File scanning in progress")
                .setAutoCancel(true)
                .setSmallIcon(R.drawable.ic_launcher);
    }

    @Override
    public void onPreExecute() {

        fileSizeHeaderTextView.setVisibility(View.GONE);
        averageFileSizeTextView.setVisibility(View.GONE);
        fileExtHeaderTextView.setVisibility(View.GONE);
        shareButton.setVisibility(View.GONE);

        progressBar.setVisibility(View.VISIBLE);
        progressBar.setProgress(0);
        secondaryProgressBar.setVisibility(View.VISIBLE);

        // Displays the progress bar for the first time.
        mBuilder.setProgress(100, 0, false);
        mNotifyManager.notify(id, mBuilder.build());
    }

    @Override
    public void onCancelled() {

    }

    @Override
    public void onPostExecute(DirectoryUIHelper directoryUIHelper) {
        this.directoryUIHelper = directoryUIHelper;

        progressBar.setVisibility(View.GONE);
        secondaryProgressBar.setVisibility(View.GONE);
        fileSizeHeaderTextView.setVisibility(View.VISIBLE);
        averageFileSizeTextView.setVisibility(View.VISIBLE);
        fileExtHeaderTextView.setVisibility(View.VISIBLE);
        shareButton.setVisibility(View.VISIBLE);

        averageFileSizeTextView.setText(getString(R.string.average_file_size, String.valueOf(directoryUIHelper.getAverageFileSize())));

        mBuilder.setProgress(0, 0, false);
        mBuilder.setContentText("File scan completed");
        mNotifyManager.notify(id, mBuilder.build());
    }

    @Override
    public void onProgressUpdate(Integer... progress) {
        // Update progress
        progressBar.setProgress(progress[0]);
        mBuilder.setProgress(100, progress[0], false);
        mNotifyManager.notify(id, mBuilder.build());
    }
}