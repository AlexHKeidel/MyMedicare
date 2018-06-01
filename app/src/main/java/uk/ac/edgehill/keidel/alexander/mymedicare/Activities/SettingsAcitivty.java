package uk.ac.edgehill.keidel.alexander.mymedicare.Activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.w3c.dom.Text;

import uk.ac.edgehill.keidel.alexander.mymedicare.R;
import uk.ac.edgehill.keidel.alexander.mymedicare.SharedPreferenceHandler.SharedPreferenceHandler;

public class SettingsAcitivty extends AppCompatActivity {

    private NumberPicker textSizeNumberPicker;
    private TextView textSizeExampleTextView;
    private RadioGroup appThemeRadiogroup;
    private Button confirmSettingsButton;
    private View settingsActivityRelativeLayout;
    private SharedPreferenceHandler sph;
    private int newTheme;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        setupInterface();
    }

    /**
     * Set up the interface to be displayed on this activity.
     */
    private void setupInterface(){
        sph = new SharedPreferenceHandler(this);
        textSizeExampleTextView = (TextView) findViewById(R.id.textSizeExampleTextView);
        textSizeExampleTextView.setTextSize(sph.getUserTextSizeChoice());
        textSizeNumberPicker = (NumberPicker) findViewById(R.id.textSizeNumberPicker);
        textSizeNumberPicker.setMaxValue(40); //maximum value for text size
        textSizeNumberPicker.setMinValue(10); //minimum value for text size
        textSizeNumberPicker.setValue(sph.getUserTextSizeChoice());
        textSizeNumberPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                textSizeExampleTextView.setTextSize(newVal); //show the text size in the example
            }
        });
        confirmSettingsButton = (Button) findViewById(R.id.confirmSettingsButton);
        confirmSettingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmSettings();
                finish();
            }
        });
        settingsActivityRelativeLayout = (RelativeLayout) findViewById(R.id.settingsRelativeLayout);
        appThemeRadiogroup = (RadioGroup) findViewById(R.id.styleChoiceRadioGroup);
        newTheme = sph.getUserColourChoice();
        System.out.println("newTheme = " + newTheme);
        switch(sph.getUserColourChoice()){
            case R.color.defaultAppColour:
                appThemeRadiogroup.check(R.id.defaultThemeRadioButton);
                settingsActivityRelativeLayout.setBackgroundColor(getResources().getColor(R.color.defaultAppColour)); //set background colour to user choice
                break;

            case R.color.darkAppColour:
                appThemeRadiogroup.check(R.id.darkThemeRadioButton);
                settingsActivityRelativeLayout.setBackgroundColor(getResources().getColor(R.color.darkAppColour)); //set background colour to user choice
                break;

            case R.color.yellowAppColour:
                appThemeRadiogroup.check(R.id.yellowRadioButton);
                settingsActivityRelativeLayout.setBackgroundColor(getResources().getColor(R.color.yellowAppColour));
                break;

            default:
                appThemeRadiogroup.check(R.id.defaultThemeRadioButton);
                settingsActivityRelativeLayout.setBackgroundColor(getResources().getColor(R.color.defaultAppColour)); //set background colour to user choice
                break;
        }

        appThemeRadiogroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.defaultThemeRadioButton:
                        newTheme = R.color.defaultAppColour;
                        appThemeRadiogroup.check(R.id.defaultThemeRadioButton);
                        settingsActivityRelativeLayout.setBackgroundColor(getResources().getColor(R.color.defaultAppColour)); //set background colour to user choice
                        break;

                    case R.id.darkThemeRadioButton:
                        newTheme = R.color.darkAppColour;
                        appThemeRadiogroup.check(R.id.darkThemeRadioButton);
                        settingsActivityRelativeLayout.setBackgroundColor(getResources().getColor(R.color.darkAppColour)); //set background colour to user choice
                        break;

                    case R.id.yellowRadioButton:
                        newTheme = R.color.yellowAppColour;
                        appThemeRadiogroup.check(R.id.yellowRadioButton);
                        settingsActivityRelativeLayout.setBackgroundColor(getResources().getColor(R.color.yellowAppColour));
                        break;

                    default:
                        appThemeRadiogroup.check(R.id.defaultThemeRadioButton);
                        settingsActivityRelativeLayout.setBackgroundColor(getResources().getColor(R.color.defaultAppColour)); //set background colour to user choice
                        break;
                }
            }
        });
    }

    /**
     * Confirm settings and write them into the shared preferences
     */
    private void confirmSettings(){
        sph.setUserTextSizeChoice(textSizeNumberPicker.getValue()); //write text size choice to user preferences
        sph.setUserColourChoice(newTheme);
    }
}
