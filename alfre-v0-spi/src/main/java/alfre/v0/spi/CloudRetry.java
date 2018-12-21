package alfre.v0.spi;

@FunctionalInterface
public interface CloudRetry {

  <T> T runWithRetries(CloudSupplier<T> supplier) throws CloudRetryException;
}
