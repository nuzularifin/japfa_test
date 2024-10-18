package com.dicoding.japfatest.presentation

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.japfatest.data.model.UserData
import com.dicoding.japfatest.databinding.ActivityDisplayDataBinding
import com.dicoding.japfatest.domain.model.UserDto
import com.dicoding.japfatest.presentation.adapter.OnClickUserData
import com.dicoding.japfatest.presentation.adapter.UserDataAdapter
import com.dicoding.japfatest.utils.CsvExporter
import com.dicoding.japfatest.utils.UiState
import com.dicoding.japfatest.utils.show
import com.dicoding.japfatest.utils.showToastMessage
import com.dicoding.japfatest.utils.toUserData
import com.dicoding.japfatest.utils.toUserDto
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DisplayDataActivity : AppCompatActivity(), OnClickUserData {

    private lateinit var binding: ActivityDisplayDataBinding
    private lateinit var userAdapter: UserDataAdapter
    private var selectedUserData: UserData? = null

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDisplayDataBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userAdapter = UserDataAdapter(this)

        binding.rvUserData.apply {
            adapter = userAdapter
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(this@DisplayDataActivity, LinearLayoutManager.VERTICAL, false)
        }

        binding.btnExport.setOnClickListener {
            val exporter = CsvExporter()
            if (exporter.exportUsersToCSV(this, userAdapter.getAllItems(), "exported_data")){
                showToastMessage(this, "CSV Export Berhasil")
            } else {
                showToastMessage(this, "CSV Export Gagal")
            }
        }

        viewModel.getUserData.observe(this){ state ->
            when(state){
                is UiState.Loading -> {
                    if (state.isLoading) {
                        binding.rvUserData.show(false)
                        binding.pbLoading.show(true)
                    } else {
                        binding.rvUserData.show(true)
                        binding.pbLoading.show(false)
                    }
                }
                is UiState.Success -> {
                    if (state.data?.isEmpty() == true) {
                        binding.rvUserData.show(false)
                        binding.tvEmpty.show(true)
                    } else {
                        binding.rvUserData.show(true)
                        binding.tvEmpty.show(false)
                        userAdapter.setData(state.data ?: emptyList())
                    }
                }
                is UiState.Error -> {
                    showToastMessage(this, state.message)
                }
            }
        }

        viewModel.deleteUserData.observe(this) { state ->
            when(state){
                is UiState.Error -> {
                    showToastMessage(this, state.message)
                }
                is UiState.Loading -> {
                }
                is UiState.Success -> {
                    if (state.data == true) {
                        selectedUserData?.let {
                            userAdapter.deleteItem(it)
                            if (userAdapter.getAllItems().isEmpty()){
                                binding.rvUserData.show(false)
                                binding.tvEmpty.show(true)
                            } else {
                                binding.tvEmpty.show(false)
                                binding.rvUserData.show(true)
                            }
                        }
                    }
                }
            }
        }

        viewModel.getAllDataUser()
    }

    override fun onDeleteClick(userDto: UserDto) {
        showDeleteConfirmationDialog(userData = userDto.toUserData())

    }

    override fun onEditClick(userDto: UserDto) {
        showEditConfirmationDialog(userData = userDto.toUserData())
    }

    private fun showDeleteConfirmationDialog(userData: UserData) {
        AlertDialog.Builder(this)
            .setTitle("Konfirmasi Hapus")
            .setMessage("Apakah Anda yakin ingin menghapus ${userData.fullName}?")
            .setPositiveButton("Hapus") { _, _ ->
                selectedUserData = userData
                viewModel.deleteUserData(userData)
            }
            .setNegativeButton("Batal", null)
            .show()
    }

    private fun showEditConfirmationDialog(userData: UserData) {
        AlertDialog.Builder(this)
            .setTitle("Konfirmasi Ubah")
            .setMessage("Apakah Anda yakin ingin mengubah ${userData.fullName}?")
            .setPositiveButton("Ubah") { _, _ ->
                openInputDataWithData(userDto = userData.toUserDto())
            }
            .setNegativeButton("Batal", null)
            .show()
    }

    private fun openInputDataWithData(userDto: UserDto) {
        val intent = Intent(this, InputDataActivity::class.java)
        intent.putExtra(InputDataActivity.EXTRA_USER_DATA, userDto)
        startActivity(intent)
    }
}