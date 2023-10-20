package com.example.workaholic.firebase

import android.app.Activity
import android.util.Log
import com.example.workaholic.activity.MainActivity
import com.example.workaholic.activity.ProfileActivity
import com.example.workaholic.activity.SignInActivity
import com.example.workaholic.activity.SignUpActivity
import com.example.workaholic.models.User
import com.example.workaholic.utils.Constants
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions

open class FireStoreClass {

    private val myFireStore = FirebaseFirestore.getInstance()

    fun registerUser(activity : SignUpActivity, userInfo : User) {
        myFireStore.collection(Constants.USERS)
            .document(getCurrUserId())
            .set(userInfo, SetOptions.merge())
            .addOnSuccessListener {
                activity.userRegisteredSuccess()
            }.addOnFailureListener {
                
                e ->
                Log.e(activity.javaClass.simpleName,
                    "Error during Signing Up")

            }
    }

    fun loadUserData(activity: Activity) {
        myFireStore.collection(Constants.USERS)
            .document(getCurrUserId())
            .get()
            .addOnSuccessListener {document ->
                val loggedInUser = document.toObject(User::class.java)

                when(activity) {
                    is SignInActivity -> {
                        if (loggedInUser != null) {
                            activity.loginSuccess(loggedInUser)
                        }
                    }
                    is MainActivity -> {
                        if (loggedInUser != null) {
                            activity.updateNavUserDetails(loggedInUser)
                        }
                    }
                    is ProfileActivity -> {
                        if (loggedInUser != null) {
                            activity.setUserDataInUI(loggedInUser)
                        }
                    }
                }

            }.addOnFailureListener {
                    e ->
                when(activity) {
                    is SignInActivity -> {
                        activity.hideProgressDialog()
                    }
                    is MainActivity -> {
                        activity.hideProgressDialog()
                    }
                    is ProfileActivity -> {
                        activity.hideProgressDialog()
                    }
                }

                Log.e(activity.javaClass.simpleName,
                    "Error during Signing In")

            }
    }

    fun getCurrUserId() : String {
        var currUser = FirebaseAuth.getInstance().currentUser
        var currUserId: String = ""

        if (currUser != null) {
            currUserId = currUser.uid
        }
        return currUserId
    }

}