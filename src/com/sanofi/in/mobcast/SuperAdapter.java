package com.sanofi.in.mobcast;

import java.util.Calendar;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.AnimationUtils;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.sanofi.in.mobcast.R;
import com.sanofi.in.mobcast.vdate;
import com.mobcast.util.Utilities;

public class SuperAdapter extends SimpleCursorAdapter {
	TextView vtv;
	private Cursor cur;
	private LayoutInflater inflate;
	LinearLayout lb;

	@SuppressWarnings("deprecation")
	public SuperAdapter(Context context, int layout, Cursor c, String[] from,
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

		/*ViewHolderItem holder = new ViewHolderItem();
		if(convertView==null){
		//ImageView iv;
		convertView = inflate.inflate(R.layout.announcelisting, null);
		holder.imgView = (ImageView) convertView.findViewById(R.id.ivType);
		holder.tView = (com.mobcast.ncp.VerticalTextView)convertView.findViewById(R.id.tvAListDetail);
		convertView.setTag(holder);
		}
		else{
			holder = (ViewHolderItem)convertView.getTag();
		}
		
		cur.moveToPosition(position);
		String type = cur.getString(cur.getColumnIndexOrThrow("type"));
		
		String bgcolor = cur.getString(cur.getColumnIndexOrThrow("read_id"));
		if (type.contentEquals("image")) {
			holder.imgView.setImageResource(R.drawable.image);
		} else if (type.contentEquals("video")) {
			holder.imgView.setImageResource(R.drawable.video);
		} else if (type.contentEquals("audio")) {
			holder.imgView.setImageResource(R.drawable.audio_blue);
		} else {
			holder.imgView.setImageResource(R.drawable.texts);
		}

		//View view = super.getView(position, convertView, parent);
		if (Integer.parseInt(bgcolor) == 0)
			convertView.setBackgroundResource(R.drawable.listgradientunread);
		else convertView.setBackgroundResource(R.drawable.listgradientnormal);
		vtv = (com.mobcast.ncp.VerticalTextView) view
				.findViewById(R.id.tvAListDetail);
		String date = holder.tView.getText().toString().trim();
		Log.d("Date", date);
		vdate vd = new vdate(date);
		holder.tView.setText(vd.getRDate());

		return convertView;*/
		ImageView iv;
		convertView = inflate.inflate(R.layout.announcelisting, null);
		iv = (ImageView) convertView.findViewById(R.id.ivType);
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
		if (Integer.parseInt(bgcolor) == 0)
			view.setBackgroundResource(R.drawable.listgradientunread);

		vtv = (TextView) convertView
				.findViewById(R.id.tvAListDetail);
		
		
//		String date = vtv.getText().toString().trim();
		String date = Utilities.convertDateToIndian(vtv.getText().toString().trim());
//		Log.d("DATE-ANNOUNCE", date);
		vdate vd = new vdate(date);
		vtv.setText(vd.getRDate());

		return view;

	}

}

class ViewHolderItem{
	ImageView imgView;
	com.sanofi.in.mobcast.VerticalTextView tView;
}

class Date {

	public String D, day;
	public int dayno;
	String weekdays[] = { "Sunday", "Monday", "Tuesday", "Wedneesday",
			"Thursday", "Friday", "Saturday" };

	public Date(String D, int dayno1) {
		this.D = D;
		dayno = dayno1;
		if (dayno <= 0)
			dayno = 7 - dayno;
	}

	public String getDay() {

		return weekdays[dayno - 1];
	}

	public String getDate() {
		return D;
	}

}
