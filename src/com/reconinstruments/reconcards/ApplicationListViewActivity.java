package com.reconinstruments.reconcards;
 
import java.io.File;
import java.util.ArrayList;
import com.reconinstruments.reconcards.multi.R;
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
import com.reconinstruments.reconcards.ReconCardApplication;
import android.widget.Toast;
 
public class ApplicationListViewActivity extends ListActivity {
	public static final String TAG = "AndroidListViewActivity";
	
	public ArrayList<String> applications = new ArrayList<String>();
	public ArrayList<ReconCardApplication> apps = new ArrayList<ReconCardApplication>();
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        Log.v(TAG, "building the apps");
        
        // find sub folders for cards containing the xml files
        buildApplications();

        setupListView();
    }
    
    public void buildApplications() {
    	//String filepath = "/mnt/storage/card";
    	String filepath = Environment.getExternalStorageDirectory().getPath() + "/card";
		File root = new File(filepath);

		if ( root.exists() ) {
			File[] files = root.listFiles();
			if ( 0 < files.length ) {
				for (File file : files) {
					Log.v(TAG, file.getAbsolutePath().toString());
				    if (file.isDirectory()) {
						buildApplication(file);
				    }
				}
			}
			else {
				Toast.makeText(getApplicationContext(),
						"no apps found in " + filepath, Toast.LENGTH_LONG).show();
			}
		}
		else {
			Toast.makeText(getApplicationContext(), "no card folder found at " + filepath, Toast.LENGTH_LONG).show();
		}
	}
    
    public void buildApplication(File appFolder) {
    	Log.v(TAG, "building app: " + appFolder.getAbsolutePath().toString() );
    	// Check if there is a stack.xml inside each directory and add it to xmlFiles ArrayList<Document>
    	File[] appFiles = appFolder.listFiles();
    	if ( 0 < appFiles.length ) {
    		for (File appFile : appFiles) {
    			if ( appFile.getName().equalsIgnoreCase("stack.xml") ) {
    				ReconCardApplication app = new ReconCardApplication( appFile.getAbsolutePath() );
					if ( null != app ) {
						apps.add( app );
						Log.v( TAG, app.name );
					}
    			}
    		}
    	}
    }
    
    public void setupListView() {
        // Binding resources Array to ListAdapter
        this.setListAdapter( new ArrayAdapter<ReconCardApplication>(this, R.layout.list_item, R.id.label, apps) );
        
        ListView lv = getListView();
 
        // listening to single list item on click
        lv.setOnItemClickListener(new OnItemClickListener() {
          public void onItemClick(AdapterView<?> parent, View view,
              int position, long id) {
 
              // selected item
        	  Log.v(TAG, "APP SELECTED " +apps.get(position).name);
        	  ReconCardApplication _app = apps.get(position);
 
              // Launching new Activity on selecting single List Item
              Intent i = new Intent(getApplicationContext(), SingleApplicationActivity.class);
              // sending data to new activity
              i.putExtra("app", (Parcelable) _app);
              startActivity(i);
          }
        });
    }
}