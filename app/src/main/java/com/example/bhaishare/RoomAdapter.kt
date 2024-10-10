package com.example.bhaishare

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class RoomAdapter(private val onRoomClick: (String) -> Unit) : RecyclerView.Adapter<RoomAdapter.RoomViewHolder>() {

    private val rooms = mutableListOf<RoomModel>()

    fun submitList(roomList: List<RoomModel>) {
        rooms.clear()
        rooms.addAll(roomList)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RoomViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(android.R.layout.simple_list_item_2, parent, false)
        return RoomViewHolder(view)
    }

    override fun onBindViewHolder(holder: RoomViewHolder, position: Int) {
        val room = rooms[position]
        holder.bind(room)
    }

    override fun getItemCount(): Int = rooms.size

    inner class RoomViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val text1: TextView = itemView.findViewById(android.R.id.text1)
        private val text2: TextView = itemView.findViewById(android.R.id.text2)

        fun bind(room: RoomModel) {
            text1.text = room.roomName
            text2.text = "Cart Value: ₹${room.cartValue}"

            itemView.setOnClickListener {
                onRoomClick(room.roomId)
            }
        }
    }
}
