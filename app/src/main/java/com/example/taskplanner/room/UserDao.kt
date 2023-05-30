package com.example.taskplanner.room

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class UserDao {
    private val db = FirebaseFirestore.getInstance()
    private val userCollection = db.collection("users")

    fun addUser(users: Users?) {
        users?.let {
            GlobalScope.launch(Dispatchers.IO) {
                userCollection.document(users.uid).set(it)
            }
        }
    }
//   fun getUserById(uid : String ): Task<DocumentSnapshot> {
//      return userCollection.document(uid).get()
//   }
}