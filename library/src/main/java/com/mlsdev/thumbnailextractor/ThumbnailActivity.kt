package com.mlsdev.thumbnailextractor

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.ViewTreeObserver
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_thumbnail.*

class ThumbnailActivity : AppCompatActivity() {

    companion object {
        const val FRAME_POSITION = "FramePosition"
        const val VIDEO_URI = "VideoUri"

        fun getStartIntent(context: Context, uri: Uri, thumbnailPosition: Long = 0): Intent {
            val intent = Intent(context, ThumbnailActivity::class.java)
            intent.putExtra(VIDEO_URI, uri)
            intent.putExtra(FRAME_POSITION, thumbnailPosition)
            return intent
        }
    }

    private lateinit var videoUri: Uri

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_thumbnail)
        title = getString(R.string.picker_title)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        videoUri = intent.getParcelableExtra(VIDEO_URI) as Uri
        setupVideoContent()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.thumbnail, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            R.id.action_menu_done -> {
                finishWithData()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun setupVideoContent() {
        viewVideoThumbnail.setDataSource(this, videoUri)
        viewThumbnailTimeline.seekListener = seekListener
        viewThumbnailTimeline.currentSeekPosition = intent.getLongExtra(FRAME_POSITION, 0).toFloat()
        viewThumbnailTimeline.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                viewThumbnailTimeline.viewTreeObserver.removeOnGlobalLayoutListener(this)
                viewThumbnailTimeline.uri = videoUri
            }
        })
    }

    private fun finishWithData() {
        val intent = Intent()
        intent.putExtra(FRAME_POSITION, ((viewVideoThumbnail.getDuration() / 100) * viewThumbnailTimeline.currentProgress).toLong() * 1000)
        intent.putExtra(VIDEO_URI, videoUri)
        setResult(RESULT_OK, intent)
        finish()
    }

    private val seekListener = object : SeekListener {
        override fun onVideoSeek(percentage: Double) {
            viewVideoThumbnail.seekTo((percentage.toInt() * viewVideoThumbnail.getDuration()) / 100)
        }
    }
}