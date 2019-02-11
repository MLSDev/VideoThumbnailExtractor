package com.mlsdev.thumbnailextractor

import android.content.Context
import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Build
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.FrameLayout
import android.widget.RelativeLayout
import androidx.core.content.ContextCompat
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.view_timeline.view.*

class ThumbnailTimeline @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyle: Int = 0
) : FrameLayout(context, attrs) {

    private var frameDimension: Int = 0
    var currentProgress = 0.0
    var currentSeekPosition = 0f
    var seekListener: SeekListener? = null
    var uri: Uri? = null
        set(value) {
            field = value
            field?.let {
                loadThumbnails().subscribe({
                    containerThumbnails.addView(ThumbnailView(context).apply { setImageBitmap(it) })
                }, {

                }, {
                    invalidate()
                })
                viewSeekBar.setDataSource(context, it, 4)
                viewSeekBar.seekTo(currentSeekPosition.toInt())
            }
        }

    init {
        View.inflate(getContext(), R.layout.view_timeline, this)
        frameDimension = context.resources.getDimensionPixelOffset(R.dimen.frame_video_height)
        isFocusable = true
        isFocusableInTouchMode = true
        setBackgroundColor(ContextCompat.getColor(context, R.color.white))
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) elevation = 8f

        val margin = Utils.convertDpToPixel(16f, context).toInt()
        val params = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT
        )
        params.setMargins(margin, 0, margin, 0)
        layoutParams = params
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        when (event!!.action) {
            MotionEvent.ACTION_MOVE -> handleTouchEvent(event)
        }
        return true
    }

    private fun handleTouchEvent(event: MotionEvent) {
        val seekViewWidth = context.resources.getDimensionPixelSize(R.dimen.frame_video_height)
        currentSeekPosition = (Math.round(event.x) - (seekViewWidth / 2)).toFloat()

        val availableWidth = containerThumbnails.width - (layoutParams as RelativeLayout.LayoutParams).marginEnd - (layoutParams as RelativeLayout.LayoutParams).marginStart
        if (currentSeekPosition + seekViewWidth > containerThumbnails.right) {
            currentSeekPosition = (containerThumbnails.right - seekViewWidth).toFloat()
        } else if (currentSeekPosition < containerThumbnails.left) {
            currentSeekPosition = paddingStart.toFloat()
        }

        currentProgress = (currentSeekPosition.toDouble() / availableWidth.toDouble()) * 100
        containerSeekBar.translationX = currentSeekPosition
        viewSeekBar.seekTo(((currentProgress * viewSeekBar.getDuration()) / 100).toInt())

        seekListener?.onVideoSeek(currentProgress)
    }

    private fun loadThumbnails(): Observable<Bitmap> {
        return Observable.create<Bitmap> {
            val metaDataSource = MediaMetadataRetriever()
            metaDataSource.setDataSource(context, uri)

            val videoLength = (metaDataSource.extractMetadata(
                    MediaMetadataRetriever.METADATA_KEY_DURATION).toInt() * 1000).toLong()

            val thumbnailCount = 7

            val interval = videoLength / thumbnailCount

            for (i in 0 until thumbnailCount - 1) {
                val frameTime = i * interval
                var bitmap = metaDataSource.getFrameAtTime(frameTime, MediaMetadataRetriever.OPTION_CLOSEST_SYNC)
                try {
                    val targetWidth: Int
                    val targetHeight: Int
                    if (bitmap.height > bitmap.width) {
                        targetHeight = frameDimension
                        val percentage = frameDimension.toFloat() / bitmap.height
                        targetWidth = (bitmap.width * percentage).toInt()
                    } else {
                        targetWidth = frameDimension
                        val percentage = frameDimension.toFloat() / bitmap.width
                        targetHeight = (bitmap.height * percentage).toInt()
                    }
                    bitmap = Bitmap.createScaledBitmap(bitmap, targetWidth, targetHeight, false)
                } catch (e: Exception) {
                    Log.e(ThumbnailTimeline::class.java.simpleName, e.message)
                }
                it.onNext(bitmap)
            }

            metaDataSource.release()
            it.onComplete()
        }.subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread())
    }
}