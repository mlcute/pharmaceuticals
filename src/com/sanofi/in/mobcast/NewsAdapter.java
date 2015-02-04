package com.sanofi.in.mobcast;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.sanofi.in.mobcast.R;
import com.mobcast.util.Utilities;

public class NewsAdapter extends SimpleCursorAdapter {

	private Cursor cur;
	private LayoutInflater inflate;

	@SuppressWarnings("deprecation")
	public NewsAdapter(Context context, int layout, Cursor c, String[] from,
			int[] to, int i) {
		super(context, layout, c, from, to);
		cur = c;
		inflate = LayoutInflater.from(context);
		// TODO Auto-generated constructor stub
	}

	private int[] colors = new int[] { 0xAAFFFFF, 0xAAc0c0c0 };

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub

		ImageView iv;
		convertView = inflate.inflate(R.layout.newslisting, null);
		iv = (ImageView) convertView.findViewById(R.id.ivNannounce);

		cur.moveToPosition(position);
		String type = cur.getString(cur.getColumnIndexOrThrow("type"));
		String bgcolor = cur.getString(cur.getColumnIndexOrThrow("read_id"));
		
		
		if (type.contentEquals("image")) {
			iv.setImageResource(R.drawable.image);
		} else if (type.contentEquals("video")) {
			iv.setImageResource(R.drawable.video);
		} else if (type.contentEquals("audio")) {
			iv.setImageResource(R.drawable.audio_blue);
		} else {
			iv.setImageResource(R.drawable.texts);
		}

		View view = super.getView(position, convertView, parent);
		int colorPos = position % colors.length;

		// view.setBackgroundColor(colors[colorPos]);
		if (Integer.parseInt(bgcolor) == 0)
			view.setBackgroundResource(R.drawable.listgradientunread);
		else
			view.setBackgroundResource(R.drawable.listgradientnormal);
		
		// vertical date logic
		TextView tv = (TextView) convertView.findViewById(R.id.tvNListDetail);
		// Log.v("date",tv.getText().toString());
//		SU VIKALP DATE ORDER ISSUE
//		vdate vd = new vdate(tv.getText().toString());
		vdate vd = new vdate(Utilities.convertDateToIndian(tv.getText().toString()));
//		EU VIKALP DATE ORDER ISSUE
		tv.setText(vd.getRDate());
		// ===================

		return view;

	}
}
