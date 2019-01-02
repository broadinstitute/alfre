package alfre.v0.spi;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.mockito.Mockito.mock;

import org.junit.jupiter.api.Test;

@SuppressWarnings("unchecked")
class CloudPathTest {

  @Test
  void getFileSystem() {
    final CloudFileSystem<CloudHost> mockedFileSystem =
        (CloudFileSystem<CloudHost>) mock(CloudFileSystem.class);
    final UnixPath unixPath = UnixPath.getPath("pathtofile");
    final CloudPath<CloudHost> cloudPath = new CloudPath<>(mockedFileSystem, unixPath);
    assertEquals(mockedFileSystem, cloudPath.getFileSystem());
  }

  @Test
  void getRoot() {
    final CloudFileSystem<CloudHost> mockedFileSystem =
        (CloudFileSystem<CloudHost>) mock(CloudFileSystem.class);
    final UnixPath rootUnixPath = UnixPath.ROOT_PATH;
    final CloudPath<CloudHost> rootCloudPath = new CloudPath<>(mockedFileSystem, rootUnixPath);
    final UnixPath childUnixPath = UnixPath.getPath("/pictures/2018/October/pumpkin.jpeg");
    final CloudPath<CloudHost> childCloudPath = new CloudPath<>(mockedFileSystem, childUnixPath);
    assertEquals(rootCloudPath, childCloudPath.getRoot());
  }

  @Test
  void getParent() {}

  @Test
  void resolve() {
    final CloudFileSystem<CloudHost> mockedFileSystem =
        (CloudFileSystem<CloudHost>) mock(CloudFileSystem.class);
    final UnixPath originalUnixPath = UnixPath.getPath(("dir/my.txt"));
    final UnixPath resolvedUnixPath = UnixPath.getPath(("/users/shirin/dir/my.txt"));
    final UnixPath exampleUnixPath = UnixPath.getPath("/users/shirin");
    final CloudPath<CloudHost> originalCloudPath =
        new CloudPath<>(mockedFileSystem, originalUnixPath);
    final CloudPath<CloudHost> resolvedCloudPath =
        new CloudPath<>(mockedFileSystem, resolvedUnixPath);
    final CloudPath<CloudHost> exampleCloudPath =
        new CloudPath<>(mockedFileSystem, exampleUnixPath);
    assertEquals(resolvedCloudPath, exampleCloudPath.resolve(originalCloudPath));
    final UnixPath badExampleUnixPath = UnixPath.getPath(("/users/kshakir"));
    final CloudPath<CloudHost> badExampleCloudPath =
        new CloudPath<>(mockedFileSystem, badExampleUnixPath);
    assertNotEquals(badExampleCloudPath, exampleCloudPath.resolve((originalCloudPath)));
  }
}
