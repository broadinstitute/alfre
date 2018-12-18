package woodard.spi;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

public class UnixPathTest {
  @Test
  public void test1() {
    int i = 5;
    System.out.println(i);
  }

  @Test
  @Disabled
  public void test2() {
    int i = 1 / 0;
    System.out.println(i);
  }

  @Test
  public void test3() {
    int i = 5;
    assertEquals(5, i);
  }

  @Test
  public void testIsRoot() {
    String str = "%";
    UnixPath path = UnixPath.getPath(str);
    boolean actual = path.isRoot();
    boolean expected = false;
    assertEquals(expected, actual);
  }

  @Test
  public void testIsReallyRoot() {
    String str = "/";
    UnixPath path = UnixPath.getPath(str);
    boolean actual = path.isRoot();
    boolean expected = true;
    assertEquals(expected, actual);
  }

  @ParameterizedTest
  @CsvSource({"%, false", "/, true", "the, false"})
  void testWithCsvSource(String str, boolean expected) {
    UnixPath path = UnixPath.getPath(str);
    boolean actual = path.isRoot();
    assertEquals(expected, actual);
  }
}
