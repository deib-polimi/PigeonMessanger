package it.polimi.wifidirectmultichat.discovery.dialog;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import it.polimi.wifidirectmultichat.R;


/**
 *
 *  Created by Stefano Cappa on 04/02/15.
 *
 */
public class TabChoosedDialog extends DialogFragment {

    static private Button tab1, tab2, tab3, tab4, tab5;
    static private int position;

    static public TabChoosedDialog newInstance(int position1) {
        position = position1;
        return new TabChoosedDialog();
    }

    @Override
    public void onDestroyView() {
        if (getDialog() != null && getRetainInstance())
            getDialog().setOnDismissListener(null);
        super.onDestroyView();
    }

    public interface MyDialogCallbackInterface {
        public void startConnection(int tabnum, int position);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.dialog, container, false);

        this.getDialog().setTitle("Quale tab?");
        tab1 = (Button) v.findViewById(R.id.tab1);
        tab2 = (Button) v.findViewById(R.id.tab2);
        tab3 = (Button) v.findViewById(R.id.tab3);
        tab4 = (Button) v.findViewById(R.id.tab4);
        tab5 = (Button) v.findViewById(R.id.tab5);

        this.setListener();

        return v;
    }

    public void setListener() {
        tab1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Log.d("dialog", "tab1");

                ((MyDialogCallbackInterface)getTargetFragment()).startConnection(1,position);
                dismiss();
            }
        });
        tab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Log.d("dialog", "tab2");

                ((MyDialogCallbackInterface)getTargetFragment()).startConnection(2,position);
                dismiss();
            }
        });
        tab3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Log.d("dialog", "tab3");

                ((MyDialogCallbackInterface)getTargetFragment()).startConnection(3,position);
                dismiss();
            }
        });
        tab4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Log.d("dialog", "tab4");

                ((MyDialogCallbackInterface)getTargetFragment()).startConnection(4,position);
                dismiss();
            }
        });
        tab5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Log.d("dialog", "tab5");

                ((MyDialogCallbackInterface)getTargetFragment()).startConnection(5,position);
                dismiss();
            }
        });

    }
}