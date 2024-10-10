package com.example.bhaishare

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private lateinit var roomAdapter: RoomAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        auth = FirebaseAuth.getInstance()

        // If the user is not logged in, redirect to LoginActivity
        if (auth.currentUser == null) {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
            return
        }

        db = FirebaseFirestore.getInstance()

        val createRoomButton = findViewById<Button>(R.id.createRoomButton)
        val roomList = findViewById<RecyclerView>(R.id.roomList)

        createRoomButton.setOnClickListener {
            initiateSharedOrder()
        }

        roomList.layoutManager = LinearLayoutManager(this)
        roomAdapter = RoomAdapter { roomId ->
            val intent = Intent(this, RoomActivity::class.java)
            intent.putExtra("roomId", roomId)
            startActivity(intent)
        }
        roomList.adapter = roomAdapter

        // Load rooms with real-time updates
        loadRooms()
    }

    private fun initiateSharedOrder() {
        val room = hashMapOf(
            "roomOwner" to auth.currentUser?.uid,
            "roomName" to "Blinkit Group Cart",
            "cartValue" to 0.0,
            "members" to listOf(auth.currentUser?.uid)
        )

        db.collection("rooms")
            .add(room)
            .addOnSuccessListener {
                Toast.makeText(this, "Group Cart Opened", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Error creating room", Toast.LENGTH_SHORT).show()
            }
    }

    private fun loadRooms() {
        db.collection("rooms").addSnapshotListener { snapshots, e ->
            if (e != null) {
                Toast.makeText(this, "Error loading rooms", Toast.LENGTH_SHORT).show()
                return@addSnapshotListener
            }

            val rooms = mutableListOf<RoomModel>()
            snapshots?.let {
                for (doc in it) {
                    val room = RoomModel(
                        doc.id,
                        doc.getString("roomName") ?: "",
                        doc.getDouble("cartValue") ?: 0.0
                    )
                    rooms.add(room)
                }
            }
            roomAdapter.submitList(rooms)
        }
    }
}
