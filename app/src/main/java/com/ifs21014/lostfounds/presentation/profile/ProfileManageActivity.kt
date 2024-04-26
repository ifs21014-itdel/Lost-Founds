package com.ifs21014.lostfounds.presentation.profile

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.ifs18005.delcomtodo.data.remote.response.DataUserResponse
import com.ifs21014.lostfounds.R
import com.ifs21014.lostfounds.data.remote.MyResult
import com.ifs21014.lostfounds.databinding.ActivityEditProfileBinding
import com.ifs21014.lostfounds.helper.Utils.Companion.observeOnce
import com.ifs21014.lostfounds.helper.getImageUri
import com.ifs21014.lostfounds.presentation.ViewModelFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody

class ProfileManageActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditProfileBinding
    private var currentImageUri: Uri? = null
    private val viewModel: ProfileViewModel by viewModels {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupView()
        setupAction()
    }

    private fun setupView() {
        showLoading(false)
    }

    private fun setupAction() {
        binding.apply {
            appbarLostFoundManage.title = "Ubah Photo"

            btnSave.setOnClickListener {

                if (currentImageUri != null) {
                    val imageFile = currentImageUri?.let { uri ->
                        contentResolver.openInputStream(uri)?.use { inputStream ->
                            val imageRequestBody =
                                inputStream.readBytes().toRequestBody("image/*".toMediaType())
                            MultipartBody.Part.createFormData("photo", "photo.jpg", imageRequestBody)
                        }
                    }
                    if (imageFile != null) {
                        editPhoto(imageFile)

                    }
                } else {
                    AlertDialog.Builder(this@ProfileManageActivity).apply {
                        setTitle("Oh No!")
                        setMessage("Pilih gambar terlebih dahulu!")
                        setPositiveButton("Oke") { _, _ -> }
                        create()
                        show()
                    }
                }
            }

            btnCamera.setOnClickListener {
                startCamera()
            }

            btnGallery.setOnClickListener {
                startGallery()
            }
        }
    }

    private fun startGallery() {
        launcherGallery.launch(
            Intent(
                Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            )
        )
    }

    private fun startCamera() {
        currentImageUri = getImageUri(this)
        launcherIntentCamera.launch(currentImageUri)
    }

    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { isSuccess ->
        if (isSuccess) {
            showImage()
        }
    }

    private val launcherGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val data: Intent? = result.data
            val selectedImageUri: Uri? = data?.data
            selectedImageUri?.let {
                currentImageUri = it
                showImage()
            }
        } else {
            Toast.makeText(
                applicationContext,
                "Tidak ada media yang dipilih!",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun showImage() {
        currentImageUri?.let {
            Glide.with(this@ProfileManageActivity)
                .load(it)
                .placeholder(R.drawable.ic_image_24)
                .into(binding.ivAccountManageCover)
        }
    }

    private fun editPhoto( cover: MultipartBody.Part) {
        viewModel.editPhoto(cover).observeOnce { result ->
            when (result) {
                is MyResult.Loading -> {
                    showLoading(true)
                }
                is MyResult.Success -> {
                    // Handle success
                    showLoading(false)
                    Toast.makeText(
                        applicationContext,
                        "Berhasil mengubah foto profil!",
                        Toast.LENGTH_SHORT
                    ).show()
                    startActivity(Intent(this@ProfileManageActivity,ProfileActivity::class.java))
                    finish()
                }
                is MyResult.Error -> {
                    // Handle error
                    showLoading(false)
                    AlertDialog.Builder(this@ProfileManageActivity).apply {
                        setTitle("Oh No!")
                        setMessage(result.error)
                        setPositiveButton("Oke") { _, _ -> }
                        create()
                        show()
                    }
                }
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.pbManage.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.btnSave.isActivated = !isLoading
        binding.btnSave.text = if (isLoading) "" else "Simpan"
    }
}
