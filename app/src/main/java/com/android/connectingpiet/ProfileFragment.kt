package com.android.connectingpiet

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.android.connectingpiet.User
import com.android.connectingpiet.UserUtils
import com.bumptech.glide.Glide
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import de.hdodenhof.circleimageview.CircleImageView

class ProfileFragment : Fragment() {

    private lateinit var userImage: CircleImageView

    private var imageUri: Uri? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        userImage = view.findViewById(R.id.user_image)
        val userName: EditText = view.findViewById(R.id.user_name)
        val userBio: EditText = view.findViewById(R.id.user_bio)
        val saveButton: Button = view.findViewById(R.id.save_button)
        val logoutButton: Button = view.findViewById(R.id.logout_button)

        userName.setText(UserUtils.user?.name)
        userBio.setText(UserUtils.user?.bio)

        userImage.setOnClickListener {
            ImagePicker.with(this)
                .crop()
                .compress(1024)
                .maxResultSize(1080, 1080)
                .start()
        }

        context?.let {
            Glide.with(it)
                .load(UserUtils.user?.imageUrl)
                .placeholder(R.drawable.person_icon)
                .centerCrop()
                .into(userImage)
        }

        saveButton.setOnClickListener {
            val newUserName = userName.text.toString()
            val newBio = userBio.text.toString()

            if (newUserName.isBlank()) {
                Toast.makeText(context, "Name field cannot be blank", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            val userDocument = FirebaseFirestore.getInstance().collection("users")
                .document(UserUtils.user?.id.toString())

            val user = User(id = UserUtils.user?.id.toString(), name = newUserName, email = UserUtils.user?.id.toString(),
                bio = newBio)

            userDocument.set(user).addOnCompleteListener {
                if (it.isSuccessful) {
                    Toast.makeText(context, "Profile updated.", Toast.LENGTH_LONG).show()
                    UserUtils.getCurrentUser()
                } else {
                    Toast.makeText(context, "Something went wrong. Please try again.", Toast.LENGTH_LONG).show()
                }
            }
        }

        logoutButton.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            val intent = Intent(activity, AuthenticationActivity::class.java)
            context?.startActivity(intent)
            activity?.finish()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            val fileUri = data?.data
            userImage.setImageURI(fileUri)
            imageUri = fileUri
            addUserImage()
        } else if (resultCode == ImagePicker.RESULT_ERROR) {
            Toast.makeText(context, ImagePicker.getError(data), Toast.LENGTH_LONG).show()
        } else {
            Toast.makeText(context, "Something went wrong. Please try again.", Toast.LENGTH_LONG).show()
        }
    }

    private fun addUserImage() {
        val firestore = FirebaseFirestore.getInstance()
        firestore.collection("users")
            .document(UserUtils.user?.id.toString())
            .get().addOnCompleteListener {
                val storage = FirebaseStorage.getInstance().reference.child("Images")
                    .child(UserUtils.user?.id.toString())

                val uploadTask = storage.putFile(imageUri!!)

                uploadTask.continueWithTask { task ->
                    if (!task.isSuccessful) {
                        Log.d("Upload Task", task.exception.toString())
                    }
                    storage.downloadUrl
                }.addOnCompleteListener { urlTaskCompleted ->
                    if (urlTaskCompleted.isSuccessful) {
                        val downloadUri = urlTaskCompleted.result

                        val newUser = User(id = UserUtils.user?.id.toString(), name = UserUtils.user?.name.toString(),
                            email = UserUtils.user?.email.toString(), bio = UserUtils.user?.bio.toString(),
                            imageUrl = downloadUri.toString())

                        firestore.collection("users").document(UserUtils.user?.id.toString())
                            .set(newUser).addOnCompleteListener { saved ->
                                if (saved.isSuccessful) {
                                    UserUtils.getCurrentUser()
                                    Toast.makeText(context, "Image Uploaded.", Toast.LENGTH_LONG).show()
                                } else {
                                    Toast.makeText(context, "Something went wrong. Please try again", Toast.LENGTH_LONG).show()
                                }
                            }
                    } else {
                        Toast.makeText(context, "Something went wrong. Please try again", Toast.LENGTH_LONG).show()
                    }
                }
            }
    }
}