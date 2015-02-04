package com.sanofi.in.mobcast;

import java.util.List;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.RotateAnimation;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.sanofi.in.mobcast.R;

public class RecruitAdapter extends ArrayAdapter<RecruitItem> {
	Context context;
	int layoutResourceId;
	List<RecruitItem> items;
	com.sanofi.in.mobcast.VerticalTextView vtv;

	public RecruitAdapter(Context context, int resource, List<RecruitItem> items) {

		super(context, resource, items);

		this.items = items;

	}

	private int[] colors = new int[] { 0xAAFFFFF, 0xAAc0c0c0 };

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		View row = convertView;
		if (row == null) {
			LayoutInflater vi = LayoutInflater.from(getContext());
			row = vi.inflate(R.layout.recruitlist_tem, null);
		}
		RecruitItem p = items.get(position);
		if (p != null) {
			TextView t1 = (TextView) row.findViewById(R.id.recruitItem1);
			TextView t2 = (TextView) row.findViewById(R.id.recruitItem2);

			t1.setText(p.text1);
			t2.setText(p.text2);

			TextView detail = (TextView) row.findViewById(R.id.recruitDetail);
			detail.setText(p.date);
			// if(android.os.Build.VERSION.SDK_INT <= 10){
			// RotateAnimation rotate=
			// (RotateAnimation)AnimationUtils.loadAnimation(getContext(),R.anim.rotateanim);
			// detail.setAnimation(rotate);}

			vtv = (com.sanofi.in.mobcast.VerticalTextView) row
					.findViewById(R.id.recruitDetail);
			Log.v("date ", vtv.getText().toString());
			vdate vd = new vdate(vtv.getText().toString());
			vtv.setText(vd.getRDate());

		}

		int colorPos = position % colors.length;
		// row.setBackgroundColor(colors[colorPos]);

		/*
		 * View view = super.getView(position, convertView, parent); int
		 * colorPos = position % colors.length;
		 * view.setBackgroundColor(colors[colorPos]);
		 */
		return row;

	}
}// end of RecruitAdaptor class
