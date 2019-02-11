package com.mlsdev.thumbnailextractor

import android.content.Context
import android.util.AttributeSet

class CenterCropVideoView constructor(context: Context, attrs: AttributeSet? = null) : VideoView(context, attrs) {

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, widthMeasureSpec)
    }

}