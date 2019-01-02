package alfre.v0.spi.function;

@FunctionalInterface
public interface SupplierWithExceptions<T, E extends Exception> {

  T get() throws E;
}
