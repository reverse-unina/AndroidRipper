/**
 * GNU Affero General Public License, version 3
 * 
 * Copyright (c) 2014-2017 REvERSE, REsEarch gRoup of Software Engineering @ the University of Naples Federico II, http://reverse.dieti.unina.it/
 *
 * This program is free software: you can redistribute it and/or  modify
 * it under the terms of the GNU Affero General Public License, version 3,
 * as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 **/

package it.unina.android.ripper.tools.actions;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.ArrayUtils;

import it.unina.android.ripper.tools.lib.AndroidTools;
import it.unina.android.ripper.tools.lib.WrapProcess;

/**
 * Interaction with the device and the Host PC
 * 
 * @author Nicola Amatucci - REvERSE
 *
 */
public class Actions {

	/**
	 * TODO: aggiungere ai metodi DEVICE e rimuovere questo
	 */
	public static String DEVICE = "emulator-5554";
	
	/**
	 * Time (in seconds) to wait AndroidRipperService to be started
	 */
	public static int ANDROID_RIPPER_SERVICE_WAIT_SECONDS = 3;

	/**
	 * Time (in seconds) to wait AndroidRipperTestCase to be started
	 */
	public static int ANDROID_RIPPER_WAIT_SECONDS = 3;

	/**
	 * Time (in seconds) to wait Android Emulator to be started
	 */
	public static int START_EMULATOR_NO_SNAPSHOOT_WAIT_SECONDS = 0;

	/**
	 * Time (in seconds) to wait Android Emulator to be started
	 */
	public static int START_EMULATOR_SNAPSHOOT_WAIT_SECONDS = 0;

	/**
	 * Send Back Key to device
	 */
	public static void sendBackKey() {
		try {
			AndroidTools.adb("-s", DEVICE, "shell", "input", "keyevent", "4").waitFor();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
		}
	}

	public static void unlockDevice() {
		try {
			AndroidTools.adb("-s", DEVICE, "shell", "input", "keyevent", "82").waitFor();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
		}
	}

	/**
	 * Send Home Key to device
	 */
	public static void sendHomeKey() {
		try {
			AndroidTools.adb("-s", DEVICE, "shell", "input", "keyevent", "3").waitFor();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
		}
	}

	/**
	 * Start AndroidRipperService on device
	 */
	public static void startAndroidRipperService() {
		try {
			AndroidTools.adb("-s", DEVICE, "shell",
					"am startservice -a it.unina.android.ripper_service.ANDROID_RIPPER_SERVICE")
					.connectStdout(System.out).connectStderr(System.out);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
		}

		sleepSeconds(ANDROID_RIPPER_SERVICE_WAIT_SECONDS);
	}

	/**
	 * Sleep
	 * 
	 * @param seconds
	 *            seconds to sleep
	 */
	public static void sleepSeconds(int seconds) {
		sleepMilliSeconds(seconds * 1000);
	}

	/**
	 * Sleep
	 * 
	 * @param milli
	 *            milliseconds to sleep
	 */
	public static void sleepMilliSeconds(int milli) {
		try {
			Thread.sleep(milli);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
		}
	}

	/**
	 * Ripper Active status
	 */
	public static boolean ripperActive = false;

	/**
	 * Ripper Active status
	 * 
	 * @return status
	 */
	public static boolean isRipperActive() {
		return ripperActive;
	}

	/**
	 * Set Ripper Active Status
	 * 
	 * @param b
	 *            status
	 */
	public static void setRipperActive(boolean b) {
		ripperActive = b;
	}

	/**
	 * Start Android Ripper for an AUT on the device
	 * 
	 * @param AUT_PACKAGE
	 *            Package of the AUT
	 */
	public static void startAndroidRipper(String AUT_PACKAGE) {
		//createAUTFilesDir(AUT_PACKAGE);

		new Thread() {
			public void run() {
				try {
					ripperActive = true;
					WrapProcess adb = AndroidTools.adb("-s", DEVICE, "shell",
										"am instrument -w -e class it.unina.android.ripper.RipperTestCase it.unina.android.ripper/android.test.InstrumentationTestRunner")
								.connectStdout(System.out).connectStderr(System.out).waitFor();
					adb.waitFor();

					ripperActive = false;
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					// e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					// e.printStackTrace();
				} // .connectStdout(System.out).connectStderr(System.err);
			}
		}.start();

		sleepSeconds(ANDROID_RIPPER_WAIT_SECONDS);
	}

	/**
	 * Start an emulator without loading the snapshot
	 * 
	 * @param AVD_NAME
	 *            Name of the emulator
	 * @param EMULATOR_PORT
	 *            Port of the emulator
	 */
	public static void startEmulatorNoSnapshotLoad(final String AVD_NAME, final int EMULATOR_PORT) {
		(new Thread() {
			public void run() {
				try {
					// AndroidTools.emulator("@"+AVD_NAME,"-partition-size","129","-no-snapshot-load",
					// "-port",Integer.toString(EMULATOR_PORT)).connectStdout(System.out).connectStderr(System.err).waitForSuccess();
					AndroidTools.emulator("@" + AVD_NAME, "-no-snapshot-load", "-port", Integer.toString(EMULATOR_PORT))
							.connectStdout(System.out).connectStderr(System.err).waitForSuccess();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					// e.printStackTrace();
				}
			}
		}).start();

		sleepSeconds(START_EMULATOR_NO_SNAPSHOOT_WAIT_SECONDS);
	}

	/**
	 * Start an emulator without loading the snapshot and wipes the user
	 * partition
	 * 
	 * @param AVD_NAME
	 *            Name of the emulator
	 * @param EMULATOR_PORT
	 *            Port of the emulator
	 */
	public static void startEmulatorNoSnapshotLoadWipeData(final String AVD_NAME, final int EMULATOR_PORT) {
		(new Thread() {
			public void run() {
				try {
					// AndroidTools.emulator("@"+AVD_NAME,"-partition-size","129","-no-snapshot-load",
					// "-wipe-data" ,
					// "-port",Integer.toString(EMULATOR_PORT)).connectStdout(System.out).connectStderr(System.err).waitForSuccess();
					AndroidTools.emulator("@" + AVD_NAME, "-no-snapshot-load", "-wipe-data", "-port",
							Integer.toString(EMULATOR_PORT)).connectStdout(System.out).connectStderr(System.err)
							.waitForSuccess();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					// e.printStackTrace();
				}
			}
		}).start();

		sleepSeconds(START_EMULATOR_NO_SNAPSHOOT_WAIT_SECONDS);
	}

	/**
	 * Start an emulator without saving the snapshot
	 * 
	 * @param AVD_NAME
	 *            Name of the emulator
	 * @param EMULATOR_PORT
	 *            Port of the emulator
	 */
	public static void startEmulatorNoSnapshotSave(final String AVD_NAME, final int EMULATOR_PORT) {
		try {
			// AndroidTools.emulator("@"+AVD_NAME,"-partition-size","129","-no-snapshot-save",
			// "-port",Integer.toString(EMULATOR_PORT)).connectStdout(System.out).connectStderr(System.out);
			AndroidTools.emulator("@" + AVD_NAME, "-no-snapshot-save", "-port", Integer.toString(EMULATOR_PORT))
					.connectStdout(System.out).connectStderr(System.out);

			sleepSeconds(START_EMULATOR_SNAPSHOOT_WAIT_SECONDS);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
		}
	}

	/**
	 * Start an emulator without loading saving the snapshot
	 * 
	 * @param AVD_NAME
	 *            Name of the emulator
	 * @param EMULATOR_PORT
	 *            Port of the emulator
	 */
	public static void startEmulatorNoSnapshotLoadSave(final String AVD_NAME, final int EMULATOR_PORT) {
		try {
			// AndroidTools.emulator("@"+AVD_NAME,"-partition-size","129","-no-snapshot-save",
			// "-port",Integer.toString(EMULATOR_PORT)).connectStdout(System.out).connectStderr(System.out);
			AndroidTools.emulator("@" + AVD_NAME, "-no-snapshot-load", "-wipe-data", "-no-snapshot-save", "-port",
					Integer.toString(EMULATOR_PORT)).connectStdout(System.out).connectStderr(System.out);

			sleepSeconds(START_EMULATOR_SNAPSHOOT_WAIT_SECONDS);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
		}
	}

	/**
	 * Start an emulator
	 * 
	 * @param AVD_NAME
	 *            Name of the emulator
	 * @param EMULATOR_PORT
	 *            Port of the emulator
	 */
	public static void startEmulator(final String AVD_NAME, final int EMULATOR_PORT) {
		try {
			// AndroidTools.emulator("@"+AVD_NAME,"-partition-size","129","-no-snapshot-save",
			// "-port",Integer.toString(EMULATOR_PORT)).connectStdout(System.out).connectStderr(System.out);
			AndroidTools.emulator("@" + AVD_NAME, "-port", Integer.toString(EMULATOR_PORT)).connectStdout(System.out)
					.connectStderr(System.out);

			sleepSeconds(START_EMULATOR_SNAPSHOOT_WAIT_SECONDS);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
		}
	}

	/**
	 * Wait for a device to be online
	 * 
	 */
	public static void waitForDeviceOnline() {

		boolean waitingDeviceBoot = true;

		do {

			try {
				final Process p = Runtime.getRuntime().exec("adb devices");

				try {
					String line = "";
					BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));
					while ((line = input.readLine()) != null) {
						if (line != null && line.contains(DEVICE)) {
							if (line.contains("device")) {
								waitingDeviceBoot = false;
							}
						}
					}
					input.close();
				} catch (Exception ex) {
					ex.printStackTrace();
				}

				p.waitFor();
			} catch (Exception ex) {
				ex.printStackTrace();
			}

			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				// e.printStackTrace();
			}

		} while (waitingDeviceBoot);

	}

	/**
	 * Wait for an device to boot
	 * 
	 */
	public static void waitForDeviceBoot() {

		boolean waitingDeviceOnline = true;

		do {

			try {
				final Process p = Runtime.getRuntime().exec("adb -s " + DEVICE + " shell getprop init.svc.bootanim");

				try {
					String line = "";
					BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));
					while ((line = input.readLine()) != null) {
						if (line.contains("stopped")) {
							waitingDeviceOnline = false;
						}
					}
					input.close();
				} catch (Exception ex) {
					ex.printStackTrace();
				}

				p.waitFor();
			} catch (Exception ex) {
				ex.printStackTrace();
			}

			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				// e.printStackTrace();
			}

		} while (waitingDeviceOnline);

	}

	/**
	 * Wait for a process identified by its package to be ended on an device
	 * 
	 * @param AUT_PACKAGE
	 *            Package of the AUT
	 */
	public static void waitForProcessToEnd(String AUT_PACKAGE) {

		boolean found = false;

		do {

			found = false;

			try {
				final Process p = Runtime.getRuntime().exec("adb -s " + DEVICE + " shell ps");

				try {
					String line = "";
					BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));

					while ((line = input.readLine()) != null) {
						if (line.contains(AUT_PACKAGE)) {
							found = true;
						}
					}

					input.close();
				} catch (Exception ex) {
					ex.printStackTrace();
				}

				p.waitFor();
			} catch (Exception ex) {
				ex.printStackTrace();
			}

			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				// e.printStackTrace();
			}

		} while (found);

	}

	/**
	 * Kill a process running on an device identified by the package of the AUT
	 * 
	 * @param AUT_PACKAGE
	 *            Package of the AUT
	 * @return
	 */
	public static boolean killProcessByPackage(String AUT_PACKAGE) {
		String pid = getProcessPID(AUT_PACKAGE);
		return killProcess(pid);
	}

	/**
	 * Kill a process on the device by using its PID
	 * 
	 * @param PID
	 *            Process ID
	 * @return
	 */
	public static boolean killProcess(String PID) {
		if (PID != null) {
			try {
				AndroidTools.adb("-s", DEVICE, "shell", "ps", "-9", PID).waitFor();
				return true;
			} catch (Exception e) {
				// e.printStackTrace();
			}
		}

		return false;
	}

	/**
	 * Get the PID of an AUT identified by its package
	 * 
	 * @param AUT_PACKAGE
	 *            Package of the AUT
	 * @return Process ID
	 */
	public static String getProcessPID(String AUT_PACKAGE) {

		String pid = null;

		try {
			final Process p = Runtime.getRuntime().exec("adb -s " + DEVICE + " shell ps " + AUT_PACKAGE);

			try {
				String line = "";
				BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));

				while ((line = input.readLine()) != null) {
					if (line.contains(AUT_PACKAGE)) {
						String[] split = line.split(" +", -3);
						pid = split[1];
					}
				}

				input.close();
			} catch (Exception ex) {
				ex.printStackTrace();
			}

			p.waitFor();
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
		}

		return pid;
	}

	/**
	 * Wait for a device
	 */
	public static void waitForDevice() {
		waitForDeviceOnline();
		System.out.println("Device " + DEVICE + " Online!");
		waitForDeviceBoot();
		System.out.println("Device " + DEVICE + " Booted!");
	}

	/**
	 * Wait for an device to be closed
	 */
	public static void waitDeviceClosed() {
		boolean waitingDeviceClose = false;

		do {

			waitingDeviceClose = false;

			try {
				final Process p = Runtime.getRuntime().exec("adb devices");

				try {
					String line = "";
					BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));
					while ((line = input.readLine()) != null) {
						if (line != null && line.contains(DEVICE)) {
							if (line.contains("device")) {
								waitingDeviceClose = true;
							}
						}
					}
					input.close();
				} catch (Exception ex) {
					ex.printStackTrace();
				}

				p.waitFor();
			} catch (Exception ex) {
				ex.printStackTrace();
			}

			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				// e.printStackTrace();
			}

		} while (waitingDeviceClose);

		System.out.println("Device offline!");

	}

	/**
	 * Wait for a process to end
	 * 
	 * @param AUT_PACKAGE
	 *            Package of the AUT
	 * @param maxIter
	 *            Max Retry
	 * @return
	 */
	public static boolean waitForProcessToEndMaxIterations(String AUT_PACKAGE, int maxIter) {
		boolean found = false;

		int iter = 0;

		do {

			found = false;

			try {
				final Process p = Runtime.getRuntime().exec("adb -s " + DEVICE + " shell ps " + AUT_PACKAGE);

				try {
					String line = "";
					BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));

					while ((line = input.readLine()) != null) {
						if (line.contains(AUT_PACKAGE)) {
							found = true;
						}
					}

					input.close();
				} catch (Exception ex) {
					ex.printStackTrace();
				}

				p.waitFor();
			} catch (Exception ex) {
				ex.printStackTrace();
			}

			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				// e.printStackTrace();
			}

		} while (found && ++iter <= maxIter);

		return (iter > maxIter);
	}

	public static boolean installAPK(String apk) {
		try {
			int sdk = getAndroidSDKVersion();
			WrapProcess p;
			if (sdk < 23) {
				p = AndroidTools.adb("-s", DEVICE, "install", "-f", apk);
			} else {
				p = AndroidTools.adb("-s", DEVICE, "install", "-f", "-g", apk);
			}
			try {
				String line = "";
				BufferedReader input = new BufferedReader(new InputStreamReader(p.getStdout()));

				while ((line = input.readLine()) != null) {
					if (line.startsWith("Failure")) {
						System.out.println(line);
						System.out.println("Failed Installing APK");
						if (line.contains("INSTALL_FAILED_NO_MATCHING_ABIS")) {
							System.out.println("System Architecture (ARM/x86) not compatible!");
						}
						return false;
					}
				}

				input.close();
			} catch (Exception ex) {
				ex.printStackTrace();
			}

			p.waitFor();
//			System.out.println(apk +" installed!");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			//// e.printStackTrace();
			return false;
		}

		return true;
	}

	public static void uninstallAPK(String autPackage) {
		try {
			AndroidTools.adb("-s", DEVICE, "shell", "pm", "clear", autPackage).connectStdout(System.out).waitFor();
			AndroidTools.adb("-s", DEVICE, "uninstall", autPackage).connectStdout(System.out).waitFor();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			//// e.printStackTrace();
		}
	}
	
	public static void stopAndClearAPP(String autPackage) {
		try {
			AndroidTools.adb("-s", DEVICE, "shell", "am", "force-stop", autPackage).connectStdout(System.out).waitFor();
			AndroidTools.adb("-s", DEVICE, "shell", "pm", "clear", autPackage).connectStdout(System.out).waitFor();
			AndroidTools.adb("-s", DEVICE, "shell", "am", "force-stop", autPackage).connectStdout(System.out).waitFor();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			//// e.printStackTrace();
		}
	}

	public static void redirectPort(int SERVICE_DEVICE_PORT, int SERVICE_HOST_PORT) {
		try {
			AndroidTools.adb("-s", DEVICE, "forward", "tcp:" + SERVICE_DEVICE_PORT, "tcp:" + SERVICE_HOST_PORT)
					.connectStderr(System.out).connectStdout(System.out);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
		}
	}

	public static void killEmulator() {
		try {
			AndroidTools.adb("-s", DEVICE, "emu", "kill").connectStderr(System.out).connectStdout(System.out);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
		}
	}

	public static void killApp(String appPackage) {
		try {
			AndroidTools.adb("-s", DEVICE, "shell", "am", "force-stop", appPackage).connectStderr(System.out)
					.connectStdout(System.out);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
		}
	}

	public static void bringToFront(String appPackage) {
		try {
			AndroidTools.adb("-s", DEVICE, "shell", "am", "start", "-n", appPackage).connectStderr(System.out)
					.connectStdout(System.out);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
		}
	}

	public static void waitForForegroundActivityPackage(String pack) {
		waitForForegroundActivityPackage(pack, 15);
	}

	public static void waitForForegroundActivityPackage(String pack, int max_retry) {
		while (max_retry-- >= 0 || checkCurrentForegroundActivityPackage(pack) == false) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				// e.printStackTrace();
			}
		}
	}

	public static boolean checkCurrentForegroundActivityPackage(String pack) {
		try {
			
			Process p = Runtime.getRuntime().exec(new String[] {"adb", 
					"-s", DEVICE, 
					"shell", "dumpsys activity | grep top-activity"});
			
			try {
				String line = "";
				BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));

				while ((line = input.readLine()) != null) {
					if (line.contains("(top-activity)")) {
						// System.out.println(line);
						if (line.contains(pack)) {
							return true;
						}
					}
				}

				input.close();
			} catch (Exception ex) {
				// ex.printStackTrace();
				System.out.println(ex.getMessage());
			}

			p.waitFor();

		} catch (Exception e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
			return false;
		}

		return false;
	}

	public static String getCurrentForegroundActivityPackage() {
		try {
			WrapProcess p = AndroidTools.adb("-s", DEVICE, "shell", "dumpsys", "activity");

			try {
				String line = "";
				BufferedReader input = new BufferedReader(new InputStreamReader(p.getStdout()));

				while ((line = input.readLine()) != null) {
					if (line.contains("(top-activity)")) {
						int start = line.lastIndexOf(":") + 1;
						int end = line.lastIndexOf("/");
						return line.substring(start, end);
					}
				}

				input.close();
			} catch (Exception ex) {
				// ex.printStackTrace();
				System.out.println(ex.getMessage());
			}

			p.waitFor();

		} catch (Exception e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
		}

		return null;
	}

	public static String getRealDeviceIP() {
		String ip = null;

		try {
			WrapProcess p = AndroidTools.adb("-s", DEVICE, "shell", "ifconfig", "rndis0");

			try {
				String line = "";
				BufferedReader input = new BufferedReader(new InputStreamReader(p.getStdout()));

				while ((line = input.readLine()) != null) {
					if (line.contains("inet addr:") || line.contains("rndis0: ip 192.168.42")) {

						try {
							Pattern pattern = Pattern.compile("192\\.168\\.42\\.\\d{1,3}");
							Matcher m = pattern.matcher(line);

							if (m.find()) {
								ip = m.group(0);
							}
						} catch (Throwable t) {
							throw new RuntimeException("Device IP not found!");
						}

					}
				}

				input.close();
			} catch (Exception ex) {
				// ex.printStackTrace();
				System.out.println(ex.getMessage());
			}

			p.waitFor();

		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.out.println(e.getMessage());
		}

		return ip;
	}
	
	public static int getAndroidSDKVersion() {
		String s = null;
		try {
			WrapProcess p = AndroidTools.adb("-s", DEVICE, "shell", "getprop", "ro.build.version.sdk");

			try {
				String line = "";
				BufferedReader input = new BufferedReader(new InputStreamReader(p.getStdout()));

				s = input.readLine();

				input.close();
				
			} catch (Exception ex) {
				// ex.printStackTrace();
				System.out.println(ex.getMessage());
			}

			p.waitFor();

		} catch (Exception e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
		}

		return Integer.parseInt((s!=null)?s:"0");
	}

	
	public static boolean checkApplicationInstalled(String pack) {
		try {
			WrapProcess p = AndroidTools.adb("-s", DEVICE, "shell", "pm", "path", pack);

			try {
				String line = "";
				BufferedReader input = new BufferedReader(new InputStreamReader(p.getStdout()));

				while ((line = input.readLine()) != null) {
					if (line.contains("apk")) {
						return true;
					}
				}

				input.close();
			} catch (Exception ex) {
				// ex.printStackTrace();
				System.out.println(ex.getMessage());
			}

			p.waitFor();

		} catch (Exception e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
			return false;
		}

		return false;
	}

	/**
	 * Verify screen status (off,locked, or on)
	 * 
	 * @return 0 if off, 1 if locked,2 if on
	 */
	public static int checkDeviceScreen() {

		boolean displayOn = false;
		boolean unlocked = false;

		int status = -1;

		try {
			WrapProcess p = AndroidTools.adb("-s", DEVICE, "shell", "dumpsys", "power");
			String line = "";
			BufferedReader input = new BufferedReader(new InputStreamReader(p.getStdout()));

			while ((line = input.readLine()) != null) {
				if (line.contains("mHoldingWakeLockSuspendBlocker"))
					if (line.contains("true"))
						unlocked = true;
					else if (line.contains("mHoldingDisplaySuspendBlocker"))
						if (line.contains("true"))
							displayOn = true;
			}

			input.close();
			p.waitFor();

		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.out.println(e.getMessage());
		}

		if (displayOn && unlocked)
			status = 2;
		else if (displayOn && !unlocked)
			status = 1;
		else
			status = 0;

		return status;
	}
	
	public static void adbShell(String ... strings) {
		String shell[] = {"shell"};
		String[] both = (String[])ArrayUtils.addAll(shell, strings);
		try {
			AndroidTools.adb(both).waitFor();
		} catch (InterruptedException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void adbSuShell(String ... strings) {
		String shell[] = {"shell", "sudo", "-c"};
		String[] both = (String[])ArrayUtils.addAll(shell, strings);
		try {
			AndroidTools.adb(both).waitFor();
		} catch (InterruptedException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	//public static void pull(String AUT_PACKAGE, String src, String dest) {
	public static void pull(String src, String dest) {
		try {
			AndroidTools.adb("-s", DEVICE, "pull", src, dest).connectStderr(System.out).connectStdout(System.out).waitFor();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void pushToSD(String string) {
		try {
			WrapProcess p;
			p = AndroidTools.adb("-s", DEVICE, "push", string, "/sdcard/").connectStderr(System.out).connectStdout(System.out);
			p.waitFor();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}
	
	public static void push(String string, String dest) {
		try {
			WrapProcess p;
			p = AndroidTools.adb("-s", DEVICE, "push", string, dest).connectStderr(System.out).connectStdout(System.out);
			p.waitFor();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}
	
	public static void installFromSD(String string) {
		try {
			int sdk = getAndroidSDKVersion();
			WrapProcess p;
			if (sdk < 23) {
				p =AndroidTools.adb("-s", DEVICE, "shell", "pm", "install", "-f", "/sdcard/"+string).connectStderr(System.out).connectStdout(System.out);
			} else {
				p =AndroidTools.adb("-s", DEVICE, "shell", "pm", "install", "-f", "-g", "/sdcard/"+string).connectStderr(System.out).connectStdout(System.out);
			}
			p.waitFor();
			System.out.println(string +" installed!");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void sendEvent(String event){
		try {
			AndroidTools.adb("-s", DEVICE, "shell", "sendevent", event).connectStdout(System.out).waitFor();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			//// e.printStackTrace();
		}
	}
	
}
