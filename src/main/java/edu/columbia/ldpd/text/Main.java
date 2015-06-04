package edu.columbia.ldpd.text;

import java.util.concurrent.Callable;

public abstract class Main {
    public static void usage () {
        System.out.println("usage:  Main [ nny | bancha | help ] [ prod | test | dev ] $PROPERTIES");
    }

	public static void main(String[] args) {
        // argument processing
        if (args.length < 3) {
            usage();
            System.exit(-1);
        }
        String environment = args[1];
        if ( ! environment.equals("dev") &&
                ! environment.equals("test") &&
                ! environment.equals("prod") ) {
            usage();
            System.exit(-1);
        }

        Configuration config = null;
        try {
            config = new Configuration(environment, args[2]);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.exit(-1);
        }
        Callable<TextIndexer> indexer = null;
        String app = args[0].toLowerCase();
        if (app.equals("nny")) {
        	indexer = new edu.columbia.ldpd.text.nny.IndexCallable(config);
        } else if (app.equals("bancha")) {
        	indexer = new edu.columbia.ldpd.text.bancha.IndexCallable(config);
        } else {
        	usage();
        	System.exit(-1);
        }
        try {
        	indexer.call();
        } catch (Exception e) {
            System.err.println("Fatal error on index file handling");
            System.err.println(e.getClass() + ": " + e.getMessage());
            System.exit(-1);
		}
	}

}
