package com.example.sj306.wakebot;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.content.Intent;

public class OptionPage extends AppCompatActivity {
    private String choice = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_option_page);
    }

    public void onWeekClick(View v){
        //On week click is set in the xml document to perform this method when clicked
        //This method sends an intent to trainPage activity containing data that 2 weeks is
        //entered and starts the activity
        Intent intent = new Intent(this, TrainPage.class);
        choice = "2 weeks";
        intent.putExtra("choice", choice);
        startActivity(intent);
    }
    public void onMonthClick(View v){
        //This method is exactly the same as the one above only it performs the action based on
        // if a month is chosen instead of a week
        Intent intent = new Intent(this, TrainPage.class);
        choice = "A month";
        intent.putExtra("choice", choice);
        startActivity(intent);
    }


}
