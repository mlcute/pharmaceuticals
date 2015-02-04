package com.mobcast.view;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mobcast.util.Utilities;
import com.nineoldandroids.view.ViewHelper;
import com.sanofi.in.mobcast.R;

public class Slider extends CustomView {

	// Event when slider change value
	public interface OnValueChangedListener {
		public void onValueChanged(int value);
	}

	private Ball ball;
	public NumberIndicator numberIndicator;
	private boolean isDisableTouchMove = true;

	boolean showNumberIndicator = false;
	boolean press = false;

	int value = 0;
	int max = 100;
	int min = 0;

	private OnValueChangedListener onValueChangedListener;

	public Slider(Context context, AttributeSet attrs) {
		super(context, attrs);
		setAttributes(attrs);
	}

	@Override
	protected void onInitDefaultValues() {
		minWidth = 80;// size of view
		minHeight = 48;
		backgroundColor = Color.parseColor("#4CAF50");
		backgroundResId = R.drawable.background_transparent;
	}

	@Override
	protected void setAttributes(AttributeSet attrs) {
		super.setAttributes(attrs);
		if (!isInEditMode()) {
			getBackground().setAlpha(0);
		}
		showNumberIndicator = attrs.getAttributeBooleanValue(MATERIALDESIGNXML,
				"showNumberIndicator", false);
		min = attrs.getAttributeIntValue(MATERIALDESIGNXML, "min", 0);
		max = attrs.getAttributeIntValue(MATERIALDESIGNXML, "max", 100);// max >
																		// min
		value = attrs.getAttributeIntValue(MATERIALDESIGNXML, "value", min);

		float size = 20;
		String thumbSize = attrs.getAttributeValue(MATERIALDESIGNXML,
				"thumbSize");
		if (thumbSize != null) {
			size = Utilities.dipOrDpToFloat(thumbSize);
		}

		ball = new Ball(getContext());
		setBallParams(size);
		addView(ball);

		// Set if slider content number indicator
		if (showNumberIndicator) {
			if (!isInEditMode()) {
				numberIndicator = new NumberIndicator(getContext());
			}
		}
	}

	private void setBallParams(float size) {
		RelativeLayout.LayoutParams params = new LayoutParams(Utilities.dpToPx(
				size, getResources()), Utilities.dpToPx(size, getResources()));
		params.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);
		ball.setLayoutParams(params);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		if (!placedBall) {
			placeBall();
		}
		if (value == min) {
			// Crop line to transparent effect
			Bitmap bitmap = Bitmap.createBitmap(canvas.getWidth(),
					canvas.getHeight(), Bitmap.Config.ARGB_8888);
			Canvas temp = new Canvas(bitmap);
			Paint paint = new Paint();
			paint.setColor(Color.parseColor("#B0B0B0"));
			paint.setStrokeWidth(Utilities.dpToPx(2, getResources()));
			temp.drawLine(getHeight() / 2, getHeight() / 2, getWidth()
					- getHeight() / 2, getHeight() / 2, paint);
			Paint transparentPaint = new Paint();
			transparentPaint.setColor(getResources().getColor(
					android.R.color.transparent));
			transparentPaint.setXfermode(new PorterDuffXfermode(
					PorterDuff.Mode.CLEAR));
			temp.drawCircle(ViewHelper.getX(ball) + ball.getWidth() / 2,
					ViewHelper.getY(ball) + ball.getHeight() / 2,
					ball.getWidth() / 2, transparentPaint);

			canvas.drawBitmap(bitmap, 0, 0, new Paint());
		} else {
			Paint paint = new Paint();
			paint.setColor(Color.parseColor("#B0B0B0"));
			paint.setStrokeWidth(Utilities.dpToPx(2, getResources()));
			canvas.drawLine(getHeight() / 2, getHeight() / 2, getWidth()
					- getHeight() / 2, getHeight() / 2, paint);
			paint.setColor(backgroundColor);
			float division = (ball.xFin - ball.xIni) / (max - min);
			int value = this.value - min;
			canvas.drawLine(getHeight() / 2, getHeight() / 2, value * division
					+ getHeight() / 2, getHeight() / 2, paint);
			// init ball's X
			ViewHelper.setX(ball,
					value * division + getHeight() / 2 - ball.getWidth() / 2);
			ball.changeBackground();
		}
		// if (press && !showNumberIndicator) {
		// /**
		// * 如果按�?，在�?显示指示器的状�?下，会将ball大�
		// * �?扩大�?�给用户�??馈。 最�?�一个�?�数：getHeight() /
		// * x，表示的是按下去�?�显示的圆�?�的�?�径
		// * 如果x=2，那么按下�?�圆�?�的直径就是这个view的高
		// * 如果x=3，按下�?�显示圆�?�的�?�径就是这个view高的�
		// * ��分之一
		// */
		// Paint paint = new Paint();
		// paint.setColor(backgroundColor);
		// paint.setAntiAlias(true);
		// canvas.drawCircle(ViewHelper.getX(ball) + ball.getWidth() / 2,
		// getHeight() / 2, getHeight() / 3, paint);
		// }
		invalidate();
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		isLastTouch = true;
		if (isEnabled()) {
			if (/*
				 * event.getAction() == MotionEvent.ACTION_DOWN ||
				 */event.getAction() == MotionEvent.ACTION_MOVE) {
				if (numberIndicator != null
						&& numberIndicator.isShowing() == false)
					numberIndicator.show();// �?��?一按下就会冒出指示器
				if ((event.getX() <= getWidth() && event.getX() >= 0)) { //isDisableTouchMove = false ;
					press = true;
					// calculate value
					int newValue = 0;
					float division = (ball.xFin - ball.xIni) / (max - min);
					if (event.getX() > ball.xFin) {
						newValue = max;
					} else if (event.getX() < ball.xIni) {
						newValue = min;
					} else {
						newValue = min
								+ (int) ((event.getX() - ball.xIni) / division);
					}
					if (value != newValue) {
						value = newValue;
						if (onValueChangedListener != null)
							onValueChangedListener.onValueChanged(newValue);
					}
					// move ball indicator
					float x = event.getX();
					x = (x < ball.xIni) ? ball.xIni : x;
					x = (x > ball.xFin) ? ball.xFin : x;
					ViewHelper.setX(ball, x);
					ball.changeBackground();

					// If slider has number indicator
					if (numberIndicator != null) {
						// move number indicator
						numberIndicator.indicator.x = x;
						// 指示器起始的y�??标是当�?控件的顶部Y�??标-当�?控件高度的一�?�，就等于从空间的垂直中心开始。
						numberIndicator.indicator.finalY = Utilities
								.getRelativeTop(this) - getHeight();
						numberIndicator.indicator.finalSize = getHeight() / 2;
						numberIndicator.numberIndicator.setText("");
					}

				} else {
					press = false;
					isLastTouch = false;
					if (numberIndicator != null)
						numberIndicator.dismiss();

				}

			} /*else if (event.getAction() == MotionEvent.ACTION_UP) {
				if (numberIndicator != null)
					numberIndicator.dismiss();
				isLastTouch = false;
				press = false;
				if ((event.getX() <= getWidth() && event.getX() >= 0)) {

				}
			}*/
		}
		return true;
	}

	public void setDisableTouchMove(boolean isDisableTouchMove){
		this.isDisableTouchMove = isDisableTouchMove;
	}
	
	private void placeBall() {
		ViewHelper.setX(ball, getHeight() / 2 - ball.getWidth() / 2);
		ball.xIni = ViewHelper.getX(ball);
		ball.xFin = getWidth() - getHeight() / 2 - ball.getWidth() / 2;
		ball.xCen = getWidth() / 2 - ball.getWidth() / 2;
		placedBall = true;
	}

	// GETERS & SETTERS

	public OnValueChangedListener getOnValueChangedListener() {
		return onValueChangedListener;
	}

	public void setOnValueChangedListener(
			OnValueChangedListener onValueChangedListener) {
		this.onValueChangedListener = onValueChangedListener;
	}

	public void setThumbSize(float size) {
		setBallParams(size);
	}

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		setValue(value, false);
	}

	/**
	 * @param value
	 * @param inRunnable
	 *            如果为true表示在runnable中跟新进度，�?�则在主�
	 *            �程中更新
	 */
	public void setValue(int value, boolean inRunnable) {
		if (value <= min) {
			value = min;
		}
		if (value >= max) {
			value = max;
		}
		setValueInRunnable(value, inRunnable);
	}

	private void setValueInRunnable(final int value, final boolean inRunnable) {
		if (placedBall == false && inRunnable == true)
			post(new Runnable() {
				@Override
				public void run() {
					setValue(value, inRunnable);
				}
			});
		else {
			this.value = value;
			float division = (ball.xFin - ball.xIni) / max;
			ViewHelper.setX(ball,
					value * division + getHeight() / 2 - ball.getWidth() / 2);
			ball.changeBackground();
		}
	}

	public int getMax() {
		return max;
	}

	public void setMax(int max) {
		this.max = max;
	}

	public int getMin() {
		return min;
	}

	public void setMin(int min) {
		this.min = min;
	}

	public boolean isShowNumberIndicator() {
		return showNumberIndicator;
	}

	public void showNumberIndicator(boolean showNumberIndicator) {
		this.showNumberIndicator = showNumberIndicator;
		if (!isInEditMode()) {
			numberIndicator = (showNumberIndicator) ? new NumberIndicator(
					getContext()) : null;
		}
	}

	@Override
	public void setBackgroundColor(int color) {
		backgroundColor = color;
		if (isEnabled()) {
			beforeBackground = backgroundColor;
		}
	}

	private boolean placedBall = false;

	private class Ball extends View {

		private float xIni, xFin, xCen;

		public Ball(Context context) {
			super(context);
			if (!isInEditMode()) {
				setBackgroundResource(R.drawable.background_switch_ball_uncheck);
			} else {
				setBackgroundResource(android.R.drawable.radiobutton_off_background);
			}
		}

		public void changeBackground() {
			if (!isInEditMode()) {
				if (value != min) {
					setBackgroundResource(R.drawable.background_checkbox);
					LayerDrawable layer = (LayerDrawable) getBackground();
					GradientDrawable shape = (GradientDrawable) layer
							.findDrawableByLayerId(R.id.shape_bacground);
					shape.setColor(backgroundColor);
				} else {
					setBackgroundResource(R.drawable.background_switch_ball_uncheck);
				}
			}
		}

	}

	// Slider Number Indicator

	public class NumberIndicator extends Dialog {

		private Indicator indicator;
		private TextView numberIndicator;

		public NumberIndicator(Context context) {
			super(context, android.R.style.Theme_Translucent);
		}

		@Override
		protected void onCreate(Bundle savedInstanceState) {
			requestWindowFeature(Window.FEATURE_NO_TITLE);
			getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
					WindowManager.LayoutParams.FLAG_FULLSCREEN);
			super.onCreate(savedInstanceState);
			setContentView(R.layout.number_indicator_spinner);
			setCanceledOnTouchOutside(false);

			RelativeLayout content = (RelativeLayout) this
					.findViewById(R.id.number_indicator_spinner_content);
			indicator = new Indicator(this.getContext());
			content.addView(indicator);

			numberIndicator = new TextView(getContext());
			numberIndicator.setTextColor(Color.WHITE);
			numberIndicator.setGravity(Gravity.CENTER);
			content.addView(numberIndicator);

			indicator.setLayoutParams(new RelativeLayout.LayoutParams(
					RelativeLayout.LayoutParams.MATCH_PARENT,
					RelativeLayout.LayoutParams.MATCH_PARENT));
		}

		@Override
		public void dismiss() {
			super.dismiss();
			try {
				indicator.y = 0;
				indicator.size = 0;
				indicator.animate = true;
			} catch (Exception e) {

			}
		}

		@Override
		public void onBackPressed() {

		}

	}

	private class Indicator extends RelativeLayout {

		// Position of number indicator
		private float x = 0;
		private float y = 0;
		// Size of number indicator
		private float size = 0;

		// Final y position after animation
		private float finalY = 0;
		// Final size after animation
		private float finalSize = 0;

		private boolean animate = true;

		private boolean numberIndicatorResize = false;

		public Indicator(Context context) {
			super(context);
			setBackgroundColor(getResources().getColor(
					android.R.color.transparent));
		}

		@Override
		protected void onDraw(Canvas canvas) {
			super.onDraw(canvas);

			if (numberIndicatorResize == false) {
				RelativeLayout.LayoutParams params = (LayoutParams) numberIndicator.numberIndicator
						.getLayoutParams();
				params.height = (int) finalSize * 2;
				params.width = (int) finalSize * 2;
				numberIndicator.numberIndicator.setLayoutParams(params);
			}

			Paint paint = new Paint();
			paint.setAntiAlias(true);
			paint.setColor(backgroundColor);
			if (animate) {
				if (y == 0)
					y = finalY + finalSize * 2;
				y -= Utilities.dpToPx(6, getResources());
				size += Utilities.dpToPx(2, getResources());
			}
			canvas.drawCircle(
					ViewHelper.getX(ball)
							+ Utilities.getRelativeLeft((View) ball.getParent())
							+ ball.getWidth() / 2, y, size, paint);
			if (animate && size >= finalSize)
				animate = false;
			if (animate == false) {
				ViewHelper.setX(
						numberIndicator.numberIndicator,
						(ViewHelper.getX(ball)
								+ Utilities.getRelativeLeft((View) ball
										.getParent()) + ball.getWidth() / 2)
								- size);
				ViewHelper.setY(numberIndicator.numberIndicator, y - size);
				numberIndicator.numberIndicator.setText(value + "");
			}
			invalidate();
		}

	}

}