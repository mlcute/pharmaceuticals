package com.mobcast.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mobcast.util.Utilities;
import com.sanofi.in.mobcast.R;

/**
 * @tips  :矩形按钮
 * @date  :2014-11-1
 */
public class ButtonRectangle extends Button {
	
	protected TextView textButton;
	protected int defaultTextColor;
	
	public ButtonRectangle(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	@Override
	protected void onInitDefaultValues(){
		super.onInitDefaultValues();
		textButton = new TextView(getContext());
		defaultTextColor = Color.WHITE;
		rippleSpeed = 5.5f;
		minWidth = 80;
		minHeight = 36;
		backgroundResId = R.drawable.background_button_rectangle;
	}
	
	@Override
	protected void onInitAttributes(AttributeSet attrs) {
		super.onInitAttributes(attrs);
		if (isInEditMode()) {
			// 为了在编译器中预览时�?报空指针，在这里产生一个textView对象。实际中�?会产生的。
			textButton = new TextView(getContext());
		}
		String text = null;
		/**
		 * 设置按钮上的文字内容
		 */
		int textResource = attrs.getAttributeResourceValue(ANDROIDXML,"text",-1);
		if(textResource != -1){
			text = getResources().getString(textResource);
		}else{
			//如果没有文字资�?，也就是@String/xx，那么就设置文字
			text = attrs.getAttributeValue(ANDROIDXML,"text");
		}
		
		/**
		 * 当文字�?为空的时候，TextView设置文字，�?�则�?设置文字
		 */
		if(text != null){
			textButton.setText(text);
		}
		
		/**
		 * 设置textSize
		 */
		String textSize = attrs.getAttributeValue(ANDROIDXML,"textSize");
		if (text != null && textSize != null) {
			textSize = textSize.substring(0, textSize.length() - 2);//12sp->12
			textButton.setTextSize(Float.parseFloat(textSize));
		}
		
		/**
		 * 设置textColor
		 */
		int textColor = attrs.getAttributeResourceValue(ANDROIDXML,"textColor",-1);
		if(text != null && textColor != -1){
			textButton.setTextColor(getResources().getColor(textColor));
		}
		else if(text != null ){
			// 16进制的color
			String color = attrs.getAttributeValue(ANDROIDXML,"textColor");
			if(color != null && !isInEditMode()) {
				textButton.setTextColor(Color.parseColor(color));
			}else {
				textButton.setTextColor(defaultTextColor);
			}
		}
		textButton.setTypeface(null, Typeface.BOLD);
		//textButton.setPadding(5, 5, 5, 5);
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		params.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
		params.setMargins(Utilities.dpToPx(5, getResources()), Utilities.dpToPx(5, getResources()), Utilities.dpToPx(5, getResources()), Utilities.dpToPx(5, getResources()));
		textButton.setLayoutParams(params);
		addView(textButton);
		
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		if (x != -1) {
			Rect src = new Rect(0, 0, getWidth()-Utilities.dpToPx(6, getResources()), getHeight()-Utilities.dpToPx(7, getResources()));
			Rect dst = new Rect(Utilities.dpToPx(6, getResources()), Utilities.dpToPx(6, getResources()), getWidth()-Utilities.dpToPx(6, getResources()), getHeight()-Utilities.dpToPx(7, getResources()));
			canvas.drawBitmap(makeCircle(), src, dst, null);
		}
		invalidate();
	}
	
	// GET AND SET
	
/*	@Override
	public void setEnabled(boolean enabled) {
		// TODO 自动生�?的方法存根
		super.setEnabled(enabled);
		textButton.setEnabled(enabled);
		if (enabled) {
			getBackground().setAlpha(255);
		}else {
			getBackground().setAlpha(25);
		}
		
	}*/
	
	public void setText(final String text){
		textButton.setText(text);
	}
	
	// Set color of text
	public void setTextColor(int color){
		textButton.setTextColor(color);
	}
	
	public void setTextSize(float size) {
		textButton.setTextSize(size);
	}

	@Override
	public TextView getTextView() {
		return textButton;
	}

}
