package com.tangledbytes.statussaver.adapters;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.tangledbytes.statussaver.R;
import com.tangledbytes.statussaver.utils.ImageStatusUtils;
import com.tangledbytes.statussaver.utils.Utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import de.hdodenhof.circleimageview.CircleImageView;

public class WhatsAppStatusAdapter extends RecyclerView.Adapter<WhatsAppStatusAdapter.ViewHolder> {
	File OUTPUT_DIRECTORY = new File(Environment.getExternalStorageDirectory(), Environment.DIRECTORY_DCIM + "/Status Saver");

    class ViewHolder extends RecyclerView.ViewHolder {
		CircleImageView thumbnail;
		TextView title;
		TextView size;
		TextView durationOrResol;
		TextView uploadTime;
		Button action;
		Button save;
		Button delete;

		public ViewHolder(View view) {
			super(view);
			thumbnail = view.findViewById(R.id.thumbnail);
			title = view.findViewById(R.id.title);
			size = view.findViewById(R.id.size);
			durationOrResol = view.findViewById(R.id.duration_or_resolution);
			uploadTime = view.findViewById(R.id.upload_time);
			action = view.findViewById(R.id.view_or_play);
			save = view.findViewById(R.id.save);
			delete = view.findViewById(R.id.delete);
		}

		public void setUpViews(final WhatsAppStatusAdapter adapter, final int position) {
			final File sourceFile = mDataLoader.statusFiles.get(position);

			thumbnail.setImageBitmap(mDataLoader.thumbnails.get(position));
			title.setText(String.format(mContext.getString(R.string.title), mDataLoader.titles.get(position)));
			size.setText(String.format(mContext.getString(R.string.size), mDataLoader.sizes.get(position)));
			durationOrResol.setText(mDataLoader.videoDurationsAndImageResolutions.get(position));
			uploadTime.setText(String.format(mContext.getString(R.string.upload_time), mDataLoader.uploadTimes.get(position)));
			action.setText(mDataLoader.actionsTitles.get(position));
			action.setOnClickListener(mDataLoader.actions.get(position));
			save.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						if (!OUTPUT_DIRECTORY.exists())
							OUTPUT_DIRECTORY.mkdir();
						File destFile = new File(OUTPUT_DIRECTORY, "status_saver_" + sourceFile.getName());
						if (destFile.exists())
							destFile.delete();
						AlertDialog alert = new AlertDialog.Builder(mContext)
							.setTitle("Saving status")
							.setMessage("Please wait...")
							.create();
							alert.show();
						try {
							// TODO: Make copy available for other devices as well
							if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
								Files.copy(sourceFile.toPath(), destFile.toPath());
							}
							ImageStatusUtils imgUtils = ImageStatusUtils.loadIfImage(destFile);
							if (imgUtils != null)
								addImage(destFile);
							else
								addVideo(destFile);
							mUtils.showToast("✅ Status saved successfully!");
						} catch (IOException e) {
							Log.v(TAG, "Exception occurred while copying statuses", e);
							mUtils.showToast("Could not save status!");
						} finally {
							alert.cancel();
						}
					}

					public Uri addVideo(File videoFile) {
						ContentValues values = new ContentValues(3);
						values.put(MediaStore.Video.Media.TITLE, videoFile.toString());
						values.put(MediaStore.Video.Media.MIME_TYPE, "video/*");
						values.put(MediaStore.Video.Media.DATA, videoFile.getAbsolutePath());
						return mContext.getContentResolver().insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, values);
					}

					public Uri addImage(File imageFile) {
						ContentValues values = new ContentValues(3);
						values.put(MediaStore.Images.Media.TITLE, imageFile.getName());
						values.put(MediaStore.Images.Media.MIME_TYPE, "image/*");
						values.put(MediaStore.Images.Media.DATA, imageFile.getAbsolutePath());
						return mContext.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
					}
				});
			delete.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(final View view) {
						new AlertDialog.Builder(mContext)
							.setTitle("Confirm")
							.setMessage("Deleted video cannot be restored, even from WhatsApp.")
							.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int button) {
									if (sourceFile.exists()) {
										if (sourceFile.delete())
											mUtils.showToast("✅ Successfully deleted!");
										else
											mUtils.showToast("❌ Could not delete!");
									}
									mDataLoader.reloadData(adapter);
								}
							})
							.setNegativeButton("cancel", null)
							.show();
					}
				});
		}
	}

	@Override
	public WhatsAppStatusAdapter.ViewHolder onCreateViewHolder(ViewGroup p1, int p2) {
		return new ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.listview_statuses, null));
	}


	@Override
	public void onBindViewHolder(WhatsAppStatusAdapter.ViewHolder holder, int position) {
		holder.setUpViews(this, position);
	}

	@Override
	public int getItemCount() {
		return mDataLoader.titles.size();
	}

	private static final String TAG= "WhatsAppStatusAdapter";
	private final Context mContext;
	private final Utils mUtils;
	private final DataLoader mDataLoader;

	public WhatsAppStatusAdapter(Context context, DataLoader dataLoader) {
		mContext = context;
		mUtils = new Utils(mContext);
		mDataLoader = dataLoader;
	}
}
