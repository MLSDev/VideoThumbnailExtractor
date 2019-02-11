package com.mlsdev.sample

import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.mlsdev.thumbnailextractor.ThumbnailExtractor
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private var uri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btn_pick_video.setOnClickListener {
            ThumbnailExtractor.with(supportFragmentManager).pickVideo().observe(this, Observer<Uri> { result ->
                if (result != null) {
                    uri = result
                    tv_picked_video_uri.text = result.toString()
                }
            })
        }
        btn_extract_thumbnail.setOnClickListener {
            if (uri != null) {
                ThumbnailExtractor.with(supportFragmentManager).extractThumbnail(uri!!).observe(this, Observer {
                    if (it != null) {
                        iv_thumbnail.setImageBitmap(it)
                    }
                })
            } else {
                Toast.makeText(this, R.string.hint_pick_video, Toast.LENGTH_LONG).show()
            }
        }
    }

}
