package uk.ac.edgehill.keidel.alexander.mymedicare.Activities;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;

import uk.ac.edgehill.keidel.alexander.mymedicare.CustomSQLite.MyDatabaseHelper;
import uk.ac.edgehill.keidel.alexander.mymedicare.R;
import uk.ac.edgehill.keidel.alexander.mymedicare.CustomSQLite.DatabaseContract;
import uk.ac.edgehill.keidel.alexander.mymedicare.SharedPreferenceHandler.SharedPreferenceHandler;

/**
 * Alexander Keidel, 22397868
 * Registration activity used to register new users and write their personal information into the
 * app database.
 * See http://developer.android.com/guide/topics/ui/controls/spinner.html for Spinner implementation
 * and http://stackoverflow.com/questions/3200551/unable-to-modify-arrayadapter-in-listview-unsupportedoperationexception
 * for the used documentation.
 */
public class RegistrationActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener{
    private MyDatabaseHelper mdh;
    private SQLiteDatabase myDatabase;
    private SharedPreferenceHandler sph;
    private View registrationActivityLayout;
    private static final String SQL_INSERT_USER_DETAILS = "INSERT INTO "
            + DatabaseContract.PersonalInformationFeeder.TABLE_NAME + " ("
            + DatabaseContract.PersonalInformationFeeder.FIRST_NAME + ", "
            + DatabaseContract.PersonalInformationFeeder.LAST_NAME + ", "
            + DatabaseContract.PersonalInformationFeeder.DATE_OF_BIRTH + ", "
            + DatabaseContract.PersonalInformationFeeder.ADDRESS_HOUSE + ", "
            + DatabaseContract.PersonalInformationFeeder.ADDRESS_POSTCODE + ", "
            + DatabaseContract.PersonalInformationFeeder.PHONE + ", "
            + DatabaseContract.PersonalInformationFeeder.EMAIL + ") VALUES (?,?,?,?,?,?,?);";

    private static final String SQL_REPLACE_USER_DETAILS = "REPLACE INTO "
            + DatabaseContract.PersonalInformationFeeder.TABLE_NAME + " ("
            + DatabaseContract.PersonalInformationFeeder._ID + ", "
            + DatabaseContract.PersonalInformationFeeder.FIRST_NAME + ", "
            + DatabaseContract.PersonalInformationFeeder.LAST_NAME + ", "
            + DatabaseContract.PersonalInformationFeeder.DATE_OF_BIRTH + ", "
            + DatabaseContract.PersonalInformationFeeder.ADDRESS_HOUSE + ", "
            + DatabaseContract.PersonalInformationFeeder.ADDRESS_POSTCODE + ", "
            + DatabaseContract.PersonalInformationFeeder.PHONE + ", "
            + DatabaseContract.PersonalInformationFeeder.EMAIL + ") VALUES (?,?,?,?,?,?,?,?);";

    private static final String SQL_QUERY_ALL_USER_DETAILS = "SELECT * FROM " + DatabaseContract.PersonalInformationFeeder.TABLE_NAME;
    private static final String SQL_QUERY_USER_BY_NAME = "SELECT * FROM " + DatabaseContract.PersonalInformationFeeder.TABLE_NAME + " WHERE " + DatabaseContract.PersonalInformationFeeder.FIRST_NAME + " = ? AND " + DatabaseContract.PersonalInformationFeeder.LAST_NAME + " = ?";
    private EditText[] editFields;
    private Spinner userSpinner;
    private ArrayAdapter<CharSequence> mySpinnerAdapter;
    private ArrayList<CharSequence> adapterArrList;
    private Button confirmButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        setupDatabaseHelper();
        setupCustomUserInterface();
        readAllRegisteredUserProfiles();
    }

    /**
     * Setting up the database helper to be used by other methods later on.
     */
    private void setupDatabaseHelper(){
        try {
            mdh = new MyDatabaseHelper(getBaseContext());
        }
        catch(Exception ex){
            ex.printStackTrace();
        }
    }

    /**
     * Sets up all the elements displayed on this activity.
     */
    private void setupCustomUserInterface(){
//        registrationActivityLayout = (RelativeLayout) findViewById(R.id.registrationActivityRelativeLayout);
//        registrationActivityLayout.setBackgroundColor(getResources().getColor(sph.getUserColourChoice()));
        userSpinner = (Spinner) findViewById(R.id.registrationSpinner);
        userSpinner.setOnItemSelectedListener(this); //setup listener
        //mySpinnerAdapter = ArrayAdapter.createFromResource(getBaseContext(), R.array.spinnerAdapterStrings, android.R.layout.simple_spinner_item);
        adapterArrList = new ArrayList<>();
        adapterArrList.add(getString(R.string.newUserSpinnerMessage));
        mySpinnerAdapter = new ArrayAdapter<CharSequence>(this, android.R.layout.simple_list_item_1, adapterArrList);
        mySpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        userSpinner.setAdapter(mySpinnerAdapter); //set spinner adapter

        editFields = new EditText[7];
        editFields[0] = (EditText) findViewById(R.id.firstNameEditText);
        editFields[1] = (EditText) findViewById(R.id.lastNameEditText);
        editFields[2] = (EditText) findViewById(R.id.dateOfBirthEditText);
        editFields[3] = (EditText) findViewById(R.id.addressLineOneEditText);
        editFields[4] = (EditText) findViewById(R.id.addresslineTwoEditText);
        editFields[5] = (EditText) findViewById(R.id.phoneNumberEditText);
        editFields[6] = (EditText) findViewById(R.id.emailEditText);

        confirmButton = (Button) findViewById(R.id.confirmDetailsButton);
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(confirmAndSavePersonalDetails()){
                    Toast.makeText(getBaseContext(), R.string.confirmedDetailsMessage, Toast.LENGTH_SHORT).show();
                    finish(); //close the activity
                } else {
                    Toast.makeText(getBaseContext(), R.string.failedToSaveDetailsMessage, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /**
     * Checks if there is an entry in the database with user details and will the invoke {@link #updateFieldsWithUserData(String[])}
     * @return
     */
    private boolean readAllRegisteredUserProfiles(){
        myDatabase = mdh.getReadableDatabase();
        Cursor queryCursor = myDatabase.rawQuery(SQL_QUERY_ALL_USER_DETAILS, null); //query all the user entries
        String[] queryData = new String[7];
        if(queryCursor != null && queryCursor.moveToFirst()){ //if the cursor is not null and has at least one entry
            do {
                for (int i = 0; i < 7; i++) {
                    queryData[i] = queryCursor.getString(i + 1);
                }
                String[] tmp = new String[2]; //new temp string array used to pass the name of the user to the spinner
                tmp[0] = queryData[0];
                tmp[1] = queryData[1];
                writeNameToSpinner(tmp);
            } while (queryCursor.moveToNext()); //read all the user profiles
            updateFieldsWithUserData(queryData); //pass the data to be updated into the interface
            myDatabase.close(); //close database
            return true;
        }
        myDatabase.close(); //close database
        return false; //The user is not registered
    }

    /**
     * Add first and last names of users from the database entries into {@link #mySpinnerAdapter}
     * @param name
     */
    private void writeNameToSpinner(String[] name){
        mySpinnerAdapter.add(name[0] + " " + name[1]);
    }

    /**
     * Update the text fields on this activity with the previously entered user data to show the user
     * that their data has been saved.
     * @param userData
     * @return
     */
    private boolean updateFieldsWithUserData(String [] userData){
        if(userData[0].equals("") || userData == null){
            return false; //there is missing data or the user has not been registered
        }
        for(int i = 0; i < editFields.length; i++){
            userData[i].replaceAll(",", "");//remove all commas from the data before displaying it
            editFields[i].setText(userData[i]); //show the user data from the array
        }
        return true; //correctly displaying all registered user data
    }

    /**
     * Confirm and save the personal details entered into the fields shown on this activity.
     * Shows a toast telling the user whether entries are missing and the saving has failed, or if the
     * saving was successful.
     * @return true if successfully saved, otherwise false
     */
    private boolean confirmAndSavePersonalDetails(){
        try{
            myDatabase = mdh.getWritableDatabase();
            String username = editFields[0].getText().toString() + " " + editFields[1].getText().toString();
            int userID = getUserID(username);
            String [] info;
            System.out.println("userID = " + userID);
            if(userID != -1){ //this user already exists in the database, replace their entry instead of creating a new one
                info = new String[8];
                info[0] = String.valueOf(userID);
                for(int i = 0; i < editFields.length; i++) {
                    info[i + 1] = editFields[i].getText().toString();
                }
                myDatabase.execSQL(SQL_REPLACE_USER_DETAILS, info); //write the data into the DB
                myDatabase.close(); //close db
                return true;
            }
            info = new String[7];
            for(int i = 0; i < editFields.length; i++) {
                info[i] = editFields[i].getText().toString();
                System.out.println("info[" + i + "] = " + info[i]);
                if (info[i] == "" || info[i] == null) {
                    Toast.makeText(RegistrationActivity.this, "Saving failed! Please enter data into all fields!", Toast.LENGTH_SHORT).show();
                    return false;
                }
            }
            myDatabase.execSQL(SQL_INSERT_USER_DETAILS, info); //write the data into the DB
            myDatabase.close(); //close db
            return true;
        } catch (Exception exc){
            exc.printStackTrace();
        }
        return false; //failed to save data
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        setupInterfaceForSelectedOption(parent.getSelectedItem().toString());
    }

    private void setupInterfaceForSelectedOption(String option){
        if(option.equals(getString(R.string.newUserSpinnerMessage))) { //New Entry selected
            clearInterfaceEntries(); //clear the interface
        } else {
            queryUserByFirstAndLastName(option);
        }
    }

    /**
     * Query the database by the displayed name and show their information to be updated
     * @param name
     * @return
     */
    private boolean queryUserByFirstAndLastName(String name){
        String[] nameArr = name.split(" ");//split at the space, WARNING, this means that names with spaces in them are going to throw errors here
        myDatabase = mdh.getReadableDatabase(); //open db in read mode
        Cursor userNameQueryCursor = myDatabase.rawQuery(SQL_QUERY_USER_BY_NAME, nameArr); //query the db
        if(userNameQueryCursor != null && userNameQueryCursor.moveToFirst()){
            String [] dataArr = new String[7];
            for(int i = 0; i < editFields.length; i++){
                dataArr[i] = userNameQueryCursor.getString(i + 1);
            }
            updateFieldsWithUserData(dataArr);
            return true; //successfully update the user data
        }
        return false; //no entry found, error!
    }

    /**
     * Return the userID of the current user in question
     * @param name
     * @return
     */
    private int getUserID(String name){
        String[] nameArr = name.split(" ");
        myDatabase = mdh.getReadableDatabase();
        Cursor c = myDatabase.rawQuery(SQL_QUERY_USER_BY_NAME, nameArr);
        if(c != null && c.moveToFirst()){
            return c.getInt(0);
        }
        else return -1;
    }
    /**
     * Clear all entry fields
     */
    private void clearInterfaceEntries(){
        for(int i = 0; i < editFields.length; i++){
            editFields[i].setText(""); //clear all entry fields
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        //Do nothing
    }
}
