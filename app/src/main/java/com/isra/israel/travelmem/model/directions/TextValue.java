package com.isra.israel.travelmem.model.directions;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class TextValue {

    @SerializedName("text")
    @Expose
    private String text;

    @SerializedName("value")
    @Expose
    private long value;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public long getValue() {
        return value;
    }

    public void setValue(long value) {
        this.value = value;
    }
}
