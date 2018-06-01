package uk.ac.edgehill.keidel.alexander.mymedicare.CustomSQLite;

import android.provider.BaseColumns;
import android.provider.ContactsContract;

/**
 * Created by Alexander Keidel, 22397868 on 14/03/2016.
 * followed the official android developer tutorial on
 * http://developer.android.com/training/basics/data-storage/databases.html
 */
public class DatabaseContract {
    public DatabaseContract(){} //empty constructor to prevent accidental instantiation

    /**
     * These inner classes define the different table layouts that are used by the DatabaseHelper class
     * The BaseColumns interface gives these tables a unique primary key field _ID, which is an automatically
     * incremented integer value.
     */
    public static abstract class PersonalInformationFeeder implements BaseColumns {
        public static final String TABLE_NAME = "user";
        public static final String FIRST_NAME = "firstname";
        public static final String LAST_NAME = "lastname";
        public static final String DATE_OF_BIRTH = "dob";
        public static final String ADDRESS_HOUSE = "addressline1";
        public static final String ADDRESS_POSTCODE = "postcode";
        public static final String PHONE = "phone";
        public static final String EMAIL = "email";
    }

    public static abstract class GeneralPracticianFeeder implements BaseColumns {
        public static final String TABLE_NAME = "gp";
        public static final String NAME = "name";
        public static final String ADDRESS_HOUSE = "addressline1";
        public static final String ADDRESS_POSTCODE = "postcode";
        public static final String PHONE = "phonenumber";
        public static final String USER_ID = "userid"; //foreign key of the associated user
    }

    public static abstract class TemperatureReadingFeeder implements BaseColumns {
        public static final String TABLE_NAME = "tmpreading";
        public static final String TEMPERATURE_READING_VALUE = "reading";
        public static final String RISK_CATEGORY = "riskcategory";
        public static final String DATE_AND_TIME = "dateandtime";
        public static final String USER_ID = "userid"; //foreign key of the associated user
    }

    public static abstract class BloodPressureReadingFeeder implements BaseColumns {
        public static final String TABLE_NAME = "bpreading";
        public static final String HIGH_VALUE = "highvalue";
        public static final String LOW_VALUE = "lowvalue";
        public static final String DATE_AND_TIME = "dateandtime";
        public static final String RISK_CATEGORY = "riskcategory";
        public static final String USER_ID = "userid"; //foreign key of the associated user
    }

    public static abstract class HeartRateReadingFeeder implements BaseColumns {
        public static final String TABLE_NAME = "hrreading";
        public static final String BEATS_PER_MINUTE = "bpm";
        public static final String DATE_AND_TIME = "dateandtime";
        public static final String RISK_CATEGORY = "riskcategory";
        public static final String USER_ID = "userid"; //foreign key of the associated user
    }
}
