package com.example.sj306.wakebot;

import android.app.IntentService;
import android.content.Intent;


public class nextDayServiceIntent extends IntentService {
    private boolean isWaiting = false;
    private boolean doneWaiting = false;
    private String serviceMessage = "";
    public nextDayServiceIntent () {
        super("nextDayServiceIntent ");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        //When this intent is called it uses the time in a day to split the accumilation to the
        //current time so far "System.currentTimeMillis()" into days then rounds it down to get a whole
        //number which represents time till the current day started it adds a day to represent the next day
        // then while the current time has not caughht up to the nxt day it waits the time period of
        // current time to the next day
        //while its waiting sets isWaiting boolean true and when whe while loop completes ses doneWaiting boolean to
        // true
        // this represents waitin till the next day
        // if both boolean are true it sends an intent to train page to let the the first input for
        // the day and to ring page to say thats its done waiting
        //Then sets boh boolean bacvk to false to carryh on the loop
        long millisInDay = 86400000;
        long currentTime = System.currentTimeMillis();
        double doubleDayFactor = currentTime/millisInDay;
        long dayFactor = (long) doubleDayFactor;
        long timeTillThisMorning = dayFactor * millisInDay;
        long timeTillTomorow = timeTillThisMorning + millisInDay;

        while(System.currentTimeMillis()<timeTillTomorow){
        try{
            wait(timeTillTomorow - System.currentTimeMillis());
            isWaiting = true;
        }catch (InterruptedException e){}
        }
        doneWaiting = true;
        if(isWaiting&&doneWaiting){
            serviceMessage = "Complete";
            Intent tServiceInt = new Intent(this, TrainPage.class);
            tServiceInt.putExtra("Done Waiting?", serviceMessage);
            Intent rServiceInt = new Intent(this, RingPage.class);
            rServiceInt.putExtra("Done Waiting?", serviceMessage);
        }
        isWaiting = false;
        doneWaiting = false;




    }
}
