package com.example.coralspawncounter

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Rect
import android.media.MediaMetadataRetriever
import android.os.Bundle
import android.os.Environment
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.coralspawncounter.databinding.ActivityVideoBinding
import org.opencv.android.OpenCVLoader
import org.opencv.android.Utils
import org.opencv.core.*
import org.opencv.imgproc.Imgproc
import java.io.File

class VideoActivity : AppCompatActivity() {
    private lateinit var viewBinding: ActivityVideoBinding
    private lateinit var counter: SpawnCounter

    init {
        // OpenCV initialization
        OpenCVLoader.initDebug()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        counter = SpawnCounter()
        viewBinding = ActivityVideoBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)
        if (allPermissionsGranted()) {
            viewBinding.surfaceView.post { runVideo() }
        }
        else {
            ActivityCompat.requestPermissions(
                this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS
            )
        }
    }

    private fun runVideo() {
        val retriever = MediaMetadataRetriever()
        val path = Environment.getExternalStoragePublicDirectory(
            Environment.DIRECTORY_DOWNLOADS
        )
        val file = File(path, "20220831_103240.mp4")
        retriever.setDataSource(file.absolutePath);
        var bmp = retriever.getFrameAtIndex(100)
        if (bmp != null) {
            val mat = Mat()
            Utils.bitmapToMat(bmp, mat)
            counter.nextImage(mat, mat)
            Utils.matToBitmap(mat, bmp)
            drawImage(bmp)
        }
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all { ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED}

    companion object {
        private const val REQUEST_CODE_PERMISSIONS = 10
        private val REQUIRED_PERMISSIONS =
            mutableListOf (
                Manifest.permission.READ_EXTERNAL_STORAGE,
            ).toTypedArray()
    }

    private fun drawImage(bitmap: Bitmap) {
        val canvas = viewBinding.surfaceView.holder.lockCanvas()
        val dest = Rect(0, 0, canvas.width, canvas.height)
        canvas.drawBitmap(bitmap, null, dest, null)
        viewBinding.surfaceView.holder.unlockCanvasAndPost(canvas)
    }
}
