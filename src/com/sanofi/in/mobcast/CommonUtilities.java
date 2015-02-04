package com.sanofi.in.mobcast;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import org.apache.http.util.ByteArrayBuffer;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.text.Html;
import android.util.Log;
import android.view.WindowManager.LayoutParams;
import android.widget.ImageView;
import android.widget.VideoView;

import com.flurry.android.FlurryAgent;
import com.sanofi.in.mobcast.R;
import com.mobcast.util.BuildVars;
import com.mobcast.util.Constants;

public class CommonUtilities extends Activity {

	final static String image_url = "http://yudh-tsec.com/images/tabbasketball.png";
	Bitmap message_bitmap = null;
	ProgressDialog pDialog;
	VideoView iv;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.showcase);
		if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
			if (!BuildVars.debug) {
				getWindow().setFlags(LayoutParams.FLAG_SECURE,
						LayoutParams.FLAG_SECURE);
			}
		}
		iv = (VideoView) findViewById(R.id.videoView1);

		/*
		 * Bitmap message_bitmap = null; // Should we download the image? if
		 * ((image_url != null) && (!image_url.equals(""))) { message_bitmap =
		 * downloadBitmap(image_url); iv.setImageBitmap(message_bitmap);
		 * Log.d("Image", "Displayed");
		 * 
		 * 
		 * } // If we didn't get the image, we're out of here if (message_bitmap
		 * == null) {
		 * 
		 * Log.d("Image", "Null hai"); } else { saveImage(); }
		 */
		DownloadFromUrl();

	}

	void saveImage() {

		String root = Environment.getExternalStorageDirectory().toString();
		File myDir = new File(root + "/saved_images");
		myDir.mkdirs();
		String fname = "Image.png";
		File file = new File(myDir, fname);

		ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		message_bitmap.compress(Bitmap.CompressFormat.JPEG, 40, bytes);

		// write the bytes in file
		FileOutputStream fo;
		try {
			fo = new FileOutputStream(file);

			fo.write(bytes.toByteArray());
			fo.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		/*
		 * if (file.exists ()) file.delete (); try { FileOutputStream out = new
		 * FileOutputStream(file);
		 * message_bitmap.compress(Bitmap.CompressFormat.PNG, 90, out);
		 * out.flush(); out.close();
		 * 
		 * } catch (Exception e) { e.printStackTrace(); }
		 */
	}

	public void DownloadFromUrl() {
		// this is the downloader method
		File file = null;
		String root = "";
		try {
			URL url = new URL(
					"http://www.youtube.com/watch?v=ogZG-HliN8M&list=PLZnxqowr6IKiDoILRWnxlFNct6ClNb-JI&index=1&feature=etp-gs-mp4");
			root = Environment.getExternalStorageDirectory().toString();
			File myDir = new File(root + "/saved_videos");
			myDir.mkdirs();
			String fname = "tp";
			file = new File(myDir, fname);

			long startTime = System.currentTimeMillis();
			Log.d("ImageManager", "download begining");
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
			 * Read bytes to the Buffer until there is nothing more to read(-1).
			 */
			ByteArrayBuffer baf = new ByteArrayBuffer(50);
			int current = 0;
			while ((current = bis.read()) != -1) {
				baf.append((byte) current);
			}

			/* Convert the Bytes read to a String. */
			FileOutputStream fos = new FileOutputStream(file);
			fos.write(baf.toByteArray());
			fos.close();
			Log.d("ImageManager",
					"download ready in"
							+ ((System.currentTimeMillis() - startTime) / 1000)
							+ " sec");

		} catch (IOException e) {
			Log.d("ImageManager", "Error: " + e);
		}
		// Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
		iv.setVideoPath(root + "/saved_videos/tp.mp4");
		iv.setVideoURI(Uri.parse(root + "/saved_videos/tp.mp4"));
		iv.requestFocus();
		iv.start();

	}

	private class LongOperation extends AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... params) {

			// perform long running operation operation

			return null;

		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
		 */

		@Override
		protected void onPostExecute(String result) {

			// execution of result of Long time consuming operation
			pDialog.dismiss();

		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.os.AsyncTask#onPreExecute()
		 */

		@Override
		protected void onPreExecute() {

			// Things to be done before execution of long running operation. For
			// example showing ProgessDialog
			pDialog = new ProgressDialog(CommonUtilities.this);
			pDialog.setMessage(Html.fromHtml("Please Wait..."));
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(false);
			pDialog.show();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.os.AsyncTask#onProgressUpdate(Progress[])
		 */

		@Override
		protected void onProgressUpdate(Void... values) {

			// Things to be done while execution of long running operation is in
			// progress. For example updating ProgessDialog

		}

	}

	static class FlushedInputStream extends FilterInputStream {
		public FlushedInputStream(InputStream inputStream) {
			super(inputStream);
		}

		@Override
		public long skip(long n) throws IOException {
			long totalBytesSkipped = 0L;
			while (totalBytesSkipped < n) {
				long bytesSkipped = in.skip(n - totalBytesSkipped);
				if (bytesSkipped == 0L) {
					int num_byte = read();
					if (num_byte < 0) {
						break; // we reached EOF
					} else {
						bytesSkipped = 1; // we read one byte
					}
				}
				totalBytesSkipped += bytesSkipped;
			}
			return totalBytesSkipped;
		}
	}
	
	/*
	 * Flurry Analytics
	 */
	@Override
	protected void onStart() {
		super.onStart();
		FlurryAgent.onStartSession(this, Constants.FLURRY_ID);
	}

	@Override
	protected void onStop() {
		super.onStop();
		FlurryAgent.onEndSession(this);
	}

}
