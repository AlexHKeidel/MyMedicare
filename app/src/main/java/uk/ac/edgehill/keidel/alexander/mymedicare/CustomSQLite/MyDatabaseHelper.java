package uk.ac.edgehill.keidel.alexander.mymedicare.CustomSQLite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Alexander Keidel, 22397868 on 14/03/2016.
 * This was created with the help of the official android developer tutorial accessible via
 * http://developer.android.com/training/basics/data-storage/databases.html#DbHelper
 */
public class MyDatabaseHelper extends SQLiteOpenHelper {
//    private static final String TEXT_TYPE = " TEXT";
//    private static final String INTEGER_TYPE = " INTEGER";
//    private static final String COMMA_SEP = ",";
    private static final String SQL_CREATE_PERSONAL_INFORMATION_ENTRIES =
        "CREATE TABLE " + DatabaseContract.PersonalInformationFeeder.TABLE_NAME + " ("
            + DatabaseContract.PersonalInformationFeeder._ID + " INTEGER PRIMARY KEY, "
            + DatabaseContract.PersonalInformationFeeder.FIRST_NAME + " TEXT, "
            + DatabaseContract.PersonalInformationFeeder.LAST_NAME + " TEXT, "
            + DatabaseContract.PersonalInformationFeeder.DATE_OF_BIRTH + " TEXT, "
            + DatabaseContract.PersonalInformationFeeder.ADDRESS_HOUSE + " TEXT, "
            + DatabaseContract.PersonalInformationFeeder.ADDRESS_POSTCODE + " TEXT, "
            + DatabaseContract.PersonalInformationFeeder.PHONE + " TEXT, "
            + DatabaseContract.PersonalInformationFeeder.EMAIL + " TEXT);";

    private static final String SQL_CREATE_GP_ENTRIES =
            "CREATE TABLE " + DatabaseContract.GeneralPracticianFeeder.TABLE_NAME + " ("
                    + DatabaseContract.GeneralPracticianFeeder._ID + " INTEGER PRIMARY KEY, "
                    + DatabaseContract.GeneralPracticianFeeder.NAME + " TEXT, "
                    + DatabaseContract.GeneralPracticianFeeder.ADDRESS_HOUSE + " TEXT, "
                    + DatabaseContract.GeneralPracticianFeeder.ADDRESS_POSTCODE + " TEXT, "
                    + DatabaseContract.GeneralPracticianFeeder.PHONE + " TEXT, "
                    + DatabaseContract.GeneralPracticianFeeder.USER_ID + " INTEGER, "
                    + "FOREIGN KEY (" + DatabaseContract.GeneralPracticianFeeder.USER_ID + ") REFERENCES " + DatabaseContract.PersonalInformationFeeder.TABLE_NAME + " (" + DatabaseContract.PersonalInformationFeeder._ID + "));";

    private static final String SQL_CREATE_TEMPERATURE_READING_ENTRIES =
            "CREATE TABLE " + DatabaseContract.TemperatureReadingFeeder.TABLE_NAME
                    + " (" + DatabaseContract.TemperatureReadingFeeder._ID + " INTEGER PRIMARY KEY, "
                    + DatabaseContract.TemperatureReadingFeeder.TEMPERATURE_READING_VALUE + " TEXT, "
                    + DatabaseContract.TemperatureReadingFeeder.DATE_AND_TIME + " TEXT, "
                    + DatabaseContract.TemperatureReadingFeeder.RISK_CATEGORY + " TEXT, "
                    + DatabaseContract.TemperatureReadingFeeder.USER_ID + " INTEGER, "
                    + "FOREIGN KEY ( " + DatabaseContract.TemperatureReadingFeeder.USER_ID + " ) REFERENCES " + DatabaseContract.PersonalInformationFeeder.TABLE_NAME + " (" + DatabaseContract.PersonalInformationFeeder._ID + " ));";

    private static final String SQL_CREATE_BLOOD_PRESSURE_READING_ENTRIES =
            "CREATE TABLE " + DatabaseContract.BloodPressureReadingFeeder.TABLE_NAME + " ("
                    + DatabaseContract.BloodPressureReadingFeeder._ID + " INTEGER PRIMARY KEY, "
                    + DatabaseContract.BloodPressureReadingFeeder.HIGH_VALUE + " INTEGER, "
                    + DatabaseContract.BloodPressureReadingFeeder.LOW_VALUE + " INTEGER, "
                    + DatabaseContract.BloodPressureReadingFeeder.DATE_AND_TIME + " TEXT, "
                    + DatabaseContract.BloodPressureReadingFeeder.RISK_CATEGORY + " TEXT, "
                    + DatabaseContract.BloodPressureReadingFeeder.USER_ID + " INTEGER, "
                    + " FOREIGN KEY ( " + DatabaseContract.BloodPressureReadingFeeder.USER_ID + ") REFERENCES " + DatabaseContract.PersonalInformationFeeder.TABLE_NAME + " (" + DatabaseContract.PersonalInformationFeeder._ID + " ));";

    private static final String SQL_CREATE_HEART_RATE_READING_ENTRIES =
            "CREATE TABLE " + DatabaseContract.HeartRateReadingFeeder.TABLE_NAME + " ("
                    + DatabaseContract.HeartRateReadingFeeder._ID + " INTEGER PRIMARY KEY, "
                    + DatabaseContract.HeartRateReadingFeeder.BEATS_PER_MINUTE + " INTEGER, "
                    + DatabaseContract.HeartRateReadingFeeder.DATE_AND_TIME + " TEXT, "
                    + DatabaseContract.HeartRateReadingFeeder.RISK_CATEGORY + " TEXT, "
                    + DatabaseContract.HeartRateReadingFeeder.USER_ID + " INTEGER, "
                    + " FOREIGN KEY ( " + DatabaseContract.HeartRateReadingFeeder.USER_ID + ") REFERENCES " + DatabaseContract.PersonalInformationFeeder.TABLE_NAME + " (" + DatabaseContract.PersonalInformationFeeder._ID + " ));";

    private static final String SQL_DELETE_ALL_ENTRIES = "DROP TABLE IF EXISTS " + DatabaseContract.PersonalInformationFeeder.TABLE_NAME + ", " + DatabaseContract.GeneralPracticianFeeder.TABLE_NAME + ", " + DatabaseContract.TemperatureReadingFeeder.TABLE_NAME + ", " + DatabaseContract.BloodPressureReadingFeeder.TABLE_NAME + ", " + DatabaseContract.HeartRateReadingFeeder.TABLE_NAME + ");";

    private static int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "MyMediCare.db";

    public MyDatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public MyDatabaseHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * Create all the relevant database tables as defined by the private static final Strings defined
     * in this class.
     * @param db SQLiteDatabase object to contain the tables for this application
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_PERSONAL_INFORMATION_ENTRIES);
        System.out.println("Successfully created Personal Information Table.");
        db.execSQL(SQL_CREATE_GP_ENTRIES);
        System.out.println("Successfully created GP Table.");
        db.execSQL(SQL_CREATE_TEMPERATURE_READING_ENTRIES);
        System.out.println("Successfully created Temperature Reading Table.");
        db.execSQL(SQL_CREATE_BLOOD_PRESSURE_READING_ENTRIES);
        System.out.println("Successfully created Blood Pressure Reading Table.");
        db.execSQL(SQL_CREATE_HEART_RATE_READING_ENTRIES);
        System.out.println("Successfully created Heart Rate Reading Table.");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //Upgrade policy at the moment is to delete all entries and start over
        db.execSQL(SQL_DELETE_ALL_ENTRIES); //delete all entries
        onCreate(db); //start over
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion){
        onUpgrade(db, oldVersion, newVersion); //just upgrade with new version number
    }
}
