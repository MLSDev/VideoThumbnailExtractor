package com.mlsdev.thumbnailextractor

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.mlsdev.thumbnailextractor.ThumbnailActivity.Companion.FRAME_POSITION
import com.mlsdev.thumbnailextractor.ThumbnailActivity.Companion.VIDEO_URI
import io.reactivex.subjects.PublishSubject

class ThumbnailExtractor : Fragment() {

    private lateinit var attachedSubject: PublishSubject<Boolean>
    private var thumbnailWidth = 0
    private var thumbnailHeight = 0

    fun pickVideo(): LiveData<Uri> {
        attachedSubject = PublishSubject.create()
        pickVideoLiveData = MutableLiveData()
        checkPickVideo()
        return pickVideoLiveData!!
    }

    fun extractThumbnail(uri: Uri): LiveData<Bitmap> {
        return extractThumbnail(uri, 0, 0)
    }

    fun extractThumbnail(uri: Uri, width: Int, height: Int): LiveData<Bitmap> {
        thumbnailHeight = height
        thumbnailWidth = width
        attachedSubject = PublishSubject.create()
        extractThumbnailLiveData = MutableLiveData()
        checkExtractThumbnail(uri)
        return extractThumbnailLiveData!!
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        attachedSubject.onNext(true)
        attachedSubject.onComplete()
    }

    private fun checkPickVideo() {
        if (!isAdded) {
            attachedSubject.subscribe { requestPickVideo() }
        } else {
            requestPickVideo()
        }
    }

    private fun checkExtractThumbnail(uri: Uri) {
        if (!isAdded) {
            attachedSubject.subscribe { requestExtractThumbnail(uri) }
        } else {
            requestExtractThumbnail(uri)
        }
    }

    private fun requestExtractThumbnail(uri: Uri) {
        startActivityForResult(ThumbnailActivity.getStartIntent(context!!, uri), PICK_THUMBNAIL)
    }

    private fun requestPickVideo() {
        val intent = Intent()
        intent.type = "video/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, getString(R.string.select_video)), PICK_VIDEO)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == PICK_VIDEO) {
                if (data != null) {
                    pickVideoLiveData?.value = data.data
                } else {
                    pickVideoLiveData?.value = null
                }
            } else if (requestCode == PICK_THUMBNAIL) {
                val imageUri = data?.getParcelableExtra(VIDEO_URI) as Uri
                val location = data.getLongExtra(FRAME_POSITION, 0)
                val bitmap = Utils.getBitmapAtFrame(context!!, imageUri, location, thumbnailWidth, thumbnailHeight)
                extractThumbnailLiveData?.value = bitmap
            }
        }
    }

    companion object {

        private const val PICK_VIDEO = 100
        private const val PICK_THUMBNAIL = 101

        private val TAG = ThumbnailExtractor::class.java.simpleName
        private var pickVideoLiveData: MutableLiveData<Uri>? = null
        private var extractThumbnailLiveData: MutableLiveData<Bitmap>? = null

        fun with(fragmentManager: FragmentManager): ThumbnailExtractor {
            var thumbnailExtractor = fragmentManager.findFragmentByTag(ThumbnailExtractor.TAG) as ThumbnailExtractor?
            if (thumbnailExtractor == null) {
                thumbnailExtractor = ThumbnailExtractor()
                fragmentManager.beginTransaction()
                        .add(thumbnailExtractor, ThumbnailExtractor.TAG)
                        .commit()
            }
            return thumbnailExtractor
        }
    }

}

