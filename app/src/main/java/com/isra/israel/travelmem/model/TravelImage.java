package com.isra.israel.travelmem.model;

import android.os.Parcel;
import android.os.Parcelable;

public class TravelImage extends TravelMedia {

    public TravelImage() {
        type = TravelMedia.TYPE_IMAGE;
    }

    protected TravelImage(Parcel in) {
        super(in);
    }

    public static final Creator<TravelImage> CREATOR = new Creator<TravelImage>() {
        @Override
        public TravelImage createFromParcel(Parcel in) {
            return new TravelImage(in);
        }

        @Override
        public TravelImage[] newArray(int size) {
            return new TravelImage[size];
        }
    };

}
