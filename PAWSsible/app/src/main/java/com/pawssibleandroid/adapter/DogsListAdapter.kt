package com.pawssibleandroid.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.pawssibleandroid.R
import com.pawssibleandroid.databinding.ItemDogListBinding
import com.pawssibleandroid.interfaces.IClickListener
import com.pawssibleandroid.models.DogModel
import java.util.*

/**
 * Dogs list adapter
 */
class DogsListAdapter(
    private val context: Context,
    dogModelArrayList: ArrayList<DogModel>?,
    iClickListener: IClickListener?
) : RecyclerView.Adapter<DogsListAdapter.UsersViewHolder>() {

    private val dogModelArrayList: ArrayList<DogModel>?
    private val iClickListener: IClickListener?

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UsersViewHolder {
        return UsersViewHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.item_dog_list,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(fileFoldersViewHolder: UsersViewHolder, position: Int) {
        val dogModel: DogModel? = dogModelArrayList?.get(position)
        fileFoldersViewHolder.itemFolderBinding.txtName.setText(dogModel?.breedName)
        Glide.with(context).load(dogModel?.photo)
            .into(fileFoldersViewHolder.itemFolderBinding.imgDog)
        fileFoldersViewHolder.itemFolderBinding.txtDescription.setText(dogModel?.description)
        fileFoldersViewHolder.itemFolderBinding.getRoot().setOnClickListener { v ->
            if (iClickListener != null) iClickListener.onClick(
                v, position
            )
        }
        fileFoldersViewHolder.itemFolderBinding.getRoot().setOnLongClickListener { v ->
            if (iClickListener != null) iClickListener.onLongClick(v, position)
            false
        }
    }

    override fun getItemCount(): Int {
        return if (dogModelArrayList.isNullOrEmpty()) 0 else dogModelArrayList.size
    }

    class UsersViewHolder(itemFileFolderBinding: ItemDogListBinding) :
        RecyclerView.ViewHolder(itemFileFolderBinding.getRoot()) {
        var itemFolderBinding: ItemDogListBinding

        init {
            itemFolderBinding = itemFileFolderBinding
        }
    }

    init {
        this.dogModelArrayList = dogModelArrayList
        this.iClickListener = iClickListener
    }

}
