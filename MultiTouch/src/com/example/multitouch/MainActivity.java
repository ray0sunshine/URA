package com.example.multitouch;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import android.os.Bundle;
import android.os.Environment;
import android.app.Activity;
import android.view.Menu;

public class MainActivity extends Activity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		File root = Environment.getExternalStorageDirectory();
		File file = new File(root, "t1.txt");
		
		try {
			if(!file.exists()){
				file.createNewFile();
			}
			FileWriter fw;
			fw = new FileWriter(file);
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write("Trollololol!");
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
}
