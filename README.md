# Java Audio file utility
This is a small, cross-platform command line utility to modify and play audio files. The supported file formats are : `AIFC`, `AIFF`, `AU`, `SND`, `WAVE`. 

The features include: 
- Changing volume
- Changing speed
- Play audio

## How to use
This is the help message displayed when either you enter a wrong command or enter the `-h` or  `--help` command. 
```bash
Usage: java-ios-1.0-SNAPSHOT.jar [-hV] <filename> [COMMAND]
A small CLI to modify audio files.
      <filename>   The name of the file.
  -h, --help       Show this help message and exit.
  -V, --version    Print version information and exit.
Commands:
  play    Play audio file.
  volume  Change volume on audio file.
  speed   Change speed on audio file.
```
For the volume and speed commands, you will need to add a `-i=` which corresponds to the intensity. 

### Example commands
**Play**: 
```bash
java -jar your_executable_path.jar your_audio_file.wav play
```
This will Play your audio file from your terminal. 

**Speed**: 
```bash
java -jar your_executable_path.jar your_audio_file.wav speed -i=1.5
```
Intensity takes positive floating point values. In the example here, it will speed up the audio file by 1.5 times. This will output a file in your current directory which will be called `original_file_name_x1.5.wav` for this example. 

**Volume**: 
```bash
java -jar your_executable_path.jar your_audio_file.wav volume -i=2.7
```
This will make the audio louder by 2.7 times. Intensity can take any floating point value. 

## Contribute / Build from source
First clone this repo on your machine and `cd` in the cloned folder: 
```bash
git clone git@github.com:BasileBux/java-io-audio-files.git
cd java-io-audio-files
```
You need to have [Maven](https://maven.apache.org/) and [SdkMan](https://sdkman.io/) installed on your system. 

Then, you need to download the project dependencies with the following command: 
```bash
./mvnw dependency:go-offline
```

To build, you have two options: 
- If you have [IntelliJ](https://www.jetbrains.com/idea/) installed, you can simply launch the project with it and, in the top right corner, select the `Package application as JAR file` option and run it. 
- If you want to do it from the terminal, you can run the following command: 
```bash
./mvnw package
```

Both options will generate a `.jar` archive in the `target` directory. To run the application, run: 
```
java -jar target/java-ios-1.0-SNAPSHOT.jar
```
and add the options you want. 

### Future options

If you want to contribute, feel free to create your issue and create a pull request. The features which would be neat to have next, are the following:
- Modifications only on a selected part of the audio file
- Changing the speed with negative intensity value reverses the audio

## Libraries
The project tries to stay the closest to `java` and `javax` and use few external dependencies. The only external dependency used, is [picocli](https://picocli.info/). 

For the audio we stayed with the `javax.sound` library which doesn't support many file types but is enough for what we aimed to do. 