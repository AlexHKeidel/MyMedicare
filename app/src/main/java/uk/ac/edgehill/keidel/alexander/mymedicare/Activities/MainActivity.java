package uk.ac.edgehill.keidel.alexander.mymedicare.Activities;

import android.app.DialogFragment;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import uk.ac.edgehill.keidel.alexander.mymedicare.CustomDialogs.TypeOfReadingPickerDialog;
import uk.ac.edgehill.keidel.alexander.mymedicare.CustomSQLite.DatabaseContract;
import uk.ac.edgehill.keidel.alexander.mymedicare.CustomSQLite.MyDatabaseHelper;
import uk.ac.edgehill.keidel.alexander.mymedicare.R;
import uk.ac.edgehill.keidel.alexander.mymedicare.SharedPreferenceHandler.SharedPreferenceHandler;

/**
 * Alexander Keidel 22397868
 * Main Activity that contains the main interface, displaying the different options, readings and more.
 */
public class MainActivity extends AppCompatActivity {
    private SharedPreferenceHandler sph;
    private int colourPreference, textSizePreference;
    private MyDatabaseHelper mdh;
    private SQLiteDatabase myDatabase;
    private static final String SQL_QUERY_ALL_USER_DETAILS = "SELECT * FROM " + DatabaseContract.PersonalInformationFeeder.TABLE_NAME;
    private static int currentUserID = 0; //current user signed into the application used to store values assosiated with their name in the database
    private static final String SQL_QUERY_ALL_TMP_READINGS = "SELECT * FROM " + DatabaseContract.TemperatureReadingFeeder.TABLE_NAME;
    private static final String SQL_QUERY_ALL_HR_READINGS = "SELECT * FROM " + DatabaseContract.HeartRateReadingFeeder.TABLE_NAME;
    private static final String SQL_QUERY_ALL_BP_READINGS = "SELECT * FROM " + DatabaseContract.BloodPressureReadingFeeder.TABLE_NAME;
    private static final String SQL_QUERY_ALL_GP_DETAILS = "SELECT * FROM " + DatabaseContract.GeneralPracticianFeeder.TABLE_NAME;
    private View mainActivityLayout;
    private View mainActivityScrollView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar); //find id of my toolbar, for the styles find res/menu/actionbar.xml
        mainActivityLayout = (LinearLayout) findViewById(R.id.mainActivityLinearLayout);
        mainActivityScrollView = (ScrollView) findViewById(R.id.mainActivityScrollView);
        setSupportActionBar(myToolbar); //set up my custom toolbar
        setupDBHelper(); //setup the db used for this application
        displayAllReadingsOnScreen(); //display all the readings on the screen
        sph = new SharedPreferenceHandler(this); //setup shared preference handler
        getUserPreferences();
    }

    @Override
    protected void onResume() {
        getUserPreferences();
        areGpDetailsEntered(); //check if gp details are entered
        displayAllReadingsOnScreen(); //display all readings again
        mainActivityLayout.setBackgroundColor(getResources().getColor(colourPreference));
        mainActivityScrollView.setBackgroundColor(getResources().getColor(colourPreference));
        if(!isUserRegistered()) { //check if user is registered
            Intent tmp = new Intent(this, WelcomeActivity.class);
            startActivity(tmp);
        }
        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.actionbar, menu);
        return true;
    }

    @Override
    public void onUserInteraction() {
        displayAllReadingsOnScreen();
        super.onUserInteraction();
    }

    /**
     * Check which toolbar icon has been pressed and execute the correct response.
     * Cases: add a new reading, open the settings or edit the input user data
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent myIntent;
        switch (item.getItemId()) {
            case R.id.action_settings: //start settings activity
                myIntent = new Intent(this, SettingsAcitivty.class);
                this.startActivity(myIntent);
                break;

            case R.id.action_addNewReading:
                //Toast.makeText(getBaseContext(), "Adding new readings are not currently supported.", Toast.LENGTH_SHORT).show();
                DialogFragment torpd = new TypeOfReadingPickerDialog();
                torpd.show(getFragmentManager(), "typeofreading");
                break;

            case R.id.action_accountManagement: //start a new registration activity
                myIntent = new Intent(this, RegistrationActivity.class);
                this.startActivity(myIntent);
                break;

            case R.id.action_gpDetails:
                myIntent = new Intent(this, GPDetailsAcitivty.class);
                this.startActivity(myIntent);
                break;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    /**
     * Set up the database helper for this activity. This is safe to use even if the db already exists.
     */
    private void setupDBHelper(){
        mdh = new MyDatabaseHelper(getBaseContext());
    }

    /**
     * Checks if a user is already registered inside the database
     * @return
     */
    private synchronized boolean isUserRegistered(){
        myDatabase = mdh.getReadableDatabase();
        Cursor tmp = myDatabase.rawQuery(SQL_QUERY_ALL_USER_DETAILS, null);
        if(tmp != null && tmp.moveToFirst()){
            tmp.close();
            myDatabase.close();
            return true;
        } else {
            tmp.close();
            myDatabase.close();
            Intent myIntent = new Intent(this, RegistrationActivity.class);
            this.startActivity(myIntent);
            return false; //user is not registered yet, start {@link #RegistrationAcitivity}
        }
    }

    /**
     * Checks if the details of the gp are entered into the database
     * @return true if the gp data is present, otherwise false
     */
    private synchronized boolean areGpDetailsEntered(){
        myDatabase = mdh.getReadableDatabase();
        Cursor tmp  = myDatabase.rawQuery(SQL_QUERY_ALL_GP_DETAILS, null);
        if(tmp != null && tmp.moveToFirst()){ //gp details are entered if this returns true
            tmp.close();
            myDatabase.close();
            return true;
        }
        tmp.close();
        myDatabase.close();
        Intent myIntent = new Intent(this, GPDetailsAcitivty.class);
        this.startActivity(myIntent);
        return false;
    }

    /**
     * Display all the readings on the screen using text views.
     */
    private void displayAllReadingsOnScreen(){
        View myLayout = findViewById(R.id.mainActivityLinearLayout);
        ((ViewGroup) myLayout).removeAllViews(); //clear the layout
        displayAllBPReadings();
        displayAllHRReadings();
        displayAllTMPReadings();
    }

    /**
     * Displays all the blood pressure readings including date, time, the reading, all in the colour
     * representing the risk level.
     */
    private void displayAllBPReadings(){
        TextView t = new TextView(this);
        View myLayout = findViewById(R.id.mainActivityLinearLayout);
        t.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        t.setGravity(Gravity.CENTER_HORIZONTAL); //set text gravity to center horizontal
        t.setTextSize(textSizePreference + 5); //setting text size
        t.setText("Blood Pressure Readings"); //display the text
        ((LinearLayout)myLayout).addView(t); //adding the view to the layout on the main activity, see http://stackoverflow.com/questions/3204852/android-add-a-textview-to-linear-layout-programmatically

        myDatabase = mdh.getReadableDatabase();
        Cursor cursor = myDatabase.rawQuery(SQL_QUERY_ALL_BP_READINGS, null);
        if(cursor != null & cursor.moveToFirst()){
            do{
                TextView t1 = new TextView(this);
                View myLayout1 = findViewById(R.id.mainActivityLinearLayout);
                String date = cursor.getString(3);
                date = date.substring(0,2) + "." + date.substring(2,4) + "." + date.substring(4, 8) + " at " + date.substring(9,11) + ":" + date.substring(11,13) + ":" + date.substring(13,15); //formatted the date into a suitable String
                String bpreadings = "H:" + cursor.getString(1) + " L:" + cursor.getString(2); //readings in readable String format
                String displayText = date + " BP = " + bpreadings;
                t1.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                switch(cursor.getString(4)){ //set text colour based on risk level
                    case "High Risk":
                        t1.setTextColor(getResources().getColor(R.color.highRiskCategory));
                        break;
                    case "Low Risk":
                        t1.setTextColor(getResources().getColor(R.color.lowRiskCategory));
                        break;
                    case "Normal":
                        t1.setTextColor(getResources().getColor(R.color.normalRiskCategory));
                        break;
                }
                t1.setGravity(Gravity.CENTER_HORIZONTAL); //set text gravity to center horizontal
                t1.setTextSize(textSizePreference); //set text size
                t1.setText(displayText); //display the text
                ((LinearLayout)myLayout1).addView(t1); //adding the view to the layout on the main activity, see http://stackoverflow.com/questions/3204852/android-add-a-textview-to-linear-layout-programmatically
            } while(cursor.moveToNext());
        }
    }

    /**
     * Displays all the heart rate readings on the screen
     */
    private void displayAllHRReadings(){
        TextView t = new TextView(this);
        View myLayout = findViewById(R.id.mainActivityLinearLayout);
        t.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        t.setGravity(Gravity.CENTER_HORIZONTAL); //set text gravity to center horizontal
        t.setTextSize(textSizePreference + 5); //setting text size
        t.setText("Heart Rate Readings"); //display the text
        ((LinearLayout)myLayout).addView(t); //adding the view to the layout on the main activity, see http://stackoverflow.com/questions/3204852/android-add-a-textview-to-linear-layout-programmatically
        myDatabase = mdh.getReadableDatabase();
        Cursor cursor = myDatabase.rawQuery(SQL_QUERY_ALL_HR_READINGS, null);
        if(cursor != null & cursor.moveToFirst()){
            do{
                TextView t1 = new TextView(this);
                View myLayout1 = findViewById(R.id.mainActivityLinearLayout);
                String date = cursor.getString(2);
                date = date.substring(0,2) + "." + date.substring(2,4) + "." + date.substring(4, 8) + " at " + date.substring(9,11) + ":" + date.substring(11,13) + ":" + date.substring(13,15); //formatted the date into a suitable String
                String hrReadings = "BPM:" + cursor.getString(1); //readings in readable String format
                String displayText = date + " HR = " + hrReadings;
                t1.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                switch(cursor.getString(3)){ //set text colour based on risk level
                    case "High Risk":
                        t1.setTextColor(getResources().getColor(R.color.highRiskCategory));
                        break;
                    case "Low Risk":
                        t1.setTextColor(getResources().getColor(R.color.lowRiskCategory));
                        break;
                    case "Normal":
                        t1.setTextColor(getResources().getColor(R.color.normalRiskCategory));
                        break;
                }
                t1.setGravity(Gravity.CENTER_HORIZONTAL); //set text gravity to center horizontal
                t1.setTextSize(textSizePreference); //set text size
                t1.setText(displayText); //display the text
                ((LinearLayout)myLayout1).addView(t1); //adding the view to the layout on the main activity, see http://stackoverflow.com/questions/3204852/android-add-a-textview-to-linear-layout-programmatically
            } while(cursor.moveToNext());
        }
    }

    /**
     * Display all temperature readings saved in the database on the screen.
     */
    private void displayAllTMPReadings(){
        TextView t = new TextView(this);
        View myLayout = findViewById(R.id.mainActivityLinearLayout);
        t.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        t.setGravity(Gravity.CENTER_HORIZONTAL); //set text gravity to center horizontal
        t.setTextSize(textSizePreference + 5); //setting text size
        t.setText("Temperature Readings"); //display the text
        ((LinearLayout)myLayout).addView(t); //adding the view to the layout on the main activity, see http://stackoverflow.com/questions/3204852/android-add-a-textview-to-linear-layout-programmatically
        myDatabase = mdh.getReadableDatabase();
        Cursor cursor = myDatabase.rawQuery(SQL_QUERY_ALL_TMP_READINGS, null);
        if(cursor != null & cursor.moveToFirst()){
            do{
                TextView t1 = new TextView(this);
                View myLayout1 = findViewById(R.id.mainActivityLinearLayout);
                String date = cursor.getString(3);
                date = date.substring(0,2) + "." + date.substring(2,4) + "." + date.substring(4, 8) + " at " + date.substring(9,11) + ":" + date.substring(11,13) + ":" + date.substring(13,15); //formatted the date into a suitable String
                String tmpReadings = cursor.getString(1); //readings in readable String format
                String displayText = date + " TMP = " + tmpReadings;
                t1.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                switch(cursor.getString(2)){ //set text colour based on risk level
                    case "High Risk":
                        t1.setTextColor(getResources().getColor(R.color.highRiskCategory));
                        break;
                    case "Low Risk":
                        t1.setTextColor(getResources().getColor(R.color.lowRiskCategory));
                        break;
                    case "Normal":
                        t1.setTextColor(getResources().getColor(R.color.normalRiskCategory));
                        break;
                }
                t1.setGravity(Gravity.CENTER_HORIZONTAL); //set text gravity to center horizontal
                t1.setTextSize(textSizePreference); //set text size
                t1.setText(displayText); //display the text
                ((LinearLayout)myLayout1).addView(t1); //adding the view to the layout on the main activity, see http://stackoverflow.com/questions/3204852/android-add-a-textview-to-linear-layout-programmatically
            } while(cursor.moveToNext());
        }
    }

    /**
     * Update the user preferences for local use within this activity
     */
    private void getUserPreferences(){
        sph = new SharedPreferenceHandler(this);
        colourPreference = sph.getUserColourChoice();
        System.out.println("colourPreference = " + colourPreference);
        textSizePreference = sph.getUserTextSizeChoice();
        System.out.println("textSizePreference = " + textSizePreference);
    }
}
