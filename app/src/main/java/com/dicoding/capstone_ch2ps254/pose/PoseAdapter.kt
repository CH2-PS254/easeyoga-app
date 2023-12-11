package com.dicoding.capstone_ch2ps254.pose

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.app.ActivityOptionsCompat
import androidx.recyclerview.widget.RecyclerView
import com.dicoding.capstone_ch2ps254.data.model.Pose
import com.dicoding.capstone_ch2ps254.databinding.ItemBinding
import com.dicoding.capstone_ch2ps254.utils.ValManager
import com.dicoding.capstone_ch2ps254.utils.extension.setImageUrl
import androidx.core.util.Pair
import com.dicoding.capstone_ch2ps254.HomeActivity

class PoseAdapter (private val poseList: List<Pose>): RecyclerView.Adapter<PoseAdapter.PoseViewHolder>() {
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
        fun bind(pose: Pose) {
            with(binding) {
                tvTitle.text = pose.name
                tvDesc.text = pose.description
                imgPose.setImageUrl(pose.image, true)
            }
            itemView.setOnClickListener {
                val intent = Intent(it.context, HomeActivity::class.java)
                intent.putExtra(ValManager.KEY_PACK_POSE, pose)

                val optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(
                    itemView.context as Activity,
                    Pair(binding.imgPose, "thumbnail"),
                    Pair(binding.tvTitle, "title"),
                    Pair(binding.tvDesc, "description"),
                )
                itemView.context.startActivity(intent, optionsCompat.toBundle())
            }
        }
    }
}