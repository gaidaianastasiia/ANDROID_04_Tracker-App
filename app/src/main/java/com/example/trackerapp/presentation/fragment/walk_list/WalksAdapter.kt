package com.example.trackerapp.presentation.fragment.walk_list

import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.trackerapp.R
import com.example.trackerapp.databinding.WalkListItemBinding
import com.example.trackerapp.entity.Walk
import com.example.trackerapp.utils.ImageManager

private val DIFF_CALLBACK: DiffUtil.ItemCallback<Walk> =
    object : DiffUtil.ItemCallback<Walk>() {
        override fun areItemsTheSame(oldItem: Walk, newItem: Walk): Boolean {
            return oldItem.mapImageName == newItem.mapImageName
        }

        override fun areContentsTheSame(oldItem: Walk, newItem: Walk): Boolean {
            return oldItem == newItem
        }
    }

class WalksAdapter(
    private val imageManager: ImageManager,
    private val onItemClickListener: OnItemClickListener,
) : ListAdapter<Walk, WalksAdapter.ViewHolder>(DIFF_CALLBACK) {

    interface OnItemClickListener {
        fun onDeleteButtonClick(idToDelete: Long)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemBinding = WalkListItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return ViewHolder(itemBinding, imageManager)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val walk = getItem(position)
        holder.bind(walk)
        holder.itemBinding.deleteWalkButton.setOnClickListener {
            onItemClickListener.onDeleteButtonClick(walk.id)
        }
    }

    class ViewHolder(
        val itemBinding: WalkListItemBinding,
        private val imageManager: ImageManager,
    ) : RecyclerView.ViewHolder(itemBinding.root) {
        fun bind(walk: Walk) {
            itemBinding.apply {
                var walkMapImage: Bitmap? = null

                if (walk.mapImageName != null) {
                    walkMapImage =
                        imageManager.getMapImage(root.context, walk.mapImageName)
                }

                if (walkMapImage != null) {
                    mapImageView.setImageBitmap(walkMapImage)
                } else {
                    mapImageView.setBackgroundResource(R.drawable.default_walk_map_image)
                }

                walkTitleTextView.text = walk.date
                timeTextView.text = walk.time
                distanceTextView.text = walk.distance
                averageSpeedTextView.text = walk.speed
            }
        }
    }
}