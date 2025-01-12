package com.browser.util

import android.app.Activity
import android.net.Uri
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.appcompat.app.AppCompatActivity
import com.canhub.cropper.CropImage
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.CropImageContractOptions
import com.canhub.cropper.CropImageOptions
import com.canhub.cropper.CropImageView

object ImageCropHandler {

    private var callback: Callback? = null

    interface Callback{
        fun onResult(uri: Uri?)
        fun onError(e: Exception)
    }

    private var cropImage: ActivityResultLauncher<CropImageContractOptions>? = null

    fun init(activity: Activity){
        cropImage = (activity as? AppCompatActivity)?.registerForActivityResult(CropImageContract()) { result ->
            when {
                result.isSuccessful -> {
                    Log.d("AIC-Sample", "Original bitmap: ${result.originalBitmap}")
                    Log.d("AIC-Sample", "Original uri: ${result.originalUri}")
                    Log.d("AIC-Sample", "Output bitmap: ${result.bitmap}")
                    Log.d("AIC-Sample", "Output uri: ${result.getUriFilePath(activity)}")
                    handleCropImageResult(result.uriContent)
                }

                result is CropImage.CancelledResult -> showErrorMessage("cropping image was cancelled by the user")
                else -> showErrorMessage("cropping image failed")
            }
        }
    }

    fun startCameraWithoutUri(includeCamera: Boolean, includeGallery: Boolean, isFixCropRatio: Boolean, quality: Int, callback: Callback
    ) {
        this.callback = callback
        cropImage?.launch(
            CropImageContractOptions(
                uri = null,
                cropImageOptions = CropImageOptions().apply {
                    outputCompressQuality = quality
                    fixAspectRatio = isFixCropRatio
                    guidelines = CropImageView.Guidelines.ON
                    imageSourceIncludeCamera = includeCamera
                    imageSourceIncludeGallery = includeGallery
                }
            )
        )
    }

//    private fun startCameraWithUri() {
//        cropImage?.launch(
//            CropImageContractOptions(
//                uri = outputUri,
//                cropImageOptions = CropImageOptions(),
//            ),
//        )
//    }

    private fun showErrorMessage(message: String) {
//        Toast.makeText(context, "Crop failed: $message", Toast.LENGTH_SHORT).show()
        callback?.onError(Exception("Crop failed: $message"))
    }

    private fun handleCropImageResult(uri: Uri?) {
        callback?.onResult(uri)
    }
}