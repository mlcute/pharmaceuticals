package com.sanofi.in.mobcast;

import java.io.File;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.FloatMath;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager.LayoutParams;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

import com.flurry.android.FlurryAgent;
import com.sanofi.in.mobcast.R;
import com.mobcast.util.BuildVars;
import com.mobcast.util.Constants;

@SuppressLint("NewApi")
public class ImageFullscreen extends Activity {
	TouchImageView imageDetail;
	Matrix matrix = new Matrix();
	Matrix savedMatrix = new Matrix();
	PointF startPoint = new PointF();
	PointF midPoint = new PointF();
	float oldDist = 1f;
	static final int NONE = 0;
	static final int DRAG = 1;
	static final int ZOOM = 2;
	int mode = NONE;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fullscreen);
		if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
			if (!BuildVars.debug) {
				getWindow().setFlags(LayoutParams.FLAG_SECURE,
						LayoutParams.FLAG_SECURE);
			}
		}
		imageDetail = (TouchImageView) findViewById(R.id.imageView1);
		/** * set on touch listner on image */

		String image = getIntent().getStringExtra("name");
		String mname = getIntent().getStringExtra("mname");

		if (mname.contains("award")) {
//			SU VIKALP AWARD IMAGE ZOOM FIXED
//			File file = new File(image);
			String root = Environment.getExternalStorageDirectory().toString();
			File myDir = new File(root + Constants.APP_FOLDER_IMG);
			File file = new File(myDir, image);
//			EU VIKALP AWARD IMAGE ZOOM FIXED

			imageDetail.setImageURI(Uri.parse(file.getAbsolutePath()));
		} else {
			Log.d("name", image);
			String root = Environment.getExternalStorageDirectory().toString();
			File myDir = new File(root + Constants.APP_FOLDER_IMG);
			File file = new File(myDir, image);

			imageDetail.setImageURI(Uri.parse(file.getAbsolutePath()));
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