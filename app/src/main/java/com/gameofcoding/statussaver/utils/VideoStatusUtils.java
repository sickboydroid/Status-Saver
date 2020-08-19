package com.gameofcoding.statussaver.utils;
import android.content.Context;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import java.io.File;
import java.net.URLConnection;

public class VideoStatusUtils {
	private static String TAG = "VideoStatusUtils";
	MediaMetadataRetriever mRetriever;

	public VideoStatusUtils(MediaMetadataRetriever retriever) {
		mRetriever = retriever;
	}

	public static VideoStatusUtils loadIfVideo(Context context, File file) {
		try {
			String mimeType = URLConnection.guessContentTypeFromName(file.toString());
			if (mimeType != null && mimeType.startsWith("video")) {
				MediaMetadataRetriever retriever = new MediaMetadataRetriever();
				retriever.setDataSource(context, Uri.fromFile(file));
				return new VideoStatusUtils(retriever);
			}
		} catch (Throwable e) {
			XLog.v(TAG, "Exception occurred while checking for video", e);
		}
		return null;
	}

    public Bitmap generateThumbnail() throws Throwable {
        Bitmap bitmap = null;
		bitmap = mRetriever.getFrameAtTime();
        return bitmap;
    }

	public String getDuration() throws Throwable {
		return mRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
	}

	public void close() throws Throwable {
		if (mRetriever != null) {
			mRetriever.release();
		}
	}
}
