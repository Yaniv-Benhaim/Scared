package tech.gamedev.scared.repo

import android.content.ContentValues
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import timber.log.Timber

class LoginRepository {


    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val firebaseFireStore: FirebaseFirestore = FirebaseFirestore.getInstance()

    fun getUser(): FirebaseUser? {
        return auth.currentUser
    }

    fun createUser(): Task<AuthResult> {
        return auth.signInAnonymously()
    }

    fun checkIfUserExists(acct: FirebaseUser?) {
        val db = FirebaseFirestore.getInstance()

        db.collection("users").document("${acct?.email}").get()
            .addOnCompleteListener(OnCompleteListener {
                if (it.result?.exists()!!) {
                } else {
                    addNewUserToFirestore(acct)
                }
            })
    }

    private fun addNewUserToFirestore(acct: FirebaseUser?) {

        val db = FirebaseFirestore.getInstance()
        val user = hashMapOf(
            "name" to "${acct?.displayName}",
            "email" to "${acct?.email}",
            "img" to "${acct?.photoUrl}"
        )

        db.collection("users").document("${acct?.email}")
            .set(user)
            .addOnSuccessListener { Timber.d("DocumentSnapshot successfully written!") }
            .addOnFailureListener { e -> Timber.tag(ContentValues.TAG).w(e, "Error writing document") }
    }




}