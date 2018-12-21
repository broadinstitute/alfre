package alfre.v0.spi;

@FunctionalInterface
public interface CloudSupplier<T> {

  T get() throws Exception;
}
