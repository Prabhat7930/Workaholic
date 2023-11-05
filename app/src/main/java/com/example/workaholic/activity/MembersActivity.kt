package com.example.workaholic.activity

import android.Manifest
import android.app.Activity
import android.app.Dialog
//import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import android.window.OnBackInvokedDispatcher
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.workaholic.R
import com.example.workaholic.adapters.MemberListItemAdapter
import com.example.workaholic.databinding.ActivityMembersBinding
import com.example.workaholic.firebase.FireStoreClass
import com.example.workaholic.models.Board
import com.example.workaholic.models.User
import com.example.workaholic.utils.Constants
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.RemoteMessage
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.io.BufferedReader
import java.io.DataOutputStream
import java.io.IOException
import java.io.InputStreamReader
import java.lang.StringBuilder
import java.net.HttpURLConnection
import java.net.SocketTimeoutException
import java.net.URL

class MembersActivity : BaseActivity() {

    private lateinit var binding : ActivityMembersBinding

    private lateinit var myBoardDetails : Board
    private lateinit var myAssignedMembersList : ArrayList<User>
    private lateinit var myUser : User
    private var anyChangesMade : Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMembersBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.statusBarColor = ContextCompat.getColor(this, R.color.colorAccent)
        }

        if (intent.hasExtra(Constants.BOARD_DETAIL)) {
            @Suppress("DEPRECATION")
            myBoardDetails = intent.getParcelableExtra(Constants.BOARD_DETAIL)!!
        }

        setupActionBar()

        showProgressDialog(resources.getString(R.string.please_wait))
        FireStoreClass().getAssignedMembersListDetails(this, myBoardDetails.assignedTo)
    }

    private fun setupActionBar() {
        setSupportActionBar(binding.toolbarMember)

        var actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.baseline_arrow_back_24_white)
            actionBar.title = resources.getString(R.string.members)
        }

        binding.toolbarMember.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_add_member, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.menu_add_members -> {
                dialogToSearchMembers()
            }
        }
        return true
    }

    fun setupMemberList(list : ArrayList<User>) {
        myAssignedMembersList = list
        hideProgressDialog()

        binding.rvMembersList.layoutManager = LinearLayoutManager(this)
        binding.rvMembersList.setHasFixedSize(true)

        val adapter = MemberListItemAdapter(this, list)
        binding.rvMembersList.adapter = adapter
    }

    fun memberDetails(user : User) {
        myBoardDetails.assignedTo.add(user.id)
        FireStoreClass().assignMemberToBoard(this@MembersActivity, myBoardDetails, user)
    }

    fun memberAssignedSuccess(user : User) {
        myUser = user
        hideProgressDialog()
        myAssignedMembersList.add(user)

        anyChangesMade = true
        setupMemberList(myAssignedMembersList)

        //SendNotificationToUserAsyncTask(myBoardDetails.name, user.fcmToken)
        CallAPILoginAsyncTask().startApiCall(myBoardDetails.name, myUser.fcmToken)
    }

    override fun getOnBackInvokedDispatcher(): OnBackInvokedDispatcher {
        if (anyChangesMade) {
            setResult(Activity.RESULT_OK)
        }
        return super.getOnBackInvokedDispatcher()
    }

    private fun dialogToSearchMembers() {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.dialog_search_member)
        dialog.findViewById<TextView>(R.id.tv_add).setOnClickListener {
            val email = dialog.findViewById<EditText>(R.id.et_search_email).text.toString()

            if (email.isNotEmpty()) {
                dialog.dismiss()
                showProgressDialog(resources.getString(R.string.please_wait))
                FireStoreClass().getMembersDetails(this@MembersActivity, email)
            }
            else {
                Toast.makeText(this, "Please enter a email to search", Toast.LENGTH_SHORT).show()
            }
        }
        dialog.findViewById<TextView>(R.id.tv_cancel).setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()

    }

    /*private inner class SendNotificationToUserAsyncTask(val boardName : String, val token : String)
        : AsyncTask<Any, Void, String>() {

        override fun onPreExecute() {
            super.onPreExecute()
            showProgressDialog(resources.getString(R.string.please_wait))
        }
        override fun doInBackground(vararg params: Any?): String {
            var result : String
            var connection : HttpURLConnection? = null
            try {
                var url = URL(Constants.FCM_BASE_URL)
                connection = url.openConnection() as HttpURLConnection
                connection.doOutput = true
                connection.doInput = true
                connection.instanceFollowRedirects = false
                connection.requestMethod = "POST"

                connection.setRequestProperty("Content-Type", "application/json")
                connection.setRequestProperty("charset", "utf-8")
                connection.setRequestProperty("Accept", "application/json")

                connection.setRequestProperty(Constants.FCM_AUTHORIZATION, "${Constants.FCM_KEY}=${Constants.FCM_SERVER_KEY}")
                connection.useCaches = false

                val wr = DataOutputStream(connection.outputStream)
                val jsonRequest = JSONObject()
                val dataObject = JSONObject()
                dataObject.put(Constants.FCM_KEY_TITLE, "Assigned to the board $boardName")
                dataObject.put(Constants.FCM_KEY_MESSAGE, "You have been assigned to the Board by ${myAssignedMembersList[0].name}")

                jsonRequest.put(Constants.FCM_KEY_DATA, dataObject)
                jsonRequest.put(Constants.FCM_KEY_TO, token)

                wr.writeBytes(jsonRequest.toString())
                wr.flush()
                wr.close()

                val httpResult : Int = connection.responseCode
                if (httpResult == HttpURLConnection.HTTP_OK) {
                    val inputStream = connection.inputStream

                    val reader = BufferedReader(InputStreamReader(inputStream))
                    val sb = StringBuilder()
                    var line : String?
                    try {
                        while(reader.readLine().also {line = it} != null) {
                            sb.append(line+"\n")
                        }
                    }
                    catch (e: IOException) {
                        e.printStackTrace()
                    }
                    finally {
                        try {
                            inputStream.close()
                        }
                        catch (e: IOException){
                            e.printStackTrace()
                        }
                    }
                    result = sb.toString()
                }
                else {
                    result = connection.responseMessage
                }
            }
            catch(e:SocketTimeoutException){
                result = "Connection Timeout"
            }
            catch (e: Exception) {
                result = "Error : "+ e.message
            }
            finally {
                connection?.disconnect()
            }

            return result
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            hideProgressDialog()
        }
    }*/

    private inner class CallAPILoginAsyncTask(){
        fun startApiCall(boardName : String, token : String) {
            Log.e("haha", "haha1")
            showProgressDialog(resources.getString(R.string.please_wait))
            lifecycleScope.launch(Dispatchers.IO) {
//                delay(5000L)
                val stringResult = makeApiCall(boardName, token)
                afterCallFinish(stringResult)
                Log.e("haha", "haha2")
            }
        }

        fun makeApiCall(boardName: String, token: String): String {
            var result : String
            var connection : HttpURLConnection? = null
            Log.e("haha", "haha3")

            try{
                val url = URL(Constants.FCM_BASE_URL)
                connection = url.openConnection() as HttpURLConnection?   //Returns a URLConnection instance that represents a connection to the remote object referred to by the URL.
                connection!!.doInput = true  //doInput tells if we get any data(by default doInput will be true and doOutput false)
                connection!!.doOutput = true //doOutput tells if we send any data with the api call
                connection.instanceFollowRedirects = false
                connection.requestMethod = "POST"
                Log.e("haha", "haha4")

                connection.setRequestProperty("Content-Type", "application/json")
                connection.setRequestProperty("charset", "utf-8")
                connection.setRequestProperty("Accept", "application/json")
                connection.setRequestProperty(Constants.FCM_AUTHORIZATION, "${Constants.FCM_KEY}=${Constants.FCM_SERVER_KEY}")

                connection.useCaches = false

                val wr = DataOutputStream(connection.outputStream)

                val jsonRequest = JSONObject()
                val dataObject = JSONObject()

                dataObject.put(Constants.FCM_KEY_TITLE, "Assigned to the Board $boardName")
                dataObject.put(
                    Constants.FCM_KEY_MESSAGE,
                    "You have been assigned to the new board by ${myAssignedMembersList[0].name}"
                )

                jsonRequest.put(Constants.FCM_KEY_DATA, dataObject)
                jsonRequest.put(Constants.FCM_KEY_TO, token)


                wr.writeBytes(jsonRequest.toString())
                wr.flush() // Flushes this data output stream.
                wr.close() // Closes this output stream and releases any system resources associated with the stream


                val httpResult : Int = connection.responseCode
                if(httpResult == HttpURLConnection.HTTP_OK){
                    //now once we have established a successful connection, we want to read the data.
                    Log.e("haha", "haha5")
                    //Returns an input stream that reads from this open connection. A SocketTimeoutException can be thrown when
                    // reading from the returned input stream if the read timeout expires before data is available for read.
                    val inputStream = connection.inputStream

                    val reader = BufferedReader(InputStreamReader(inputStream))
                    val stringBuilder = StringBuilder()
                    var line : String?

                    try{
                        while (reader.readLine().also { line = it } != null) {
                            stringBuilder.append(line+"\n")
                            Log.i("TAG", "doInBackground: $line\n")
                        }
                    }
                    catch (e:IOException){
                        e.printStackTrace()
                    }
                    finally {
                        try {
                            inputStream.close()
                        }
                        catch (e:IOException){
                            e.printStackTrace()
                        }
                    }
                    result = stringBuilder.toString()
                }
                else{
                    result = connection.responseMessage
                }
            }
            catch (e:SocketTimeoutException){
                result = "Connection Timeout"
            }
            catch (e:Exception){
                result = "Error + ${e.message}"
            }
            finally {
                connection?.disconnect()
            }

            return result
        }

        fun afterCallFinish(result: String?) {
            hideProgressDialog()
            Log.i("JSON RESPONSE RESULT", result.toString())
        }

    }


}