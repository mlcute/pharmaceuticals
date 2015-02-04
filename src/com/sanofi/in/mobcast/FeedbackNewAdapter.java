package com.sanofi.in.mobcast;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mobcast.util.Utilities;

public class FeedbackNewAdapter extends SimpleCursorAdapter {

	private Cursor cur;
	com.sanofi.in.mobcast.VerticalTextView vtv;
	TextView tv1,tv2,tv3;
	LayoutInflater inflate;

	public FeedbackNewAdapter(Context context, int layout, Cursor c,
			String[] from, int[] to, int i) {
		super(context, layout, c, from, to);
		cur = c;
		inflate = LayoutInflater.from(context);

		// TODO Auto-generated constructor stub
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		
		
		convertView = inflate.inflate(R.layout.feedbacklist_item, null);
		tv1  = (TextView)convertView.findViewById(R.id.feedbackTitle);
		tv2  = (TextView)convertView.findViewById(R.id.feedbackDescription);
		tv3  = (TextView)convertView.findViewById(R.id.feedbackQuestionNo);
		
		View view = super.getView(position, convertView, parent);

		cur.moveToPosition(position);
		
		String bgcolor = cur.getString(cur.getColumnIndexOrThrow("read_id"));
		Log.d(cur.getString(cur.getColumnIndexOrThrow("feedbackNo")),
				cur.getString(cur.getColumnIndexOrThrow("read_id")));

		if (Integer.parseInt(bgcolor) == 0) {
			view.setBackgroundResource(R.drawable.listgradientunread);
		} else {
			view.setBackgroundResource(R.drawable.listgradientnormal);
		}
		
		tv1.setText(cur.getString(cur.getColumnIndexOrThrow("feedbackTitle")));
		tv2.setText(cur.getString(cur.getColumnIndexOrThrow("feedbackDescription")));
		tv3.setText(cur.getString(cur.getColumnIndexOrThrow("feedbackTotalQuestions")));
		
		vtv = (com.sanofi.in.mobcast.VerticalTextView) view
				.findViewById(R.id.feedbackDate);
//		SU VIKALP FEEDBACK DATE SORT ORDER
//		String date = vtv.getText().toString();
		String date =	Utilities.convertDateToIndian(vtv.getText().toString().trim());
//		EU VIKALP FEEDBACK DATE SORT ORDER
		vdate vd = new vdate(date);
		vtv.setText(vd.getRDate());
		

		return view;
	}

}
