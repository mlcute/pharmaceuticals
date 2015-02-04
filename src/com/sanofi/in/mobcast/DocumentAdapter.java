package com.sanofi.in.mobcast;

import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.RotateAnimation;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.sanofi.in.mobcast.R;

public class DocumentAdapter extends SimpleCursorAdapter {
	private Cursor cur;
	private LayoutInflater inflate;

	@SuppressWarnings("deprecation")
	public DocumentAdapter(Context context, int layout, Cursor c,
			String[] from, int[] to, int i) {

		super(context, layout, c, from, to);

		cur = c;
		inflate = LayoutInflater.from(context);
		// TODO Auto-generated constructor stub
	}

	private int[] colors = new int[] { 0xAAFFFFF, 0xAAc0c0c0 };

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub

		convertView = inflate.inflate(R.layout.documentlist_item, null);
		ImageView iv = (ImageView) convertView.findViewById(R.id.documentType);
		cur.moveToPosition(position);
		String type = cur.getString(cur.getColumnIndexOrThrow("type"));
		if (type.equals("pdf")) {
			iv.setImageResource(R.drawable.pdf);
		} else if (type.equals("ppt")) {
			iv.setImageResource(R.drawable.powerpoint);
		} else if (type.equals("xls")) {
			iv.setImageResource(R.drawable.excel);
		} else if (type.equals("doc")) {
			iv.setImageResource(R.drawable.word);
		}

		TextView detail = (TextView) convertView
				.findViewById(R.id.documentDetail);
		detail.setText(cur.getString(cur.getColumnIndexOrThrow("detail")));
		// if(android.os.Build.VERSION.SDK_INT <= 10){
		// RotateAnimation rotate=
		// (RotateAnimation)AnimationUtils.loadAnimation(mContext,R.anim.rotateanim);
		// detail.setAnimation(rotate);}

		View view = super.getView(position, convertView, parent);
		// int colorPos = position % colors.length;

		// view.setBackgroundColor(colors[colorPos]);

		try {
			String isread = cur.getString(cur.getColumnIndexOrThrow("read_id"));
			Log.v("isread", isread);
			if (Integer.parseInt(isread) == 0) {
				view.setBackgroundResource(R.drawable.listgradientunread);
			}
		} catch (Exception e) {
			e.printStackTrace();
			Log.v("background color list view", "error");
		}

		vdate vd = new vdate(detail.getText().toString());
		detail.setText(vd.getRDate());

		return view;

	}

	@Override
	public int getCount() {
		return cur.getCount();

	}
}
