package com.example.bhaishare

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

class RoomActivity : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private var roomId: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_room)

        db = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        roomId = intent.getStringExtra("roomId") ?: ""

        val roomTitle = findViewById<TextView>(R.id.roomTitle)
        val blinkitLinkInput = findViewById<EditText>(R.id.blinkitLinkInput)
        val cartValueInput = findViewById<EditText>(R.id.cartValueInput)
        val addCartButton = findViewById<Button>(R.id.addCartButton)

        // Real-time updates for room details
        loadRoomDetails(roomTitle)

        addCartButton.setOnClickListener {
            val cartLink = blinkitLinkInput.text.toString()
            val cartValue = cartValueInput.text.toString().toDoubleOrNull() ?: 0.0

            if (cartLink.isNotEmpty() && cartValue > 0) {
                sendBlinkitCartLink(cartLink, cartValue)
            } else {
                Toast.makeText(this, "Invalid cart details", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Load room details with real-time updates
    private fun loadRoomDetails(roomTitle: TextView) {
        db.collection("rooms").document(roomId)
            .addSnapshotListener { documentSnapshot, e ->
                if (e != null) {
                    Toast.makeText(this, "Failed to load room", Toast.LENGTH_SHORT).show()
                    return@addSnapshotListener
                }

                documentSnapshot?.let {
                    roomTitle.text = it.getString("roomName")
                }
            }
    }

    private fun sendBlinkitCartLink(cartLink: String, cartValue: Double) {
        db.collection("rooms").document(roomId)
            .update("cartValue", FieldValue.increment(cartValue))
            .addOnSuccessListener {
                Toast.makeText(this, "Cart Link Shared", Toast.LENGTH_SHORT).show()

                // Redirect to MainActivity
                val intent = Intent(this, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
            }
            .addOnFailureListener {
                Toast.makeText(this, "Error sharing cart link", Toast.LENGTH_SHORT).show()
            }
    }
}
