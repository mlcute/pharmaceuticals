package com.sanofi.in.mobcast;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import com.sanofi.in.mobcast.R;
import com.mobcast.util.Utilities;


public class TrainingAdapter extends SimpleCursorAdapter {
	private Cursor cur;
	private LayoutInflater inflate;
	ImageView icon;

	public TrainingAdapter(Context context, int layout, Cursor c,
			String[] from, int[] to) {
		super(context, layout, c, from, to);
		cur = c;
		inflate = LayoutInflater.from(context);
		// TODO Auto-generated constructor stub
	}// end of contructor

	private int[] colors = new int[] { 0xAAFFFFF, 0xAAc0c0c0 };

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub

		convertView = inflate.inflate(R.layout.trainlisting, null);

		// if(android.os.Build.VERSION.SDK_INT <= 10){
		// RotateAnimation rotate=
		// (RotateAnimation)AnimationUtils.loadAnimation(mContext,R.anim.rotateanim);
		// detail.setAnimation(rotate);}
		cur.moveToPosition(position);
		String bgcolor = cur.getString(cur.getColumnIndexOrThrow("read_id"));
		String type = cur.getString(cur.getColumnIndexOrThrow("type"));

		View view = super.getView(position, convertView, parent);

		if (Integer.parseInt(bgcolor) == 0)
			view.setBackgroundResource(R.drawable.listgradientunread);
		TextView tv = (TextView) convertView.findViewById(R.id.tvTListDetail);
		icon = (ImageView) convertView.findViewById(R.id.ivTannounce);
//		SU VIKALP DATE ORDER ISSUE
//		vdate vd = new vdate(tv.getText().toString().trim());
		vdate vd = new vdate(Utilities.convertDateToIndian(tv.getText().toString().trim()));
//		EU VIKALP DATE ORDER ISSUE
		tv.setText(vd.getRDate());

		if (type.contentEquals("pdf")) {
			icon.setImageResource(R.drawable.pdf_blue);
		} else if (type.contentEquals("video")) {
			icon.setImageResource(R.drawable.video);
		} else if (type.contentEquals("audio")) {
			icon.setImageResource(R.drawable.audio_blue);
		}else if (type.equals("xls")) {
			icon.setImageResource(R.drawable.excel_blue);
		} else if (type.equals("doc")) {
			icon.setImageResource(R.drawable.word_blue); }
			else  {
			icon.setImageResource(R.drawable.powerpoint_blue);
		}

		// view.setBackgroundColor(colors[colorPos]);

		return view;

	}// end of getView

}
