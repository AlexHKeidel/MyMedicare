package uk.ac.edgehill.keidel.alexander.mymedicare.CustomDialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;

import uk.ac.edgehill.keidel.alexander.mymedicare.CustomSQLite.DatabaseContract;
import uk.ac.edgehill.keidel.alexander.mymedicare.CustomSQLite.MyDatabaseHelper;
import uk.ac.edgehill.keidel.alexander.mymedicare.R;
import uk.ac.edgehill.keidel.alexander.mymedicare.SmsHelper.SmsHelper;

/**
 * Created by Alexander Keidel on 04/05/2016.
 * Temperatue Dialog Fragment used to query the user for their temperature reading and writing it into the database
 */
public class TemperatureDialog extends DialogFragment {
    private EditText tmpReadingEditText;
    private Switch tmpSwitch;
    private static final String SQL_QUERY_ALL_USER_DETAILS = "SELECT * FROM " + DatabaseContract.PersonalInformationFeeder.TABLE_NAME;
    private static final String SQL_INSERT_TMP_READING = "INSERT INTO " + DatabaseContract.TemperatureReadingFeeder.TABLE_NAME + " ("
            + DatabaseContract.TemperatureReadingFeeder.TEMPERATURE_READING_VALUE + ", "
            + DatabaseContract.TemperatureReadingFeeder.RISK_CATEGORY + ", "
            + DatabaseContract.TemperatureReadingFeeder.DATE_AND_TIME + ", "
            + DatabaseContract.TemperatureReadingFeeder.USER_ID + ") VALUES (?,?,?,?);";
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.tmp_dialog, null); //see answer from http://stackoverflow.com/questions/16192378/findviewbyid-returns-null-tried-everything
        tmpReadingEditText = (EditText) dialogView.findViewById(R.id.tmpReadingEditText);
        tmpSwitch = (Switch) dialogView.findViewById(R.id.tmpSwitch);
        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(dialogView)
                // Add action buttons
                .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        try {
                            MyDatabaseHelper mdh = new MyDatabaseHelper(getActivity().getBaseContext());
                            SQLiteDatabase myDb = mdh.getReadableDatabase(); //get readable database to get the user name
                            Cursor userDataCursor = myDb.rawQuery(SQL_QUERY_ALL_USER_DETAILS, null);
                            String userName[] = new String[2];
                            int userID; //init. userID
                            if (userDataCursor != null && userDataCursor.moveToFirst()) {
                                userName[0] = userDataCursor.getString(1);
                                userName[1] = userDataCursor.getString(2); //get the user first name and last name
                                userName[0].replaceAll(",", ""); //removing all commas
                                userName[1].replaceAll(",", ""); //removing all commas
                                userID = userDataCursor.getInt(0);  //getting the user ID
                                System.out.println("userID = " + userID);
                            } else {
                                return; //error!
                            }
                            //Write the data in the following format: temperature reading, risk category, date and time, userID
                            String[] sqlData = new String[4];
                            if (tmpSwitch.isChecked()) { //Write in Farenheit
                                sqlData[0] = tmpReadingEditText.getText().toString() + "°F"; //add reading value
                            } else { //Write in Celcius
                                sqlData[0] = tmpReadingEditText.getText().toString() + "°C"; //add reading value
                            }
                            SimpleDateFormat sdf = new SimpleDateFormat("ddMMyyyy_HHmmss"); //see http://stackoverflow.com/questions/5369682/get-current-time-and-date-on-android
                            sqlData[1] = sdf.format(new Date()); //adding date and time
                            sqlData[2] = assessTemperatureRiskLevel(sqlData[0]); //adding risk level
                            sqlData[3] = String.valueOf(userID); //adding user ID for foreign key reference
                            /**
                             * Testing
                             */
                            for (int i = 0; i < sqlData.length; i++) {
                                System.out.println("sqlData[" + i + "] =" + sqlData[i]);
                            }
                            //END OF TESTING
                            myDb.execSQL(SQL_INSERT_TMP_READING, sqlData);
                        } catch (Exception ex){
                            ex.printStackTrace();
                            Toast.makeText(getActivity().getBaseContext(), "Please enter a value or press cancel",Toast.LENGTH_LONG).show();
                            DialogFragment tmp = new TemperatureDialog();
                            tmp.show(getActivity().getFragmentManager(), "tmpreading");
                        }
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        TemperatureDialog.this.getDialog().cancel();
                    }
                });
        return builder.create();
    }

    /**
     * Assess the risk level of the provided reading
     * @param reading
     * @return
     */
    private String assessTemperatureRiskLevel(String reading){
        float value;
        if(reading.contains("F")){
            reading = reading.replaceAll("°F", ""); //remove the unit identifier
            value = Float.parseFloat(reading);
            if(value <= 93.0f ||value >= 100.4f){
                Toast.makeText(getActivity().getBaseContext(), "Your temperature is at high risk! Contacting your GP!", Toast.LENGTH_LONG).show();
                SmsHelper tmp = new SmsHelper(getActivity().getBaseContext());
                return "High Risk";
            } else if(value >= 98.6f){
                Toast.makeText(getActivity().getBaseContext(), "Your temperature is at low risk! Please make another reading soon!", Toast.LENGTH_LONG).show();
                return "Low Risk";
            } else {

                Toast.makeText(getActivity().getBaseContext(), "Your temperature is normal.", Toast.LENGTH_LONG).show();
                return "Normal";
            }
        } else {
            reading = reading.replaceAll("°C", ""); //remove the unit identifier
            value = Float.parseFloat(reading);
            if(value <= 35.0f || value >= 38.0f){
                Toast.makeText(getActivity().getBaseContext(), "Your temperature is at high risk! Contacting your GP!", Toast.LENGTH_LONG).show();
                SmsHelper tmp = new SmsHelper(getActivity().getBaseContext());
                return "High Risk";
            } else if(value >= 37.0f){
                Toast.makeText(getActivity().getBaseContext(), "Your temperature is at low risk! Please make another reading soon!", Toast.LENGTH_LONG).show();
                return "Low Risk";
            } else {
                Toast.makeText(getActivity().getBaseContext(), "Your temperature is normal.", Toast.LENGTH_LONG).show();
                return "Normal";
            }
        }
    }
}
