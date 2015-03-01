# WiFiDirect MultiChat

## Informations
WiFiDirect MultiChat (aka MultiChat) is a demo Android's application that try to overcome some WiFi-Direct limitations.
I driver degli smarphone commerciali disponibili, al momento, non permettono ad un device di partecipare contemporaneamente a due gruppi WiFi Direct.
Questa app cerca di superare questo limite facendo si che un utente possa comunicare con un vasto numero di persone nelle vicinanza senza una connessione ad internet, sfruttando comunicazioni point-to-point.
L'obiettivo principale è quello di gestire contemporaneamente più chat, accodando i messaggi inviati quando la connessione non è disponibile, per inviarli contemporaneamente appena possibile.

MultiChat è stata creata per Android 4.4 (KitKat) o superiore. Questa scelta è legata al fatto che WiFiDirect nelle versioni precedenti si è dimostrato troppo instabile ed inaffidabile.

E' importante ricordare che si tratta di una applicazione dimostrativa, quindi funzionalità come la gestione della rotazione dello schermo, standby del dispositivo, wifi disattivato and so on, non sono gestite come in un prodotto commerciale.

## Results
VIDEO YOUTUBE 

Come si nota dal video, l'app funziona perfettamente e con buone performance.<br/>
Il problema è la fase di Discovery del protocollo / l'implementazione di WiFi-Direct in Android, infatti:<br/>
1. Il tempo di ricerca dei dispositivi è troppo elevato al crescere del numero di utenti<br/>
2. Dopo un certo tempo, il dispositivo non risulta più rilevabile dagli altri, quindi bisogna riavviare la Discovery su tutti i device<br/>
3. A volte la parte wifi di Android crasha e l'unico modo per risolvere il problema è un completo reboot del dispositivo (questa sitauzione è riconoscibile dalle impostazioni Wifi di android, dove improvvisamente non risulta più un grado di scansionare le rete wifi)

Questo mostra che è possibile rendere scalabile il protocollo WiFi Direct in Android in un caso particolare di utilizzo .


## News
- *03/01/2015* - **MultiChat** public release


## Features
1. **Cambiare il nome del dispositivo** tramite Java Reflection
2. Visualizzazione dei dispositivi nelle vicinanze
3. Connessione / disconnessione tra dispositivi
4. Connessione di più client allo stesso gruppo owner (ma senza chat di gruppo)
5. Visualizzazione degli indirizzi ip dei dispositivi tramite scambio di messaggi (funzione non disponibile nelle api ufficiali)
6. Filtro dei messaggi in ricezione se contengono parole presenti in una blacklist (disattivato di default)
7. Filtro dei messaggi troppo brevi o vuoti (attivato di default)
8. Creazione di una chat tra GO e un client
9. Gestione di infinite chat, accessibili tramite tab
10. Riconnessione automatica inviando un messaggio in una chat aperta precedentemente, se il dispositivo associato è discoverable
11. Accodamento dei messaggi scritti in una chat nel caso in cui il dispositivo non sia discoverable, ed invio automatico al successvo tentativo di riconnessione
12. *"Eternal Discovery"* che permette il riavvio della fase di Discovery ad ogni evento di disconnessione o errore.

## Possibile future extensions
* Chat di gruppo, cioè il GO riceve i messaggi di un client e li reinvia a tutti gli altri suoi client in broadcast
* Chat di gruppo private tra clients. Cioè un client usa il GO come server per inviare messaggi ad un altro client, con cui non è direttamente connesso. In questo caso è necessario prevedere meccanismi di sicurezza per evitare che il GO possa leggere i messaggi privati.
* Utilizzare dispositivi intermedi come "ponti radio" per inviare messaggi a lunga distanza, conoscendo in precedenza il macaddress del dispositivo di destinazione (poichè non discoverable, non essendo nel range del mittente).
* and so on... ;)

## Usage
### General usage
1. Attivare il WiFi su tutti i dispositivi
2. Aprire l'app su tutti i dispositivi scelti
3. Scegliere i nomi dei dispositivi toccando la "cardview" sotto la scritta "This device"
2. Attendere la ricerca dei dispositivi nelle vicinanze
3. Collegarsi ad un dispositivo toccando l'elemento della lista
4. Chattare

### Reconnection usage
1. Attivare il WiFi su tutti i dispositivi
2. Aprire l'app su tutti i dispositivi scelti
3. Scegliere i nomi dei dispositivi toccando la "cardview" sotto la scritta "This device"
2. Attendere la ricerca dei dispositivi nelle vicinanze
3. Collegarsi ad un dispositivo toccando l'elemento della lista
4. Chattare
5. Disconnettere uno dei device, cliccando sulla seconda icona nella toolbar
6. Attendere alcuni secondi che "Eternal Discovery" trovi nuovamente il dispositivo
7. Scrivere un messaggio nella chat ed attendere la riconnessione automatica e l'invio di tale messaggeio. In alternativa collegarsi manualmente al dispositivo per continuare la chat.

### Queuing messages usage
1. Attivare il WiFi su tutti i dispositivi
2. Aprire l'app su tutti i dispositivi scelti
3. Scegliere i nomi dei dispositivi toccando la "cardview" sotto la scritta "This device"
2. Attendere la ricerca dei dispositivi nelle vicinanze
3. Collegarsi ad un dispositivo toccando l'elemento della lista
4. Chattare
5. Disconnettere uno dei device, cliccando sulla seconda icona nella toolbar
6. Disattivare la discovery riavviata da "Eternal Discovery" in modo che la lista di "Other devices" sia vuota
7. Scrivere alcuni messaggi nella chat in modo che siano accodati
8. Riattivare la discovery e riconnettersi al dispositivo in uno dei metodi specificati nel caso "Reconnection usage", step 7. 
9. I messaggi accodati saranno inviati a destinazione in un unico messaggio


## Important things

### Configuration
If you want to configure this app as you prefer, pay attention to: `Configuration.java`.

If you want to realese this application without debug messages inside chats, change this constant to "false" :
```java
    public static final boolean DEBUG_VERSION = true;
```

If you want to change client's and groupowner's ports, change this:
```java
    public static final int GROUPOWNER_PORT = 4545;
    public static final int CLIENT_PORT = 5000;
```

If you want to change the maximum number of devices that a GO can manage for the chat, change this:
```java
    public static final int THREAD_COUNT = 20;
```

This attributes are used inside this app to exchange informations betweend devices, like a protocol, to initialize the associated chat:
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
As you can see in `DestinationDeviceTabList` (attribute deviceList) and `ServiceList` (attribute serviceList) there is this annotation because if you access or change this attributes without the custom logic that i implemented in these classes, to do secure operations, you can obtain Exceptions or other problems.
These classes remap list's indexes, add/set object without duplicates and in a particular way, that is very necessary to this software. Every time that you want to change something here, you should create a secure method to manage these attributes.

### P2pDestinationDevice, a WifiP2pDevice abstraction
Nelle API di Android c'è una mancanza enorme, cioè devi metodi per ottenere l'indirizzo ip del dispositivo corrente in modo rapido, facile e sicuro.
Esistono due modi per farlo, il primo richiede l'esecuzione di comandi linux nella shell, il secondo (quello che ho scelto) è il seguente: per ottenere l'ip del group owner è in onConnectionInfoAvailable, soluzione molto facile, ma per il client bisogna ottenerlo dal Socket, una volta che il client ha stabilito la connessione. Quindi è il GO a ottenere tutti gli indirizzi IP, mentre i client possono conoscere solo quello del proprio GO, ma non il loro.
Quindi è necessario trasmette l'indirizzo del Client al client stesso, perchè esso lo salvi e lo possa utilizzare per visualizzarlo nell'interfaccia grafica.

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
TODO



*Stefano*
