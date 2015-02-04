package com.mobcast.calc;

import android.os.Parcel;
import android.os.Parcelable;

public class Product implements Parcelable {
	private String mName;
	private String mMin;
	private String mMax;
	private String mSlab[];

	public Product() {
		super();
		// TODO Auto-generated constructor stub
	}

	public Product(String mName, String mMin, String mMax, String[] mSlab) {
		super();
		this.mName = mName;
		this.mMin = mMin;
		this.mMax = mMax;
		this.mSlab = mSlab;
	}

	public String getmName() {
		return mName;
	}

	public void setmName(String mName) {
		this.mName = mName;
	}

	public String getmMin() {
		return mMin;
	}

	public void setmMin(String mMin) {
		this.mMin = mMin;
	}

	public String getmMax() {
		return mMax;
	}

	public void setmMax(String mMax) {
		this.mMax = mMax;
	}

	public String[] getmSlab() {
		return mSlab;
	}

	public void setmSlab(String[] mSlab) {
		this.mSlab = mSlab;
	}

	protected Product(Parcel in) {
		mName = in.readString();
		mMin = in.readString();
		mMax = in.readString();
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(mName);
		dest.writeString(mMin);
		dest.writeString(mMax);
	}

	@SuppressWarnings("unused")
	public static final Parcelable.Creator<Product> CREATOR = new Parcelable.Creator<Product>() {
		@Override
		public Product createFromParcel(Parcel in) {
			return new Product(in);
		}

		@Override
		public Product[] newArray(int size) {
			return new Product[size];
		}
	};
}
