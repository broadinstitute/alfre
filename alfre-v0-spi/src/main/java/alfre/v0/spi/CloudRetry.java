package alfre.v0.spi;

import alfre.v0.spi.function.SupplierWithExceptions;

@FunctionalInterface
public interface CloudRetry {

  <T, E extends Exception> T runWithRetries(SupplierWithExceptions<T, E> supplier)
      throws CloudRetryException;
}
