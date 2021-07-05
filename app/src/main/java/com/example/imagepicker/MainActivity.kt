package com.example.imagepicker

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.activity.result.ActivityResult
import androidx.core.content.FileProvider
import com.example.imagepicker.databinding.ActivityMainBinding
import java.io.File
import java.io.IOException
import androidx.activity.result.contract.ActivityResultContracts.*

class MainActivity : AppCompatActivity() {

    private val TAG = MainActivity::class.java.simpleName

    lateinit var binding: ActivityMainBinding

    private var tempFile: File? = null
    private var photoUri: Uri? = null

    /**
     * https://developer.android.com/training/basics/intents/result
     */
    private val requestActivity =
        registerForActivityResult(StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                val intent = result.data
                intent?.let {
                    val uriList = mutableListOf<Uri>()
                    /**
                     * 앨범 이미지
                     */
                    it.clipData?.let { clipData ->
                        // 여러장 선택 했을때
                        repeat(clipData.itemCount) { idx ->
                            uriList.add(clipData.getItemAt(idx).uri)
                        }
                    }.run {
                        // 한 장 선택 했을때
                        it.data?.let { uri ->
                            uriList.add(uri)
                        }
                    }

                    mAdapter.updateData(uriList)
                    // 앨범 선택시 카메라 촬영 후 남아있던 photoUri 초기화
                    photoUri = null
                }
                /**
                 * 카메라 촬영 이미지
                 */
                photoUri?.let { uri ->
                    val uriList = mutableListOf<Uri>(uri)
                    mAdapter.updateData(uriList)

                }
            }
        }


    lateinit var mAdapter: ImageAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mAdapter = ImageAdapter(emptyList())

        binding.rvMain.apply {
            setHasFixedSize(true)
            adapter = mAdapter
        }

        binding.btnSelectFile.setOnClickListener {
            chooseImagePickerIntent()
        }

    }


    private fun chooseImagePickerIntent() {

        tempFile = createImageFile()
        tempFile?.let { file ->
            photoUri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                FileProvider.getUriForFile(this, "${packageName}.provider", file)
            } else {
                Uri.fromFile(file)
            }
        }

        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE).apply {
            putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
        }

        val albumIntent = Intent().apply {
            type = "image/*"
            action = Intent.ACTION_GET_CONTENT
            putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        }

        val chooserIntent = Intent.createChooser(albumIntent, "Select File")
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, arrayOf(cameraIntent))
        requestActivity.launch(chooserIntent)

    }

    private fun createImageFile(): File? {

        //내부 저장소
        //val path = cacheDir.absolutePath

        //외부 앱 공간 저장소
        val path = getExternalFilesDir(Environment.DIRECTORY_PICTURES)?.absolutePath

        //외부 저장소 (공용 공간)
        //val path = Environment.getExternalStorageDirectory().absolutePath

        val storageDir = File(path)

        if (!storageDir.exists()) {
            storageDir.mkdirs()
        }
        Log.d(TAG, "path 경로 : $path")

        try {
            val tempFile = File.createTempFile("IMG", ".jpg", storageDir)
            Log.d(TAG, "createImageFile 경로 : ${tempFile.absolutePath}")

            return tempFile

        } catch (e: IOException) {
            e.printStackTrace()
        }

        return null
    }

}