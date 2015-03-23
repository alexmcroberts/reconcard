package com.reconinstruments.reconcards.multi;

import java.io.File;
import java.util.ArrayList;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class ApplicationListViewActivity extends ListActivity {
    private final String TAG = this.getClass().getSimpleName();

    private final Boolean DEBUG = true;

    private final String MULTI_CARD_ROOT_PATH = Environment.getExternalStorageDirectory().getPath() + "/multi_card"; // "/mnt/storage/multi_card"

    public ArrayList<ReconCardApplication> mApps = new ArrayList<ReconCardApplication>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setupListView();
    }

    @Override
    public void onResume() {
        super.onResume();


        Log.v(TAG, "Building the apps...");

        mApps.clear();

        // find sub folders for cards containing the xml files
        File rootDir = new File(MULTI_CARD_ROOT_PATH);

        if (rootDir.exists()) {
            createApps(rootDir);
            if(mApps.size() == 0) {
                Toast.makeText(getApplicationContext(), "No apps found in " + rootDir.getPath(), Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(getApplicationContext(), "No card folder found at " + rootDir.getPath(), Toast.LENGTH_LONG).show();
        }
    }

    /**
     * createApps is a recursive function that traverse through the directory until it finds stack.xml
     *
     * @param rootDir
     */
    public void createApps(File rootDir) {
        File[] fileDirList = rootDir.listFiles();
        if (fileDirList.length < 0) {
            for (File fileDir : fileDirList) {
                if (DEBUG) Log.v(TAG, "Searching: " + fileDir.getAbsolutePath().toString() + "...");
                if (fileDir.isDirectory()) {
                    createApps(fileDir);
                } else if (fileDir.isFile() && fileDir.getName().equalsIgnoreCase("stack.xml")) {
                    ReconCardApplication app = new ReconCardApplication(fileDir.getAbsolutePath());
                    if (app != null) {
                        mApps.add(app);
                        if (DEBUG) Log.v(TAG, "Created a app #" + mApps.size() + ": " + app.name);
                    }
                }
            }
        }
    }

    public void setupListView() {
        // Binding resources Array to ListAdapter
        this.setListAdapter(new ArrayAdapter<ReconCardApplication>(this, R.layout.list_item, R.id.label, mApps));

        ListView activityListView = getListView();

        // listening to single list item on click
        activityListView.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // selected item
                if(DEBUG) Log.v(TAG, "App #" + position + " selected: " + mApps.get(position).name);
                ReconCardApplication app = mApps.get(position);

                // Launching new Activity on selecting single List Item
                Intent i = new Intent(getApplicationContext(), SingleApplicationActivity.class);
                // sending data to new activity
                i.putExtra("app", (Parcelable) app);
                startActivity(i);
            }
        });
    }
}