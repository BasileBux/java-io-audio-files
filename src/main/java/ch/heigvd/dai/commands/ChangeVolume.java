package ch.heigvd.dai.commands;
import java.util.concurrent.Callable;

import ch.heigvd.dai.ios.AudioChangeVolume;
import ch.heigvd.dai.ios.VolumeModifiable;
import picocli.CommandLine;


@CommandLine.Command(name = "volume", description = "Change volume on audio file.")
public class ChangeVolume implements Callable<Integer> {
    @CommandLine.ParentCommand protected Root parent;

    @CommandLine.Option(
      names = {"-i", "--intensity"},
      description = "intensity of the modification",
      required = true)
  protected float modificationIntensity;

    @Override
    public Integer call() {
        VolumeModifiable modifier = new AudioChangeVolume();
        
        modifier.changeVolume(parent.filename, modificationIntensity);
        return 0;
    }
}

