# Pigeon Messenger

![alt tag](http://www.stefanocappa.it/publicfiles/Github_repositories_images/Pigeon_Messenger/pigeon_messenger_header_github.png)

<br>

## Informations
Wi-Fi Direct MultiChat (aka Pigeon Messenger)  is a demo Android's application that try to overcome some Wi-Fi Direct's limitations.
At the moment, the wifi diver of commercial devices doesn't allow a device to partecipate concurrently in two Wi-Fi Direct's groups.
This app tries to overcome this limitation, indeed, a user can communicate with a large number of nearby people without an internet connection, using point-to-point communications.
The main goal is the simultaneous management of multiple chats, queuing messages sent when the connection is not available and send them at the same time as soon as possible.

Pigeon Messenger requires Android 4.4 KitKat (API 19) or higher. This choice is related to to the fact that in previous versions, this protocol was unstable and unreliable.

It's important to remember that this is a demo application, so features like the management of screen's rotation, standby device, wifi not available and so on, are not managed as a commercial product.


## Requirements
- AndroidStudio
- **Lombok (automatically downloaded as gradle dependency) + Lombok plugin for IntelliJ / AndroidStudio [Available here](https://plugins.jetbrains.com/plugin/6317)**


## Results

[![ScreenShot](http://www.stefanocappa.it/publicfiles/Github_repositories_images/Pigeon_Messenger/youtube-video-pigeonmessenger.png)](https://www.youtube.com/watch?v=rW_dcTSOTlo)

Pigeon Messenger works with good performances.<br/>
The main problems are the "Discovery Phase" of this protocol and the Wi-Fi Direct's implementation in Android, in fact:<br/>
1. The discovery time is too high when the number of devices increases <br/>
2. After a certain time, a device is no longer discoverable from others, so you need to restart the Discovery Phase on all devices <br/>
3. Sometimes, especially in KitKat, the WiFi part of Android crashes and the only way to solve this annoying problem is a complete reboot of the device (this situation is recognizable when Android can't find other network in Wi-Fi Setting's app).

This shows that it's possible to extend the Wi-Fi Direct protocol in Android in some particular and limited scenarios, for example a chat.

## News
- *04/27/2016* - **Pigeon Messenger** Alpha 3 (updated to Android N, also reverted some commits to fix an [issue](https://github.com/deib-polimi/PigeonMessanger/issues/2))
- *03/13/2015* - **Pigeon Messenger** Alpha 2 (updated to Android 5.1 with the new support libraries)
- *03/02/2015* - **Pigeon Messenger** Alpha 1 public release


## Features
You can:<br/>
1. **change the device name** with Java Reflection<br/>
2. show a list of nearby devices<br/>
3. manage connection and disconnection between devices<br/>
4. show the ip address of a device, also if it's a client (not available in Android's official API)<br/>
5. block incoming messages with blacklisted words (not enabled by default)<br/>
6. block incoming messages if too short or empty (enabled by default)<br/>
7. create a chat between a GO and a client<br/>
8. manage an infinite number of chats<br/>
9. sending messages in a chat, previously stopped, if the associated device is discoverable<br/>
10. enqueue messages if the device is not discoverable, and send all of them at the next attempt to reconnect<br/>
11. use *"Eternal Discovery"*, a way to restart the discovery phase every time that there are errors or disconnections<br/>

## Future extensions
- [ ] Connect more clients at the same GO without limitations
- [ ] Group chats where the GO receives messages from a client and it sends these messages to all other clients in broadcast
- [ ] Private chats between clients, i.e. a client uses his GO like a server to send private messages to another client, because in Wi-Fi Direct the communication between clients in a group is impossible. In this case is necessary to provide security mechanisms, like encryption, because a GO should never read private messages between its clients.
- [ ] and so on... ;)


## Images
<br/>
![alt tag](http://www.stefanocappa.it/publicfiles/Github_repositories_images/Pigeon_Messenger/tre_immagini.png)
<br/>


## Usage
### General usage
1. Activate Wi-Fi on all devices
2. Open this app on all devices
3. Touch the "cardview" under the words "This Device" to choose the device name
2. Wait until devices are discovered
3. Connect your device to another one touching an element in the list under the words "Other Devices"
4. Chat with this device

### Reconnection usage
1. Activate Wi-Fi on all devices
2. Open this app on all devices
3. Touch the "cardview" under the words "This Device" to choose the device name
2. Wait until devices are discovered
3. Connect your device to another one touching an element in the list under the words "Other Devices"
4. Chat with this device
5. Disconnect one device clicking on the second icon in the toolbar
6. Wait some seconds, because the "Eternal Discovery" requires some seconds to re-discover this device
7. Write a message in the chat and wait the automatic reconnection. Alternatively connect manually to the device to continue the chat.

### Queuing messages usage
1. Activate Wi-Fi on all devices
2. Open this app on all devices
3. Touch the "cardview" under the words "This Device" to choose the device name
2. Wait until devices are discovered
3. Connect your device to another one touching an element in the list under the words "Other Devices"
4. Chat with this device
5. Disconnect one device clicking on the second icon in the toolbar
6. Disable the Discovery Phase that was automatically restarted by "Eternal Discovery", so  the list under "Other Devices" will be empty
7. Write some messages in the chat. As you can see the connection is not possible, but don't despair, because this app is able to enqueue your messages
8. Restart the Discovery Phase e reconnect to the device in one of the methods described above ("Reconnection usage", step 7)
9. All messages in the queue will be automatically sent to destination in only one message


## Important things

### Configuration
If you want to configure this app as you prefer, pay attention to: `Configuration.java`.

If you want to release this application without debug messages inside chats, change this constant to "false" :
```java
    public static final boolean DEBUG_VERSION = true;
```

If you want to change client's and GO's ports, change this:
```java
    public static final int GROUPOWNER_PORT = 4545;
    public static final int CLIENT_PORT = 5000;
```

If you want to change the maximum number of devices that a GO can manage for the chat, change this:
```java
    public static final int THREAD_COUNT = 20;
```

This attributes are used inside this app to exchange information between devices, like a protocol, to initialize the associated chat:
```java
    public static final String MAGICADDRESSKEYWORD = "4<D<D<R<3<5<5";
    public static final String PLUSSYMBOLS = "++++++++++++++++++++++++++";
```

If yuo want to change the termination String of all device's names change this attribute :
```java
    public static final String SERVICE_INSTANCE = "polimip2p";
```

The following attributes are used inside this app:
```java
    public static final int THREAD_POOL_EXECUTOR_KEEP_ALIVE_TIME = 10; //don't touch this!
    public static final String TXTRECORD_PROP_AVAILABLE = "available";
    public static final String SERVICE_REG_TYPE = "_presence._tcp";
    public static final int MESSAGE_READ = 0x400 + 1;
    public static final int FIRSTMESSAGEXCHANGE = 0x400 + 2;
    public static final String MESSAGE_READ_MSG = "MESSAGE_READ";
    public static final String FIRSTMESSAGEXCHANGE_MSG = "FIRSTMESSAGEXCHANGE";
```
### Message Filter
To change/add blacklisted words pay attention to `MessageFilter.java`.
Every message that contains one or more of this words will be filtered on reception.

Example: i want remove every message that contains at least one of this words: "illegal", "piracy", "crack", "Piracy".
Add to the lowerCaseBlackList this words in this way:

```java
    /**
     * Private constructor, because is a singleton class.
     */
    private MessageFilter() {
        lowerCaseBlackList = new ArrayList<>();
        //add here all the words that you want to blacklist
        lowerCaseBlackList.add("illegal"); // OK
        lowerCaseBlackList.add("piracy"); // OK
        lowerCaseBlackList.add("crack"); // OK

        //useless because ev ery words in this list are elaborated as "lower case".
        //lowerCaseBlackList.add("Piracy"); // USELESS
    }
```
### The "@UseOnlyPrivateHere" annotation
I created this annotation as a custom java annotation to advise developers that some attributes must be private.
Obviously, if you want you can make every public attribute, also with this annotation, but can be very dangerous.
As you can see in `DestinationDeviceTabList` (attribute deviceList) and `ServiceList` (attribute serviceList) there is this annotation, because if you access or change this attributes without the custom logic that i implemented in these classes, to do secure operations, you can obtain Exceptions or other problems.
These classes remap list's indexes, add/set object without duplicates and in a particular way, that is very necessary to this software. Every time that you want to change something here, you should create a secure method to manage these attributes.

### P2pDestinationDevice, a WifiP2pDevice abstraction
In Android you can't retrieve the current IP Address in a quick and easy way, because this method is not available in Google APIs.
Its possible to do this in two ways: the first one requires to execute a shell's command, the second one requires only java.
I chose the second solution, because it's quicker to implement. In particular, i get the GO's IP Address inside onConnectionInfoAvailable, but to retrieve the client's IP Address i need to ask this information to the socket on GO's side when the connection has been established.
Only GOs can retrieve Client's IP addresses, because clients can obtain only the GO's IP address.
Therefore, it's necessary to send the client's IP address to the client itself from its GO, to be able to store them in a variable and use this information for something else, for example to show the ip in the UI or to open other sockets.

#### GO IP Address
In `MainActivity.java`
```java
    @Override
    public void onConnectionInfoAvailable(WifiP2pInfo p2pInfo) {
    (...)
    //set Group Owner ip address
    TabFragment.getWiFiP2pServicesFragment().setLocalDeviceIpAddress(p2pInfo.groupOwnerAddress.getHostAddress());
    (..)
    }
```

#### Client IP Address
In `GroupOwnerSocketHandler.java`  (GO)
```java
    Socket clientSocket = socket.accept(); //because now i'm connected with the client/peer device
    pool.execute(new ChatManager(clientSocket, handler));
    ipAddress = clientSocket.getInetAddress(); //local variable with a get method
```

In `MainActivity.java` (GO)
```java
private void sendAddress(String deviceMacAddress, String name, ChatManager chatManager) {
    if (chatManager != null) {
        InetAddress ipAddress;
        if (socketHandler instanceof GroupOwnerSocketHandler) {
            ipAddress = ((GroupOwnerSocketHandler) socketHandler).getIpAddress();

            Log.d(TAG, "sending message with MAGICADDRESSKEYWORD, with ipaddress= " + ipAddress.getHostAddress());

            chatManager.write((Configuration.PLUSSYMBOLS + Configuration.MAGICADDRESSKEYWORD +
                        "___" + deviceMacAddress + "___" + name + "___" + ipAddress.getHostAddress()).getBytes());
        } else {
            Log.d(TAG, "sending message with MAGICADDRESSKEYWORD, without ipaddress");
            //i use "+" symbols as initial spacing to be sure that also if some initial character will be lost i'll have always
            //the Configuration.MAGICADDRESSKEYWORD and i can set the associated device to the correct WifiChatFragment.
            chatManager.write((Configuration.PLUSSYMBOLS + Configuration.MAGICADDRESSKEYWORD +
                        "___" + deviceMacAddress + "___" + name).getBytes());
        }
    }
}
```

In `MainActivity.java` (CLIENT)
```java
if (readMessage.contains(Configuration.MAGICADDRESSKEYWORD)) {
    WifiP2pDevice p2pDevice = new WifiP2pDevice();
    p2pDevice.deviceAddress = readMessage.split("___")[1];
    p2pDevice.deviceName = readMessage.split("___")[2];
    P2pDestinationDevice device = new P2pDestinationDevice(p2pDevice);

    if (readMessage.split("___").length == 3) {
    Log.d(TAG, "handleMessage, p2pDevice created with: " + p2pDevice.deviceName + ", " + p2pDevice.deviceAddress);
                        manageAddressMessageReception(device);
    } else if (readMessage.split("___").length == 4) {
            device.setDestinationIpAddress(readMessage.split("___")[3]);

            //set client ip address
            TabFragment.getWiFiP2pServicesFragment().setLocalDeviceIpAddress(device.getDestinationIpAddress());

            Log.d(TAG, "handleMessage, p2pDevice created with: " + p2pDevice.deviceName + ", "
                                + p2pDevice.deviceAddress + ", " + device.getDestinationIpAddress());
            manageAddressMessageReception(device);
    }
}
```



## License

Copyright 2015 Stefano Cappa

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

<br/>
**Created by Stefano Cappa**
