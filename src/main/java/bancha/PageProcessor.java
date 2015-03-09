package bancha;


public interface PageProcessor {
    void processPage(BanchaPage page) throws BanchaException;
    void cleanUp() throws BanchaException;
}
