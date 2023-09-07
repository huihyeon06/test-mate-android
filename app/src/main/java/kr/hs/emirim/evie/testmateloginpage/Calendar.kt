package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import kr.hs.emirim.evie.testmateloginpage.GoalMainActivity
import kr.hs.emirim.evie.testmateloginpage.R

class Calendar : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calendar)


        // navgation
        var navHome : ImageButton = findViewById(R.id.nav_home)
        var navWrong : ImageButton = findViewById(R.id.nav_wrong)
        var navGoal : ImageButton = findViewById(R.id.nav_goal)
        var navCal : ImageButton = findViewById(R.id.nav_cal)

//        navHome.setOnClickListener {
//            val intent = Intent(this, HomeActivity::class.java)
//            startActivity(intent)
//        }
        navWrong.setOnClickListener {
            val intent = Intent(this, Wrong_answer_note::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(intent)
        }
        navGoal.setOnClickListener {
            val intent = Intent(this, GoalMainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(intent)
        }
        navCal.setOnClickListener {
            val intent = Intent(this, Calendar::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(intent)
        }

    }
}
