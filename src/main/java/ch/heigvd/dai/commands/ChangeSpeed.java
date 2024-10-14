package ch.heigvd.dai.commands;
import java.util.concurrent.Callable;

import ch.heigvd.dai.ios.AudioChangeSpeed;
import ch.heigvd.dai.ios.SpeedModifiable;
import picocli.CommandLine;


@CommandLine.Command(name = "speed", description = "Change speed on audio file.")

public class ChangeSpeed implements Callable<Integer> {
    @CommandLine.ParentCommand protected Root parent;

    @CommandLine.Option(
      names = {"-i", "--intensity"},
      description = "intensity of the modification",
      required = true)
  protected float modificationIntensity;

    @Override
    public Integer call() {
        SpeedModifiable modifier = new AudioChangeSpeed();
        
        modifier.changeSpeed(parent.filename, modificationIntensity);
        return 0;
    }
    
}
