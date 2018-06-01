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
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;

import uk.ac.edgehill.keidel.alexander.mymedicare.CustomSQLite.DatabaseContract;
import uk.ac.edgehill.keidel.alexander.mymedicare.CustomSQLite.MyDatabaseHelper;
import uk.ac.edgehill.keidel.alexander.mymedicare.R;
import uk.ac.edgehill.keidel.alexander.mymedicare.SmsHelper.SmsHelper;

/**
 * Created by Alexander Keidel, 22397868 on 04/05/2016.
 * See http://developer.android.com/guide/topics/ui/dialogs.html
 * and http://stackoverflow.com/questions/5369682/get-current-time-and-date-on-android
 * for used supporting documentation.
 * DialogFragment to assert the users blood pressure and save it in the database, whilst providing risk level assessment
 */
public class BloodPressureDialog extends DialogFragment {
    private EditText highEditText, lowEditText;

    private static final String SQL_QUERY_ALL_USER_DETAILS = "SELECT * FROM " + DatabaseContract.PersonalInformationFeeder.TABLE_NAME;
    private static final String SQL_INSERT_BP_READING = "INSERT INTO " + DatabaseContract.BloodPressureReadingFeeder.TABLE_NAME + " ("
            + DatabaseContract.BloodPressureReadingFeeder.HIGH_VALUE + ","
            + DatabaseContract.BloodPressureReadingFeeder.LOW_VALUE + ","
            + DatabaseContract.BloodPressureReadingFeeder.DATE_AND_TIME + ","
            + DatabaseContract.BloodPressureReadingFeeder.RISK_CATEGORY +","
            + DatabaseContract.BloodPressureReadingFeeder.USER_ID + ") VALUES (?,?,?,?,?);";
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.bp_dialog, null); //see answer from http://stackoverflow.com/questions/16192378/findviewbyid-returns-null-tried-everything
        builder.setView(dialogView);
        highEditText = (EditText) dialogView.findViewById(R.id.bpHighReadingEditText);
        lowEditText = (EditText) dialogView.findViewById(R.id.bpLowReadingEditText);
        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
                // Add action buttons
                builder.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
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
                            //Write data in the following order: high reading, low reading, date and time, risk category, userID
                            String[] sqlData = new String[5];
                            sqlData[0] = highEditText.getText().toString(); //adding high reading
                            sqlData[1] = lowEditText.getText().toString(); //adding low reading
                            SimpleDateFormat sdf = new SimpleDateFormat("ddMMyyyy_HHmmss"); //see http://stackoverflow.com/questions/5369682/get-current-time-and-date-on-android
                            sqlData[2] = sdf.format(new Date()); //adding date and time
                            sqlData[3] = assessBloodPressureRiskLevel(Integer.parseInt(sqlData[0]), Integer.parseInt(sqlData[1])); //adding risk level
                            sqlData[4] = String.valueOf(userID); //adding user ID for foreign key reference
                            /**
                             * Testing
                             */
                            for (int i = 0; i < sqlData.length; i++) {
                                System.out.println("sqlData[" + i + "] =" + sqlData[i]);
                            }
                            //END OF TESTING
                            myDb.execSQL(SQL_INSERT_BP_READING, sqlData);
                        } catch (Exception ex){
                            ex.printStackTrace();
                            DialogFragment tmp = new BloodPressureDialog();
                            tmp.show(getActivity().getFragmentManager(), "bpreading");
                            Toast.makeText(getActivity().getBaseContext(), "Please enter two values or press cancel.", Toast.LENGTH_LONG).show();
                        }
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        BloodPressureDialog.this.getDialog().cancel();
                    }
                });
        return builder.create();
    }

    /**
     * Assesses the blood pressure risk level and prompts the user with a Toast message.
     * If the risk is high, an SmsHelper will be initiated to send a text message to the GP
     * @param high high reading
     * @param low low reading
     * @return risk level
     */
    private String assessBloodPressureRiskLevel(int high, int low){
        if(high < 120 || high >= 180 || low < 80 || low >= 110){ //evaluation for high risk level of blood pressure
            Toast.makeText(getActivity().getBaseContext(), "Your blood pressure is at high risk, contacting your GP!", Toast.LENGTH_LONG).show();
            SmsHelper tmp = new SmsHelper(getActivity().getBaseContext());
            return "High Risk";
        } else if (high >= 120 || low >= 80){ //evaluation for low risk level of blood pressure
            Toast.makeText(getActivity().getBaseContext(), "Your blood pressure is at low risk, please make another reading soon!", Toast.LENGTH_LONG).show();
            return "Low Risk";
        } else {
            Toast.makeText(getActivity().getBaseContext(), "Your blood pressure is normal.", Toast.LENGTH_SHORT).show();
            return "Normal";
        }
    }
}
