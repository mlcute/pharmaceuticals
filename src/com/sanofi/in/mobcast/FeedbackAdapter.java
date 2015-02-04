package com.sanofi.in.mobcast;

import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.HashMap;

import android.content.Intent;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.sanofi.in.mobcast.R;

public class FeedbackAdapter extends ArrayAdapter<FeedbackForm> {
	Context context;
	int layoutResourceId;
	List<FeedbackForm> items;
	int hashMapKey;
	ArrayList<FeedbackQuestion> al1;
	int totalques;
	String title;
	ArrayList<FeedbackForm> list;

	public FeedbackAdapter(Context context, int resource,
			List<FeedbackForm> items) {
		super(context, resource, items);

		this.items = items;

	}

	private int[] colors = new int[] { 0xAAFFFFF, 0xAAc0c0c0 };

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		View row = convertView;
		if (row == null) {
			LayoutInflater vi = LayoutInflater.from(getContext());
			row = vi.inflate(R.layout.feedbacklist_item, null);
		}

		FeedbackForm ff = items.get(position);

		if (ff != null) {

			TextView title = (TextView) row.findViewById(R.id.feedbackTitle);
			TextView questionNo = (TextView) row
					.findViewById(R.id.feedbackQuestionNo);
			TextView date = (TextView) row.findViewById(R.id.feedbackDate);

			Log.v("feedback adapter", ff.getTitle());
			title.setText(ff.getTitle());
			Integer quesNo = ff.getTotalQuestion();
			questionNo.setText(quesNo.toString());
			date.setText(ff.getDate());

			try {
				date.setText(new vdate(ff.getDate().toString()).getRDate());
			} catch (Exception e) {
			}

		}// end of if

		// int colorPos = position % colors.length;
		// row.setBackgroundColor(colors[colorPos]);

		String root = Environment.getExternalStorageDirectory().toString();// Getting
																			// the
																			// sdcard
																			// address
		try {
			// Log.v("Feedback","in outer try");
			FileInputStream fis = new FileInputStream(root
					+ "/mobcastFeedbackObject.ser");

			if (fis.equals(null))
				Log.v("feedback", "fis is null");

			ObjectInputStream ois = new ObjectInputStream(fis);
			if (ois.equals(null))
				Log.v("feedback", "ois is null");

			ArrayList objArray = new ArrayList<FeedbackForm>();

			while (ois.readObject() != null) {
				objArray.add((FeedbackForm) ois.readObject());
				Log.v("feedback", "object added");
			}

			/*
			 * list = new ArrayList<FeedbackForm>(10);
			 * 
			 * 
			 * 
			 * while (ois.readObject() != null) { list.add((FeedbackForm)
			 * ois.readObject()); Log.v("feedback","object added"); }
			 */

			try {
				while (true) {
					ois.readObject();
				}
			} catch (Exception e) {
				// This ALWAYS happens
			}

			ois.close();

			/*
			 * for (int i=0;i<10;i++) { if(list.get(i)!=null) { TextView
			 * title=(TextView)row.findViewById(R.id.feedbackTitle); TextView
			 * questionNo=(TextView)row.findViewById(R.id.feedbackQuestionNo);
			 * TextView date=(TextView)row.findViewById(R.id.feedbackDate);
			 * 
			 * title.setText(list.get(i).getTitle()); Integer
			 * quesNo=list.get(i).getTotalQuestion();
			 * questionNo.setText(quesNo.toString());
			 * date.setText(list.get(i).getDate());
			 * 
			 * 
			 * } }
			 */} catch (Exception e) {
			e.printStackTrace();
		}

		finally {

			return row;
		}

	}// end of getView

}// end of FeedbackAdapter
