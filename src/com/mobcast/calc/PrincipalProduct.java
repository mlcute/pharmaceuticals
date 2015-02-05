package com.mobcast.calc;

import android.os.Parcel;
import android.os.Parcelable;

public class PrincipalProduct implements Parcelable {
	private String mName;

	protected PrincipalProduct(Parcel in) {
		mName = in.readString();
	}

	public PrincipalProduct() {
		super();
		// TODO Auto-generated constructor stub
	}

	public PrincipalProduct(String mName) {
		super();
		this.mName = mName;
	}

	public String getmName() {
		return mName;
	}

	public void setmName(String mName) {
		this.mName = mName;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(mName);
	}

	@SuppressWarnings("unused")
	public static final Parcelable.Creator<PrincipalProduct> CREATOR = new Parcelable.Creator<PrincipalProduct>() {
		@Override
		public PrincipalProduct createFromParcel(Parcel in) {
			return new PrincipalProduct(in);
		}

		@Override
		public PrincipalProduct[] newArray(int size) {
			return new PrincipalProduct[size];
		}
	};
}
