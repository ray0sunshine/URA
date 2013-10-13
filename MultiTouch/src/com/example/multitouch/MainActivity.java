package com.example.multitouch;

import java.io.IOException;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;

import com.dropbox.sync.android.DbxAccountManager;
import com.dropbox.sync.android.DbxException;
import com.dropbox.sync.android.DbxException.Unauthorized;
import com.dropbox.sync.android.DbxFile;
import com.dropbox.sync.android.DbxFileSystem;
import com.dropbox.sync.android.DbxPath;
import com.dropbox.sync.android.DbxPath.InvalidPathException;

public class MainActivity extends Activity {
	
	private static final String appKey = "d1ttu0y9xq09760";
	private static final String appSecret = "32zax4zjtdjpooh";
	
	private static final int REQUEST_LINK_TO_DBX = 0;
	
	private DbxAccountManager dbxAccMgr;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		dbxAccMgr = DbxAccountManager.getInstance(getApplicationContext(), appKey, appSecret);
		dbxAccMgr.startLink((Activity)this, REQUEST_LINK_TO_DBX);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
	    if (requestCode == REQUEST_LINK_TO_DBX) {
	    	Log.d("yolo", "1");
	        if (resultCode == Activity.RESULT_OK) {
	        	DbxFileSystem dbxFs = null;
				try {
					dbxFs = DbxFileSystem.forAccount(dbxAccMgr.getLinkedAccount());
				} catch (Unauthorized e1) {
					e1.printStackTrace();
				}
				
	        	DbxFile record = null;
				try {
					record = dbxFs.create(new DbxPath("t1.txt"));
				} catch (InvalidPathException e1) {
					e1.printStackTrace();
				} catch (DbxException e1) {
					e1.printStackTrace();
				}
				
				try{
					record.writeString("Trollololol!");
				} catch (IOException e) {
					e.printStackTrace();
				} finally {
					record.close();
				}
	        }
	    } else {
	        super.onActivityResult(requestCode, resultCode, data);
	    }
	}
}
