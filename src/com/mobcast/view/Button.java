package com.mobcast.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.util.AttributeSet;
import android.widget.TextView;

import com.mobcast.util.Utilities;
import com.sanofi.in.mobcast.R;

public abstract class Button extends RippleView {

	public Button(Context context, AttributeSet attrs) {
		super(context, attrs);
		onInitAttributes(attrs);
	}
	
	@Override
	protected void onInitDefaultValues() {
		backgroundColor = Color.parseColor("#2196f3");// 默认的背景色，�?色
		///beforeBackground = backgroundColor;// error
	}
	
	protected void onInitAttributes(AttributeSet attrs) {
		setAttributes(attrs);
	}
	
	// ### RIPPLE EFFECT ###
	
	/**
	 * @return 涟漪的bitmap
	 */
	public Bitmap makeCircle() {
		// 画涟漪时�?考虑到按钮的边界区域，�?�?把按钮的阴影边界也填满了
		Bitmap output = Bitmap.createBitmap(
				getWidth() - Utilities.dpToPx(6, getResources()), 
				getHeight() - Utilities.dpToPx(7, getResources()), Config.ARGB_8888);
		return makeCircleFromBitmap(output);
	}
	
	// Set color of background
	@Override
	public void setBackgroundColor(int color) {
		backgroundColor = color;
		if (isEnabled()) {
			beforeBackground = backgroundColor;
		}
		try {
			LayerDrawable layer = (LayerDrawable) getBackground();
			// �?个按钮的框架都是由drawable中的xml文件制定的，xml文件中都有一个item的id�?�：shape_bacground
			GradientDrawable shape = (GradientDrawable) layer.findDrawableByLayerId(R.id.shape_bacground);
			/**
			 * 给这个图片设置背景色，因为图片的主体是�?明的所以�?�以直接显示背景色
			 * 效果就是一个�?明但有阴影的框架下有了背景色，这样的方�?�?�以方便的设置�?�?�颜色的按钮，让按钮看起�?�还是浑然一体
			 */
			shape.setColor(backgroundColor);
			/**
			 * 当�?新设定背景色�?�，�?检查涟漪颜色。如果已�?设定了涟漪颜色，那么就用之�?的。如果没设定就�?新生�?
			 */
			if (!settedRippleColor) {
				rippleColor = makePressColor(255);
			}
		} catch (Exception ex) {
			// Without bacground
		}
	}

	abstract public TextView getTextView();
	
}
