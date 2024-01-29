package com.akhil.demotask

import android.Manifest
import android.R.attr.path
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.provider.MediaStore.Images
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.akhil.demotask.databinding.ActivityMainBinding
import com.akhil.demotask.model.request.FileUploadRequest
import com.akhil.demotask.service.viewmodel.FileUploadModelFactory
import com.akhil.demotask.service.viewmodel.FileUploadViewModel
import com.akhil.demotask.util.ApiException
import com.akhil.demotask.util.InternetAlertDialog
import com.akhil.demotask.util.NoInternetException
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.coroutines.launch
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import java.util.Date


class MainActivity : BaseActivity(), KodeinAware, View.OnClickListener {
    private lateinit var binding: ActivityMainBinding
    private lateinit var bottomSheetDialog: BottomSheetDialog
    override val kodein: Kodein by kodein(this)
    private val factory: FileUploadModelFactory by instance()
    private lateinit var viewModel: FileUploadViewModel
    private lateinit var internetAlertDialog: InternetAlertDialog
    private var photoFile: Bitmap? = null
    private val REQUEST_CODE = 42
    private var imageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel =
            ViewModelProvider(this, factory)[FileUploadViewModel::class.java]
        internetAlertDialog = InternetAlertDialog.getInstance(this)

        binding.btnSelectImage.setOnClickListener(this)
        binding.btnPreviewImage.setOnClickListener(this)
        binding.btnSubmit.setOnClickListener(this)
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btnSelectImage -> {
                binding.tvImagePath.text = ""
                binding.ivSelectedImage.setImageURI(null)
                when (PackageManager.PERMISSION_GRANTED) {
                    ContextCompat.checkSelfPermission(
                        this, Manifest.permission.CAMERA
                    ) -> {
                        // You can use the API that requires the permission.
                        showBottomSheetDialog()
                    }

                    else -> {
                        // You can directly ask for the permission.
                        // The registered ActivityResultCallback gets the result of this request.
                        requestMultiplePermissions.launch(
                            arrayOf(
                                Manifest.permission.READ_MEDIA_IMAGES,
                                Manifest.permission.CAMERA
                            )
                        )
                    }
                }
            }

            R.id.btnPreviewImage -> {
                binding.ivSelectedImage.setImageURI(imageUri)
            }

            R.id.btnSubmit -> {
                if (photoFile != null) {
                    photoFile?.let { it1 ->
                        uploadFileToServer(it1)
                    }

                } else{
                    Toast.makeText(applicationContext, "Please select image", Toast.LENGTH_SHORT).show()
                }

            }

        }
    }

    private fun uploadFileToServer(bitmap: Bitmap?) {
        var imageFile: File? = null
        if (bitmap != null) {
            val fileName = "Pic${Date().time}"
            val os: OutputStream
            imageFile = File(applicationContext.cacheDir, "$fileName.png")

            try {
                os = FileOutputStream(imageFile)
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, os)
                os.flush()
                os.close()
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }

        lifecycleScope.launch {
            showLoading()

            val request = FileUploadRequest()
            request.apply {
                file = if (imageFile?.absolutePath != null) {
                    imageFile.toString()
                } else {
                    ""
                }
            }
            try {

                val response = viewModel.UploadFile(request)

                //response
                if (response.success) {
                    hideLoading()
                    Toast.makeText(applicationContext, response.message, Toast.LENGTH_SHORT).show()

                } else {
                    hideLoading()

                    Toast.makeText(applicationContext, response.message, Toast.LENGTH_SHORT).show()
                }
            } catch (e: ApiException) {
                hideLoading()
                Toast.makeText(applicationContext, e.message, Toast.LENGTH_SHORT).show();
                e.printStackTrace()
            } catch (e: NoInternetException) {
                hideLoading()
                internetAlertDialog.show()
            } catch (e: Throwable) {
                hideLoading()
                Toast.makeText(applicationContext, e.message, Toast.LENGTH_SHORT).show();
                e.printStackTrace()
            }
        }


    }


    private val requestMultiplePermissions = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        permissions.entries.forEach {
            Log.d("DEBUG", "${it.key} = ${it.value}")
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            val takenImage = data?.extras?.get("data") as Bitmap
            photoFile = takenImage

            setImage(takenImage)
        }

        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun showBottomSheetDialog() {

        bottomSheetDialog =
            BottomSheetDialog(this, R.style.ImagePickerBottomSheetDialogTheme)
        bottomSheetDialog.setContentView(R.layout.bottomsheet_image_picker)

        val ivCamera = bottomSheetDialog.findViewById<ImageView>(R.id.ivCamera)
        val tvCamera = bottomSheetDialog.findViewById<TextView>(R.id.tvCamera)
        val ivGallery = bottomSheetDialog.findViewById<ImageView>(R.id.ivGallery)
        val tvGallery = bottomSheetDialog.findViewById<TextView>(R.id.tvGallery)

        ivCamera?.setOnClickListener {
            try {
                val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                startActivityForResult(takePictureIntent, REQUEST_CODE)

            } catch (e: ActivityNotFoundException) {
                // display error state to the user
            }
            bottomSheetDialog.dismiss()

        }

        tvCamera?.setOnClickListener {
            try {
                val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                startActivityForResult(takePictureIntent, REQUEST_CODE)

            } catch (e: ActivityNotFoundException) {
                // display error state to the user
            }
        }

        ivGallery?.setOnClickListener {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))

            bottomSheetDialog.dismiss()
        }
        tvGallery?.setOnClickListener {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))

            bottomSheetDialog.dismiss()
        }
        bottomSheetDialog.show()

    }

    private val pickMedia =
        registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            // Callback is invoked after the user selects a media item or closes the
            // photo picker.
            if (uri != null) {

                val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, uri)
                photoFile = bitmap
                setImage(bitmap)
            } else {
                Log.d("PhotoPicker", "No media selected")
            }
        }

    private fun setImage(bitmap: Bitmap) {
        // CALL THIS METHOD TO GET THE URI FROM THE BITMAP
        imageUri = getImageUri(applicationContext, bitmap)

        // CALL THIS METHOD TO GET THE ACTUAL PATH
        val finalFile: File? = getRealPathFromURI(imageUri)?.let { File(it) }
        val filename: String = finalFile!!.absolutePath.substring(finalFile!!.absolutePath.lastIndexOf("/") + 1)
        binding.tvImagePath.text = "File name :- $filename"
    }

    private fun getImageUri(inContext: Context, inImage: Bitmap): Uri {
        val bytes = ByteArrayOutputStream()
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val path = Images.Media.insertImage(inContext.contentResolver, inImage, "Pic${Date().time}", null)
        return Uri.parse(path)
    }

    private fun getRealPathFromURI(uri: Uri?): String? {
        val cursor = contentResolver.query(uri!!, null, null, null, null)
        cursor!!.moveToFirst()
        val idx = cursor.getColumnIndex(Images.ImageColumns.DATA)
        return cursor.getString(idx)
    }

}