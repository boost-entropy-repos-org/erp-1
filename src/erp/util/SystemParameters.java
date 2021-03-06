package erp.util;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class SystemParameters {

    /**
     * The instance of the singleton
     */
    private static SystemParameters _instance = null;

    /**
     * The Logger instance of the class
     */
    private final static Logger logger = LogManager.getLogger(SystemParameters.class);
    private long startUpTimestamp = System.currentTimeMillis();



    /**
     * Used for caching the system.properties file
     */
    private Properties properties = new Properties();
    public static final String MULTIPART_UPLOAD_DIRECTORY = "MULTIPART_UPLOAD_DIRECTORY";
    public static String appRoot = "";
  
    private SystemParameters() {
        super();
        logger.info("Initializing from empty constructor");
    }

    private SystemParameters(String parametersFile, String appRoot) {
        try {
            if(logger.isDebugEnabled()) logger.debug("STARTING SYSTEMPARAMS"); 
            this.appRoot = appRoot;       
            
            // read the parameters
            FileInputStream in = new FileInputStream(parametersFile);
            properties = new Properties();
            properties.put(this.MULTIPART_UPLOAD_DIRECTORY, StringToolbox.replaceAll(appRoot, "\\", "/") + "/tmp/");
            properties.load(in);
            in.close();

            String logFileFullPathName = StringToolbox.replaceAll(appRoot, "\\", "/") + "/logs/erp.log";                       
            if(logger.isDebugEnabled()) logger.debug("Initializing from : " + parametersFile + "\n log file is written to : " + logFileFullPathName);          
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            logger.fatal("Cannot find system.properties file : " + parametersFile + " due to : " + e.toString());
            logger.fatal(e, e);
        } catch (IOException e) {
            e.printStackTrace();
            logger.fatal("General IO error while loading system.properties file : " + parametersFile + " into Properties object, due to : " + e.toString());
            logger.fatal(e, e);
        }
    }

    /**
     * Singleton static instance getter method. This methos is static
     * synchronized in order to to achieve the single instatiaton of the
     * _instance parameters. Even in a multithreaded environment (like JVM) this
     * is guaranteed through the static synchronization.
     *
     * @return a SystemParameter instance
     */
    public static synchronized SystemParameters getInstance() {
        if (_instance == null) {
            _instance = new SystemParameters();
        }
        return _instance;
    }

    /**
     * Singleton static instance getter method. This methos is static
     * synchronized in order to to achieve the single instatiaton of the
     * _instance parameters. Even in a multithreaded environment (like JVM) this
     * is guaranteed through the static synchronization. This method is just an
     * overloaded version of the previous one. It is used from the Web tier for
     * initialization purposes.
     *
     * @param parametersFile is the full path name of the system.parameters file
     * @param refresh if true then the cache is flushed and refreshed, used for
     * refreshing operations should they are necessary of course.
     * @return a SystemParameter instance
     */
    public static synchronized SystemParameters getInstance(String parametersFile, String appRoot, boolean refresh) {
        if ((_instance == null) || refresh) {
            _instance = new SystemParameters(parametersFile, appRoot);
        }
        return _instance;
    }

    /**
     * Reads a system parameter from the cache
     *
     * @param name is the name of the parameter
     * @return the String value of the parameter.
     */
    public String getProperty(String name) {
        return (String) properties.get(name);
    }

    /**
     * Returns the startup timestamp
     *
     * @return the start up timestamp (epoc number)
     */
    public long getStartupTimestamp() {
        return this.startUpTimestamp;
    }

}
