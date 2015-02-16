package it.polimi.wifidirectmultichat.discovery.services.localdevicedialog;

import android.support.v4.app.DialogFragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import it.polimi.wifidirectmultichat.R;

/**
 * Created by Stefano Cappa on 16/02/15.
 */
public class LocalDeviceDialogFragment extends DialogFragment {

    static private Button confirmButton;
    static private EditText deviceNameEditText;

    static public LocalDeviceDialogFragment newInstance() {
        return new LocalDeviceDialogFragment();
    }

    public interface DialogCallbackInterface {
        public void changeLocalDeviceName(String deviceName);
    }

    @Override
    public void onDestroyView() {
        if (getDialog() != null && getRetainInstance())
            getDialog().setOnDismissListener(null);
        super.onDestroyView();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.dialog, container, false);

        this.getDialog().setTitle("Quale tab?");
        deviceNameEditText = (EditText) v.findViewById(R.id.deviceNameEditText);
        confirmButton = (Button) v.findViewById(R.id.confirmButton);

        this.setListener();

        return v;
    }

    public void setListener() {
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {

                ((DialogCallbackInterface)getTargetFragment()).changeLocalDeviceName(deviceNameEditText.getText().toString());
                dismiss();
            }
        });
    }
}
