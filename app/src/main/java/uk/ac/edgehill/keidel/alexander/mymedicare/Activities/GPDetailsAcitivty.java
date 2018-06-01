package uk.ac.edgehill.keidel.alexander.mymedicare.Activities;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import uk.ac.edgehill.keidel.alexander.mymedicare.CustomSQLite.DatabaseContract;
import uk.ac.edgehill.keidel.alexander.mymedicare.CustomSQLite.MyDatabaseHelper;
import uk.ac.edgehill.keidel.alexander.mymedicare.R;

public class GPDetailsAcitivty extends AppCompatActivity {

    private Button confirmButton;
    private EditText [] editFields;
    private static final String SQL_INSERT_GP_DETAILS = "INSERT INTO " + DatabaseContract.GeneralPracticianFeeder.TABLE_NAME + " ("
            + DatabaseContract.GeneralPracticianFeeder.NAME + ", "
            + DatabaseContract.GeneralPracticianFeeder.ADDRESS_HOUSE + ", "
            + DatabaseContract.GeneralPracticianFeeder.ADDRESS_POSTCODE + ", "
            + DatabaseContract.GeneralPracticianFeeder.PHONE + ", "
            + DatabaseContract.GeneralPracticianFeeder.USER_ID + ") VALUES (?,?,?,?,?);";
    private static final String SQL_QUERY_GP_DETAILS = "SELECT * FROM " + DatabaseContract.GeneralPracticianFeeder.TABLE_NAME;
    private static final String SQL_DELETE_GP_DETAILS = "DELETE FROM " + DatabaseContract.GeneralPracticianFeeder.TABLE_NAME;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gpdetails);
        setupInterface();
        displayGpDetails();
    }

    /**
     * Setup the interface to be interacted programmatically within other methods
     */
    private void setupInterface(){
        editFields = new EditText[4];
        editFields[0] = (EditText) findViewById(R.id.gpdetailsFullNameEditText);
        editFields[1] = (EditText) findViewById(R.id.gpdetailsAddressStreetEditText);
        editFields[2] = (EditText) findViewById(R.id.gpdetailsAddressPostcodeEditText);
        editFields[3] = (EditText) findViewById(R.id.gpdetailsPhoneNumberEditText);
        confirmButton = (Button) findViewById(R.id.gpdetailsConfirmButton);
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(confirmAndWriteGpDetailsToDb()){
                    Toast.makeText(getBaseContext(), "GP Details Saved", Toast.LENGTH_SHORT).show();
                    finish(); //close this activity
                } else {
                    Toast.makeText(getBaseContext(), "Saving failed. Make sure to enter all the fields!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /**
     * Confirms and writes the gp details in to the sqlite database
     * @return
     */
    private boolean confirmAndWriteGpDetailsToDb(){
        String[] sqlData = new String[5];
        sqlData[0] = editFields[0].getText().toString();
        sqlData[1] = editFields[1].getText().toString();
        sqlData[2] = editFields[2].getText().toString();
        sqlData[3] = editFields[3].getText().toString();
        sqlData[4] = "1";
        for(int i = 0; i < sqlData.length; i++){
            if(sqlData[i] == null || sqlData[i].equals("")){
                return false; //something went wrong
            }
        }
        MyDatabaseHelper mdbh = new MyDatabaseHelper(getBaseContext());
        SQLiteDatabase myDb = mdbh.getWritableDatabase();
        myDb.execSQL(SQL_DELETE_GP_DETAILS); //delete all entries
        myDb.execSQL(SQL_INSERT_GP_DETAILS, sqlData); //write the new gp entry
        return true;
    }

    /**
     * Check and display the gp information if it is present in the database.
     */
    private void displayGpDetails(){
        try {
            MyDatabaseHelper mdbh = new MyDatabaseHelper(getBaseContext());
            SQLiteDatabase myDb = mdbh.getWritableDatabase();
            Cursor cursor = myDb.rawQuery(SQL_QUERY_GP_DETAILS, null);
            if (cursor != null && cursor.moveToFirst()) {
                for (int i = 0; i < editFields.length; i++) {
                    editFields[i].setText(cursor.getString(i + 1));
                }
            }
        } catch (Exception ex){
            ex.printStackTrace(); //no regsitered data
        }
    }
}
