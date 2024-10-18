package com.dicoding.japfatest.presentation

import android.Manifest
import android.app.Activity
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.icu.util.Calendar
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.lifecycleScope
import com.dicoding.japfatest.R
import com.dicoding.japfatest.data.model.UserData
import com.dicoding.japfatest.databinding.ActivityInputDataBinding
import com.dicoding.japfatest.domain.model.UserDto
import com.dicoding.japfatest.utils.LocationHelper
import com.dicoding.japfatest.utils.LocationPermissionHelper
import com.dicoding.japfatest.utils.UiState
import com.dicoding.japfatest.utils.getCurrentDateTime
import com.dicoding.japfatest.utils.getFileFromUri
import com.dicoding.japfatest.utils.show
import com.dicoding.japfatest.utils.showToastMessage
import com.dicoding.japfatest.utils.toUserData
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.jvm.Throws

@AndroidEntryPoint
class InputDataActivity  : AppCompatActivity(){

    private lateinit var binding: ActivityInputDataBinding

    private val mainViewModel: MainViewModel by viewModels()

    private lateinit var cameraLauncher: ActivityResultLauncher<Intent>
    private lateinit var galleryLauncher: ActivityResultLauncher<Intent>
    private lateinit var currentPhotoPath: String
    private lateinit var locationHelper: LocationHelper

    private var userDto = UserDto()

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted){
            getLocation()
        } else {
            Toast.makeText(this, "Izin Lokasi Ditolak", Toast.LENGTH_SHORT).show()
        }
    }


    companion object {
        private const val CAMERA_PERMISSION_CODE = 100
        private const val GALLERY_PERMISSION_CODE = 101
        const val EXTRA_USER_DATA = "EXTRA_USER_DATA"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInputDataBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val selectedUserDto: UserDto? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getSerializableExtra(EXTRA_USER_DATA, UserDto::class.java)
        } else {
            intent.getSerializableExtra(EXTRA_USER_DATA) as? UserDto
        }

        if (selectedUserDto != null){
            userDto = selectedUserDto
            binding.etFullName.setText(userDto.fullName)
            binding.etAddress.setText(userDto.address)
            binding.etDateOfBirth.setText(userDto.birthDate)
            binding.etDateAndTime.setText(userDto.birthDate)
            binding.rgGender.check(if (userDto.gender == "L") R.id.rb_male else R.id.rb_female)
            binding.imgUpload.setImageURI(Uri.parse(userDto.photoUri))
            binding.tvLocation.text = "Lat: ${userDto.latitude}, Lng: ${userDto.longitude}"
        }


        cameraLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == Activity.RESULT_OK){
                val imageBitmap = BitmapFactory.decodeFile(currentPhotoPath)
                binding.imgUpload.setImageBitmap(imageBitmap)
                userDto.photoUri = currentPhotoPath
            }
        }

        galleryLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == Activity.RESULT_OK){
                val imageUri: Uri? = result.data?.data
                binding.imgUpload.setImageURI(imageUri)
                userDto.photoUri = getFileFromUri(this, imageUri)?.absolutePath
            }
        }

        locationHelper = LocationHelper(this)

        binding.etDateAndTime.setText(getCurrentDateTime())
        binding.etDateAndTime.setOnClickListener {
            showDateTimePicker()
        }
        binding.etDateAndTime.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus){
                showDateTimePicker()
            }
        }
        binding.etDateOfBirth.setOnClickListener {
            showDateTimePicker(useTimer = false, binding.etDateOfBirth)
        }
        binding.etDateOfBirth.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus){
                showDateTimePicker(useTimer = false, binding.etDateOfBirth)
            }
        }

        binding.rgGender.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId){
                R.id.rb_male -> {
                   userDto.gender = "L"
                }
                R.id.rb_female -> {
                   userDto.gender = "P"
                }
                else -> userDto.gender = ""
            }

            binding.rgGender.check(checkedId)
        }

        binding.rlUpload.setOnClickListener {
            showImagePickerDialog()
        }

        binding.btnCurrentLocation.setOnClickListener {
            if(LocationPermissionHelper.hasLocationPermission(this)){
                getLocation()
            } else {
                requestLocationPermission()
            }
        }

        binding.btnSave.setOnClickListener {
            userDto.address = binding.etAddress.text.toString()
            userDto.birthDate = binding.etDateOfBirth.text.toString()
            userDto.dateTime = binding.etDateAndTime.text.toString()
            userDto.fullName = binding.etFullName.text.toString()
            println(userDto.toString())
            mainViewModel.saveUserData(userData = userDto.toUserData())
        }


        mainViewModel.saveUserData.observe(this){ state ->
            when(state) {
                is UiState.Loading -> {
                    if (state.isLoading){
                        binding.pbLoading.show(true)
                        binding.btnSave.show(false)
                    } else {
                        binding.pbLoading.show(false)
                        binding.btnSave.show(true)
                    }
                }

                is UiState.Success -> {
                    if (state.data == true){
                        openMainDashboard()
                    }
                }

                is UiState.Error -> {
                    showToastMessage(this, state.message)
                }
            }
        }
    }

    private fun openMainDashboard(){
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun requestLocationPermission() {
        requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
    }

    private fun getLocation() {
        locationHelper.getCurrentLocation { currentLocation ->
            currentLocation?.let {
                val locationText = "Lat: ${it.latitude}, Lng: ${it.longitude}"
                binding.tvLocation.text = locationText
                userDto.latitude = it.latitude.toString()
                userDto.longitude = it.longitude.toString()
//                lifecycleScope.launch {
//                    val addressResult = locationHelper.getAddressFromLatLong(it.latitude, it.longitude)
//                    addressResult?.let { address ->
//                        val addressText = address.fullAddress
//
//                    }
//                }
            } ?: run {
                Toast.makeText(this, "Gagal mendapatkan Lokasi saat ini", Toast.LENGTH_SHORT).show()
            }
        }
    }


    private fun showImagePickerDialog() {
        val options = arrayOf("Ambil dari Kamera", "Pilih dari gallery")
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Pilih sumber gambar")
        builder.setItems(options) { _, which ->
            when(which){
                0 -> checkCameraPermission()
                1 -> checkGalleryPermission()
            }
        }
        builder.show()
    }

    private fun checkCameraPermission(){
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(
                this, arrayOf(Manifest.permission.CAMERA), CAMERA_PERMISSION_CODE
            )
        } else {
            openCamera()
        }
    }

    private fun openCamera(){
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        val photoFile: File? = try {
            createImageFile()
        } catch (e: IOException){
            null
        }

        photoFile?.also {
            val photoURI: Uri = FileProvider.getUriForFile(
                this,
                "com.dicoding.japfatest.fileprovider",
                it
            )
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
            cameraLauncher.launch(takePictureIntent)
        }
    }

    @Throws(IOException::class)
    private fun createImageFile(): File {
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val storageDir: File? = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            "JPEG_${timeStamp}_",".jpg", storageDir
        ).apply {
            currentPhotoPath = absolutePath
        }
    }

    private fun checkGalleryPermission(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // Android 13 ke atas
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES)
                != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                    this, arrayOf(Manifest.permission.READ_MEDIA_IMAGES), GALLERY_PERMISSION_CODE
                )
            } else {
                openGallery()
            }
        } else {
            // Android 12 ke bawah
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                    this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), GALLERY_PERMISSION_CODE
                )
            } else {
                openGallery()
            }
        }
    }

    private fun openGallery(){
        val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        galleryIntent.type = "image/*"
        galleryLauncher.launch(galleryIntent)
    }

    private fun showDateTimePicker(useTimer: Boolean = true, editText: EditText? = null) {
        val calender = Calendar.getInstance()

        val datePickerDialog = DatePickerDialog(
            this,
            { _, year, month, dayOfMonth ->
                calender.set(year, month, dayOfMonth)
                if (useTimer) {
                    showTimePickerDialog(calender)
                } else {
                    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                    editText?.setText(dateFormat.format(calender.time))
                }
            },
            calender.get(Calendar.YEAR),
            calender.get(Calendar.MONTH),
            calender.get(Calendar.DAY_OF_MONTH),
        )

        datePickerDialog.show()
    }

    private fun showTimePickerDialog(calendar: Calendar){
        val timePickerDialog = TimePickerDialog(
            this,
            { _, hourOfDay, minute ->
                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                calendar.set(Calendar.MINUTE, minute)

                val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
                binding.etDateAndTime.setText(dateFormat.format(calendar.time))
            },
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            true
        )

        timePickerDialog.show()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode){
            CAMERA_PERMISSION_CODE -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)){
                    openCamera()
                } else {
                    Toast.makeText(this, "Izin Kamera Ditolak", Toast.LENGTH_SHORT).show()
                }
            }
            GALLERY_PERMISSION_CODE -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)){
                    openGallery()
                } else {
                    Toast.makeText(this, "Izin Gallery Ditolak", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}