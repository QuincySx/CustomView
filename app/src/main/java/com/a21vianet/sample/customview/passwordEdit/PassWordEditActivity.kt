package com.a21vianet.sample.customview.passwordEdit

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.a21vianet.sample.customview.R

class PassWordEditActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pass_word_edit)
        findViewById<PassWordEditTextView>(R.id.passWordEditText).apply {
            setOnEditCompleteListener {
                Toast.makeText(this@PassWordEditActivity, it, Toast.LENGTH_SHORT).show()
                clear()
            }
        }
    }
}