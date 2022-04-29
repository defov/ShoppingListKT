package com.example.shoppinglisttesting.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.example.shoppinglisttesting.R
import com.example.shoppinglisttesting.data.local.ShoppingItem
import kotlinx.android.synthetic.main.item_shopping.view.*
import javax.inject.Inject

class ShoppingItemAdapter @Inject constructor(
    private val glide: RequestManager
) : RecyclerView.Adapter<ShoppingItemAdapter.ShoppingItemViewHolder>() {
    class ShoppingItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    private val diffCallback = object: DiffUtil.ItemCallback<ShoppingItem>() {
        override fun areItemsTheSame(oldItem: ShoppingItem, newItem: ShoppingItem): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: ShoppingItem, newItem: ShoppingItem): Boolean {
            return oldItem.hashCode() == newItem.hashCode()
        }
    }

    private val differ = AsyncListDiffer(this, diffCallback)

    var shoppingItems: List<ShoppingItem>
        get() = differ.currentList
        set(value) = differ.submitList(value)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShoppingItemViewHolder {
        return ShoppingItemViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_shopping,
                parent,
                false
            )
        )
    }

    private var onItemClickListener: ((ShoppingItem) -> Unit)? = null;

    fun setOnItemClickListener(listener: (ShoppingItem) -> Unit) {
        onItemClickListener = listener
    }

    override fun onBindViewHolder(holder: ShoppingItemViewHolder, position: Int) {
        val shoppingItem = shoppingItems[position]
        holder.itemView.apply {
            glide.load(shoppingItem.imageUrl).into(ivShoppingImage)

            tvName.text = shoppingItem.name

            val price = "Price: ${shoppingItem.price}€"
            val amount = "${shoppingItem.amount}X"
            tvShoppingItemPrice.text = price
            tvShoppingItemAmount.text = amount

            setOnClickListener {
                onItemClickListener?.let { click ->
                    click(shoppingItem)
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return shoppingItems.size
    }
}