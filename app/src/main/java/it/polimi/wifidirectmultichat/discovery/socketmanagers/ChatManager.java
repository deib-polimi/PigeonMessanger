package it.polimi.wifidirectmultichat.discovery.socketmanagers;

/*
 * Copyright (C) 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import android.os.Handler;
import android.support.annotation.NonNull;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import it.polimi.wifidirectmultichat.discovery.Configuration;
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

    private static final String TAG = "ChatHandler";

    private Socket socket = null;
    private final Handler handler;
    @Getter @Setter private boolean disable = false; //attribute to stop or start chatmanager
    private InputStream iStream;
    private OutputStream oStream;

    /**
     * Constructor of the class
     * @param socket Represents the {@link java.net.Socket} required in order to communicate
     * @param handler Represents the {@link android.os.Handler} required in order to communicate
     */
    public ChatManager(@NonNull Socket socket, @NonNull Handler handler) {
        this.socket = socket;
        this.handler = handler;
    }

    /**
     * Method to execute the {@link it.polimi.wifidirectmultichat.discovery.socketmanagers.ChatManager}'s Thread
     * To stop the execution, please use ".setDisable(true);".
     */
    @Override
    public void run() {
        Log.d(TAG,"ChatManager started");
        try {
            iStream = socket.getInputStream();
            oStream = socket.getOutputStream();
            byte[] buffer = new byte[1024];
            int bytes;

            //this method's call is used to call handleMessage's case Configuration.FIRSTMESSAGEXCHANGE in the MainActivity.
            handler.obtainMessage(Configuration.FIRSTMESSAGEXCHANGE, this).sendToTarget();

            while (!disable) { //...if enabled
                try {
                    // Read from the InputStream
                    if(iStream!=null) {
                        bytes = iStream.read(buffer);
                        if (bytes == -1) {
                            break;
                        }

                        //this method's call is used to call handleMessage's case Configuration.MESSAGE_READ in the MainActivity.
                        handler.obtainMessage(Configuration.MESSAGE_READ, bytes, -1, buffer).sendToTarget();
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
