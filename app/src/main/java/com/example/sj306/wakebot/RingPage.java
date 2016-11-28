package com.example.sj306.wakebot;

import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import java.util.StringTokenizer;

public class RingPage extends AppCompatActivity {
    private boolean hadFirst = false;
    private boolean gotValue = false;
    private String servMessage = "";
    private String comTrainData = "";
    private long[] finalWeekArray = new long[7];
    private int currentToken = 0;
    private String inHouseData = "";
    private int weekCount = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Wits for the wait message from service
        //if recieved gets train page dat for the first time
        //  the first time it does this is becauses when the process are finished in the train page presumebly the
        // next day has not started e.g. data finished on saturday 5 and the data says the next ring day is monday 2
        // then the next time it uses the already stored value it got the first time

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ring_page);
        Bundle serviceData = getIntent().getExtras();
        if(serviceData==null){
            return;
        }
        servMessage = serviceData.getString("Done Waiting?");
        if(servMessage==null){
            return;
        }
        else if(servMessage.equals("Complete")){
            gotValue = true;
        }
        while(true) {
            if (!hadFirst) {
                getTrainPageData();
                hadFirst = true;
                inHouseData = comTrainData;
            } else if (hadFirst && gotValue) {
                useData(inHouseData);
            } else {
                return;
            }
            ringOnStoredtime();
        }
    }
    public void useData(String build){
        toWeekArray(build);
    }
    public void getTrainPageData(){
        //gets the string from train page
        //if receved value from service
        Bundle trainData = getIntent().getExtras();
        if(trainData==null){
            return;
        }
        if(gotValue){
            comTrainData = trainData.getString("Finished training");
            if(comTrainData!=null){
                toWeekArray(comTrainData);
            }
        }
    }
    public void toWeekArray(String build){
        //translates string into an long array
        StringTokenizer harvest = new StringTokenizer(build, "");
        if (currentToken < 7){//Can use weeks.Length instead of dayAmount
            finalWeekArray[currentToken] = Long.parseLong(harvest.nextToken());
            currentToken += 1;
        }
    }
    public void ringOnStoredtime(){
        //rings on the time stored in the array plus a week
        // also the data saved is a week behind each time it is used
        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
        long millisInDay = 86400000;
        long millisInWeek = millisInDay*7;
        //doesnt account for days passing the stored time e.g. current time is thurs at 3 and we are using
        // a value from monday at 5
        if(System.currentTimeMillis()>finalWeekArray[weekCount]+millisInWeek){
            r.play();
            finalWeekArray[weekCount] += millisInWeek;
            weekCount += 1;
            if(weekCount>7){
                weekCount = 0;
            }
        }

    }
    public void onRingClick(){
        //when the button is clicked stops the ringing
        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
        r.stop();
    }

}
