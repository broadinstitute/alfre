package alfre.v0.spi.function;

@FunctionalInterface
public interface BiConsumerWithExceptions<T, U, E extends Exception> {

  void accept(T t, U u) throws E;
}
