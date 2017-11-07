## AndroidRipper

**AndroidRipper** is a toolset for the automatic GUI testing of mobile Android Applications.

It is developed and maintained by the **REvERSE** (REsEarch laboRatory of Software Engineering) Group of the University of Naples "Federico II".


## Test Environment Setup

Android Ripper works both on Windows and Unix OSs (it has been tested on Windows 10 and Ubuntu/Mint Linux) and has been tested on the latest Android APIs, up to Android 7.0 Nugat. To setup the environment to run Android Ripper, please follow the steps below. 

### Step 1 - Install Android SDK

[Download](https://developer.android.com/studio/index.html) and Install the Official Android SDK.

### Step 2 - Install Oracle JAVA 8

On Ubuntu Linux you can do this via the following commands:

```
sudo add-apt-repository ppa:webupd8team/java
sudo apt-get update
sudo apt-get install oracle-java8-installer
sudo apt-get install oracle-java8-set-default
```

### Step 3 - Download Android Tools and APIs using the Android SDK Manager

Use Android SDK Manager to download and install the required:
* Android SDK Tools
* Android SDK Platform Tools
* Android SDK Build Tools

For each API download the related SDK Platform and the relatd Intel and ARM System Images.

### Step 4 - Modify the PATH environment variable

Add to the PATH environment variable the following directories:

* PathToAndroidSdk/tools/
* PathToAndroidSdk/platform-tools/
* PathToAndroidSdk/build-tools/BUILDNUMBER/

e.g. in Linux add to the .bachrc file in the home folder:

```
export PATH=$PATH:/home/utente/android-sdk-linux/platform-tools/:/home/utente/android-sdk-linux/tools/:/home/utente/android-sdk-linux/build-tools/24.0.2/
```
### Step 5 - Android Virtual Devices (AVDs) Setup

The Android Ripper needs at least two AVDs to work properly because some apps can only run on a specific architecture. Create two different virtual devices, one equipped with an Intel System Image and one with an ARM System Image.

The values of the **avd_name_x86** and the **avd_name_arm** paramaters in the configuration file have to match the name of these two virtual devices.

> Android Ripper uses the Intel virtual device unless the app under test only works on an ARM architecture.


## Android Ripper Configuration

The default.properties file contains the Android Ripper configuration parameters. This file is used by default if no properties parameter is specified when running AndroidRipper. It is possible to create new properties files to manage different AndroidRipper configurations.

A template default.properties files is provided with the Android Ripper release.

### Configuration parameters

**target =** specifies the target device: can be _device_ for Hardware Device or _avd_ for Virtual Device

**avd_name_x86 =** Name of the AVD equipped with an x86 architecture
**avd_name_arm =** Name of the AVD equipped with an ARM architecture
**avd_port =** Port number used to communicate with the AVD (e.g. 5554)

**device =** the device id (e.g. obtained by the _adb devices_ command)
> To exploit an Hardware Device please enable USB Debug and USB Tethering on the device.

**driver=** Exploration strategy. Valid strategies: random, systematic
**scheduler=** Strategy implemented by the component that schedule the next event to be executed. For a Random Exploration Strategy the only possible value is "random" (default). For a Systematic Exploration Strategy: "breadth", "depth".


**random.events=** Number of events to be fired (used only for the random strategy)
**random.seed=** Random seed (used only for the random strategy; default is _System.currentTimeMillis()_)

**sleep_after_event=** 1000
**sleep_before_start_ripping=** 10000
**model=** if set to _1_ the model output is enabled, otherwise is disabled


## Android Ripper Execution

To launch Android Ripper from the command line:

```
java -jar AndroidRipper.jar apkFile [config.properties]
```

**Parameter 1** APK name and path.

**Parameter 2** Configuration name and file. A default property file is used if the last argument is left empty. This configuration file (default.properties) is placed in the AndroidRipper folder.


## Android Ripper Produced Artifacts

The following outputs are produced by Android Ripper:

* **Execution Log** (Logcat –v) in the logcat folder
* **Execution Statistics** In the report.xml file
* **GUI Model of the Application** in xml format, in the model folder

