package alfre.v0.cli;

public class Main {

  public static void main(final String[] args) {
    final Options options = MainCommand.parseOptions(args);
    new MainRunner().run(options);
  }
}
