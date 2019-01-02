package alfre.v0.spi.function;

@FunctionalInterface
public interface FunctionWithExceptions<T, R, E extends Exception> {

  R apply(T t) throws E;
}
