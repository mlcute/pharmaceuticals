package com.sanofi.in.mobcast;

import java.util.Calendar;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sanofi.in.mobcast.R;

public class SpecialAdapter extends SimpleCursorAdapter {

	private Cursor cur;

	LayoutInflater inflate;
	TextView vtv1;
	com.sanofi.in.mobcast.VerticalTextView vtv;
	LinearLayout bg;

	@SuppressWarnings("deprecation")
	public SpecialAdapter(Context context, int layout, Cursor c, String[] from,
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

		convertView = inflate.inflate(R.layout.eventlisting, null);
		iv = (ImageView) convertView.findViewById(R.id.ivStatus);
		vtv = (com.sanofi.in.mobcast.VerticalTextView) convertView
				.findViewById(R.id.tvEventADetail);

		LinearLayout bg = (LinearLayout) convertView
				.findViewById(R.id.eventlistbg);
		cur.moveToPosition(position);

		String type = cur.getString(cur.getColumnIndexOrThrow("rsvp"));
		if (type.contentEquals("3")) {
			iv.setVisibility(ImageView.GONE);
		} else if (type.contentEquals("1")) {
			iv.setImageResource(R.drawable.yes);
		} else {
			iv.setImageResource(R.drawable.no);
		}

		View view = super.getView(position, convertView, parent);
		vtv1 = (TextView) view.findViewById(R.id.tvListDate);

		// vertical date logic
		// =======================================================
		{
			String fulldate = vtv.getText().toString();

			String year = fulldate.substring(0, 4);
			String day11 = fulldate.substring(8, 10);
			String month11 = (fulldate.substring(5, 7));
			String date = day11 + "-" + month11 + "-" + year;

			vtv.setText(date);

			vdate vd1 = new vdate(date);
			vtv.setText(vd1.getRDate());

		}
		// vertical date logic ends
		// =======================================================

		String month = "";
		int monthno = 0;
//		SU VIKALP
//		int monthno = Integer.parseInt(vtv1.getText().toString()
//				.substring(4, 5));
		try{
			monthno = Integer.parseInt(vtv1.getText().toString()
					.substring(3, 5));
		}catch(Exception e){
			monthno = 0;
		}
//		EU VIKALP
		
		bg = (LinearLayout) view.findViewById(R.id.eventlistbg);
		if (monthno == 1)
			month = "Jan";
		else if (monthno == 2)
			month = "Feb";
		else if (monthno == 3)
			month = "Mar";
		else if (monthno == 4)
			month = "Apr";
		else if (monthno == 5)
			month = "May";
		else if (monthno == 6)
			month = "Jun";
		else if (monthno == 7)
			month = "Jul";
		else if (monthno == 8)
			month = "aug";
		else if (monthno == 9)
			month = "Sep";
		else if (monthno == 10)
			month = "Oct";
		else if (monthno == 11)
			month = "Nov";
		else if (monthno == 12)
			month = "Dec";

		vtv1.setText(vtv1.getText().toString().substring(0, 3) + month);

		try {
			String isread = cur.getString(cur.getColumnIndexOrThrow("read_id"));

			if (Integer.parseInt(isread) == 0) {
				bg.setBackgroundResource(R.drawable.listgradientunread);
				int padding_in_dp = 6; // 6 dps
				final float scale = view.getResources().getDisplayMetrics().density;
				int padding_in_px = (int) (padding_in_dp * scale + 0.5f);
				bg.setPadding(padding_in_px, 0, 0, 0);
			}
		} catch (Exception e) {
			e.printStackTrace();
			Log.v("background color list view", "error");
		}

		return view;

	}

}
