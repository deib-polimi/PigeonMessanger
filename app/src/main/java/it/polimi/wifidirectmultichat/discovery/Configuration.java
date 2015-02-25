package it.polimi.wifidirectmultichat.discovery;

/**
 * Class to configure some important attribute.
 * Created by Stefano Cappa on 17/02/15.
 */
public class Configuration {
    public static final int GROUPOWNER_PORT = 4545;
    public static final int CLIENT_PORT = 5000;
    public static final int THREAD_COUNT = 30; //maximum number of clients that this GO can manage
    public static final int THREAD_POOL_EXECUTOR_KEEP_ALIVE_TIME = 10; //don't touch this!!!

    public static final String TXTRECORD_PROP_AVAILABLE = "available";
    public static final String SERVICE_INSTANCE = "_polimip2p";
    public static final String SERVICE_REG_TYPE = "_presence._tcp";

    public static final int MESSAGE_READ = 0x400 + 1;
    public static final int MY_HANDLE = 0x400 + 2;

    public static final String MESSAGE_READ_MSG = "MESSAGE_READ";
    public static final String MY_HANDLE_MSG = "MY_HANDLE";

    public static final String MAGICADDRESSKEYWORD = "4<D<D<R<3<5<5";
}
