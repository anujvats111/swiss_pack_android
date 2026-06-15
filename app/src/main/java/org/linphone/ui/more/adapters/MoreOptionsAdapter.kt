package org.linphone.ui.more.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import org.linphone.databinding.MoreOptionItemBinding
import org.linphone.ui.more.model.MoreOptionModel

class MoreOptionsAdapter(
    private val onClick: (MoreOptionModel) -> Unit
) : RecyclerView.Adapter<MoreOptionsAdapter.MoreOptionViewHolder>() {

    private val items = arrayListOf<MoreOptionModel>()

    fun submitList(list: List<MoreOptionModel>) {
        items.clear()
        items.addAll(list)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MoreOptionViewHolder {
        val binding = MoreOptionItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return MoreOptionViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MoreOptionViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    inner class MoreOptionViewHolder(
        private val binding: MoreOptionItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(model: MoreOptionModel) {
            binding.optionTitle.text = model.title
            binding.optionIcon.setImageResource(model.iconResId)

            binding.root.setOnClickListener {
                onClick(model)
            }
        }
    }
}
