package com.example.taskplanner

import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class UserDao {
   private val db = FirebaseFirestore.getInstance()
   private val userCollection = db.collection("users")

   fun addUser(user: User?){
      user?.let {
         GlobalScope.launch(Dispatchers.IO) {
            userCollection.document(user.uid).set(it)
         }
      }
   }
//   fun getUserById(uid : String ): Task<DocumentSnapshot> {
//      return userCollection.document(uid).get()
//   }
}