package com.edw.picslide;

import java.io.File;
import java.io.FileFilter;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.text.InputType;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

public class PictureSliderActivity extends Activity 
	implements OnItemClickListener{
	
	com.baidu.mobads.AdView baiduAdView;
	com.google.ads.AdView googleAdView;
	
	TextView tvFolderPath;
	EditText etInterval;
	ListView lvFolderList;
	
	File[] folders = null;
	File curFolder = null;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        
        setContentView(R.layout.main);
        
        tvFolderPath = (TextView)findViewById(R.id.tvFolderPath);
        etInterval = (EditText)findViewById(R.id.etInterval);
        lvFolderList = (ListView)findViewById(R.id.lvFolderList);

        etInterval.setInputType(InputType.TYPE_CLASS_NUMBER);
        
        Config.path = Environment.getExternalStorageDirectory().getAbsolutePath();
        Log.d("sdcard", Config.path);
        
        lvFolderList.setAdapter(new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_single_choice, 
                listFolders(Config.path)));
        lvFolderList.setOnItemClickListener(this);
        
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
       
        // screen resolution
        Config.SCR_W = dm.widthPixels;
        Config.SCR_H = dm.heightPixels;
        Log.d("SCR_W, H", Config.SCR_W +" , " + Config.SCR_H);
        
      baiduAdView = (com.baidu.mobads.AdView) this.findViewById(R.id.baiduAdView);
      baiduAdView.setVisibility(com.baidu.mobads.AdView.VISIBLE);
      
      googleAdView = (com.google.ads.AdView)this.findViewById(R.id.googleAdView);
      googleAdView.loadAd(new com.google.ads.AdRequest());
    }
    
    public String[] listFolders(String path){
    	curFolder = new File(path);
    	folders = curFolder.listFiles(new FileFilter() {
    	    public boolean accept(File file) {
    	    	if(file.isDirectory())
    	    		return true;
    	    	String filename = file.getName();
    	    	for(int i=0; i<Config.TYPES.length; i++){
					if(filename.toLowerCase().endsWith(Config.TYPES[i]))
						return true;
				}
    	    	return false;
    	    }
    	});
    	String[] list = new String[folders.length];
    	for (int i=0; i<list.length; i++){
    		list[i] = folders[i].getName();
    	}
    	return list;
    }
    
    public void start_onclick(View view){
    	Config.isRandom = false;
try{    Config.interval = Integer.parseInt(etInterval.getText().toString()); }
catch(Exception ex){ Config.interval = 3; }
		if(Config.interval < 1) Config.interval = 1;
    	Intent intent = new Intent(this, SliderActivity.class);
    	startActivity(intent);
    }
    
    public void random_onclick(View view){
    	Config.isRandom = true;
try{    Config.interval = Integer.parseInt(etInterval.getText().toString()); }
catch(Exception ex){ Config.interval = 3; }
		if(Config.interval < 1) Config.interval = 1;
    	Intent intent = new Intent(this, SliderActivity.class);
    	startActivity(intent);
    }


	public void onItemClick(AdapterView<?> arg0, View arg1, int pos, long id) {
		if(!folders[pos].isDirectory()) {
			Config.path = folders[pos].getAbsolutePath();
			Intent intent = new Intent(this, SliderActivity.class);
	    	startActivity(intent);
			return;
		}
		Config.path = folders[pos].getAbsolutePath();
		lvFolderList.setAdapter(new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_single_choice, 
                listFolders(Config.path)));
		
		Log.d("sdcard", Config.path);
		tvFolderPath.setText(Config.path);
	}
	
	@Override
	public void onBackPressed() {
		if(Config.path.compareTo(Environment.getExternalStorageDirectory()
				.getAbsolutePath()) != 0){
			Config.path = curFolder.getParent();
			lvFolderList.setAdapter(new ArrayAdapter<String>(this,
	                android.R.layout.simple_list_item_single_choice, 
	                listFolders(Config.path)));
			
			Log.d("sdcard", Config.path);
			tvFolderPath.setText(Config.path);
			return;
		}
		super.onBackPressed();
	}
}