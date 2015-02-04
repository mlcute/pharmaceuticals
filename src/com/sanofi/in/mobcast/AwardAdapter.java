package com.sanofi.in.mobcast;

import java.io.File;
import java.util.Calendar;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.sanofi.in.mobcast.R;
import com.mobcast.util.Utilities;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

public class AwardAdapter extends SimpleCursorAdapter {
	TextView txtView,txtView2,txtView3,txtView4;
	private Cursor cur;
	private LayoutInflater inflate;
	@SuppressWarnings("deprecation")
	public AwardAdapter(Context context, int layout, Cursor c, String[] from,
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
		/*
		 * ImageView iv; convertView = inflate.inflate(R.layout.awardlist_item,
		 * null); iv = (ImageView) convertView.findViewById(R.id.awardImage);
		 * cur.moveToPosition(position); String imagePath = cur
		 * .getString(cur.getColumnIndexOrThrow("imagePath")); String bgcolor =
		 * cur.getString(cur.getColumnIndexOrThrow("read_id")); Bitmap bmp =
		 * BitmapFactory.decodeFile(imagePath); // ThumbnailUtils tu = new
		 * ThumbnailUtils(bmp, bmp.getWidth()/10, // bmp.getHeight()/10);
		 * ThumbnailUtils tu = new ThumbnailUtils();
		 * Log.d("View Award","get view"); bmp = tu.extractThumbnail(bmp,
		 * bmp.getWidth() / 10, bmp.getHeight() / 10);
		 * 
		 * 
		 * byte[] imageData = null;
		 * 
		 * try {
		 * 
		 * final int THUMBNAIL_SIZE = 64;
		 * 
		 * FileInputStream fis = new FileInputStream(imagePath); Bitmap
		 * imageBitmap = BitmapFactory.decodeStream(fis);
		 * 
		 * imageBitmap = Bitmap.createScaledBitmap(imageBitmap, 160, 90, false);
		 * 
		 * ByteArrayOutputStream baos = new ByteArrayOutputStream();
		 * imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
		 * imageData = baos.toByteArray(); iv.setImageBitmap(imageBitmap); }
		 * catch(Exception ex) {
		 * 
		 * }
		 * 
		 * 
		 * 
		 * iv.setImageBitmap(bmp); TextView detail = (TextView)
		 * convertView.findViewById(R.id.awardDetail);
		 * detail.setText(cur.getString(cur.getColumnIndexOrThrow("detail")));
		 * vdate vd = new vdate(detail.getText().toString());
		 * detail.setText(vd.getRDate());
		 * 
		 * // if(android.os.Build.VERSION.SDK_INT <= 10){ // RotateAnimation
		 * rotate= //
		 * (RotateAnimation)AnimationUtils.loadAnimation(mContext,R.anim
		 * .rotateanim); // detail.setAnimation(rotate);}
		 * 
		 * View view = super.getView(position, convertView, parent); if
		 * (Integer.parseInt(bgcolor) == 0)
		 * view.setBackgroundResource(R.drawable.listgradientunread); //
		 * view.setBackgroundColor(colors[colorPos]); return view;
		 */
		ImageView imgview;
			convertView = inflate.inflate(R.layout.awardlist_item, null);
			
			
			imgview = (ImageView) convertView
					.findViewById(R.id.awardImage);
			txtView = (TextView) convertView
					.findViewById(R.id.awardDetail);
			txtView2 = (TextView)convertView
					.findViewById(R.id.awardTitle);
			txtView3 = (TextView)convertView
					.findViewById(R.id.awardName);
			txtView4 = (TextView)convertView
					.findViewById(R.id.awardSum);
			
			
			//view = super.getView(position, convertView, parent);
			
		final ProgressBar spinner=(ProgressBar) convertView.findViewById(R.id.loading);
		cur.moveToPosition(position);
		String imagePath = cur
				.getString(cur.getColumnIndexOrThrow("imagePath"));
		Log.d("image path",imagePath);
		String fileLink = cur
				.getString(cur.getColumnIndexOrThrow(AnnounceDBAdapter.KEY_FILELINK));
		Log.d("File link",fileLink);

		// Bitmap bmp = BitmapFactory.decodeFile(imagePath);
		// ThumbnailUtils tu = new ThumbnailUtils();
		// Log.d("View Award", "get view");
		// bmp = tu.extractThumbnail(bmp, bmp.getWidth() / 10,
		// bmp.getHeight() / 10);
		// viewimgview.setImageBitmap(bmp);

		ImageLoader loader = ImageLoader.getInstance();
		loader.init(ImageLoaderConfiguration.createDefault(mContext));
		File f = new File(imagePath);
		if(f.exists()){
		loader.displayImage("file://" + imagePath, imgview,
				getDisplayOptions(),new SimpleImageLoadingListener()
		{
			public void onLoadingStarted(String imageUri, View view) {		
				spinner.setVisibility(View.VISIBLE);
			}
			public void onLoadingComplete(String imageUri, View view,Bitmap loadedImage) {
				spinner.setVisibility(View.GONE);
			}
		});}
		else{
			//spinner.setVisibility(View.INVISIBLE);
			//imgview.setVisibility(View.INVISIBLE);
			
			loader.displayImage(fileLink, imgview,
					getDisplayOptions(),new SimpleImageLoadingListener()
			{
				public void onLoadingStarted(String imageUri, View view) {		
					spinner.setVisibility(View.VISIBLE);
				}
				public void onLoadingComplete(String imageUri, View view,Bitmap loadedImage) {
					spinner.setVisibility(View.GONE);
				}
			});
			
			
		}
		Calendar c = Calendar.getInstance();
		
		int day = c.get(Calendar.DAY_OF_MONTH);
		int month = c.get(Calendar.MONTH)+1;
		int year = c.get(Calendar.YEAR);
		View view = super.getView(position, convertView, parent);
		
		String rdate =cur.getString(cur
				.getColumnIndexOrThrow("date")); //ADDED VIKALP AWARD RDATE
		
		String date =cur.getString(cur
				.getColumnIndexOrThrow("detail")); /*String.valueOf(day)+"-"+String.valueOf(month)+"-"+String.valueOf(year);*/
		Log.d("DATE", date);
		Log.d("RDATE", rdate);
//		SU VIKALP DATE ORDER ISSUE
//		vdate vd = new vdate(Utilities.convertDateToIndian(date.toString().trim()));
		vdate vd = new vdate(Utilities.convertDateToIndian(rdate.toString().trim())); //ADDED VIKALP AWARD RDATE
//		EU VIKALP DATE ORDER ISSUE
		txtView.setText(vd.getRDate());
		
		txtView2.setText(cur.getString(cur.getColumnIndexOrThrow("title")));
		txtView3.setText(cur.getString(cur.getColumnIndexOrThrow("name")));
		txtView4.setText(cur.getString(cur.getColumnIndexOrThrow("summary")));
		
		
		String bgcolor = cur.getString(cur.getColumnIndexOrThrow("read_id"));
		Log.d("Color", "Color "+bgcolor+" "+cur.getString(cur
				.getColumnIndexOrThrow("detail")));
		if (Integer.parseInt(bgcolor) == 0)
			convertView.setBackgroundResource(R.drawable.listgradientunread);
		else convertView.setBackgroundResource(R.drawable.listgradientnormal);
		// view.setBackgroundColor(colors[colorPos]);
		return view;
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		cur.moveToPosition(position);
		return cur;
		
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public int getCount() {
		return cur.getCount();

	}

	class ViewHolderItem {
		ImageView imgview;
		TextView txtView;
		TextView txtView2;
		TextView txtView3;
		TextView txtView4;
		View view;
	}

	private DisplayImageOptions getDisplayOptions() {
		DisplayImageOptions options = new DisplayImageOptions.Builder()
				.cacheInMemory(true).resetViewBeforeLoading(true).build();
		return options;
	}
}