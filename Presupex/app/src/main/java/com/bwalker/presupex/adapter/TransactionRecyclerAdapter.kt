package com.bwalker.presupex.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bwalker.presupex.R
import com.bwalker.presupex.data.TransactionEntity
import com.bwalker.presupex.util.Util

class TransactionRecyclerAdapter(
    private val items: List<TransactionEntity>
) : RecyclerView.Adapter<TransactionRecyclerAdapter.TransactionViewHolder>() {

    // Holds references to each item view
    class TransactionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val img: ImageView = itemView.findViewById(R.id.itemImage)
        val category: TextView = itemView.findViewById(R.id.itemCategory)
        val amount: TextView = itemView.findViewById(R.id.itemAmount)
        val date: TextView = itemView.findViewById(R.id.itemDate)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_transaction, parent, false)
        return TransactionViewHolder(view)
    }

    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
        val transaction = items[position]

        // Put real data from the transaction
        holder.category.text = transaction.category
        holder.amount.text = Util.formatCurrency(transaction.amount)
        holder.date.text = transaction.date

        // Image handling
        if (transaction.image != null) {
            holder.img.setImageBitmap(transaction.image)
        } else {
            holder.img.setImageResource(R.mipmap.ic_launcher) // Placeholder icon
        }
    }

    override fun getItemCount(): Int = items.size
}
