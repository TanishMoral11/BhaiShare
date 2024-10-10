package com.example.bhaishare

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore

class RoomListActivity : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore
    private lateinit var roomAdapter: RoomAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_room_list)

        db = FirebaseFirestore.getInstance()

        val roomList = findViewById<RecyclerView>(R.id.roomList)
        roomList.layoutManager = LinearLayoutManager(this)
        roomAdapter = RoomAdapter { roomId ->
            // Handle room click here, open RoomActivity
            val intent = Intent(this, RoomActivity::class.java)
            intent.putExtra("roomId", roomId)
            startActivity(intent)
        }
        roomList.adapter = roomAdapter

        loadRooms()
    }

    private fun loadRooms() {
        db.collection("rooms").get()
            .addOnSuccessListener { result ->
                val rooms = result.map { doc ->
                    RoomModel(doc.id, doc.getString("roomName") ?: "", doc.getDouble("cartValue") ?: 0.0)
                }
                roomAdapter.submitList(rooms)
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error loading rooms", Toast.LENGTH_SHORT).show()
            }
    }
}
