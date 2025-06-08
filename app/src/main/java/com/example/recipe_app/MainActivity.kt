package com.example.recipe_app

import android.content.Intent
import android.os.Bundle
import android.widget.FrameLayout

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.recipe_app.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {
    private lateinit var binding : ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.bottomNavigationView.setOnItemSelectedListener {
            when(it.itemId){
                R.id.home -> {
                    replaceFragment(HomeFragment())
                    true
                }
                R.id.profile -> {
                    replaceFragment(ProfileFragment())
                    true
                }
                R.id.addRecipe -> {
                replaceFragment(AddRecipeFragment())
                true
            }
                else -> false
            }
        }

        if (savedInstanceState == null) {
            replaceFragment(HomeFragment())
            binding.bottomNavigationView.selectedItemId = R.id.home
        }

    }

    private fun replaceFragment(fragment : Fragment){
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frameLayout, fragment)
        fragmentTransaction.commit()
    }




}

