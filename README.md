# WiFiDirect MultiChat

## Informations
WiFiDirect MultiChat (aka MultiChat) is a demostrative Android's application that try to overcome some WiFi-Direct limitations.
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
6. Creazione di una chat tra GO e un client
7. Gestione di infinite chat, accessibili tramite tab
8. Riconnessione automatica inviando un messaggio in una chat aperta precedentemente, se il dispositivo associato è disocverable
9. Accodamento dei messaggi scritti in una chat nel caso in cui il dispositivo non sia discoverable, ed invio automaticatico al successvo tentativo di riconnessione
10. *"Eternal Discovery"* che permette il riavvio della fase di Discovery ad ogni evento di disconnessione o errore.

## Future extensions
1. Chat di gruppo, cioè il GO riceve i messaggi di un client e li reinvia a tutti gli altri suo client in broadcast
2. Chat di gruppo private tra clients. Cioè un client usa il GO come server per inviare messaggi ad un altro client, con cui non è direttamente connesso.

## License







``` bash
    $ cd learnDCL
    $ git branch dcl
    $ git checkout dcl
```
* Open the project contained in the root folder with *Android Studio*.
* Read **carefully** the class `MainActivity.java` in the `app` module. 

```
    https://dl.dropboxusercontent.com/blablabla
```


*Stefano*
