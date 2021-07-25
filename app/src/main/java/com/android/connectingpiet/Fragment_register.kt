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
import com.google.firebase.firestore.FirebaseFirestore

class fragment_register : Fragment() {

    companion object{
        const val TAG="RegisterFragment"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_register, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val goToLogin: TextView = view.findViewById(R.id.go_to_login)
        goToLogin.setOnClickListener{
            fragmentManager?.beginTransaction()?.replace(R.id.auth_fragment_container, fragment_login())
                    ?.addToBackStack(null)
                    ?.commit()
        }

        val emailText: TextInputLayout =view.findViewById(R.id.email_text)
        val nameText: TextInputLayout =view.findViewById(R.id.name_text)
        val passwordText: TextInputLayout =view.findViewById(R.id.password_text)
        val confirmPasswordText: TextInputLayout =view.findViewById(R.id.confirm_password_text)
        val registerButton: Button =view.findViewById(R.id.register_button)
        val registerProgress: ProgressBar =view.findViewById(R.id.register_progress)

        registerButton.setOnClickListener{
            val email = emailText.editText?.text.toString()
            val name = nameText.editText?.text.toString()
            val password = passwordText.editText?.text.toString()
            val confirmpassword = confirmPasswordText.editText?.text.toString()
            emailText.error = null
            nameText.error=null
            passwordText.error = null
            confirmPasswordText.error=null

            if (TextUtils.isEmpty(email)) {
                emailText.error = "Email is Required"
                return@setOnClickListener
            }

            if (TextUtils.isEmpty(name)) {
                nameText.error = "Name is Required"
                return@setOnClickListener
            }

            if (TextUtils.isEmpty(password)) {
                passwordText.error = "Password is Required"
                return@setOnClickListener
            }

            if (TextUtils.isEmpty(confirmpassword)) {
                confirmPasswordText.error = "Confirm Your Password"
                return@setOnClickListener
            }

            if(password!=confirmpassword){
                confirmPasswordText.error="Password do not Match"
                return@setOnClickListener
            }

            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                emailText.error = "Please Enter Valid Mail address"
                return@setOnClickListener
            }

            registerProgress.visibility=View.VISIBLE
            val auth= FirebaseAuth.getInstance()

            FirebaseAuth.getInstance().createUserWithEmailAndPassword(email,password)
                    .addOnCompleteListener{task->

                        if(task.isSuccessful){
                            val user = User(auth.currentUser?.uid!!,name, email)
                            val firestore=FirebaseFirestore.getInstance().collection("users")

                            firestore.document(auth.currentUser?.uid!!).set(user)

                                    .addOnCompleteListener {task2->
                                        registerProgress.visibility=View.GONE
                                        if(task2.isSuccessful){
                                            val intent=Intent(activity, MainActivity::class.java)
                                            startActivity(intent)
                                        }
                                        else{
                                            Toast.makeText(context,"Something Went Wrong, Please Try Again",Toast.LENGTH_LONG).show()
                                            Log.d(TAG,task.exception.toString())
                                        }
                                    }

                            val intent=Intent(activity, MainActivity::class.java)
                            startActivity(intent)
                        }else{
                            registerProgress.visibility=View.GONE
                            Toast.makeText(context,"Something Went Wrong, Please Try Again",Toast.LENGTH_LONG).show()
                            Log.d(TAG,task.exception.toString())
                        }
                    }
        }

    }
}