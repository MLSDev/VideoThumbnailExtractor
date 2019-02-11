package com.mlsdev.thumbnailextractor

import android.content.Context
import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.util.DisplayMetrics

object Utils {
    fun convertDpToPixel(dp: Float, context: Context): Float {
        return dp * (context.resources.displayMetrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)
    }

    fun getBitmapAtFrame(context: Context, uri: Uri, frameTime: Long, width: Int, height: Int): Bitmap {
        val mediaMetadataRetriever = MediaMetadataRetriever()
        mediaMetadataRetriever.setDataSource(context, uri)
        var bitmap = mediaMetadataRetriever.getFrameAtTime(frameTime, MediaMetadataRetriever.OPTION_CLOSEST_SYNC)
        try {
            if (width > 0) {
                bitmap = Bitmap.createScaledBitmap(bitmap, width, height, false)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return bitmap
    }
}