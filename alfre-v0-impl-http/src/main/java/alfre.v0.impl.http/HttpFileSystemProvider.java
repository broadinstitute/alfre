package alfre.v0.impl.http;

public class HttpFileSystemProvider extends AbstractHttpFileSystemProvider {

  @Override
  public String getScheme() {
    return "http";
  }
}
