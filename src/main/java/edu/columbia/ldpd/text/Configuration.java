package edu.columbia.ldpd.text;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.Collections;
import java.util.Properties;


public class Configuration {
	private static final String DEFAULT = "default";
    private static final String DEFAULT_COLLECTIONS = "default.collections";
    private static final String DEFAULT_HOMEDIR = "default.homeDir";
    private static final String DEFAULT_URL_PREFIX = "default.urlPrefix";
    private static final String[] EMPTY = new String[0];
    
    private final String env;
    private final Properties properties;
    private final String collectionsKey;
    private final String homeDirKey;
    private final String urlPrefixKey;

    public Configuration() {
    	this(DEFAULT);
    }

    public Configuration(String env, String path) throws IOException {
    	this(env,new FileInputStream(path));
    }

    public Configuration(String env, InputStream src) throws IOException {
        this(env);
        properties.load(src);
    }

    public Configuration(String env) {
        this.env = env;
        this.collectionsKey = env + ".collections";
        this.homeDirKey = env + ".homeDir";
        this.urlPrefixKey = env + ".urlPrefix";
        this.properties = new Properties();
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
    public Iterable<Path> teiFiles() throws IOException {
    	return java.nio.file.Files.newDirectoryStream(teiDir().toPath());
    }
    public File xmlDir() throws FileNotFoundException {
    	String dirPath = get("xmlDir");
    	File dir = new File(dirPath);
    	if (!dir.isAbsolute()){
    		return new File(homeDir(),dirPath);
    	} else {
    		return dir;
    	}
    }
    public Iterable<Path> xmlFiles() throws IOException {
    	String dirPath = get("xmlDir");
        String xmlPath = get("xmlFile");
        if (dirPath != null && xmlPath == null) {
        	return java.nio.file.Files.newDirectoryStream(xmlDir().toPath());
        }
        File xmlFile = new File(xmlPath);
    	if (!xmlFile.isAbsolute()){
    		File parent = (dirPath == null) ? homeDir() : xmlDir();
    		return Collections.singletonList(new File(parent,xmlPath).toPath());
    	} else {
    		return Collections.singletonList(new File(xmlPath).toPath());
    	}
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
    public boolean onlyCollections() {
    	return Boolean.valueOf(get("onlyCollections"));
    }
}
