package ch.heigvd.dai.commands;

import java.util.concurrent.Callable;

import ch.heigvd.dai.ios.AudioPlayback;
import ch.heigvd.dai.ios.Playable;
import picocli.CommandLine;

@CommandLine.Command(name = "play", description = "Play audio file.")
public class Play implements Callable<Integer> {
    @CommandLine.ParentCommand protected Root parent;

    @Override
    public Integer call() {
        Playable player = new AudioPlayback();
        
        player.play(parent.getFilename());
        return 0;
    }
}
