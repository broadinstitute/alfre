package woodard.spi;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.mockito.Mockito.mock;

import java.nio.file.FileSystem;
import org.junit.jupiter.api.Test;

class CloudNioPathTest {

  @Test
  void getFileSystem() {
    FileSystem mockedFileSystem = mock(FileSystem.class);
    UnixPath unixPath = UnixPath.getPath("pathtofile");
    String host = "example.com";
    CloudNioPath cloudPath = new CloudNioPath(host, unixPath, mockedFileSystem);
    assertEquals(mockedFileSystem, cloudPath.getFileSystem());
  }

  @Test
  void getRoot() {
    FileSystem mockedFileSystem = mock(FileSystem.class);
    UnixPath rootUnixPath = UnixPath.ROOT_PATH;
    String host = "example.com";
    CloudNioPath rootCloudPath = new CloudNioPath(host, rootUnixPath, mockedFileSystem);
    UnixPath childUnixPath = UnixPath.getPath("/pictures/2018/October/pumpkin.jpeg");
    CloudNioPath childCloudPath = new CloudNioPath(host, childUnixPath, mockedFileSystem);
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
    CloudNioPath originalCloudNioPath = new CloudNioPath(host, originalUnixPath, mockedFileSystem);
    CloudNioPath resolvedCloudPath = new CloudNioPath(host, resolvedUnixPath, mockedFileSystem);
    CloudNioPath exampleCloudPath = new CloudNioPath(host, exampleUnixPath, mockedFileSystem);
    assertEquals(resolvedCloudPath, exampleCloudPath.resolve(originalCloudNioPath));
    UnixPath badExampleUnixPath = UnixPath.getPath(("/users/kshakir"));
    CloudNioPath badExampleCloudPath = new CloudNioPath(host, badExampleUnixPath, mockedFileSystem);
    assertNotEquals(badExampleCloudPath, exampleCloudPath.resolve((originalCloudNioPath)));
  }
}
