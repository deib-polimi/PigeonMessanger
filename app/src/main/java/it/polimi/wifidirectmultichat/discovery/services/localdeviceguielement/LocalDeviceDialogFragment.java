package it.polimi.wifidirectmultichat.discovery.services.localdeviceguielement;

import android.support.v4.app.DialogFragment;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import it.polimi.wifidirectmultichat.R;

/**
 * Class that represents the DialogFragment to change the local device name, not only in the GUI
 * but also to be discoverable with this new name, by other devices.
 * Created by Stefano Cappa on 16/02/15.
 */
public class LocalDeviceDialogFragment extends DialogFragment {

    private Button confirmButton;
    private EditText deviceNameEditText;

    /**
     * {@link it.polimi.wifidirectmultichat.discovery.services.WiFiP2pServicesFragment} implements this interface.
     * But the method to change the device name in
     */
    public interface DialogConfirmListener {
        public void changeLocalDeviceName(String deviceName);
    }

    /**
     * Method to obtain a new Fragment's instance.
     * @return This Fragment instance.
     */
    public static LocalDeviceDialogFragment newInstance() {
        return new LocalDeviceDialogFragment();
    }

    /**
     * Default Fragment constructor.
     */
    public LocalDeviceDialogFragment() {}


    @Override
    public void onDestroyView() {
        if (getDialog() != null && getRetainInstance()) {
            getDialog().setOnDismissListener(null);
        }
        super.onDestroyView();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.dialog, container, false);

        getDialog().setTitle(getResources().getString(R.string.choose_device_name));
        deviceNameEditText = (EditText) v.findViewById(R.id.deviceNameEditText);
        confirmButton = (Button) v.findViewById(R.id.confirmButton);

        //set listener to call changeLocalDeviceName in WiFiP2pServicesFragment, after a click on confirmButton
        this.setListener();

        return v;
    }

    public void setListener() {
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                ((DialogConfirmListener)getTargetFragment()).changeLocalDeviceName(deviceNameEditText.getText().toString());
                dismiss();
            }
        });
    }
}
