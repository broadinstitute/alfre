package alfre.v0.impl.http;

public class HttpsFileSystemProvider extends AbstractHttpFileSystemProvider {

  @Override
  public String getScheme() {
    return "https";
  }
}
