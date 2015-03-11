package bancha;


public interface BanchaPageTransformer<T> {
    public T transform(BanchaPage page) throws BanchaException;

    public String idFor(BanchaPage page);
}
