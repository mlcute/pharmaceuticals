package com.sanofi.in.mobcast;

import java.io.File;
import java.net.URI;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.sanofi.in.mobcast.R;

public class Imageadapter extends SimpleCursorAdapter {

	private Cursor cur;

	LayoutInflater inflate;

	@SuppressWarnings("deprecation")
	public Imageadapter(Context context, int layout, Cursor c, String[] from,
			int[] to, int i) {
		super(context, layout, c, from, to);
		cur = c;
		inflate = LayoutInflater.from(context);

		// TODO Auto-generated constructor stub
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub

		convertView = inflate.inflate(R.layout.uploadlistitem, null);

		ImageView iv = (ImageView) convertView.findViewById(R.id.ivPreview);
		cur.moveToPosition(position);

		String picturePath = cur
				.getString(cur.getColumnIndexOrThrow("address"));

		iv.setImageBitmap(getPreview(picturePath));

		View view = super.getView(position, convertView, parent);
		return view;

	}

	Bitmap getPreview(String str) {
		File image = new File(str);

		BitmapFactory.Options bounds = new BitmapFactory.Options();
		bounds.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(image.getPath(), bounds);
		if ((bounds.outWidth == -1) || (bounds.outHeight == -1))
			return null;

		int originalSize = (bounds.outHeight > bounds.outWidth) ? bounds.outHeight
				: bounds.outWidth;

		BitmapFactory.Options opts = new BitmapFactory.Options();
		opts.inSampleSize = originalSize / 128;
		return BitmapFactory.decodeFile(image.getPath(), opts);
	}

}
