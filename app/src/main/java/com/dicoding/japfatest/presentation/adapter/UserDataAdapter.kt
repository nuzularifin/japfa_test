package com.dicoding.japfatest.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dicoding.japfatest.data.model.UserData
import com.dicoding.japfatest.databinding.UserDataItemBinding
import com.dicoding.japfatest.domain.model.UserDto
import com.dicoding.japfatest.utils.convertDateString
import com.dicoding.japfatest.utils.toUserData

class UserDataAdapter(
    private val listener: OnClickUserData
) : RecyclerView.Adapter<UserDataAdapter.UserDataViewHolder>() {

    private var usersData = mutableListOf<UserDto>()

    class UserDataViewHolder(
        private val binding: UserDataItemBinding,
        private var listener: OnClickUserData
    ) : RecyclerView.ViewHolder(binding.root){
        fun bind(userDto: UserDto){
            binding.tvName.text = "${userDto.fullName} (${userDto.gender})"
            binding.tvAddress.text = userDto.address
            binding.tvDate.text = convertDateString(userDto.dateTime ?: "")
            Glide.with(itemView)
                .load(userDto.photoUri)
                .into(binding.imgPhoto)

            binding.ivDelete.setOnClickListener {
                listener.onDeleteClick(userDto)
            }

            binding.ivEdit.setOnClickListener {
                listener.onEditClick(userDto)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserDataViewHolder {
        val view = UserDataItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return UserDataViewHolder(view, listener)
    }

    override fun getItemCount(): Int = usersData.size

    override fun onBindViewHolder(holder: UserDataViewHolder, position: Int) {
        holder.bind(usersData[position])
    }

    fun setData(data: List<UserDto>){
        usersData.clear()
        usersData.addAll(data)
        notifyDataSetChanged()
    }

    fun deleteItem(userData: UserData){
        val position = usersData.indexOfFirst { it.toUserData() == userData }
        if (position >= 0) {
            usersData.removeAt(position)
            notifyItemRemoved(position)
            notifyItemRangeChanged(position, usersData.size)
        }
    }

    fun getAllItems() : List<UserDto> {
        return usersData
    }
}

interface OnClickUserData {
    fun onDeleteClick(userDto: UserDto)
    fun onEditClick(userDto: UserDto)
}