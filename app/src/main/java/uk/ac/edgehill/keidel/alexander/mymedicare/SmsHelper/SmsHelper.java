package uk.ac.edgehill.keidel.alexander.mymedicare.SmsHelper;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import uk.ac.edgehill.keidel.alexander.mymedicare.CustomSQLite.DatabaseContract;
import uk.ac.edgehill.keidel.alexander.mymedicare.CustomSQLite.MyDatabaseHelper;

/**
 * Created by Alexander Keidel on 06/05/2016.
 * Supporting class to easily contact the gp from different activities, dialogs or fragments.
 * See http://stackoverflow.com/questions/4967448/send-sms-in-android
 * for used supporting documentation.
 */
public class SmsHelper {
    private MyDatabaseHelper mdh;
    private SQLiteDatabase myDb;
    private String phoneNumber, message = "";
    Cursor cursor;
    private static final String SQL_QUERY_GP_PHONE_NUMBER = "SELECT " + DatabaseContract.GeneralPracticianFeeder.PHONE
            + " FROM " + DatabaseContract.GeneralPracticianFeeder.TABLE_NAME;
    private static final String SQL_QUERY_GP_NAME = "SELECT " + DatabaseContract.GeneralPracticianFeeder.NAME
            + " FROM " + DatabaseContract.GeneralPracticianFeeder.TABLE_NAME;
    private static final String SQL_QUERY_PATIENT_NAME = "SELECT " + DatabaseContract.PersonalInformationFeeder.FIRST_NAME + ", "
            + DatabaseContract.PersonalInformationFeeder.LAST_NAME + " FROM "
            + DatabaseContract.PersonalInformationFeeder.TABLE_NAME;


    public SmsHelper(Context context){
        mdh = new MyDatabaseHelper(context);
        myDb = mdh.getReadableDatabase();
        cursor = myDb.rawQuery(SQL_QUERY_GP_PHONE_NUMBER, null);
        if(cursor != null && cursor.moveToFirst()){
            phoneNumber = cursor.getString(0);
        }
        cursor = myDb.rawQuery(SQL_QUERY_GP_NAME, null);
        if(cursor != null && cursor.moveToFirst()){
            message += "This is an automated message to " + cursor.getString(0) +". ";
        }
        cursor = myDb.rawQuery(SQL_QUERY_PATIENT_NAME, null);
        if(cursor != null && cursor.moveToFirst()){
            message += "Your patient " + cursor.getString(0) + " " + cursor.getString(1) + " is at high risk. Please try to contact them for medical attention.";
        }
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("sms:" + phoneNumber));
        intent.putExtra("sms_body", message);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); //see for supporting documentation http://stackoverflow.com/questions/3689581/calling-startactivity-from-outside-of-an-activity
        context.startActivity(intent);
    }
}
