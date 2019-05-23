package com.isra.israel.travelmem.static_helpers;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.net.Uri;

public class VideoStaticHelper {

    public static Bitmap getFrame1(Context context, Uri uri) {
        MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
        mediaMetadataRetriever.setDataSource(context, uri);
        mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
        // TODO VERY LOW get scaled frame
        return mediaMetadataRetriever.getFrameAtTime(1);
    }
}
