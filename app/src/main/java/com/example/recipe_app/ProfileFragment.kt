package com.example.recipe_app

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button

//Havent finish because out of time
class ProfileFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        val logoutButton: Button = view.findViewById(R.id.logOutButton)

        logoutButton.setOnClickListener {
            //return to Splash Activity
            val intent = Intent(requireActivity(), SplashActivity::class.java)
            startActivity(intent)
            requireActivity().finish() // close MainActivity
        }

        return view
    }


}