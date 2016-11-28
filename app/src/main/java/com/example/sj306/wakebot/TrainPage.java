package com.example.sj306.wakebot;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import java.util.StringTokenizer;

public class TrainPage extends AppCompatActivity {
    private boolean hadFirst = false;
    private boolean gotValue = false;
    private String timesSoFar = "";
    private long firstToday = 0;
    private int currentDay = 0;
    private int dayAmount = 0;
    private int currentToken = 0;
    private long[] month = new long[28];
    private long[] weeks = new long[14];
    private long[] total = new long[7];
    private boolean finishedWeek = false;
    private boolean finishedMonth = false;
    private int totCount = 0;
    private String strWeek = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_train_page);
        //starts the nextDay service do no waiting on click is needed
        Intent intent = new Intent(this,nextDayServiceIntent.class);
        startService(intent);

    }
    public void onClick(View v){
        //Check if the first value of the day has been entered if so doesnt record the current time
        //else it does
        //Then sets the got value based on whether it has recieved a value from the next day service
        //Then sets the hadFirst boolean based the got value
        //Then chooses how to save the input array into string based on input from option page
        //which says whether its 2 weeks or a month
        Bundle optionData = getIntent().getExtras();
        if(optionData==null){
            return;
        }
        if(!hadFirst){
            firstToday = System.currentTimeMillis();
        }
        else{
            firstToday = 0;
        }
        setGotValue();
        setHadFirst();
        String choice = optionData.getString("choice");

        if(choice==null){
            return;
        }
        else if(choice.equals("2 weeks")){
            saveTime(14);
            dayAmount = 14;

        }
        else if(choice.equals("A month")){
            saveTime(28);
            dayAmount = 28;

        }
        else{
            return;
        }
    }

    @Override
    protected void onResume() { //not sure where to put it instead of onclick as is inefficient in onclick
        //This method gets the saved string and tranfered and checkes if the last value has been entered depending
        // on the option sele1cted tranfered ito respected array then puts that array into the total array as a string
        // then its saves the total array then agian if either of the option  array are completed saves
        // gets the string saved and sends it to ring page and starts ring page
        SharedPreferences timePref = getSharedPreferences("user input", Context.MODE_PRIVATE);
        timesSoFar = timePref.getString("Times so far", "");
        if(finishedWeek) {
            toWeekArray(timesSoFar);
            toTotalArray(weeks);
        }
        else if(finishedMonth){
            toMonthArray(timesSoFar);
            toTotalArray(month);
        }
        else{
            return;
        }
        saveToTotal();
        if(finishedMonth||finishedWeek) {
            strWeek = timePref.getString("The completed week average", "");
            Intent i = new Intent(this, RingPage.class);
            i.putExtra("Finished training", strWeek);
            startActivity(i);
        }
        else{
            return;
        }
    }
    public void setGotValue(){
        //Every time this is called it checks to see if it has recieved a value from the
        //nextDay service intent(which waits the next day) if so sets gotValue to true else gotValue is false
        Bundle serviceData = getIntent().getExtras();
        if(serviceData==null){
            return;
        }
        String servValue = serviceData.getString("Done Waiting?");
        if(servValue==null){
            return;
        }
        else if(servValue.equals("Complete")){
            gotValue = true;
        }
        else{
            gotValue = false;
        }
    }
    public void setHadFirst(){
        //if gotValue is true sets hadFirst to false so it can recieve a new value
        //then sets gotValue back to false to restart the process
        if(gotValue){
            hadFirst = false;
            gotValue = false;
        }
        else{
            hadFirst = true;
        }
    }
    public void saveTime(int days){
        //uses shared prefrences to save the current values in the array so far since the types are limited
        // i have stranslated to string
        //Then it checks  if it has finished depending on the option selected and if the last input has been entered
        SharedPreferences timePref = getSharedPreferences("user input", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = timePref.edit();
        StringBuilder dwarvenString = new StringBuilder();
        if(currentDay<(days-1)){
            dwarvenString.append(firstToday).append(",");
            currentDay += 1;
        }
        editor.putString("Times so far", dwarvenString.toString());
        if(days==14&&currentDay==13){
            finishedWeek = true;
        }
        if(days==28&&currentDay==27){
            finishedMonth = true;
        }
    }
    public void saveToTotal(){
        //This does the same as the method above "saveTime" only there is no option as saves it to the same place
        SharedPreferences timePref = getSharedPreferences("user input", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = timePref.edit();
        StringBuilder elvenString = new StringBuilder();
        if(totCount<7){
            elvenString.append(total[totCount]).append(",");
            totCount += 1;
        }
        editor.putString("The completed week average", elvenString.toString());
    }
    public void toWeekArray(String build){
        //This method uses a tokenizer to split any string into its array components in this case the
        //string built from user input
        //puts it into the weeks array as long values
        StringTokenizer harvest = new StringTokenizer(build, "");
        if (currentToken < (dayAmount - 1)) {//Can use weeks.Length instead of dayAmuont
            weeks[currentToken] = Long.parseLong(harvest.nextToken());
            currentToken += 1;
        }
    }
    public void toMonthArray(String build){
        //This method does the same as the method above only saves into the month array
        StringTokenizer harvest = new StringTokenizer(build, "");
        if (currentToken < (dayAmount - 1)) {//Can use weeks.Length instead of dayAmuont
            month[currentToken] = Long.parseLong(harvest.nextToken());
            currentToken += 1;
        }
    }
    public void toTotalArray(long[] timePeriod){
        //Depending on the timePeriod long array inputed ("2 weeks" or "A month") for every day in
        //it checks if there is a value for that day next week if there is it adds it to the total and divedes
        //by the number of iterations for that day and sets both values to zero to begin the week again
        //doesnt account for a day not being recorded
        boolean firstValue = false;
        int dayInWeek = 0;
        int itPerDay = 0;
        for(int dayOfWeek=0; dayOfWeek<7; dayOfWeek++){
            firstValue = false;
            while(timePeriod[(dayInWeek+dayOfWeek)+7]!=0) {
                if(!firstValue) {
                    total[dayOfWeek] += timePeriod[(dayInWeek+dayOfWeek)];
                    dayInWeek += 7;
                    itPerDay += 1;
                    firstValue = true;
                }
                else {
                    total[dayOfWeek] += timePeriod[(dayInWeek+dayOfWeek)+7];
                    dayInWeek += 7;
                    itPerDay += 1;
                }
            }
            double doubleCurrDay = total[dayOfWeek]/itPerDay;
            long currDay = (long) doubleCurrDay;
            total[dayOfWeek] = currDay;
            dayInWeek = 0;
            itPerDay = 0;
        }

    }



}
