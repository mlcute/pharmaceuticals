package com.sanofi.in.mobcast;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Html;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager.LayoutParams;
import android.view.WindowManager;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.AdapterContextMenuInfo;

import com.flurry.android.FlurryAgent;
import com.sanofi.in.mobcast.R;
import com.mobcast.util.BuildVars;
import com.mobcast.util.Constants;

public class Imageupload extends Activity {

	Button submit, camera, browse;
	ListView imagesList;
	EditText description;
	String descriptionValue;
	TextView tv;
	private static final String JPEG_FILE_PREFIX = "IMG_";
	private static final String JPEG_FILE_SUFFIX = ".jpg";
	private static int RESULT_LOAD_IMAGE = 1;
	private static final int CAMERA_REQUEST = 1888;
	String mCurrentPhotoPath = "";
	private Imageadapter dataadapter;
	AnnounceDBAdapter adb;

	int serverResponseCode = 0;
	ProgressDialog dialog = null;

	

	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.upload);
		if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
			if (!BuildVars.debug) {
				getWindow().setFlags(LayoutParams.FLAG_SECURE,
						LayoutParams.FLAG_SECURE);
			}
		}
		this.getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		description = (EditText) findViewById(R.id.editText1);
		submit = (Button) findViewById(R.id.button3);
		tv = (TextView) findViewById(R.id.TextView1);
		camera = (Button) findViewById(R.id.button2);
		browse = (Button) findViewById(R.id.button1);
		adb = new AnnounceDBAdapter(getApplicationContext());
		mCurrentPhotoPath = "";

		submit.setOnClickListener(myhandler1);
		submit.setOnTouchListener(myhandler2);
		camera.setOnClickListener(myhandler1);
		camera.setOnTouchListener(myhandler2);
		browse.setOnClickListener(myhandler1);
		browse.setOnTouchListener(myhandler2);

		imagesList = (ListView) findViewById(R.id.listView1);
		imagesList.setOnTouchListener(new OnTouchListener() {
			// Setting on Touch Listener for handling the touch inside
			// ScrollView
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// Disallow the touch request for parent scroll on touch of
				// child view
				v.getParent().requestDisallowInterceptTouchEvent(true);
				return false;
			}
		});

		// to retrive value from shared preferences
		descriptionValue = getPreferences(MODE_PRIVATE).getString(
				"descriptionText", "");
		description.setText(descriptionValue);

	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		// to save value of description into shared preferences
		getPreferences(MODE_PRIVATE).edit()
				.putString("descriptionText", description.getText().toString())
				.commit();

	}

	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.uploadcontext, menu);
	}

	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
				.getMenuInfo();

		switch (item.getItemId()) {

		case R.id.delete:

			/**/

			Cursor cursor = (Cursor) imagesList
					.getItemAtPosition(info.position);
			String _id = cursor.getString(cursor.getColumnIndexOrThrow("_id"));
			adb.open();
			adb.deleterow(_id, "Images");
			Log.e("deleting ", info.position + "");
			adb.close();

			onResume();

			return true;

		default:
			return super.onContextItemSelected(item);
		}

	}

	View.OnClickListener myhandler1 = new View.OnClickListener() {
		public void onClick(View v) {

			// for submit
			if (v.getId() == R.id.button3) {
				description.setText("");

				new RetreiveFeedTask(Imageupload.this)
						.execute(com.mobcast.util.Constants.UPDATE_POST);

			}

			// for camera
			if (v.getId() == R.id.button2) {
				// Toast.makeText(getApplicationContext(), "fetch camera",
				// Toast.LENGTH_SHORT).show();
				// Intent cameraIntent = new
				// Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
				// startActivityForResult(cameraIntent, CAMERA_REQUEST);

				Intent takePictureIntent = new Intent(
						MediaStore.ACTION_IMAGE_CAPTURE);

				try {
					getPreferences(MODE_PRIVATE).edit()
							.putString("photopath", "").commit();
					File f = createImageFile();

					takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
							Uri.fromFile(f));
					startActivityForResult(takePictureIntent, CAMERA_REQUEST);

				} catch (Exception E) {
					E.printStackTrace();
				}

			}

			// for browse image
			if (v.getId() == R.id.button1) {
				Intent i = new Intent(
						Intent.ACTION_PICK,
						android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

				startActivityForResult(i, RESULT_LOAD_IMAGE);

				/*
				 * String root =
				 * Environment.getExternalStorageDirectory().toString(); File
				 * myDir = new File(root + "/.mobcast/mobcast_images");
				 */
			}

		}
	};

	View.OnTouchListener myhandler2 = new View.OnTouchListener() {

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			// TODO Auto-generated method stub
			// btn.setBackgroundResource(R.drawable.gradient2);

			if (event.getAction() == android.view.MotionEvent.ACTION_DOWN) {
				v.setBackgroundResource(R.drawable.gradient2);
				int padding_in_dp = 5; // 6 dps
				final float scale = getResources().getDisplayMetrics().density;
				int padding_in_px = (int) (padding_in_dp * scale + 0.5f);

				v.setPadding(padding_in_px, padding_in_px, padding_in_px,
						padding_in_px);

			} else if (event.getAction() == android.view.MotionEvent.ACTION_UP) {
				v.setBackgroundResource(R.drawable.button_gradient);

				int padding_in_dp = 5; // 6 dps
				final float scale = getResources().getDisplayMetrics().density;
				int padding_in_px = (int) (padding_in_dp * scale + 0.5f);

				v.setPadding(padding_in_px, padding_in_px, padding_in_px,
						padding_in_px);
			}

			return false;
		}

	};

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK
				&& null != data) {
			Uri selectedImage = data.getData();
			String[] filePathColumn = { MediaStore.Images.Media.DATA };

			Cursor cursor = getContentResolver().query(selectedImage,
					filePathColumn, null, null, null);
			cursor.moveToFirst();

			int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
			String picturePath = cursor.getString(columnIndex);
			cursor.close();

			// Toast.makeText(getApplicationContext(), picturePath,
			// Toast.LENGTH_SHORT).show();

			adb.open();
			adb.createImage(picturePath);
			adb.close();
			// ImageView imageView = (ImageView) findViewById(R.id.imgView);
			// imageView.setImageBitmap(BitmapFactory.decodeFile(picturePath));

			// String picturePath contains the path of selected Image
			onResume();
		}

		// if (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK) {
		if (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK) {
			/*
			 * Bitmap bitmap = (Bitmap) data.getExtras().get("data"); //
			 * imageView.setImageBitmap(photo);
			 * 
			 * String timeStamp = System.currentTimeMillis()%10000+""; //new
			 * SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
			 * 
			 * String filename = timeStamp+"upload.jpg"; //File sd =
			 * Environment.getExternalStorageDirectory(); String root =
			 * Environment.getExternalStorageDirectory().toString(); File sd =
			 * new File(root + "/.mobcast/mobcast_images"); try{ sd.mkdirs();
			 * }catch(Exception e){}
			 * 
			 * File dest = new File(sd, filename); if (dest.exists ())
			 * dest.delete (); // Bitmap bitmap =
			 * (Bitmap)data.getExtras().get("data"); try { FileOutputStream out
			 * = new FileOutputStream(dest);
			 * bitmap.compress(Bitmap.CompressFormat.PNG, 100, out); //
			 * bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
			 * out.flush(); out.close(); } catch (Exception e) {
			 * e.printStackTrace(); }
			 * 
			 * String picturePath=sd+"/"+filename; //
			 * Toast.makeText(getApplicationContext(), picturePath,
			 * Toast.LENGTH_LONG).show(); adb.open();
			 * adb.createImage(picturePath); adb.close();
			 */
			adb.open();
			adb.createImage(getPreferences(MODE_PRIVATE).getString("photopath",
					""));
			adb.close();

		}

	}

	private File createImageFile() throws IOException {
		// Create an image file name
		String timeStamp = System.currentTimeMillis() % 100 + "";
		File myDir = new File(Environment.getExternalStorageDirectory()
				.toString() + Constants.APP_FOLDER_IMG);
		myDir.mkdirs();
		String imageFileName = JPEG_FILE_PREFIX + timeStamp + "_";

		File image = File
				.createTempFile(imageFileName, JPEG_FILE_SUFFIX, myDir);
		// mCurrentPhotoPath = image.getAbsolutePath();
		getPreferences(MODE_PRIVATE).edit()
				.putString("photopath", image.getAbsolutePath()).commit();
		return image;
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();

		adb.open();
		displayListView();
		adb.close();

		if (imagesList.getCount() < 1) {
			tv.setVisibility(TextView.VISIBLE);
		} else {
			tv.setVisibility(TextView.GONE);
		}

	}

	private void displayListView() {

		Cursor cursor = adb.fetchAllImages();

		String[] columns = new String[] { "name", "size" };

		int[] to = new int[] { R.id.tvName, R.id.tvSize, };

		dataadapter = new Imageadapter(this, R.layout.uploadlistitem, cursor,
				columns, to, 0);

		// Assign adapter to ListView
		imagesList.setAdapter(dataadapter);
		imagesList.setSelector(R.drawable.listselector);
		registerForContextMenu(imagesList);

	}

	class RetreiveFeedTask extends AsyncTask<String, Void, String> {

		String str = "";

		ProgressDialog pDialog;
		private Activity activity;

		Context context = null;

		public RetreiveFeedTask(Activity activity) {

			Log.d("check point", "1");
			this.activity = activity;
			context = activity;
			pDialog = new ProgressDialog(context);

		}

		protected void onPreExecute() {
			super.onPreExecute();
			if (context != null) {
				pDialog = new ProgressDialog(context);
				pDialog.setMessage(Html.fromHtml("Please Wait..."));
				pDialog.setIndeterminate(false);
				pDialog.setCancelable(false);
				pDialog.show();
			}
		}

		protected String doInBackground(String... urls) {
			Log.d("posting", "started");
			try {
				byte[] result = null;
				try {
					HttpPost httpost = new HttpPost(
							com.mobcast.util.Constants.UPDATE_CAPTURE);
					Log.d("check point", "2");
					MultipartEntity entity = new MultipartEntity();
					Log.d("check point", "3");
					entity.addPart("note", new StringBody(description.getText()
							.toString()));
					Log.v("note", description.getText().toString());
					SessionManagement sm = new SessionManagement(context);
					String email = sm.getUserDetails().get("name");
					Log.v("email", email);
					entity.addPart("emailID", new StringBody(email));
					entity.addPart(com.mobcast.util.Constants.user_id, new StringBody(email));
					int max = imagesList.getCount();
					entity.addPart("countOfImages", new StringBody(max + ""));
					Log.v("countOfImages", max + "");
					File imageFile;
					adb.open();
					Cursor c = adb.fetchAllImages();
					adb.close();
					if (c != null) {
						c.moveToFirst();
						for (int i = 1; i <= max; i++) {
							imageFile = new File(c.getString(c
									.getColumnIndexOrThrow("address")));
							entity.addPart("image" + i, new FileBody(imageFile));

							String _id = c.getString(c
									.getColumnIndexOrThrow("_id"));
							adb.open();
							adb.deleterow(_id, "Images");
							Log.e("deleting ", "_id");
							adb.close();

							if (c.isLast())
								break;
							else
								c.moveToNext();
						}

					}

					httpost.setEntity(entity);

					HttpResponse response;
					HttpClient httpclient = new DefaultHttpClient();
					response = httpclient.execute(httpost);
					Log.v("http post", response.toString());

					result = EntityUtils.toByteArray(response.getEntity());
					str = new String(result, "iso-8859-1");
					Log.v("http post", str);
				} catch (Exception e) {
					e.printStackTrace();
				}

			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
			return null;
		}

		protected void onPostExecute(String feed) {
			// TODO: check this.exception
			// TODO: do something with the feed
			try {
				pDialog.dismiss();
				pDialog = null;
				Toast.makeText(context, "Posted", Toast.LENGTH_SHORT).show();
				finish();
			} catch (Exception e) {
				// nothing
			}
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
