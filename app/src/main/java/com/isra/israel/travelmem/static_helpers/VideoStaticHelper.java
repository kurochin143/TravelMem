package com.isra.israel.travelmem.static_helpers;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.net.Uri;

public class VideoStaticHelper {

    public static Bitmap getFrameAtHalf(Context context, Uri uri) {
        MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
        mediaMetadataRetriever.setDataSource(context, uri);
        String durationStr = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
        long duration = Long.parseLong(durationStr);
        long half = duration / 2;
        // TODO VERY LOW get scaled frame
        return mediaMetadataRetriever.getFrameAtTime(half * 1000);
    }
}
