package com.edw.picslide;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.BitmapFactory.Options;
import android.util.Log;

public class ImageProc {
	
	public static Bitmap readBitmap(String pathname, int screenWidth, int screenHeight){
    	Bitmap bmp = null;
    	boolean isRotate = false;
    	
    	Options options = new Options();
    	options.inJustDecodeBounds = true;
    	BitmapFactory.decodeFile(pathname, options);    	
//    	Log.d(pathname, options.outWidth + "x" + options.outHeight);
    	
    	float scale = (float)options.outHeight/options.outWidth;
    	int width = screenWidth;
		int height = (int) (screenWidth * scale);
		
    	if(options.outWidth > options.outHeight){
    		isRotate = true;
    		width = screenHeight;
    		height = (int) (screenHeight * scale);
    	}

    	final int minSideLength = Math.min(width, height);
        options.inSampleSize = computeSampleSize(options, minSideLength,
                width * height);
    	
        options.inJustDecodeBounds = false;
        options.inInputShareable = true;
        options.inPurgeable = true;
    	
    	bmp = BitmapFactory.decodeFile(pathname, options);
    	
    	if(isRotate){
    		Matrix matrix = new Matrix();
    		matrix.postRotate(90);
    		bmp = Bitmap.createBitmap(bmp, 0, 0,
    		        bmp.getWidth(), bmp.getHeight(), matrix, true);
    	}
    	if(bmp != null)
    		Log.d(pathname, bmp.getWidth() + "x" + bmp.getHeight());
    	return bmp;
    }
    
    public static int computeSampleSize(BitmapFactory.Options options,
            int minSideLength, int maxNumOfPixels) {
        int initialSize = computeInitialSampleSize(options, minSideLength,
                maxNumOfPixels);

        int roundedSize;
        if (initialSize <= 8) {
            roundedSize = 1;
            while (roundedSize < initialSize) {
                roundedSize <<= 1;
            }
        } else {
            roundedSize = (initialSize + 7) / 8 * 8;
        }

        return roundedSize;
    }

    private static int computeInitialSampleSize(BitmapFactory.Options options,
            int minSideLength, int maxNumOfPixels) {
        double w = options.outWidth;
        double h = options.outHeight;

        int lowerBound = (maxNumOfPixels == -1) ? 1 : (int) Math.ceil(Math
                .sqrt(w * h / maxNumOfPixels));
        int upperBound = (minSideLength == -1) ? 128 : (int) Math.min(Math
                .floor(w / minSideLength), Math.floor(h / minSideLength));

        if (upperBound < lowerBound) {
            // return the larger one when there is no overlapping zone.
            return lowerBound;
        }

        if ((maxNumOfPixels == -1) && (minSideLength == -1)) {
            return 1;
        } else if (minSideLength == -1) {
            return lowerBound;
        } else {
            return upperBound;
        }
    }
    
}
