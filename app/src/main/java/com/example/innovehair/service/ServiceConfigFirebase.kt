package com.example.innovehair.service

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

class ServiceConfigFirebase {

    /*private val db = FirebaseDatabase.getInstance()
    val ref = db.reference*/

    val firestore = FirebaseFirestore.getInstance()
    val storage = FirebaseStorage.getInstance().reference
}