package uk.ac.edgehill.keidel.alexander.mymedicare.CustomDialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;

import uk.ac.edgehill.keidel.alexander.mymedicare.R;

/**
 * Created by Alexander Keidel, 22397868 on 04/05/2016.
 * See http://developer.android.com/guide/topics/ui/dialogs.html
 * for used aiding documentation.
 */
public class TypeOfReadingPickerDialog extends DialogFragment {
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.dialogPickTypeOfReading)
                .setItems(R.array.dialogPickTypeOfReadingOptionsArray, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // The 'which' argument contains the index position
                        // of the selected item
                        switch(which){
                            case 0: //blood pressure
                                DialogFragment bpd = new BloodPressureDialog();
                                bpd.show(getFragmentManager(), "bpreading");
                                break;
                            case 1: //heart rate
                                DialogFragment hrd = new HeartrateDialog();
                                hrd.show(getFragmentManager(), "hrreading");
                                break;
                            case 2: //temperature
                                DialogFragment tmpd = new TemperatureDialog();
                                tmpd.show(getFragmentManager(), "tmpreading");
                                break;
                        }
                    }
                });
        return builder.create();
    }
}
