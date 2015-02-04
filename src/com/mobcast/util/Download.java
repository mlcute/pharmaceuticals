package com.mobcast.util;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import org.apache.http.util.ByteArrayBuffer;

import com.sanofi.in.mobcast.AnnounceDBAdapter;
import com.sanofi.in.mobcast.LoginV2;
import com.sanofi.in.mobcast.OpenContent;
import com.sanofi.in.mobcast.R;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnCancelListener;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Environment;
import android.text.Html;
import android.util.Log;

public class Download extends AsyncTask<String, String, String> {

	ProgressDialog pDialog;
	private Activity activity;
	String Tablename, _id;
	String link, type, name, ename;
	Cursor c;
	boolean cancel = false;

	/**
	 * constructor
	 */

	public Download(Activity activity, String Tablename, String _id) {

		this.activity = activity;
		this.Tablename = Tablename;
		this._id = _id;
		pDialog = new ProgressDialog(activity);

	}

	/**
	 * background
	 */

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		if (activity != null) {
			pDialog = new ProgressDialog(activity);
			pDialog.setMessage(Html.fromHtml("Downloading file"));
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
			pDialog.setOnCancelListener(new OnCancelListener() {

				public void onCancel(DialogInterface dialog) {

					cancel = true;

				}
			});
		}
	}

	@Override
	protected String doInBackground(String... params) {

		try {

			AnnounceDBAdapter adb = new AnnounceDBAdapter(activity);
			adb.open();
			c = adb.getrow(Tablename, _id);
			adb.close();

			if ((Tablename.equals(AnnounceDBAdapter.SQLITE_ANNOUNCE))||(Tablename.equals(AnnounceDBAdapter.SQLITE_NEWS))) {
				name = c.getString((c
						.getColumnIndexOrThrow(AnnounceDBAdapter.KEY_NAME)));
				type = c.getString((c
						.getColumnIndexOrThrow(AnnounceDBAdapter.KEY_TYPE)));
				link = c.getString((c
						.getColumnIndexOrThrow(AnnounceDBAdapter.KEY_FILELINK)));
				System.out.println("Downloading for announcements" + name
						+ "   " + type + "   " + link);
				DownloadFromUrl(activity);
			}
			else if(Tablename.equals(AnnounceDBAdapter.SQLITE_AWARD))
			
					{
				name = c.getString((c
						.getColumnIndexOrThrow(AnnounceDBAdapter.KEY_IMAGEPATH)));
				link = c.getString((c
						.getColumnIndexOrThrow(AnnounceDBAdapter.KEY_FILELINK)));
				System.out.println("Downloading for awards" + name
						+ "   " + type + "   " + link);
				type = "award";
				DownloadFromUrl(activity);
				
					}
			else if (Tablename.equals(AnnounceDBAdapter.SQLITE_TRAINING)){
				name = c.getString((c
						.getColumnIndexOrThrow(AnnounceDBAdapter.KEY_NAME)));
				type = c.getString((c
						.getColumnIndexOrThrow(AnnounceDBAdapter.KEY_TYPE)));
				link = c.getString((c
						.getColumnIndexOrThrow(AnnounceDBAdapter.KEY_FILELINK)));
				ename = c.getString((c
						.getColumnIndexOrThrow(AnnounceDBAdapter.KEY_ENCRYPTION)));
				
				System.out.println("Downloading for Training " + name
						+ "   " + type + "   " + link);
				
			//	if ((type.contentEquals("audio"))
			//			|| (type.contentEquals("video"))) {DownloadFromUrl(activity);}
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
			pDialog.dismiss();
			pDialog = null;
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
				File myDir = new File(root + "/.mobcast/mobcast_images");
				myDir.mkdirs();
				String fname = name;
				File file = new File(myDir, fname);
				if (file.exists())
					file.delete();
				long startTime = System.currentTimeMillis();
				Log.d("ImageManager", "download begining");
				String url1 = url.toString().replaceAll(" ", "%20");
				url = new URL(url1);
				Log.d("ImageManager", "download url:" + url);
				Log.d("ImageManager", "downloaded file name:");
				/* Open a connection to that URL. */
				URLConnection ucon = url.openConnection();

				/*
				 * Define InputStreams to read from the URLConnection.
				 */
				InputStream is = ucon.getInputStream();
				BufferedInputStream bis = new BufferedInputStream(is);

				/*
				 * Read bytes to the Buffer until there is nothing more to
				 * read(-1).
				 */
				ByteArrayBuffer baf = new ByteArrayBuffer(50);
				int current = 0;
				while ((current = bis.read()) != -1) {
					baf.append((byte) current);
					if (cancel)
						break;
				}

				/* Convert the Bytes read to a String. */
				if (!cancel) {
					FileOutputStream fos = new FileOutputStream(file);
					fos.write(baf.toByteArray());
					fos.close();
					Log.d("ImageManager",
							"download ready in"
									+ ((System.currentTimeMillis() - startTime) / 1000)
									+ " sec");
				}

			}
			else if (type.contentEquals("award")) {
				
				Log.e("download"," award here");
				//TODO download files here
				 Log.e("name",name);
				 Log.e("link",link);
			
				File file = new File(name);
				if (file.exists())
					file.delete();
				URLConnection ucon = url.openConnection();
				InputStream is = ucon.getInputStream();
				BufferedInputStream bis = new BufferedInputStream(
						is);
				ByteArrayBuffer baf = new ByteArrayBuffer(50);
				int current = 0;
				while ((current = bis.read()) != -1) {
					if(cancel) break;
					baf.append((byte) current);
				}

				FileOutputStream fos = new FileOutputStream(file);
				fos.write(baf.toByteArray());
				fos.close();
				if(cancel) file.delete();

			}
			else if (type.contentEquals("video")) {

				File myDir = new File(root + "/.mobcast/mobcast_videos");
				myDir.mkdirs();
				String fname = name;
				// Log.v("fname",fname);
				File file = new File(myDir, fname);
				if (file.exists())
					file.delete();
				long startTime = System.currentTimeMillis();
				String url1 = url.toString().replaceAll(" ", "%20");
				url = new URL(url1);
				Log.d("ImageManager", "download begining");
				Log.d("ImageManager", "download url:" + url);
				Log.d("ImageManager", "downloaded file name:");
				/* Open a connection to that URL. */
				URLConnection ucon = url.openConnection();
				/*
				 * Define InputStreams to read from the URLConnection.
				 */
				InputStream is = ucon.getInputStream();
				// bookmarkstart
				/*
				 * Read bytes to the Buffer until there is nothing more to
				 * read(-1) and write on the fly in the file.
				 */
				FileOutputStream fos = new FileOutputStream(file);
				final int BUFFER_SIZE = 25 * 1024;
				BufferedInputStream bis = new BufferedInputStream(is,
						BUFFER_SIZE);
				byte[] baf = new byte[BUFFER_SIZE];
				int actual = 0;
				while (actual != -1) {
					fos.write(baf, 0, actual);
					actual = bis.read(baf, 0, BUFFER_SIZE);
					if (cancel) {
						file.delete();
						break;
					}
				}

				fos.close();

				// bookmarkend
				Log.d("ImageManager",
						"download ready in"
								+ ((System.currentTimeMillis() - startTime) / 1000)
								+ " sec");

				String link = root + "/.mobcast/mobcast_images/" + name;

			}
			else if (type.contentEquals("audio")) {
				Log.v("training", "in audio");

				File myDir = new File(Environment.getExternalStorageDirectory()
						.getAbsolutePath() + "/.mobcast/mobcast_audio");
				myDir.mkdirs();
				String fname = name;

				File file = new File(myDir, fname);
				if (file.exists())
					file.delete();

				// long startTime = System.currentTimeMillis();
				String url1 = url.toString().replaceAll(" ", "%20");
				url = new URL(url1);

				URLConnection ucon = url.openConnection();

				InputStream is = ucon.getInputStream();
				BufferedInputStream bis = new BufferedInputStream(is);

				ByteArrayBuffer baf = new ByteArrayBuffer(50);
				int current = 0;
				while ((current = bis.read()) != -1) {
					baf.append((byte) current);
					if (cancel)
						break;
				}

				/* Convert the Bytes read to a String. */
				if (!cancel) {
					FileOutputStream fos = new FileOutputStream(file);
					fos.write(baf.toByteArray());
					fos.close();
				}

			}// end of audio
			else{
				try {
					url = new URL(link);
					root = Environment
							.getExternalStorageDirectory()
							.toString();
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

					File myDir = new File(root + "/.mobcast/"
							+ foldername);

					String fname = ename;

					myDir.mkdirs();

					
					File file = new File(myDir, fname);
					if (file.exists())
						file.delete();
					URLConnection ucon = url.openConnection();
					InputStream is = ucon.getInputStream();
					BufferedInputStream bis = new BufferedInputStream(
							is);
					ByteArrayBuffer baf = new ByteArrayBuffer(50);
					int current = 0;
					while ((current = bis.read()) != -1) {
						baf.append((byte) current);
						if(cancel) break;
					}
					FileOutputStream fos = new FileOutputStream(
							file);
					fos.write(baf.toByteArray());
					fos.close();
						if(cancel) file.delete();

				} catch (Exception e) {
					
				}
			}

		} catch (IOException e) {
			Log.d("ImageManager", "Error: " + e);
		}

	}

}