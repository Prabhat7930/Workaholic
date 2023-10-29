package com.example.workaholic.firebase

import android.app.Activity
import android.util.Log
import android.widget.Toast
import com.example.workaholic.activity.BoardActivity
import com.example.workaholic.activity.MainActivity
import com.example.workaholic.activity.MembersActivity
import com.example.workaholic.activity.ProfileActivity
import com.example.workaholic.activity.SignInActivity
import com.example.workaholic.activity.SignUpActivity
import com.example.workaholic.activity.TaskListActivity
import com.example.workaholic.models.Board
import com.example.workaholic.models.User
import com.example.workaholic.utils.Constants
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.toObject

open class FireStoreClass {

    private val myFireStore = FirebaseFirestore.getInstance()

    fun registerUser(activity : SignUpActivity, userInfo : User) {
        myFireStore.collection(Constants.USERS)
            .document(getCurrUserId())
            .set(userInfo, SetOptions.merge())
            .addOnSuccessListener {
                activity.userRegisteredSuccess()
            }.addOnFailureListener {
                activity.hideProgressDialog()
                Log.e(activity.javaClass.simpleName, "Error during Signing Up")
            }
    }

    fun loadUserData(activity: Activity, readBoardList : Boolean = false) {
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
                            activity.updateNavUserDetails(loggedInUser, readBoardList)
                        }
                    }
                    is ProfileActivity -> {
                        if (loggedInUser != null) {
                            activity.setUserDataInUI(loggedInUser)
                        }
                    }
                    is BoardActivity -> {
                        if (loggedInUser != null) {
                            activity.setupForUser(loggedInUser)
                        }
                    }
                }

            }.addOnFailureListener {
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
                    is BoardActivity -> {
                        activity.hideProgressDialog()
                    }
                }

                Log.e(activity.javaClass.simpleName,
                    "Error during Signing In")

            }
    }

    fun updateUserProfileData(activity: ProfileActivity, userHashMap: HashMap<String, Any>) {
        myFireStore.collection(Constants.USERS)
            .document(getCurrUserId())
            .update(userHashMap)
            .addOnSuccessListener {
                Toast.makeText(activity, "Profile Updated successfully!", Toast.LENGTH_SHORT).show()
                activity.profileUpdateSuccess()
            }
            .addOnFailureListener {
                Toast.makeText(activity,
                    "Error Updating Profile", Toast.LENGTH_SHORT).show()

            }
    }

    fun registerBoard(activity: BoardActivity, boardInfo: Board) {
        myFireStore.collection(Constants.BOARDS)
            .document()
            .set(boardInfo, SetOptions.merge())
            .addOnSuccessListener {
                Toast.makeText(activity,
                    "Board Created Successfully!", Toast.LENGTH_SHORT).show()
                activity.boardCreatedSuccessfully()
            }
            .addOnFailureListener {
                activity.hideProgressDialog()
                Toast.makeText(activity,
                    "Board Creation Failed!", Toast.LENGTH_SHORT).show()
            }
    }

    fun getBoardList(activity: MainActivity) {
        myFireStore.collection(Constants.BOARDS)
            .whereArrayContains(Constants.ASSIGNED_TO, getCurrUserId())
            .get()
            .addOnSuccessListener {
                document ->
                val boardList : ArrayList<Board> = ArrayList()
                for (i in document.documents) {
                    var board = i.toObject(Board::class.java)!!
                    board.documentId = i.id
                    Log.w(board.documentId, i.id)
                    boardList.add(board)
                }

                activity.setupBoardListToUI(boardList)
            }
            .addOnFailureListener {
                activity.hideProgressDialog()
            }
    }

    fun getBoardDetails(activity : TaskListActivity, documentId : String) {
        myFireStore.collection(Constants.BOARDS)
            .document(documentId)
            .get()
            .addOnSuccessListener {
                document ->
                val board = document.toObject(Board::class.java)!!
                board.documentId = document.id
                activity.boardDetails(board)
            }
    }

    fun addUpdateTaskList(activity: TaskListActivity, board : Board) {
        val taskListHashMap = HashMap<String, Any>()
        taskListHashMap[Constants.TASK_LIST] = board.taskList

        myFireStore.collection(Constants.BOARDS)
            .document(board.documentId)
            .update(taskListHashMap)
            .addOnSuccessListener {
                activity.addUpdateTaskListSuccess()
            }
            .addOnFailureListener {
                activity.hideProgressDialog()
            }
    }

    fun getAssignedMembersListDetails(activity : MembersActivity, assignedTo : ArrayList<String>) {
        myFireStore.collection(Constants.USERS)
            .whereIn(Constants.ID, assignedTo)
            .get()
            .addOnSuccessListener {
                document ->

                val usersList : ArrayList<User> = ArrayList()

                for (i in document.documents) {
                    val user = i.toObject(User::class.java)!!
                    usersList.add(user)
                }

                activity.setupMemberList(usersList)
            }
            .addOnFailureListener {
                activity.hideProgressDialog()
            }
    }

    fun getCurrUserId() : String {
        var currUser = FirebaseAuth.getInstance().currentUser
        var currUserId = ""

        if (currUser != null) {
            currUserId = currUser.uid
        }
        return currUserId
    }

}