package com.example.recipe_app

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.recipe_app.databinding.ActivitySplashBinding
import java.io.File
import java.io.FileOutputStream

class SplashActivity : AppCompatActivity() {

    lateinit var binding:ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding=ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.startBtn.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        //check whether filesDir have these 2 json file or not, if not then only copy to there, if yes then no need
        val filesDir = this.filesDir
        val recipesFile = File(filesDir, "recipes.json")
        if (!recipesFile.exists()) {
            copyJsonFromAssets(this, "recipes.json")
        }

        val recipeTypesFile = File(filesDir, "recipetypes.json")
        if (!recipeTypesFile.exists()) {
            copyJsonFromAssets(this, "recipetypes.json")
        }
    }

    fun copyJsonFromAssets(context: Context, fileName: String) {
        val inputStream = context.assets.open(fileName)
        val outFile = File(context.filesDir, fileName)
        val outputStream = FileOutputStream(outFile)
        inputStream.copyTo(outputStream)
        inputStream.close()
        outputStream.close()
    }
}