# RxImagePicker

ThumbnailExtractor is an easy way to extract and pick thumbnail image from Video frames

## Setup

To use this library your ` minSdkVersion` must be >= 17.

In your build.gradle :

```gradle
dependencies {
    implementation 'com.mlsdev.thumbnailextractor:library:1.0.0'    
}
```

## Example

Pick video from gallery:

```kotlin
ThumbnailExtractor.with(supportFragmentManager).pickVideo().observe(this, Observer<Uri> { result ->
                //receive video URI
            })
```

Extract image from video:

```kotlin
ThumbnailExtractor.with(supportFragmentManager).extractThumbnail(videoUri).observe(this, Observer {
                    //receive frame bitmap
                })
```

## Sample App
<img src="https://cloud.githubusercontent.com/assets/1778155/11761109/cb70a420-a0bd-11e5-8cf1-e2b172745eab.png" width="400">

## Authors
* [Sergey Glebov](mailto:glebov@mlsdev.com) ([frederikos][github-frederikos]), MLSDev 

## License
VideoThumbnailExtractor is released under the MIT license. See LICENSE for details.

## About MLSDev

[<img src="https://cloud.githubusercontent.com/assets/1778155/11761239/ccfddf60-a0c2-11e5-8f2a-8573029ab09d.png" alt="MLSDev.com">][mlsdev]

VideoThumbnailExtractor is maintained by MLSDev, Inc. We specialize in providing all-in-one solution in mobile and web development. Our team follows Lean principles and works according to agile methodologies to deliver the best results reducing the budget for development and its timeline. 

Find out more [here][mlsdev] and don't hesitate to [contact us][contact]!

[mlsdev]: http://mlsdev.com
[contact]: http://mlsdev.com/contact_us
[github-frederikos]: https://github.com/frederikos

