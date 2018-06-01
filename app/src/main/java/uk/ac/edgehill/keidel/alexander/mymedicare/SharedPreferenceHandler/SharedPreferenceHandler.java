package uk.ac.edgehill.keidel.alexander.mymedicare.SharedPreferenceHandler;

import android.content.Context;
import android.content.SharedPreferences;

import uk.ac.edgehill.keidel.alexander.mymedicare.R;

/**
 * Created by Alexander Keidel on 05/05/2016.
 * Used to get access to the shared preferences of the user to update the user interface
 */
public class SharedPreferenceHandler {
    private final String COLOUR_CHOICE_TAG = "COLOUR";
    private final String TEXT_SIZE_TAG = "TEXT_SIZE";
    private int userColourChoice;
    private int userTextSizeChoice;
    private SharedPreferences userSharedPreferences;

    public SharedPreferenceHandler(Context context){
        userSharedPreferences = context.getSharedPreferences(context.getResources().getString(R.string.app_name), Context.MODE_PRIVATE);
        userColourChoice = userSharedPreferences.getInt(COLOUR_CHOICE_TAG, R.color.defaultAppColour);
        userTextSizeChoice = userSharedPreferences.getInt(TEXT_SIZE_TAG, context.getResources().getInteger(R.integer.defaultTextSize));
    }

    public int getUserColourChoice(){
        return userColourChoice;
    }

    public int getUserTextSizeChoice(){
        return userTextSizeChoice;
    }

    public void setUserColourChoice(int userColourChoice){
        this.userTextSizeChoice = userColourChoice;
        userSharedPreferences.edit().putInt(COLOUR_CHOICE_TAG, userColourChoice).apply(); //write to user preferences
    }

    public void setUserTextSizeChoice(int userTextSizeChoice){
        this.userTextSizeChoice = userTextSizeChoice;
        userSharedPreferences.edit().putInt(TEXT_SIZE_TAG, userTextSizeChoice).apply(); //write to user preferences
    }
}
