package com.angela.notemoment.login

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.angela.notemoment.Logger
import com.angela.notemoment.MainActivity
import com.angela.notemoment.R
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.firebase.auth.FirebaseAuth


class LoginFragment : Fragment() {

    val RC_SIGN_IN = 12345

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        //fb login
        val authProvider: List<AuthUI.IdpConfig> = listOf(
            AuthUI.IdpConfig.FacebookBuilder().build(),
            AuthUI.IdpConfig.GoogleBuilder().build()
        )

        startActivityForResult(
            AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(authProvider)
                .setAlwaysShowSignInMethodScreen(true)
                .setIsSmartLockEnabled(false)
                .setLogo(R.drawable.flag_taiwan)
                .setTheme(R.style.LoginTheme)
                .build(),
            RC_SIGN_IN
        )

        return inflater.inflate(R.layout.fragment_login, container, false)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            if (resultCode != Activity.RESULT_OK) {
                val response = IdpResponse.fromResultIntent(data)
                if (response == null) {
                    // back button is pressed
                    Toast.makeText(
                        context,
                        "尚未登入",
                        Toast.LENGTH_SHORT
                    ).show()
                    (activity as? MainActivity)?.finish()
                }
            } else {
                // Successfully signed in
                Toast.makeText(context, "登入成功", Toast.LENGTH_SHORT).show()
                val user = FirebaseAuth.getInstance().currentUser
                Logger.i("log in user name ${user?.displayName}")
                findNavController().navigateUp()
            }
        }
    }

    private fun signOut() {
        AuthUI.getInstance()
            .signOut(context!!)
            .addOnSuccessListener {
                Toast.makeText(context, "已登出", Toast.LENGTH_SHORT).show()
            }
    }


}