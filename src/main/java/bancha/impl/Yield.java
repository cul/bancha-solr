package bancha.impl;

import org.apache.commons.digester.Rule;


public class Yield<T> extends Rule {
    private final T wrapped;
    private Yield(T wrapped){
        this.wrapped = wrapped;
    }
    @Override
    public void begin(String namespace, String name, org.xml.sax.Attributes attributes) {
        this.digester.push(wrapped);
    }
    @Override
    public void end(String namespace, String name) {
        this.digester.pop();
    }
    public T peek() {
        return this.wrapped;
    }
    public static <S> Yield<S> wrap(S wrapped){
        return new Yield<>(wrapped);
    }
}
