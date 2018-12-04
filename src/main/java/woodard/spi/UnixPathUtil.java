package woodard.spi;

import com.google.common.collect.PeekingIterator;

public class UnixPathUtil {
  static void appendToPath(
      UnixPath other,
      PeekingIterator<String> left,
      PeekingIterator<String> right,
      StringBuilder result) {
    while (left.hasNext()) {
      result.append(UnixPath.PARENT_DIR);
      result.append(UnixPath.SEPARATOR);
      left.next();
    }
    while (right.hasNext()) {
      result.append(right.next());
      result.append(UnixPath.SEPARATOR);
    }
    if (result.length() > 0 && !other.hasTrailingSeparator()) {
      result.deleteCharAt(result.length() - 1);
    }
  }
}
