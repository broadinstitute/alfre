package alfre.v0.spi;

public interface CloudHost extends Comparable<CloudHost> {

  String getUriAsString(String cloudPath);

  String getRelativeHostPath(String cloudPath);
}
