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
}
