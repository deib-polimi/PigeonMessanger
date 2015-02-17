
package it.polimi.wifidirectmultichat.discovery.socketmanagers;

import android.os.Handler;
import android.support.annotation.NonNull;
import android.util.Log;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;

import it.polimi.wifidirectmultichat.discovery.Configuration;
import it.polimi.wifidirectmultichat.discovery.MainActivity;

/**
 * Class the implements the ClientSocket Handler. It's used only by the clients/peers.
 *
 * Created by Stefano Cappa on 04/02/15, based on google code samples.
 */
public class ClientSocketHandler extends Thread {

    private static final String TAG = "ClientSocketHandler";

    private Handler handler;
    private ChatManager chat;
    private InetAddress mAddress; //this is the ip address, NOT THE MACADDRESS!!!
    private Socket socket;

    /**
     * Constructor of the class.
     * @param handler Represents the handler required in order to communicate
     * @param groupOwnerAddress Represents the ip address of the group owner of this client/peer
     */
    public ClientSocketHandler(@NonNull Handler handler, @NonNull InetAddress groupOwnerAddress) {
        this.handler = handler;
        this.mAddress = groupOwnerAddress;
    }

    /**
     * Method to start the ChatManager
     */
    @Override
    public void run() {
        socket = new Socket();
        try {
            socket.bind(null);
            socket.connect(new InetSocketAddress(mAddress.getHostAddress(),
                    Configuration.GROUPOWNER_PORT), Configuration.CLIENT_PORT);
            Log.d(TAG, "Launching the I/O handler");
            chat = new ChatManager(socket, handler,false); //disable=false per poter avviare chatmanager
            new Thread(chat).start();
        } catch (IOException e) {
            Log.e(TAG,"IOException throwed by socket", e);
            try {
                socket.close();
            } catch (IOException e1) {
                Log.e(TAG,"IOException during close Socket" , e1);
            }
            return;
        }
    }



    /**
     * Method to close the client/peer socket and kill this entire thread.
     */
    public void closeSocketAndKillThisThread() {
        if(socket!=null && !socket.isClosed()) {
            try {
                socket.close();
            } catch (IOException e) {
                Log.e(TAG,"IOException during close Socket" , e);
            }
        }

        //to interrupt this thread, without the threadpoolexecutor
        if(!this.isInterrupted()) {
            Log.d(TAG,"Stopping ClientSocketHandler");
            this.interrupt();
        }
    }
}
