package com.android.connectingpiet

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.*

class fragment_login : Fragment() {
    companion object{
        const val TAG="LoginFragment"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val goToRegister:TextView = view.findViewById(R.id.go_to_register)
        goToRegister.setOnClickListener{
            fragmentManager?.beginTransaction()?.replace(R.id.auth_fragment_container, fragment_register())
                ?.addToBackStack(null)
                ?.commit()
        }
        val emailText:TextInputLayout=view.findViewById(R.id.email_text)
        val passwordText:TextInputLayout=view.findViewById(R.id.password_text)
        val loginButton:Button=view.findViewById(R.id.login_button)
        val loginProgress:ProgressBar=view.findViewById(R.id.login_progress)

        loginButton.setOnClickListener {
            val email = emailText.editText?.text.toString()
            val password = passwordText.editText?.text.toString()
            emailText.error = null
            passwordText.error = null


            if (TextUtils.isEmpty(email)) {
                emailText.error = "Email is Required"
                return@setOnClickListener
            }
            if (TextUtils.isEmpty(password)) {
                passwordText.error = "Password is Required"
                return@setOnClickListener
            }
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                emailText.error = "Please Enter Valid Mail address"
                return@setOnClickListener
            }
            loginProgress.visibility=View.VISIBLE

            FirebaseAuth.getInstance().signInWithEmailAndPassword(email,password)
                .addOnCompleteListener{task->
                    loginProgress.visibility=View.GONE
                    if(task.isSuccessful){
                        val intent = Intent(activity, MainActivity::class.java)
                        startActivity(intent)
                    }else{
                        Toast.makeText(context,"Something Went Wrong. Please try again.",Toast.LENGTH_LONG).show()
                        Log.d(TAG,task.exception.toString())
                    }
                }
        }


    }
}