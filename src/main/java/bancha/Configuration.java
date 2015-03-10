package bancha;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;


public class Configuration {
    private static final String DEFAULT_COLLECTIONS = "default.collections";
    private static final String DEFAULT_HOMEDIR = "default.homeDir";
    private static final String DEFAULT_URL_PREFIX = "default.urlPrefix";
    private static final String[] EMPTY = new String[0];
    
    private final String env;
    private final Properties properties;
    private final String collectionsKey;
    private final String homeDirKey;
    private final String urlPrefixKey;
    public Configuration(String env, String path) throws IOException {
    	this(env,new FileInputStream(path));
    }
    public Configuration(String env, InputStream src) throws IOException {
        properties = new Properties();
        properties.load(src);
        this.env = env;
        this.collectionsKey = env + ".collections";
        this.homeDirKey = env + ".homeDir";
        this.urlPrefixKey = env + ".urlPrefix";
    }
    public File homeDir() throws FileNotFoundException {
        String path = properties.getProperty(
            homeDirKey,
            properties.getProperty(DEFAULT_HOMEDIR)
        );
        if (path == null) throw new FileNotFoundException("no homeDir configured \"" + homeDirKey + "\"");
        return new File(path);
    }
    public File errataDir() throws FileNotFoundException {
        return new File(homeDir(),get("errataDir"));
    }
    public File teiDir() throws FileNotFoundException {
        return new File(xmlDir(),"tei");
    }
    public File xmlDir() throws FileNotFoundException {
        return new File(homeDir(),get("xmlDir"));
    }
    public String get(String key){
        return properties.getProperty(
            env + "." + key,
            properties.getProperty("default." + key)
        );
    }

    public String set(String key, String value){
    	return (String) properties.setProperty(env + "." + key, value);
    }

    public String [] collections() {
        String collections = properties.getProperty(
            collectionsKey,
            properties.getProperty(DEFAULT_COLLECTIONS)
        );
        if (collections == null) return EMPTY;
        return collections.split(",");
    }
    public String urlPrefix() {
        return properties.getProperty(
            urlPrefixKey,
            properties.getProperty(DEFAULT_URL_PREFIX)
        );
    }
}
