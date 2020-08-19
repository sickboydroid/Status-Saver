package com.gameofcoding.statussaver.adapters;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.v4.content.FileProvider;
import android.view.View;
import android.widget.Toast;
import com.gameofcoding.statussaver.R;
import com.gameofcoding.statussaver.activities.MainActivity;
import com.gameofcoding.statussaver.utils.AppConstants;
import com.gameofcoding.statussaver.utils.ImageStatusUtils;
import com.gameofcoding.statussaver.utils.Utils;
import com.gameofcoding.statussaver.utils.VideoStatusUtils;
import com.gameofcoding.statussaver.utils.XLog;
import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.text.SimpleDateFormat;
import java.text.ParseException;

public class DataLoader {
	private static final String TAG = "DataLoader";
	public List<Bitmap> thumbnails;
	public List<String> titles;
	public List<String> sizes;
	public List<String> uploadTimes;
	public List<View.OnClickListener> actions;
	public List<String> actionsTitles;
	public List<File> statusFiles;
	public List<String> videoDurationsAndImageResolutions;
	public Context mContext;

	private static boolean mIsLoading;
	private static AlertDialog dialog;

	public DataLoader(Context context) {
		mContext = context;	
	}

	public void reloadData(Listener listener) {
		reloadData(null, listener);
	}

	public void reloadData(WhatsAppStatusAdapter adapter) {
		reloadData(adapter, null);
	}

	public void reloadData(final WhatsAppStatusAdapter adapter, final Listener listener) {
		if (isLoading())
			return;
		reinitializeVars();
		ExecutorService exec = Executors.newSingleThreadExecutor();
		exec.submit(new Runnable() {
				@Override
				public void run() {
					try {
						setLoading(null, true);
						loadInBackground();
					} catch (Throwable e) {
						XLog.v(TAG, "Exception occurred loading statuses", e);
					} finally {
						if (listener != null)
							listener.onFinish();
						setLoading(adapter, false);
					}
				}

				private void loadInBackground() {
					File[] files = AppConstants.WHATSAPP_STATUS_DIRECTORY.listFiles();

					// Load all videos
					for (final File file : files) {
						if (file.isHidden() || file.isDirectory())
							continue;
						VideoStatusUtils videoStatusUtils = VideoStatusUtils.loadIfVideo(mContext, file);
						if (videoStatusUtils == null)
							continue;
						try {
							thumbnails.add(videoStatusUtils.generateThumbnail());
							String duration = Long.valueOf(videoStatusUtils.getDuration()) / 1000 + " sec.";
							videoDurationsAndImageResolutions.add(String.format(mContext.getString(R.string.duration),
																				duration));
							videoStatusUtils.close();
						} catch (Throwable e) {
							XLog.v(TAG, "Exception occurred while create thumbnail for video", e);
						}
						actions.add(new View.OnClickListener() {
								@Override
								public void onClick(View view) {
									try {
										Intent intent = new Intent();
										intent.setAction(Intent.ACTION_VIEW);
										Uri videoUri = FileProvider.getUriForFile(mContext, mContext.getPackageName() + ".provider", file);
										intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
										intent.setData(videoUri);
										mContext.startActivity(intent);
									} catch (ActivityNotFoundException anfe) {
										Toast.makeText(mContext, "No activity found to open this attachment.", Toast.LENGTH_LONG).show();
									}
								}
							});
						actionsTitles.add(mContext.getString(R.string.play));
						titles.add(formatTitle(file.getName()));
						sizes.add(Utils.readableFileSize(file.length()));
						String lastModified = new Date(file.lastModified()).toString();
						uploadTimes.add(lastModified.substring(0, lastModified.indexOf("GMT")));
						statusFiles.add(file);
					}

					// Load all images
					for (final File file : files) {
						if (file.isHidden() || file.isDirectory())
							continue;
						ImageStatusUtils imgUtils = ImageStatusUtils.loadIfImage(file);
						if (imgUtils == null)
							continue;
						thumbnails.add(imgUtils.generateThumbnail(120, 120));
						String resolution = imgUtils.getResolution();
						videoDurationsAndImageResolutions.add(String.format(mContext.getString(R.string.resolution),
																			resolution));
						actions.add(new View.OnClickListener() {
								@Override
								public void onClick(View view) {
									try {
										Intent intent = new Intent();
										intent.setAction(Intent.ACTION_VIEW);
										Uri photoURI = FileProvider.getUriForFile(mContext, mContext.getPackageName() + ".provider", file);
										intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
										intent.setData(photoURI);
										mContext.startActivity(intent);
									} catch (ActivityNotFoundException anfe) {
										Toast.makeText(mContext, "No activity found to open this attachment.", Toast.LENGTH_LONG).show();
									}
								}
							});
						actionsTitles.add(mContext.getString(R.string.view));
						titles.add(formatTitle(file.getName()));
						sizes.add(Utils.readableFileSize(file.length()));
						Date lastModified = new Date(file.lastModified());
						uploadTimes.add(lastModified.toString());
						statusFiles.add(file);
					}
				}
			});
	}

	private void reinitializeVars() {
		thumbnails = new ArrayList<>();
		titles = new ArrayList<>();
		sizes = new ArrayList<>();
		uploadTimes = new ArrayList<>();
		actions = new ArrayList<>();
		actionsTitles = new ArrayList<>();
		statusFiles = new ArrayList<>();
		videoDurationsAndImageResolutions = new ArrayList<>();
	}

	private String formatTitle(String title) {
		if (title.length() > 20)
			title = title.substring(0, 9) + ".." + title.substring(title.length() - 9, title.length());
		else if (title.length() < 20) {
			while (title.length() != 20)
				title += " ";
		}
		return title;
	}

	public void setLoading(final WhatsAppStatusAdapter adapter, final boolean loading) {
		mIsLoading = loading;
		if (MainActivity.mSwipeRefreshLyt != null) {
			MainActivity.mSwipeRefreshLyt.post(new Runnable() {
					@Override
					public void run() {
						if(adapter != null)
							adapter.notifyDataSetChanged();
						MainActivity.mSwipeRefreshLyt.setRefreshing(loading);
					}
				});
		}
	}

	public boolean isLoading() {
		return mIsLoading;
	}

	public static interface Listener {
		void onFinish();
	}
}
