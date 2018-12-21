package alfre.v0.spi;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

@SuppressWarnings("WeakerAccess")
public class UnixPathTest {
  @Test
  public void testIsRoot() {
    final String str = "%";
    final UnixPath path = UnixPath.getPath(str);
    final boolean actual = path.isRoot();
    final boolean expected = false;
    assertEquals(expected, actual);
  }

  @Test
  public void testIsReallyRoot() {
    final String str = "/";
    final UnixPath path = UnixPath.getPath(str);
    final boolean actual = path.isRoot();
    final boolean expected = true;
    assertEquals(expected, actual);
  }

  @ParameterizedTest
  @CsvSource({"%, false", "/, true", "the, false"})
  void testWithCsvSource(final String str, final boolean expected) {
    final UnixPath path = UnixPath.getPath(str);
    final boolean actual = path.isRoot();
    assertEquals(expected, actual);
  }
}
