package com.dicoding.asclepius.view

import android.annotation.SuppressLint
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.dicoding.asclepius.databinding.ActivityResultBinding
import com.dicoding.asclepius.helper.ImageClassifierHelper
import org.tensorflow.lite.task.vision.classifier.Classifications
import java.text.SimpleDateFormat
import java.util.Date

class ResultActivity : AppCompatActivity() {
    private lateinit var binding: ActivityResultBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val image = intent.getStringExtra(IMAGE_URI)
        if (image != null) {
            val imageUri = Uri.parse(image)
            displayImage(imageUri)

            val imageClassifierHelper = ImageClassifierHelper(
                context = this,
                classifierListener = object : ImageClassifierHelper.ClassifierListener {
                    override fun onError(error: String) {
                        Log.e(TAG, "Error: $error")
                    }

                    override fun onResults(result: List<Classifications>?, inferenceTime: Long) {
                        result?.let { showResult(it) }
                    }
                }
            )

            try {
                imageClassifierHelper.classifyStaticImage(imageUri)
            } catch (e: Exception) {
                Log.e(TAG, "Error: $e")
            }
        } else {
            Log.e(TAG, "Image URI is null")
            finish()
        }
    }
//=============================================================================================

    @SuppressLint("SetTextI18n", "SimpleDateFormat")
    private fun showResult(result: List<Classifications>) {
        val topResult = result[0]
        val label = topResult.categories[0].label
        val score = topResult.categories[0].score

        fun Float.formatToString(): String {
            return String.format("%.2f%%", this * 100)
        }

        val formattedDate = SimpleDateFormat("yyyy-MM-dd").format(Date())
        val formattedTime = SimpleDateFormat("HH:mm:ss").format(Date())
        val dateNow = "$formattedDate  $formattedTime"

        binding.resultText.text = "$label ${score.formatToString()}"
    }

    private fun displayImage(uri: Uri) {
        Log.d(TAG, "Display Image: $uri")
        binding.resultImage.setImageURI(uri)
    }

    companion object {
        const val IMAGE_URI = "imgUri"
        const val TAG = "showImage"
    }
}
