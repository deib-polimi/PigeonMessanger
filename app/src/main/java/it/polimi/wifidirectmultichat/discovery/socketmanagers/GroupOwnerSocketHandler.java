
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
import java.net.ServerSocket;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import it.polimi.wifidirectmultichat.discovery.Configuration;

/**
 * Class the implements the ServerSocket Handler. It's used only by the Group Owner.
 *
 * Created by Stefano Cappa on 04/02/15, based on google code samples.
 */
public class GroupOwnerSocketHandler extends Thread {

    private static final String TAG = "GroupOwnerSocketHandler";

    private ServerSocket socket = null;
    private Handler handler;

    /**
     * Class constructor.
     * @param handler Represents the {@link android.os.Handler} required in order to communicate
     * @throws IOException Exception throwed by {@link ServerSocket} (SERVERPORT).
     */
    public GroupOwnerSocketHandler(@NonNull Handler handler) throws IOException {
        try {
            socket = new ServerSocket(Configuration.GROUPOWNER_PORT);
            this.handler = handler;
            Log.d("GroupOwnerSocketHandler", "Socket Started");
        } catch (IOException e) {
            Log.e(TAG, "IOException during open ServerSockets with port 4545", e);
            pool.shutdownNow();
            throw e;
        }

    }

    /**
     * A ThreadPool for client sockets.
     */
    private final ThreadPoolExecutor pool = new ThreadPoolExecutor(
            Configuration.THREAD_COUNT, Configuration.THREAD_COUNT,
            Configuration.THREAD_POOL_EXECUTOR_KEEP_ALIVE_TIME, TimeUnit.SECONDS,
            new LinkedBlockingQueue<Runnable>());


    /**
     * Method to close the group owner sockets and kill this entire thread.
     */
    public void closeSocketAndKillThisThread() {
        if(socket!=null && !socket.isClosed()) {
            try {
                socket.close();
            } catch (IOException e) {
                Log.e(TAG, "IOException during close Socket", e);
            }
            pool.shutdown();
        }
    }

    /**
     * Method to start the GroupOwnerSocketHandler.
     * Attention you can't stop this method, because there is a while(true) inside.
     */
    @Override
    public void run() {
        while (true) {
            try {
                // A blocking operation. Initiate a ChatManager instance when
                // there is a new connection
                if(socket!=null && !socket.isClosed()) {
                    pool.execute(new ChatManager(socket.accept(), handler));
                    Log.d(TAG, "Launching the I/O handler");
                }
            } catch (IOException e) {
                //if there is an exception, after closing socket and pool, the execution stops with a "break".
                try {
                    if (socket != null && !socket.isClosed()) {
                        socket.close();
                    }
                } catch (IOException ioe) {
                    Log.e(TAG, "IOException during close Socket", ioe);
                }
                pool.shutdownNow();
                break; //stop the while(true).
            }
        }
    }

}
