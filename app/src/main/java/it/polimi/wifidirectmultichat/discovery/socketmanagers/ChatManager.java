package it.polimi.wifidirectmultichat.discovery.socketmanagers;

import android.os.Handler;
import android.support.annotation.NonNull;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import it.polimi.wifidirectmultichat.discovery.MainActivity;
import lombok.Getter;
import lombok.Setter;

/**
 * Class to manage reading and writing of messages with a socket. It's use a Handler
 * to send messages to the GUI, i.e. the Android's UI Thread.
 * This class is used by {@link it.polimi.wifidirectmultichat.discovery.socketmanagers.ClientSocketHandler}
 * and {@link it.polimi.wifidirectmultichat.discovery.socketmanagers.GroupOwnerSocketHandler}.
 *
 * Created by Stefano Cappa on 04/02/15, based on google code samples.
 *
 */
public class ChatManager implements Runnable {

    private Socket socket = null;
    private Handler handler;
    @Getter @Setter private boolean disable = false; //attributo per interrompere/abilitare il chatmanager

    /**
     * Constructor of the class
     * @param socket Represents the {@link java.net.Socket} required in order to communicate
     * @param handler Represents the {@link android.os.Handler} required in order to communicate
     * @param disable Represents the boolean's attribute to enable/disable the internal while cycle.
     *                By default, disable is false.
     */
    public ChatManager(@NonNull Socket socket, @NonNull Handler handler, boolean disable) {
        this.socket = socket;
        this.handler = handler;
        this.disable = disable;
    }

    private InputStream iStream;
    private OutputStream oStream;
    private static final String TAG = "ChatHandler";

    /**
     * Method to execute the {@link it.polimi.wifidirectmultichat.discovery.socketmanagers.ChatManager}'s Thread
     * To stop the execution, please use ".setDisable(true);".
     */
    @Override
    public void run() {
        Log.i(TAG,"ChatManager started");
        try {
            iStream = socket.getInputStream();
            oStream = socket.getOutputStream();
            byte[] buffer = new byte[1024];
            int bytes;
            handler.obtainMessage(MainActivity.MY_HANDLE, this).sendToTarget();

            while (!disable) { //...if enabled
                try {
                    // Read from the InputStream
                    if(iStream!=null && buffer!=null) {
                        bytes = iStream.read(buffer);
                        if (bytes == -1) {
                            break;
                        }

                        // Send the obtained bytes to the MainActivity
                        handler.obtainMessage(MainActivity.MESSAGE_READ, bytes, -1, buffer).sendToTarget();
                    }
                } catch (IOException e) {
                    Log.e(TAG, "disconnected", e);
                }
            }
        } catch (IOException e) {
            Log.e(TAG,"Exception : " + e.toString());
        } finally {
            try {
                iStream.close();
                socket.close();
            } catch (IOException e) {
                Log.e(TAG,"Exception during close socket or isStream",  e);
            }
        }
    }

    /**
     * Method to write a byte array (that can be a message) on the output stream.
     * @param buffer byte[] array that represents data to write. For example, a String converted in byte[] with ".getBytes();"
     */
    public void write(byte[] buffer) {
        try {
            oStream.write(buffer);
        } catch (IOException e) {
            Log.e(TAG, "Exception during write", e);
        }
    }

}
