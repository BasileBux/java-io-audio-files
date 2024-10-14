package ch.heigvd.dai.commands;

import picocli.CommandLine;

@CommandLine.Command(
    description = "A small CLI to modify audio files.",
    version = "1.0.0",
    subcommands = {
      Play.class,
            ChangeVolume.class,
            ChangeSpeed.class
    },
    scope = CommandLine.ScopeType.INHERIT,
    mixinStandardHelpOptions = true)
public class Root {

  @CommandLine.Parameters(index = "0", description = "The name of the file.")
  protected String filename;

  public String getFilename() {
    return filename;
  }

}
