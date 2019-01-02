package alfre.v0.spi.function;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/** Via: https://stackoverflow.com/a/27644392 */
@SuppressWarnings({"unused", "RedundantThrows"})
public final class LambdaExceptionUtil {

  /**
   * .forEach(rethrowConsumer(name -> System.out.println(Class.forName(name)))); or
   * .forEach(rethrowConsumer(ClassNameUtil::println));
   */
  public static <T, E extends Exception> Consumer<T> rethrowConsumer(
      final ConsumerWithExceptions<T, E> consumer) throws E {
    return t -> {
      try {
        consumer.accept(t);
      } catch (final Exception exception) {
        throwAsUnchecked(exception);
      }
    };
  }

  /** Same as rethrowConsumer for a BiConsumer. */
  public static <T, U, E extends Exception> BiConsumer<T, U> rethrowBiConsumer(
      final BiConsumerWithExceptions<T, U, E> biConsumer) throws E {
    return (t, u) -> {
      try {
        biConsumer.accept(t, u);
      } catch (final Exception exception) {
        throwAsUnchecked(exception);
      }
    };
  }

  /** .map(rethrowFunction(name -> Class.forName(name))) or .map(rethrowFunction(Class::forName)) */
  public static <T, R, E extends Exception> Function<T, R> rethrowFunction(
      final FunctionWithExceptions<T, R, E> function) throws E {
    return t -> {
      try {
        return function.apply(t);
      } catch (final Exception exception) {
        throwAsUnchecked(exception);
        return null;
      }
    };
  }

  /** rethrowSupplier(() -> new StringJoiner(new String(new byte[]{77, 97, 114, 107}, "UTF-8"))). */
  public static <T, E extends Exception> Supplier<T> rethrowSupplier(
      final SupplierWithExceptions<T, E> function) throws E {
    return () -> {
      try {
        return function.get();
      } catch (final Exception exception) {
        throwAsUnchecked(exception);
        return null;
      }
    };
  }

  /** uncheck(() -> Class.forName("xxx")); */
  public static <E extends Exception> void uncheck(final RunnableWithExceptions<E> runnable) {
    try {
      runnable.run();
    } catch (final Exception exception) {
      throwAsUnchecked(exception);
    }
  }

  /** uncheck(() -> Class.forName("xxx")); */
  public static <R, E extends Exception> R uncheck(final SupplierWithExceptions<R, E> supplier) {
    try {
      return supplier.get();
    } catch (final Exception exception) {
      throwAsUnchecked(exception);
      return null;
    }
  }

  /** uncheck(Class::forName, "xxx"). */
  public static <T, R, E extends Exception> R uncheck(
      final FunctionWithExceptions<T, R, E> function, final T t) {
    try {
      return function.apply(t);
    } catch (final Exception exception) {
      throwAsUnchecked(exception);
      return null;
    }
  }

  @SuppressWarnings("unchecked")
  private static <E extends Throwable> void throwAsUnchecked(final Exception exception) throws E {
    throw (E) exception;
  }
}
