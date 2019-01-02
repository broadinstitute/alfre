package alfre.v0.util;

import java.nio.file.Path;
import java.util.Objects;

@SuppressWarnings("WeakerAccess")
public final class PathPair {
  private final Path one;
  private final Path two;

  public PathPair(final Path one, final Path two) {
    this.one = one;
    this.two = two;
  }

  public Path getOne() {
    return one;
  }

  public Path getTwo() {
    return two;
  }

  @Override
  public boolean equals(final Object other) {
    if (this == other) {
      return true;
    }
    if (!(other instanceof PathPair)) {
      return false;
    }
    final PathPair that = (PathPair) other;
    return Objects.equals(getOne(), that.getOne()) && Objects.equals(getTwo(), that.getTwo());
  }

  @Override
  public int hashCode() {
    return Objects.hash(getOne(), getTwo());
  }

  @Override
  public String toString() {
    return String.format("PathPair{one=%s, two=%s}", getOne(), getTwo());
  }
}
