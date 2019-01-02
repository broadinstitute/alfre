package alfre.v0.spi.function;

@FunctionalInterface
public interface RunnableWithExceptions<E extends Exception> {

  void run() throws E;
}
