
package it.polimi.deib.p2pchat.discovery.socketmanagers;

/*
 * Copyright (C) 2011 The Android Open Source Project
 * Copyright 2015 Stefano Cappa
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
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;

import it.polimi.deib.p2pchat.discovery.Configuration;

/**
 * Class the implements the ClientSocket Handler. It's used only by the clients/peers.
 * <p></p>
 * Created by Stefano Cappa on 04/02/15, based on google code samples.
 */
public class ClientSocketHandler extends Thread {

    private static final String TAG = "ClientSocketHandler";

    private final Handler handler;
    private final InetAddress mAddress; //this is the ip address, NOT THE MACADDRESS!!!
    private Socket socket;

    /**
     * Constructor of the class.
     *
     * @param handler           Represents the handler required in order to communicate
     * @param groupOwnerAddress Represents the ip address of the group owner of this client/peer
     */
    public ClientSocketHandler(@NonNull Handler handler, @NonNull InetAddress groupOwnerAddress) {
        this.handler = handler;
        this.mAddress = groupOwnerAddress;
    }

    /**
     * Method to start the {@link it.polimi.deib.p2pchat.discovery.socketmanagers.ChatManager}
     */
    @Override
    public void run() {
        ChatManager chat;
        socket = new Socket();
        try {
            socket.bind(null);
            socket.connect(new InetSocketAddress(mAddress.getHostAddress(),
                    Configuration.GROUPOWNER_PORT), Configuration.CLIENT_PORT);
            Log.d(TAG, "Launching the I/O handler");
            chat = new ChatManager(socket, handler);
            new Thread(chat).start();
        } catch (IOException e) {
            Log.e(TAG, "IOException throwed by socket", e);
            try {
                socket.close();
            } catch (IOException e1) {
                Log.e(TAG, "IOException during close Socket", e1);
            }
        }
    }


    /**
     * Method to close the client/peer socket and kill this entire thread.
     */
    public void closeSocketAndKillThisThread() {
        if (socket != null && !socket.isClosed()) {
            try {
                socket.close();
            } catch (IOException e) {
                Log.e(TAG, "IOException during close Socket", e);
            }
        }

        //to interrupt this thread, without the threadpoolexecutor
        if (!this.isInterrupted()) {
            Log.d(TAG, "Stopping ClientSocketHandler");
            this.interrupt();
        }
    }
}
