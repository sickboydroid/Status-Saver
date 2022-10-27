package com.tangledbytes.statussaver.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.util.Log;

import java.io.File;
public class ImageStatusUtils {
	private static final String TAG= "StatusUtils";
    private final File mSourceFile;
    private final BitmapFactory.Options mOptions;

    private ImageStatusUtils(File sourceFile, BitmapFactory.Options options) {
		mSourceFile = sourceFile;
		mOptions = options;
    }

    public static ImageStatusUtils loadIfImage(File sourceFile) {
		if (sourceFile == null || !sourceFile.exists())
			return null;
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(sourceFile.toString(), options);
		if (options.outWidth > 0 && options.outHeight > 0)
			return new ImageStatusUtils(sourceFile, options);
		return null;
    }

	public String getResolution() {
		return mOptions.outWidth + "x" + mOptions.outHeight;
	}

	private int calculateInSampleSize(int reqWidth, int reqHeight) {
		// Raw height and width of image
		final int height = mOptions.outHeight;
		final int width = mOptions.outWidth;

		int inSampleSize = 1;

		if (height > reqHeight || width > reqWidth) {
			final int halfHeight = height / 2;
			final int halfWidth = width / 2;

			// Calculate the largest inSampleSize value that is a power of 2 and keeps both
			// height and width larger than the requested height and width.
			while ((halfHeight / inSampleSize) >= reqHeight
				   && (halfWidth / inSampleSize) >= reqWidth) {
				inSampleSize *= 2;
			}
		}
		return inSampleSize;
    }

	public Bitmap generateThumbnail(int maxWidth, int maxHeight) {
		mOptions.inSampleSize = calculateInSampleSize(maxWidth, maxHeight);
		mOptions.inJustDecodeBounds = false;
		Bitmap image = BitmapFactory.decodeFile(mSourceFile.toString(), mOptions);
		if (maxHeight > 0 && maxWidth > 0) {
			int width = mOptions.outWidth;
			int height = mOptions.outHeight;
			float ratioBitmap = (float) width / (float) height;
			float ratioMax = (float) maxWidth / (float) maxHeight;

			int finalWidth = maxWidth;
			int finalHeight = maxHeight;
			if (ratioMax > ratioBitmap) {
				finalWidth = (int) ((float)maxHeight * ratioBitmap);
			} else {
				finalHeight = (int) ((float)maxWidth / ratioBitmap);
			}
			image = Bitmap.createScaledBitmap(image, finalWidth, finalHeight, true);
			return image;
		} else {
			return image;
		}
	}
    public Bitmap generateThumbnail2(int maxHeight, int maxWidth) {
		String imagePath = mSourceFile.toString();
		Bitmap mScaledBitmap = null;

		// actual width and height of image
        int actualHeight = mOptions.outHeight;
        int actualWidth = mOptions.outWidth;

		// max Height and width values of the compressed image is taken as 816x612
        float imgRatio = actualWidth / actualHeight;
        float maxRatio = maxWidth / maxHeight;

		// width and height values are set maintaining the aspect ratio of the image
        if (actualHeight > maxHeight || actualWidth > maxWidth) {
            if (imgRatio < maxRatio) {
                imgRatio = maxHeight / actualHeight;
                actualWidth = (int) (imgRatio * actualWidth);
                actualHeight = (int) maxHeight;
            } else if (imgRatio > maxRatio) {
                imgRatio = maxWidth / actualWidth;
                actualHeight = (int) (imgRatio * actualHeight);
                actualWidth = (int) maxWidth;
            } else {
                actualHeight = (int) maxHeight;
                actualWidth = (int) maxWidth;
            }
        }

		if (actualHeight < 1)
			actualHeight = maxHeight;
		if (actualWidth < 1)
			actualWidth = maxWidth;

		// setting inSampleSize value allows to load a scaled down version of the original image
        mOptions.inSampleSize = calculateInSampleSize(actualWidth, actualHeight);

		// inJustDecodeBounds set to false to load the actual bitmap
        mOptions.inJustDecodeBounds = false;
		Bitmap bitmapImage = null;
        try {
			// load the bitmap from its path
            bitmapImage = BitmapFactory.decodeFile(imagePath, mOptions);
			mScaledBitmap = Bitmap.createBitmap(actualWidth, actualHeight, Bitmap.Config.ARGB_8888);
        } catch (OutOfMemoryError e) {
			Log.e(TAG, "Exception occured while loading bitmap in memory", e);
        }

        float ratioX = actualWidth / (float) mOptions.outWidth;
        float ratioY = actualHeight / (float) mOptions.outHeight;
        float middleX = actualWidth / 2.0f;
        float middleY = actualHeight / 2.0f;

        Matrix scaleMatrix = new Matrix();
        scaleMatrix.setScale(ratioX, ratioY, middleX, middleY);

        Canvas canvas = new Canvas(mScaledBitmap);
        canvas.setMatrix(scaleMatrix);
        canvas.drawBitmap(bitmapImage,
						  middleX - bitmapImage.getWidth() / 2,
						  middleY - bitmapImage.getHeight() / 2,
						  new Paint(Paint.FILTER_BITMAP_FLAG));
		mScaledBitmap = Bitmap.createBitmap(mScaledBitmap, 0, 0,
											mScaledBitmap.getWidth(),
											mScaledBitmap.getHeight(),
											null, true);
		return mScaledBitmap;
	}
}
