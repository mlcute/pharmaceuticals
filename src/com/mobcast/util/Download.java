package com.mobcast.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.PowerManager;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.sanofi.in.mobcast.AnnounceDBAdapter;
import com.sanofi.in.mobcast.R;

public class Download extends AsyncTask<String, String, String> {
	private PowerManager.WakeLock mWakeLock;
	ProgressBar mProgressBar;
	private Activity activity;
	String Tablename, _id;
	String link, type, name, ename;
	Cursor c;
	boolean cancel = false;
	private static final String TAG = Download.class.getSimpleName();
	Dialog mDialog;
	int mDownloadedSize = 0;
	int mTotalSize = 0;
	private Button mCancelButton;
	TextView mCurrentDownloadPer;
	TextView mCurrentDownload;

	/**
	 * constructor
	 */

	public Download(Activity activity, String Tablename, String _id) {

		this.activity = activity;
		this.Tablename = Tablename;
		this._id = _id;
	}

	/**
	 * background
	 */

	@Override
	protected void onPreExecute() {
		super.onPreExecute();

		if (activity != null) {
			mDialog = new Dialog(activity, R.style.popupStyle);
			mDialog = new Dialog(activity, R.style.DialogTheme);
			mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
			mDialog.setContentView(R.layout.custom_progress_dialog);
			Window dialogWindow = mDialog.getWindow();
			WindowManager.LayoutParams lp = dialogWindow.getAttributes();

			dialogWindow.setGravity(Gravity.CLIP_VERTICAL);

			dialogWindow.setAttributes(lp);

			mCurrentDownloadPer = (TextView) mDialog
					.findViewById(R.id.downloadProgressPer);
			mCurrentDownload = (TextView) mDialog
					.findViewById(R.id.downloadProgressCurrent);

			mDialog.setCancelable(false);
			
			mDialog.show();

			mProgressBar = (ProgressBar) mDialog
					.findViewById(R.id.downloadProgressBar);
			mProgressBar.setProgress(0);
			Drawable customDrawable = activity.getResources().getDrawable(
					R.drawable.custom_progressbar);// blue progress bar
			mProgressBar.setProgressDrawable(customDrawable);
			mCancelButton = (Button) mDialog
					.findViewById(R.id.downloadProgressCancel);
			mCancelButton.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					mDialog.dismiss();
					showErrorMessage();
				}
			});
			
			try{
				PowerManager pm = (PowerManager) activity.getSystemService(Context.POWER_SERVICE);
		        mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
		             getClass().getName());
		        mWakeLock.acquire();
			}catch(Exception e){
				Log.i(TAG, e.toString());
			}
		}
	}

	@Override
	protected String doInBackground(String... params) {

		try {

			AnnounceDBAdapter adb = new AnnounceDBAdapter(activity);
			adb.open();
			c = adb.getrow(Tablename, _id);
			adb.close();

			if ((Tablename.equals(AnnounceDBAdapter.SQLITE_ANNOUNCE))
					|| (Tablename.equals(AnnounceDBAdapter.SQLITE_NEWS))) {
				name = c.getString((c
						.getColumnIndexOrThrow(AnnounceDBAdapter.KEY_NAME)));
				type = c.getString((c
						.getColumnIndexOrThrow(AnnounceDBAdapter.KEY_TYPE)));
				link = c.getString((c
						.getColumnIndexOrThrow(AnnounceDBAdapter.KEY_FILELINK)));
				System.out.println("Downloading for announcements" + name
						+ "   " + type + "   " + link);
				DownloadFromUrl(activity);
			} else if (Tablename.equals(AnnounceDBAdapter.SQLITE_AWARD))

			{
				name = c.getString((c
						.getColumnIndexOrThrow(AnnounceDBAdapter.KEY_IMAGEPATH)));
				link = c.getString((c
						.getColumnIndexOrThrow(AnnounceDBAdapter.KEY_FILELINK)));
				System.out.println("Downloading for awards" + name + "   "
						+ type + "   " + link);
				type = "award";
				name = name.substring((name.lastIndexOf('/') + 1));
				DownloadFromUrl(activity);

			} else if (Tablename.equals(AnnounceDBAdapter.SQLITE_TRAINING)) {
				name = c.getString((c
						.getColumnIndexOrThrow(AnnounceDBAdapter.KEY_NAME)));
				type = c.getString((c
						.getColumnIndexOrThrow(AnnounceDBAdapter.KEY_TYPE)));
				link = c.getString((c
						.getColumnIndexOrThrow(AnnounceDBAdapter.KEY_FILELINK)));
				ename = c
						.getString((c
								.getColumnIndexOrThrow(AnnounceDBAdapter.KEY_ENCRYPTION)));

				System.out.println("Downloading for Training " + name + "   "
						+ type + "   " + link);
				DownloadFromUrl(activity);
			}

		} catch (Exception e) {
			e.printStackTrace();
			return "false";
		}
		return "true";
	}

	/**
	 * on getting result
	 */
	@Override
	protected void onPostExecute(String result) {
		if (onPostExecuteListener != null) {
			onPostExecuteListener.onPostExecute(result);
		}
		// something...
		try {
			mDialog.dismiss();
			mDialog = null;
			mWakeLock.release();
		} catch (Exception e) {
			// nothing
		}

	}

	OnPostExecuteListener onPostExecuteListener;

	public void setOnPostExecuteListener(
			OnPostExecuteListener onPostExecuteListener) {
		this.onPostExecuteListener = onPostExecuteListener;
	}

	public interface OnPostExecuteListener {
		public abstract void onPostExecute(String result);
	}

	public void DownloadFromUrl(Context ctx) { // this is the downloader method
		try {
			URL url = new URL(link);
			String root = Environment.getExternalStorageDirectory().toString();
			if (type.contentEquals("image")) {
				File myDir = new File(root + Constants.APP_FOLDER_IMG);
				myDir.mkdirs();
				String fname = name;

				File file = new File(myDir, fname);
				if (file.exists()) {
					file.delete();
					deleteFile(fname);
				}
				String url1 = url.toString().replaceAll(" ", "%20");
				url = new URL(url1);
				try {
					URLConnection ucon = url.openConnection();
					InputStream is = ucon.getInputStream();
					FileOutputStream fos = new FileOutputStream(file);
					mTotalSize = ucon.getContentLength();
					byte data[] = new byte[1024];
					int bufferLength = 0;
					mDialog.show();
					while ((bufferLength = is.read(data)) > 0) {
						fos.write(data, 0, bufferLength);
						mDownloadedSize += bufferLength;
						((Activity) ctx).runOnUiThread(new Runnable() {
							public void run() {
								mProgressBar.setMax(mTotalSize);
								mProgressBar.setProgress(mDownloadedSize);
								float per = ((float) mDownloadedSize / mTotalSize) * 100;
								mCurrentDownloadPer.setText((int) per + "%");
								mCurrentDownload
										.setText(formatFileSize(mDownloadedSize)
												+ "/"
												+ formatFileSize(mTotalSize));
							}
						});

					}
					fos.flush();
					fos.close();
					is.close();
					mDialog.dismiss();
				} catch (Exception e) {
					Log.i(TAG, e.toString());
					file.delete();
					deleteFile(file.getName());
					mDialog.dismiss();
					showErrorMessage();
				}
			} else if (type.contentEquals("award")) {
				File myDir = new File(root + Constants.APP_FOLDER_IMG);
				myDir.mkdirs();
				String fname = name;
				File file = new File(myDir, fname);
				if (file.exists()) {
					file.delete();
					deleteFile(fname);
				}
				String url1 = url.toString().replaceAll(" ", "%20");
				url = new URL(url1);
				try {
					URLConnection ucon = url.openConnection();
					InputStream is = ucon.getInputStream();
					FileOutputStream fos = new FileOutputStream(file);
					mTotalSize = ucon.getContentLength();
					byte data[] = new byte[1024];
					int bufferLength = 0;
					while ((bufferLength = is.read(data)) > 0) {
						fos.write(data, 0, bufferLength);
						mDownloadedSize += bufferLength;
						((Activity) ctx).runOnUiThread(new Runnable() {
							public void run() {
								mProgressBar.setMax(mTotalSize);
								mProgressBar.setProgress(mDownloadedSize);
								float per = ((float) mDownloadedSize / mTotalSize) * 100;
								mCurrentDownloadPer.setText((int) per + "%");
								mCurrentDownload
										.setText(formatFileSize(mDownloadedSize)
												+ "/"
												+ formatFileSize(mTotalSize));
							}
						});

					}
					fos.flush();
					fos.close();
					is.close();
					mDialog.dismiss();
				} catch (Exception e) {
					Log.i(TAG, e.toString());
					file.delete();
					mDialog.dismiss();
					deleteFile(file.getName());
					showErrorMessage();
				}
			} else if (type.contentEquals("video")) {

				File myDir = new File(root + Constants.APP_FOLDER_VIDEO);
				myDir.mkdirs();
				String fname = name;
				File file = new File(myDir, fname);
				if (file.exists()) {
					file.delete();
					deleteFile(fname);
				}
				String url1 = url.toString().replaceAll(" ", "%20");
				url = new URL(url1);
				try {
					URLConnection ucon = url.openConnection();
					InputStream is = ucon.getInputStream();
					FileOutputStream fos = new FileOutputStream(file);
					mTotalSize = ucon.getContentLength();
					byte data[] = new byte[1024];
					int bufferLength = 0;
					while ((bufferLength = is.read(data)) > 0) {
						fos.write(data, 0, bufferLength);
						mDownloadedSize += bufferLength;
						((Activity) ctx).runOnUiThread(new Runnable() {
							public void run() {
								mProgressBar.setMax((int) mTotalSize);
								mProgressBar.setProgress(mDownloadedSize);
								float per = ((float) mDownloadedSize / mTotalSize) * 100;
								mCurrentDownloadPer.setText((int) per + "%");
								mCurrentDownload
										.setText(formatFileSize(mDownloadedSize)
												+ "/"
												+ formatFileSize(mTotalSize));
							}
						});

					}
					fos.flush();
					fos.close();
					is.close();
					mDialog.dismiss();
				} catch (Exception e) {
					Log.i(TAG, e.toString());
					file.delete();
					mDialog.dismiss();
					deleteFile(file.getName());
					showErrorMessage();
				}
			} else if (type.contentEquals("audio")) {
				File myDir = new File(Environment.getExternalStorageDirectory()
						.getAbsolutePath() + Constants.APP_FOLDER_AUDIO);
				myDir.mkdirs();
				String fname = name;
				File file = new File(myDir, fname);
				if (file.exists()) {
					file.delete();
					deleteFile(fname);
				}
				String url1 = url.toString().replaceAll(" ", "%20");
				url = new URL(url1);
				try {
					URLConnection ucon = url.openConnection();
					InputStream is = ucon.getInputStream();
					FileOutputStream fos = new FileOutputStream(file);
					mTotalSize = ucon.getContentLength();
					byte data[] = new byte[1024];
					int bufferLength = 0;
					while ((bufferLength = is.read(data)) > 0) {
						fos.write(data, 0, bufferLength);
						mDownloadedSize += bufferLength;
						((Activity) ctx).runOnUiThread(new Runnable() {
							public void run() {
								mProgressBar.setMax((int) mTotalSize);
								mProgressBar.setProgress(mDownloadedSize);
								float per = ((float) mDownloadedSize / mTotalSize) * 100;
								mCurrentDownloadPer.setText((int) per + "%");
								mCurrentDownload
										.setText(formatFileSize(mDownloadedSize)
												+ "/"
												+ formatFileSize(mTotalSize));
							}
						});

					}
					fos.flush();
					fos.close();
					is.close();
					mDialog.dismiss();
				} catch (Exception e) {
					Log.i(TAG, e.toString());
					file.delete();
					mDialog.dismiss();
					deleteFile(file.getName());
					showErrorMessage();
				}
			}// end of audio
			else {
				url = new URL(link);
				root = Environment.getExternalStorageDirectory().toString();
				Log.v("type is ", type.toString());
				String foldername = "";
				if (type.contentEquals("pdf"))
					foldername = activity.getString(R.string.pdf);
				else if (type.contentEquals("ppt"))
					foldername = activity.getString(R.string.ppt);
				else if (type.contentEquals("doc"))
					foldername = activity.getString(R.string.doc);
				else if (type.contentEquals("xls"))
					foldername = activity.getString(R.string.xls);
				else if (type.contentEquals("video"))
					foldername = "mobcast_videos";
				else if (type.contentEquals("audio"))
					foldername = "mobcast_audio";
				File myDir = new File(root + Constants.APP_FOLDER+foldername);
				myDir.mkdirs();
				String fname = ename;
				File file = new File(myDir, fname);

				if (file.exists()) {
					deleteFile(fname);
				}
				String url1 = url.toString().replaceAll(" ", "%20");
				url = new URL(url1);
				try {
					URLConnection ucon = url.openConnection();
					InputStream is = ucon.getInputStream();
					FileOutputStream fos = new FileOutputStream(file);
					if (file.exists()) {
						deleteFile(name);
					}
					mTotalSize = ucon.getContentLength();
					byte data[] = new byte[1024];
					int bufferLength = 0;
					while ((bufferLength = is.read(data)) > 0) {
						fos.write(data, 0, bufferLength);
						mDownloadedSize += bufferLength;
						((Activity) ctx).runOnUiThread(new Runnable() {
							public void run() {
								mProgressBar.setMax(mTotalSize);
								mProgressBar.setProgress(mDownloadedSize);
								float per = ((float) mDownloadedSize / mTotalSize) * 100;
								mCurrentDownloadPer.setText((int) per + "%");
								mCurrentDownload
										.setText(formatFileSize(mDownloadedSize)
												+ "/"
												+ formatFileSize(mTotalSize));
							}
						});

					}
					fos.flush();
					fos.close();
					is.close();
					mDialog.dismiss();
				} catch (Exception e) {
					Log.i(TAG, e.toString());
					file.delete();
					mDialog.dismiss();
					deleteFile(file.getName());
					showErrorMessage();
				}
			}

		} catch (IOException e) {
			Log.d("ImageManager", "Error: " + e);
			Log.d("AudioManager", "Error: " + e);
			Log.d("VideoManager", "Error: " + e);
			Log.d("FileManager", "Error: " + e);
		}

	}

	public void deleteFile(String fname) {
		try {
			File f = new File(Environment.getExternalStorageDirectory()
					.getAbsolutePath() + fname);
			f.delete();
		} catch (Exception e) {
			Log.i(TAG, e.toString());
		}
	}

	@SuppressLint("DefaultLocale")
	public static String formatFileSize(long size) {
		if (size < 1024) {
			return String.format("%d B", size);
		} else if (size < 1024 * 1024) {
			return String.format("%.1f KB", size / 1024.0f);
		} else if (size < 1024 * 1024 * 1024) {
			return String.format("%.1f MB", size / 1024.0f / 1024.0f);
		} else {
			return String.format("%.1f GB", size / 1024.0f / 1024.0f / 1024.0f);
		}
	}

	public void showErrorMessage() {
		try {
			Toast.makeText(
					activity,
					activity.getResources().getString(
							R.string.download_interrupted), Toast.LENGTH_SHORT)
					.show();
		} catch (Exception e) {
			Log.i(TAG, e.toString());
		}

	}
}
