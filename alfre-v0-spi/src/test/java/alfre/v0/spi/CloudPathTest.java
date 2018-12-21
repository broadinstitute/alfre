package alfre.v0.spi;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.mockito.Mockito.mock;

import org.junit.jupiter.api.Test;

class CloudPathTest {

  @Test
  void getFileSystem() {
    final CloudFileSystem mockedFileSystem = mock(CloudFileSystem.class);
    final UnixPath unixPath = UnixPath.getPath("pathtofile");
    final String host = "example.com";
    final CloudPath cloudPath = new CloudPath(mockedFileSystem, unixPath);
    assertEquals(mockedFileSystem, cloudPath.getFileSystem());
  }

  @Test
  void getRoot() {
    final CloudFileSystem mockedFileSystem = mock(CloudFileSystem.class);
    final UnixPath rootUnixPath = UnixPath.ROOT_PATH;
    final String host = "example.com";
    final CloudPath rootCloudPath = new CloudPath(mockedFileSystem, rootUnixPath);
    final UnixPath childUnixPath = UnixPath.getPath("/pictures/2018/October/pumpkin.jpeg");
    final CloudPath childCloudPath = new CloudPath(mockedFileSystem, childUnixPath);
    assertEquals(rootCloudPath, childCloudPath.getRoot());
  }

  @Test
  void getParent() {}

  @Test
  void resolve() {
    final CloudFileSystem mockedFileSystem = mock(CloudFileSystem.class);
    final UnixPath originalUnixPath = UnixPath.getPath(("dir/my.txt"));
    final UnixPath resolvedUnixPath = UnixPath.getPath(("/users/shirin/dir/my.txt"));
    final UnixPath exampleUnixPath = UnixPath.getPath("/users/shirin");
    final String host = "example.com";
    final CloudPath originalCloudPath = new CloudPath(mockedFileSystem, originalUnixPath);
    final CloudPath resolvedCloudPath = new CloudPath(mockedFileSystem, resolvedUnixPath);
    final CloudPath exampleCloudPath = new CloudPath(mockedFileSystem, exampleUnixPath);
    assertEquals(resolvedCloudPath, exampleCloudPath.resolve(originalCloudPath));
    final UnixPath badExampleUnixPath = UnixPath.getPath(("/users/kshakir"));
    final CloudPath badExampleCloudPath = new CloudPath(mockedFileSystem, badExampleUnixPath);
    assertNotEquals(badExampleCloudPath, exampleCloudPath.resolve((originalCloudPath)));
  }
}
