package alfre.v0.spi;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.mockito.Mockito.mock;

import java.nio.file.FileSystem;
import org.junit.jupiter.api.Test;

class CloudPathTest {

  @Test
  void getFileSystem() {
    FileSystem mockedFileSystem = mock(FileSystem.class);
    UnixPath unixPath = UnixPath.getPath("pathtofile");
    String host = "example.com";
    CloudPath cloudPath = new CloudPath(host, unixPath, mockedFileSystem);
    assertEquals(mockedFileSystem, cloudPath.getFileSystem());
  }

  @Test
  void getRoot() {
    FileSystem mockedFileSystem = mock(FileSystem.class);
    UnixPath rootUnixPath = UnixPath.ROOT_PATH;
    String host = "example.com";
    CloudPath rootCloudPath = new CloudPath(host, rootUnixPath, mockedFileSystem);
    UnixPath childUnixPath = UnixPath.getPath("/pictures/2018/October/pumpkin.jpeg");
    CloudPath childCloudPath = new CloudPath(host, childUnixPath, mockedFileSystem);
    assertEquals(rootCloudPath, childCloudPath.getRoot());
  }

  @Test
  void getParent() {}

  @Test
  void resolve() {
    FileSystem mockedFileSystem = mock(FileSystem.class);
    UnixPath originalUnixPath = UnixPath.getPath(("dir/my.txt"));
    UnixPath resolvedUnixPath = UnixPath.getPath(("/users/shirin/dir/my.txt"));
    UnixPath exampleUnixPath = UnixPath.getPath("/users/shirin");
    String host = "example.com";
    CloudPath originalCloudPath = new CloudPath(host, originalUnixPath, mockedFileSystem);
    CloudPath resolvedCloudPath = new CloudPath(host, resolvedUnixPath, mockedFileSystem);
    CloudPath exampleCloudPath = new CloudPath(host, exampleUnixPath, mockedFileSystem);
    assertEquals(resolvedCloudPath, exampleCloudPath.resolve(originalCloudPath));
    UnixPath badExampleUnixPath = UnixPath.getPath(("/users/kshakir"));
    CloudPath badExampleCloudPath = new CloudPath(host, badExampleUnixPath, mockedFileSystem);
    assertNotEquals(badExampleCloudPath, exampleCloudPath.resolve((originalCloudPath)));
  }
}
