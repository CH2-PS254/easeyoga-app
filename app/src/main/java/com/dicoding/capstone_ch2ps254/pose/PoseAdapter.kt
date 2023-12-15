package com.dicoding.capstone_ch2ps254.pose

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.dicoding.capstone_ch2ps254.data.model.Poses
import com.dicoding.capstone_ch2ps254.databinding.ItemBinding
import com.dicoding.capstone_ch2ps254.utils.extension.setImageUrl

class PoseAdapter (private val poseList: List<Poses>): RecyclerView.Adapter<PoseAdapter.PoseViewHolder>() {

    interface OnItemClickListener {
        fun onItemClick(id: Int, name: String)
    }

    private var listener: OnItemClickListener? = null

    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.listener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PoseAdapter.PoseViewHolder {
        val binding = ItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PoseViewHolder(binding)
    }

    override fun getItemCount(): Int = poseList.size

    override fun onBindViewHolder(holder: PoseAdapter.PoseViewHolder, position: Int) {
        poseList[position].let { pose ->
            holder.bind(pose)
        }
    }

    inner class PoseViewHolder(private val binding: ItemBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(poses: Poses) {
            with(binding) {
                tvTitle.text = poses.name
                tvDesc.text = poses.description
                imgPose.setImageUrl(poses.image, true)
            }
            itemView.setOnClickListener {
                listener?.onItemClick(poses.id, poses.name)
            }
        }
    }
}