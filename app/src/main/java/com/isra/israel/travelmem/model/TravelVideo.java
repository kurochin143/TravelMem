package com.isra.israel.travelmem.model;

import android.os.Parcel;

public class TravelVideo extends TravelMedia {

    public TravelVideo() {
        type = TravelMedia.TYPE_VIDEO;
    }

    protected TravelVideo(Parcel in) {
        super(in);
    }

    public static final Creator<TravelVideo> CREATOR = new Creator<TravelVideo>() {
        @Override
        public TravelVideo createFromParcel(Parcel in) {
            return new TravelVideo(in);
        }

        @Override
        public TravelVideo[] newArray(int size) {
            return new TravelVideo[size];
        }
    };
}
