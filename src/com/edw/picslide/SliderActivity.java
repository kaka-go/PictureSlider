package com.edw.picslide;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Random;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

public class SliderActivity extends Activity implements OnTouchListener{
    
	ImageView ivSlide;
	String[] imgFiles = null;
	Bitmap curBmp = null;
	int curId = 0;
	int totalNum = 1;
	Handler slideHandler = new Handler();
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        
        setContentView(R.layout.slide);
        
        ivSlide = (ImageView)findViewById(R.id.ivSlide);
        ivSlide.setOnTouchListener(this);
        
        File file = new File(Config.path);
        if(!file.isDirectory()){
        	Config.path = file.getParent();
        	imgFiles =listImages(Config.path);
        	totalNum = imgFiles.length;
        	String name = file.getName();
        	for(int i=0;i<totalNum;i++){
        		if(imgFiles[i].compareTo(name)==0)
        			curId = i;
        	}
        	showImage(imgFiles[curId]);
        	Log.d(Config.path, curId + " - " + imgFiles.length + file.getName());
        	return;
        }
        
        imgFiles = listImages(Config.path);
        Log.d("image num", "" + imgFiles.length);
        msg(Config.path + "\nimage num " + imgFiles.length + "\ninterval: " + Config.interval);
        
        if(imgFiles == null || imgFiles.length == 0){
        	finish();
        }
        else{
        	totalNum = imgFiles.length;
        	sildeView(imgFiles[curId]);
        }
    }
    
    public String[] listImages(String path){
    	File curFolder = new File(path);
    	String[] images = curFolder.list(new FilenameFilter(){
			public boolean accept(File dir, String filename) {
				if(filename.endsWith(".jpg")
				|| filename.endsWith(".png")
				|| filename.endsWith(".bmp")){
					return true;
				}
				return false;
			}
    	});
    	return images;
    }
    
    public void msg(String str){
    	Toast.makeText(this, str, Toast.LENGTH_LONG).show();
    }
    
    public void showImage(String name){
    	if(curBmp != null){
    		curBmp = null;
    	}
    	curBmp = ImageProc.readBitmap(Config.path + "/" + name, Config.SCR_W, Config.SCR_H);
    	ivSlide.setImageBitmap(curBmp);
    }
    
    public void sildeView(String name){
    	
    	showImage(name);
    	
    	if(slideHandler != null){
	    	slideHandler.postDelayed(new Runnable(){
				public void run() {
					curId ++;
					curId %= totalNum;
					
					if(Config.isRandom)
						curId = new Random().nextInt(totalNum);
					
					try{
						sildeView(imgFiles[curId]);
					}catch(Exception ex){}
				}
	    	}, Config.interval * 1000);
    	}
    	
    }
    
    @Override
	public void onBackPressed() {
    	slideHandler = null;
		super.onBackPressed();
	}
    
    
    

	public boolean onTouch(View arg0, MotionEvent event) {
		switch(event.getAction()){
	        case MotionEvent.ACTION_DOWN:
//	        	Log.d("Touch", event.getX() + ", " + event.getY());
	        	if(event.getY() > Config.SCR_H/3 * 2){
	        		curId ++;
					curId %= totalNum;
					showImage(imgFiles[curId]);
					return true;
	        	}
	        	else if(event.getY() < Config.SCR_H/3){
	        		curId --;
					if(curId<0) curId = totalNum - 1;
					showImage(imgFiles[curId]);
					return true;
	        	}
	        	else{
	        		curId = new Random().nextInt(totalNum);
	        		showImage(imgFiles[curId]);
	        	}
                break;
	        case MotionEvent.ACTION_MOVE:
                break;
	        case MotionEvent.ACTION_UP:
	        	break;
		}
		return false;
	}

}