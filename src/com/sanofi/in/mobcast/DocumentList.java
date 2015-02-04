package com.sanofi.in.mobcast;

import java.io.File;
import java.util.HashMap;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.WindowManager.LayoutParams;
import android.view.animation.AnimationUtils;
import android.view.animation.RotateAnimation;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;

import com.flurry.android.FlurryAgent;
import com.sanofi.in.mobcast.R;
import com.mobcast.util.BuildVars;
import com.mobcast.util.Constants;

public class DocumentList extends Activity {

	private AnnounceDBAdapter dbHelper;
	// private SimpleCursorAdapter dataAdapter;
	private DocumentAdapter dataAdapter;
	TextView detail;
	ListView listView;
	ImageView symbol;
	ImageButton search;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.documentlist);
		if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
			if (!BuildVars.debug) {
				getWindow().setFlags(LayoutParams.FLAG_SECURE,
						LayoutParams.FLAG_SECURE);
			}
		}
		search = (ImageButton) findViewById(R.id.sample_button);
		search.setOnTouchListener(myhandler2);
		search.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent i = new Intent(DocumentList.this, Search.class);
				i.putExtra("tablename", "Document");
				startActivity(i);

			}
		});

	}

	View.OnTouchListener myhandler2 = new View.OnTouchListener() {

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			// TODO Auto-generated method stub
			// btn.setBackgroundResource(R.drawable.gradient2);

			if (event.getAction() == android.view.MotionEvent.ACTION_DOWN) {
				v.setBackgroundResource(R.drawable.gradient2);
				int padding_in_dp = 7; // 6 dps
				final float scale = getResources().getDisplayMetrics().density;
				int padding_in_px = (int) (padding_in_dp * scale + 0.5f);

				// v.setPadding(padding_in_px, padding_in_px, padding_in_px,
				// padding_in_px);

			} else if (event.getAction() == android.view.MotionEvent.ACTION_UP) {
				v.setBackgroundResource(R.drawable.button_gradient);

				int padding_in_dp = 7; // 6 dps
				final float scale = getResources().getDisplayMetrics().density;
				int padding_in_px = (int) (padding_in_dp * scale + 0.5f);

				// v.setPadding(padding_in_px, padding_in_px, padding_in_px,
				// padding_in_px);
			}

			return false;
		}

	};

	protected void onResume() {
		super.onResume();

		// detail=(TextView)findViewById(R.id.tvListDetail);
		// RotateAnimation rotate=
		// (RotateAnimation)AnimationUtils.loadAnimation(this,R.anim.rotateanim);
		// detail.setAnimation(rotate);
		// symbol=(ImageView)findViewById(R.id.ivTannounce);
		dbHelper = new AnnounceDBAdapter(this);
		dbHelper.open();
		// IMPORTANT CONCEPT

		// Clean all data
		// dbHelper.deleteAllCountries();
		// Add some data
		// Generate ListView from SQLite Database
		displayListView();

		dbHelper.close();
	}

	// context menu code
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.context_menu, menu);

		// menu.setHeaderTitle("Please Select");
		// menu.setHeaderIcon(R.drawable.ic_launcher);

	}

	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
				.getMenuInfo();

		switch (item.getItemId()) {
		case R.id.read:
			itemRead(info.position);

			return true;
		case R.id.unread:

			itemUnread(info.position);
			return true;
		case R.id.delete:

			itemDelete(info.position);

			return true;
		case R.id.view:

			itemView(info.position);

			return true;
		default:
			return super.onContextItemSelected(item);
		}
	}

	public void itemDelete(int position) {
		Cursor cursor = (Cursor) listView.getItemAtPosition(position);
		String _id = cursor.getString(cursor.getColumnIndexOrThrow("_id"));
		// Toast.makeText(this, "You have chosen the delete fot announceid = " +
		// _id, Toast.LENGTH_SHORT).show();

		AnnounceDBAdapter announce = new AnnounceDBAdapter(DocumentList.this);
		announce.open();
		// delete code
		announce.deleterow(_id, "Document");
		announce.close();

		onResume();

	}

	public void itemRead(int position) {
		Cursor cursor = (Cursor) listView.getItemAtPosition(position);
		String _id = cursor.getString(cursor.getColumnIndexOrThrow("_id"));
		// Toast.makeText(this, "You have chosen the delete for announceid = " +
		// _id, Toast.LENGTH_SHORT).show();

		AnnounceDBAdapter announce = new AnnounceDBAdapter(DocumentList.this);
		announce.open();
		// set row read code for announcement row
		announce.readrow(_id, "Document");

		announce.close();
		onResume();

	}

	public void itemUnread(int position) {
		Cursor cursor = (Cursor) listView.getItemAtPosition(position);
		String _id = cursor.getString(cursor.getColumnIndexOrThrow("_id"));
		// Toast.makeText(this, "You have chosen the delete for announceid = " +
		// _id, Toast.LENGTH_SHORT).show();

		AnnounceDBAdapter announce = new AnnounceDBAdapter(DocumentList.this);
		announce.open();
		// set row read code for announcement row
		announce.unreadrow(_id, "Document");

		announce.close();
		Log.v("marked", "unread");
		onResume();

	}

	// context menu code ends here

	public void itemView(int position) {

		try {
			// Get the cursor, positioned to the corresponding row in the result
			// set
			Cursor cursor = (Cursor) listView.getItemAtPosition(position);

			// Get the state's capital from this row in the database.
			String title = cursor.getString(cursor
					.getColumnIndexOrThrow("title"));
			String detail = cursor.getString(cursor
					.getColumnIndexOrThrow("detail"));
			String summary = cursor.getString(cursor
					.getColumnIndexOrThrow("summary"));
			String tid = cursor.getString(cursor.getColumnIndexOrThrow("tid"));
			String name = cursor
					.getString(cursor.getColumnIndexOrThrow("name"));

			String type = cursor
					.getString(cursor.getColumnIndexOrThrow("type"));
			String ename = cursor.getString(cursor
					.getColumnIndexOrThrow("ename"));

			String _id = cursor.getString(cursor.getColumnIndexOrThrow("_id"));

			Log.v("Training type", type);

			NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
			mNotificationManager.cancelAll();

			
			Intent show;
			show = new Intent(DocumentList.this, Document.class);
			show.putExtra("title", title);
			show.putExtra("detail", detail);
			// show.putExtra("from", from);
			show.putExtra("type", type);
			show.putExtra("_id", _id);
			show.putExtra("name", name);
			show.putExtra("ename", ename);
			show.putExtra("summary", summary);
			show.putExtra("id", tid);
			show.putExtra("mtitle", "Documents");
			// show.putExtra("_id", _id);
			startActivity(show);

			/*
			 * if(type.equals("pdf"))
			 * 
			 * { Log.v("training", "in pdf"); Intent show; Log.v("training",
			 * "symbol set with"); File file = new
			 * File(Environment.getExternalStorageDirectory().getAbsolutePath()
			 * + "/.mobcast/"+getString(R.string.pdf)+"/"+name);
			 * 
			 * show = new Intent(Intent.ACTION_VIEW);
			 * show.setFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
			 * show.setAction(Intent.ACTION_VIEW);
			 * show.setDataAndType(Uri.fromFile(file), "application/pdf");
			 * startActivity(show);
			 * 
			 * 
			 * 
			 * 
			 * }//end of pdf
			 * 
			 * if(type.equals("ppt"))
			 * 
			 * {
			 * 
			 * Intent show;
			 * 
			 * File file = new
			 * File(Environment.getExternalStorageDirectory().getAbsolutePath()
			 * + "/.mobcast/"+getString(R.string.ppt)+"/"+name);
			 * 
			 * show = new Intent(Intent.ACTION_VIEW);
			 * show.setFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
			 * show.setAction(Intent.ACTION_VIEW);
			 * show.setDataAndType(Uri.fromFile(file),
			 * "application/vnd.ms-powerpoint"); startActivity(show);
			 * 
			 * 
			 * 
			 * 
			 * }//end of ppt
			 * 
			 * if(type.equals("doc")) {
			 * 
			 * Intent show;
			 * 
			 * File file = new
			 * File(Environment.getExternalStorageDirectory().getAbsolutePath()
			 * + "/.mobcast/"+getString(R.string.doc)+"/"+name); show = new
			 * Intent(Intent.ACTION_VIEW);
			 * show.setFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
			 * show.setAction(Intent.ACTION_VIEW);
			 * show.setDataAndType(Uri.fromFile(file), "application/msword");
			 * startActivity(show);
			 * 
			 * 
			 * }//end of doc
			 * 
			 * if(type.equals("xls")) {
			 * 
			 * Intent show;
			 * 
			 * File file = new
			 * File(Environment.getExternalStorageDirectory().getAbsolutePath()
			 * + "/.mobcast/"+getString(R.string.xls)+"/"+name); show = new
			 * Intent(Intent.ACTION_VIEW);
			 * show.setFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
			 * show.setAction(Intent.ACTION_VIEW);
			 * show.setDataAndType(Uri.fromFile(file),
			 * "application/vnd.ms-excel"); startActivity(show);
			 * 
			 * 
			 * }//end of xls
			 */

		} catch (Exception e) {
			Toast.makeText(getBaseContext(), "Suitable App Not Found",
					Toast.LENGTH_LONG).show();
		}

	}

	private void displayListView() {

		Cursor cursor = dbHelper.fetchAllDocument();

		// The desired columns to be bound
		String[] columns = new String[] { AnnounceDBAdapter.KEY_TITLE,
				// AnnounceDBAdapter.KEY_DETAIL,
				AnnounceDBAdapter.KEY_SUMMARY };

		// the XML defined views which the data will be bound to
		int[] to = new int[] { R.id.documentTitle,
				// R.id.documentDetail,
				R.id.tvListSummary };

		// create the adapter using the cursor pointing to the desired data
		// as well as the layout information
		dataAdapter = new DocumentAdapter(this, R.layout.documentlist_item,
				cursor, columns, to, 0);

		/*
		 * dataAdapter = new SimpleCursorAdapter( this, R.layout.eventlisting,
		 * cursor, columns, to, 0);
		 */

		listView = (ListView) findViewById(R.id.documentList);
		// Assign adapter to ListView
		listView.setAdapter(dataAdapter);
		registerForContextMenu(listView);

		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> listView, View view,
					int position, long id) {
				itemView(position);
			}
		});// onClick

	}// end of displayList
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

}// end of TrainingList
