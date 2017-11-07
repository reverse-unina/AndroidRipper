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

package it.unina.android.ripper.driver;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.CopyOption;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Properties;

import it.unina.android.ripper.comparator.ActivityNameComparator;
import it.unina.android.ripper.comparator.ActivityStructureComparator;
import it.unina.android.ripper.comparator.IComparator;
import it.unina.android.ripper.comparator.WidgetPropertiesComparator;
import it.unina.android.ripper.driver.device.AndroidVirtualDevice;
import it.unina.android.ripper.driver.device.HardwareDevice;
import it.unina.android.ripper.driver.exception.RipperRuntimeException;
import it.unina.android.ripper.driver.exception.RipperUncaughtExceptionHandler;
import it.unina.android.ripper.driver.random.RandomDriver;
import it.unina.android.ripper.driver.systematic.SystematicDriver;
import it.unina.android.ripper.installer.OSSpecific;
import it.unina.android.ripper.installer.SearchableManifest;
import it.unina.android.ripper.installer.ZipUtils;
import it.unina.android.ripper.observer.RipperEventListener;
import it.unina.android.ripper.planner.Planner;
import it.unina.android.ripper.scheduler.BreadthScheduler;
import it.unina.android.ripper.scheduler.DepthScheduler;
import it.unina.android.ripper.scheduler.Scheduler;
import it.unina.android.ripper.scheduler.UniformRandomScheduler;
import it.unina.android.ripper.termination.TerminationCriterion;
import it.unina.android.ripper.tools.actions.Actions;
import it.unina.android.ripper.utils.RipperStringUtils;
import it.unina.android.shared.ripper.input.RipperInput;
import it.unina.android.shared.ripper.output.RipperOutput;

/**
 * Configure the AndroidRipperDriver, handle console output, start up the
 * ripping process
 * 
 * @author Nicola Amatucci - REvERSE
 *
 */
public class AndroidRipperStarter {

	public static String shell_CMD = OSSpecific.getShellCommand();

	/**
	 * Driver Type = RANDOM
	 */
	public final static String DRIVER_RANDOM = "random";
	
	/**
	 * Driver Type = SYSTEMATIC
	 */
	public final static String DRIVER_SYSTEMATIC = "systematic";

	/**
	 * Version
	 */
	public final static String VERSION = "2017.10";

	/**
	 * Configuration
	 */
	Properties conf;

	/**
	 * Driver Instance
	 */
	AbstractDriver driver;

	/**
	 * Configuration file name
	 */
	String configFile;

	/**
	 * Path to apk to test
	 */
	String apkToTest = null;

	/**
	 * RipperEventListener
	 */
	RipperEventListener eventListener;

	public RipperUncaughtExceptionHandler mRipperUncaughtExceptionHandler;
	
	/**
	 * Constructor
	 * 
	 * @param driverType
	 *            RipperDriver type
	 * @param apk
	 *            Apk to test
	 * @param configFile
	 *            Configuration file name
	 */
	public AndroidRipperStarter(String apk, String configFile, RipperEventListener eventListener, RipperUncaughtExceptionHandler ripperUncaughtExceptionHandler) {
		super();
		
		//set UncaughtExceptionHandler
		mRipperUncaughtExceptionHandler = ripperUncaughtExceptionHandler;
		Thread.currentThread().setUncaughtExceptionHandler(mRipperUncaughtExceptionHandler);
	
		String debugRipper = System.getenv("ANDROID_RIPPER_DEBUG");
		if (debugRipper != null && debugRipper.equals("1")) {
			mRipperUncaughtExceptionHandler.setPrintStackTrace(true);
		}
		
		if (new File(configFile).exists() == false) {
			throw new RipperRuntimeException(AndroidRipperStarter.class, "AndroidRipperStarter", "File " + configFile + " not Found!");
		}

		if (new File(apk).exists() == false) {
			throw new RipperRuntimeException(AndroidRipperStarter.class, "AndroidRipperStarter", "Apk " + apk + " not Found!");
		}

		this.configFile = configFile;
		this.apkToTest = apk;
		this.eventListener = eventListener;

		println("Loading configuration");
		conf = this.loadConfigurationFile(this.configFile);
	}

	public AndroidRipperStarter(String apkFile, String configFile, RipperEventListener eventListener, RipperUncaughtExceptionHandler ripperUncaughtExceptionHandler,
			HashMap<String, String> configurationOverride) {
		this(sanitizePath(apkFile), configFile,
				eventListener, ripperUncaughtExceptionHandler);

		for (String key : configurationOverride.keySet()) {
			String value = configurationOverride.get(key);
			
			if (key.equals("load")) {
				conf.put("load_manual_sequences", value);
			} else if (key.equals("store")) {
				conf.put("store_manual_sequences", value);
			} else {
				conf.put(key, value);
			}
		}
	}

	/**
	 * Starting staus. False if is started or not started
	 */
	boolean mIsStarting = false;
	public boolean isStarting() { return this.mIsStarting; }
	
	/**
	 * Configure and StartUp Ripping Process
	 */
	public void startRipping() {

		this.mIsStarting = true;
		
		validateEnvorinment();

		Scheduler scheduler = null;
		Planner planner = null;
		RipperInput ripperInput = null;
		RipperOutput ripperOutput = null;
		TerminationCriterion terminationCriterion = null;
		IComparator comparator = null;

		if (conf != null) {
			String driverType = conf.getProperty("driver", DRIVER_RANDOM);

			if (driverType.equals(DRIVER_SYSTEMATIC)) {
				println("Systematic Ripper " + VERSION);
			} else if (driverType.equals(DRIVER_RANDOM)) {
				println("Random Ripper " + VERSION);
			} else {
				throw new RipperRuntimeException(AndroidRipperStarter.class, "startRipping", "Driver Type not suppported!");
			}

			String reportFile = new String("report.xml");
			String logFilePrefix = new String("log_");
			String avd_name_x86 = conf.getProperty("avd_name_x86", null);
			String avd_name_arm = conf.getProperty("avd_name_arm", null);
			String avd_port = conf.getProperty("avd_port", "5554");

			boolean model_output_enable = conf.getProperty("model", "0").equals("1");
			
			boolean planner_activity_detector_enable = conf.getProperty("detector", "0").equals("1");
			boolean planner_rotation_enable = conf.getProperty("planner.rotation", "0").equals("1");
			
			String ping_max_retry = conf.getProperty("socket.ping_max_retry", "10");
			String ack_max_retry = conf.getProperty("socket.ack_max_retry", "10");
			String failure_threshold = conf.getProperty("socket.failure_threshold", "10");
			String ping_failure_threshold = conf.getProperty("socket.ping_failure_threshold", "5");
			String sleep_after_task = conf.getProperty("sleep_after_task", "5");
			String sleep_after_event = conf.getProperty("sleep_after_event", "0");

			String target = conf.getProperty("target", "avd");
			String device = conf.getProperty("device", null);
			
			String wait_after_install = conf.getProperty("sleep_after_install", "0");
			String wait_before_install = conf.getProperty("sleep_before_install", "0");
			String wait_after_manual_sequence = conf.getProperty("sleep_after_manual_sequence", "0");
			
			if (target != null && target.equals("device") && (device == null || device.equals(""))) {
				throw new RipperRuntimeException(AndroidRipperStarter.class, "startRipping", "No Device SET!");
			}

			DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
			Date date = new Date();
			String apkName = this.apkToTest.substring(this.apkToTest.lastIndexOf('/') + 1) + "_"
					+ dateFormat.format(date);

			String base_result_dir = null;
			try {
				base_result_dir = new java.io.File(".").getCanonicalPath() + "/" + apkName;
			} catch (Exception ex) {
				base_result_dir = "./" + apkName;
			}

			new File(base_result_dir).mkdirs();

			// temp path
			String tempPath = base_result_dir + "/temp";
			new File(tempPath).mkdir();

			String logcatPath = base_result_dir + "/logcat/";
			String xmlOutputPath = base_result_dir + "/model/";
			
		
			// installer parameters
			String aut_apk = apkToTest;
			String extractorClass = "SimpleNoValuesExtractor";

			String myPath = sanitizePath(Paths.get("").toAbsolutePath().toString() + "/tools/");

			String debugKeyStorePath = myPath + "/";// conf.getProperty("android_keystore_path",
													// null);
			String testSuitePath = myPath + "/AndroidRipper/";// conf.getProperty("testsuite_path",
																// null);
			String serviceApkPath = myPath + "/";// conf.getProperty("service_apk_path",
													// null);
			String toolsPath = myPath + "/";// conf.getProperty("tools_path",
											// null);

			// validation
			if (target != null && target.equals("avd")) {
				if (avd_name_x86 == null) {
					throw new RipperRuntimeException(AndroidRipperStarter.class, "startRipping", "avd_name_x86 null!");
				}

				if (avd_name_arm == null) {
					throw new RipperRuntimeException(AndroidRipperStarter.class, "startRipping", "avd_name_arm null!");
				}
			}

			if (aut_apk == null) {
				throw new RipperRuntimeException(AndroidRipperStarter.class, "startRipping", "aut_apk null!");
			}

			if (new File(aut_apk).exists() == false) {
				throw new RipperRuntimeException(AndroidRipperStarter.class, "startRipping", aut_apk + " does not exist!");
			}

			// get apk infos
			String[] APKinfos = extractAPKInfos(aut_apk);

			String aut_package = conf.getProperty("aut_package", APKinfos[0]);
			String aut_main_activity = conf.getProperty("main_activity", APKinfos[1]);

			println("DETECTED aut_package = " + aut_package);
			println("DETECTED aut_main_activity = " + aut_main_activity);

			if (aut_package == null || (aut_package != null && aut_package.equals(""))) {
				throw new RipperRuntimeException(AndroidRipperStarter.class, "startRipping", "aut_package null!");
			} else {
				mRipperUncaughtExceptionHandler.setPackageName(aut_package);
				
				//aut blacklist
				if (new File("blacklist.txt").exists()) {
					String line;
					try (
					    InputStream fis = new FileInputStream("blacklist.txt");
					    InputStreamReader isr = new InputStreamReader(fis, Charset.forName("UTF-8"));
					    BufferedReader br = new BufferedReader(isr);
					) {
					    while ((line = br.readLine()) != null) {
					        if (line.toLowerCase().equals(aut_package.toLowerCase())) {
					        	throw new RipperRuntimeException(AndroidRipperStarter.class, "startRipping", "App BlackListed!");
					        }
					    }
					} catch (FileNotFoundException e1) {
						throw new RipperRuntimeException(AndroidRipperStarter.class, "startRipping", e1.getMessage(), e1);
					} catch (IOException e1) {
						throw new RipperRuntimeException(AndroidRipperStarter.class, "startRipping", e1.getMessage(), e1);
					}
				}
			}
			
			//String no_reinstall_str = conf.getProperty("no_reinstall", "0");
			//boolean no_reinstall = (no_reinstall_str != null && no_reinstall_str.equals("1"));
			
			if (aut_main_activity == null) {
				throw new RipperRuntimeException(AndroidRipperStarter.class, "startRipping", "aut_main_activity null!");
			}
			
			String sleep_before_start_ripping = conf.getProperty("sleep_before_start_ripping", howMuchSleepINeed(aut_main_activity));
			
			// check avd
			String avd_name = avd_name_x86;
			if (target != null && target.equals("avd")) {

				if (checkAVD(avd_name_x86) == false) {
					throw new RipperRuntimeException(AndroidRipperStarter.class, "startRipping", "AVD X86 does not exist!");
				} else {

				}

				if (checkAVD(avd_name_arm) == false) {
					throw new RipperRuntimeException(AndroidRipperStarter.class, "startRipping", "AVD ARM does not exist!");
				}

				try {
					if (ZipUtils.containsDirectory(aut_apk, "lib")) {
						if (ZipUtils.containsDirectory(aut_apk, "lib/x86")
								|| ZipUtils.containsDirectory(aut_apk, "lib/x86_64")) {
							avd_name = avd_name_x86;
						} else if (ZipUtils.containsDirectory(aut_apk, "lib/armeabi")
								|| ZipUtils.containsDirectory(aut_apk, "lib/armeabi-v7a")
								|| ZipUtils.containsDirectory(aut_apk, "lib/arm64-v8a")) {
							avd_name = avd_name_arm;
						} else {
							throw new RipperRuntimeException(AndroidRipperStarter.class, "startRipping", "APK problem inspecting libs: unknown architecture!");
						}
					}
				} catch (Exception ex) {
					throw new RipperRuntimeException(AndroidRipperStarter.class, "startRipping", "APK problem inspecting libs!");
				}
			}

			if (new File(serviceApkPath + "/AndroidRipperService.apk").exists() == false) {
				throw new RipperRuntimeException(AndroidRipperStarter.class, "startRipping", serviceApkPath + "/AndroidRipperService.apk does not exist!");
			}

			if (new File(toolsPath).exists() == false || new File(toolsPath).isDirectory() == false) {
				throw new RipperRuntimeException(AndroidRipperStarter.class, "startRipping", toolsPath + " does not exist or is not a directory!");
			}

			if (new File(testSuitePath).exists() == false || new File(testSuitePath).isDirectory() == false) {
				throw new RipperRuntimeException(AndroidRipperStarter.class, "startRipping", testSuitePath + " does not exist or is not a directory!");
			}

			if (new File(debugKeyStorePath + "/debug.keystore").exists() == false) {
				throw new RipperRuntimeException(AndroidRipperStarter.class, "startRipping", debugKeyStorePath + "/debug.keystore does not exist!");
			}

			String ANDROID_RIPPER_SERVICE_WAIT_SECONDS = new String("3");
			String ANDROID_RIPPER_WAIT_SECONDS = new String("3");

			// RANDOM CONFIGURATION PARAMETERS
			String legacyRandomNumEvents = conf.getProperty("events", "50");
			String randomNumEvents = conf.getProperty("random.events", legacyRandomNumEvents);
			println("Number of random events : "+randomNumEvents);
			String randomTime = conf.getProperty("random.time_sec", null);

			// seed
			String myDefaultSeed = Long.toString(System.currentTimeMillis());
			String legacyRandomseed = conf.getProperty("seed", myDefaultSeed);
			String randomSeed = conf.getProperty("random.seed", legacyRandomseed);
			println("Random Seed = " + randomSeed);
			
			try {
				Files.copy(Paths.get(configFile), Paths.get(base_result_dir + "/default.properties"));
			} catch (IOException e1) {
				throw new RipperRuntimeException(AndroidRipperStarter.class, "startRipping", e1.getMessage(), e1);
			}

			String coverageFrequency = conf.getProperty("coverage.frequency", "100");
			String newLogFrequency = new String("100");
			String num_events_per_session = new String("0");

			//Planner Configuration
			Planner.ACTIVITY_DETECTOR_ENABLE = planner_activity_detector_enable;
			Planner.CAN_CHANGE_ORIENTATION = planner_rotation_enable;
			
			planner = new it.unina.android.ripper.planner.ConfigurationBasedPlanner();

			ripperInput = new it.unina.android.shared.ripper.input.XMLRipperInput();
			ripperOutput = new it.unina.android.shared.ripper.output.XMLRipperOutput();

			if (new java.io.File(logcatPath).exists() == false)
				new java.io.File(logcatPath).mkdir();

			if (new java.io.File(xmlOutputPath).exists() == false)
				new java.io.File(xmlOutputPath).mkdir();

			long seedLong = System.currentTimeMillis();
			if (randomSeed != null && randomSeed.equals("") == false) {
				seedLong = Long.parseLong(randomSeed);
			}

			String schedulerClass = conf.getProperty("scheduler",
					((driverType.equals(DRIVER_RANDOM)) ? "random" : "breadth"));
			if (driverType.equals(DRIVER_RANDOM)) {
				scheduler = new UniformRandomScheduler(seedLong);
			} else if (driverType.equals(DRIVER_SYSTEMATIC)) {
				if (schedulerClass != null && schedulerClass.equals("breadth")) {
					scheduler = new BreadthScheduler();
					println("Breadth First Scheduler");
				} else if (schedulerClass != null && schedulerClass.equals("depth")) {
					scheduler = new DepthScheduler();
					println("Depth First Scheduler");
				} else {
					throw new RipperRuntimeException(AndroidRipperStarter.class, "startRipping", "Scheduler not valid.");
				}
			} else {
				throw new RipperRuntimeException(AndroidRipperStarter.class, "startRipping", "Driver not valid.");
			}

			if (driverType.equals(DRIVER_SYSTEMATIC)) {
				terminationCriterion = new it.unina.android.ripper.termination.EmptyActivityStateListTerminationCriterion();
				
				
				
				String comparatorName = conf.getProperty("comparator", "activity-structure");
				
				if (comparatorName != null && comparatorName.equals("activity-name")) {
					comparator = new ActivityNameComparator();
				} else if (comparatorName != null && comparatorName.equals("widget-properties")) {
					comparator = new WidgetPropertiesComparator();
				} else {
					//default: "activity-structure"
					comparator = new ActivityStructureComparator();
				}
				
				
				
			} else if (driverType.equals(DRIVER_RANDOM)) {
				if (randomTime == null) {
					terminationCriterion = new it.unina.android.ripper.termination.MaxEventsTerminationCriterion(Integer.parseInt(randomNumEvents));
				} else {
					terminationCriterion = new it.unina.android.ripper.termination.TestingTimeBasedTerminationCriterion(Long.parseLong(randomTime) * 1000);
				}
			} else {
				throw new RipperRuntimeException(AndroidRipperStarter.class, "startRipping", "Driver not valid.");
			}

			// create APKs
			println("Creating APKs...");
			createAPKs(testSuitePath, aut_package, aut_main_activity, extractorClass, toolsPath, debugKeyStorePath,
					aut_apk, tempPath);

			println("Starting Ripper...");
			if (driverType.equals(DRIVER_SYSTEMATIC)) {

				driver = new SystematicDriver(scheduler, planner, ripperInput, comparator, terminationCriterion,
						ripperOutput);
				terminationCriterion.init(driver);
			} else if (driverType.equals(DRIVER_RANDOM)) {

				try {
					System.out.println(base_result_dir + "/random-seed.txt");
					Files.write(Paths.get(base_result_dir + "/random-seed.txt"), randomSeed.getBytes());
				} catch (IOException e1) {
					throw new RipperRuntimeException(AndroidRipperStarter.class, "startRipping", e1.getMessage(), e1);
				}
				
				if (driverType.equals(DRIVER_RANDOM)) {
					driver = new RandomDriver(scheduler, planner, ripperInput, ripperOutput, terminationCriterion);
					((RandomDriver) driver).RANDOM_SEED = seedLong;
					((RandomDriver) driver).NUM_EVENTS = Integer.parseInt(randomNumEvents);
					((RandomDriver) driver).NUM_EVENTS_PER_SESSION = Integer.parseInt(num_events_per_session);
					((RandomDriver) driver).NEW_LOG_FREQUENCY = Integer.parseInt(newLogFrequency);
					((RandomDriver) driver).COVERAGE_FREQUENCY = Integer.parseInt(coverageFrequency);
				}				

			} else {

				throw new RipperRuntimeException(AndroidRipperStarter.class, "startRipping", "Driver Type not supported!");

			}

			if (driver != null) {

				// installer
				driver.SERVICE_APK_PATH = serviceApkPath;
				driver.TEMP_PATH = tempPath;
				driver.RESULTS_PATH = base_result_dir;

				// apply common configuration parameters
				driver.REPORT_FILE = reportFile;
				driver.LOG_FILE_PREFIX = logFilePrefix;
				driver.AUT_PACKAGE = aut_package;
				driver.AUT_MAIN_ACTIVITY = aut_main_activity;
				driver.SLEEP_AFTER_TASK = Integer.parseInt(sleep_after_task);
				driver.SLEEP_AFTER_EVENT = Integer.parseInt(sleep_after_event);
				driver.PING_MAX_RETRY = Integer.parseInt(ping_max_retry);
				driver.ACK_MAX_RETRY = Integer.parseInt(ack_max_retry);
				driver.FAILURE_THRESHOLD = Integer.parseInt(failure_threshold);
				driver.PING_FAILURE_THRESHOLD = Integer.parseInt(ping_failure_threshold);
				driver.LOGCAT_PATH = logcatPath;
				driver.XML_OUTPUT_PATH = xmlOutputPath;
				driver.TOOLS_PATH=toolsPath;
				driver.WAIT_AFTER_INSTALL=Integer.parseInt(wait_after_install);
				driver.WAIT_BEFORE_INSTALL=Integer.parseInt(wait_before_install);
				
				driver.MODEL_OUTPUT_ENABLE = model_output_enable;
				
				driver.SLEEP_BEFORE_START_RIPPING=Integer.parseInt(sleep_before_start_ripping);
								
				if (target.equals("device")) {
					driver.device = new HardwareDevice(device);
				} else { // target.equals("avd")
					driver.device = new AndroidVirtualDevice(avd_name, Integer.parseInt(avd_port));
				}

				Actions.ANDROID_RIPPER_SERVICE_WAIT_SECONDS = Integer.parseInt(ANDROID_RIPPER_SERVICE_WAIT_SECONDS);
				Actions.ANDROID_RIPPER_WAIT_SECONDS = Integer.parseInt(ANDROID_RIPPER_WAIT_SECONDS);

				driver.setRipperEventListener(eventListener);
				driver.startRipping();
				this.mIsStarting = false;
				
				while (driver.isRunning()) {
					// TODO: stop pause commands
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						//e.printStackTrace();
					}
				}
			}

		} else {
			throw new RipperRuntimeException(AndroidRipperStarter.class, "startRipping", "Missing configuration file!");
		}

	}

	/**
	 * Load the configuration file into a Properties class instance
	 * 
	 * @param fileName
	 *            configuration file name
	 * @return
	 */
	private Properties loadConfigurationFile(String fileName) {
		Properties conf = new Properties();

		try {
			conf.load(new FileInputStream(fileName));
			return conf;
		} catch (IOException ex) {
			throw new RipperRuntimeException(AndroidRipperStarter.class, "loadConfigurationFile", "Unable to load cofngiruation file!", ex);
		}

		//return null;
	}

	/**
	 * Print a formatted debug line
	 * 
	 * @param line
	 *            line to print
	 */
	protected void println(String line) {
		if (eventListener != null) {
			eventListener.ripperLog(line);
		} else {
			System.out.println("[" + System.currentTimeMillis() + "] " + line);
		}
	}

	protected String[] getAppInfo(String sourcePath) {
		String[] ret = new String[2];

		String path = sourcePath + File.separator + "AndroidManifest.xml";
		SearchableManifest doc = new SearchableManifest(path);

		String thePackage = doc.parseXpath(MANIFEST_XPATH);
		String theClass = doc.parseXpath(CLASS_XPATH);

		String dot = (theClass.endsWith(".") || theClass.startsWith(".")) ? "" : ".";
		theClass = thePackage + dot + theClass;

		ret[0] = thePackage;
		ret[1] = theClass;

		return ret;
	}

	public final static String MANIFEST_XPATH = "//manifest[1]/@package";
	public final static String CLASS_XPATH = "//activity[intent-filter/action/@name='android.intent.action.MAIN'][1]/@name";

	protected void validateEnvorinment() {
		// system variables validation
//		String java_home = System.getenv("JAVA_HOME");
//		if (java_home == null || java_home.equals("")) {
//			throw new RipperRuntimeException(AndroidRipperStarter.class, "validateEnvorinment", "JAVA_HOME not set!");
//			//throw new RuntimeException("JAVA_HOME not set!");
//		}
//
//		String android_sdk = System.getenv("ANDROID_HOME");
//		if (android_sdk == null || android_sdk.equals("")) {
//			//throw new RuntimeException("ANDROID_HOME not set!");
//			throw new RipperRuntimeException(AndroidRipperStarter.class, "validateEnvorinment", "ANDROID_HOME not set!");
//		}

		// String path = System.getenv("PATH");

		if (validateCommand("java") == false) {
			//throw new RuntimeException("java not in PATH");
			throw new RipperRuntimeException(AndroidRipperStarter.class, "validateEnvorinment", "java executable not in PATH!");
		}

		if (validateCommand("jarsigner") == false) {
			//throw new RuntimeException("jarsigner not in PATH");
			throw new RipperRuntimeException(AndroidRipperStarter.class, "validateEnvorinment", "jarsigner executable not in PATH!");
		}

		if (validateCommand("zipalign") == false) {
			//throw new RuntimeException("zipalign not in PATH");
			throw new RipperRuntimeException(AndroidRipperStarter.class, "validateEnvorinment", "zipalign executable not in PATH!");
		}

//		if (validateCommand("android.bat list avd") == false) {
//			if (validateCommand("android list avd") == false) {
//				//throw new RuntimeException("android not in PATH");
//				throw new RipperRuntimeException(AndroidRipperStarter.class, "validateEnvorinment", "android executable not in PATH!");
//			}
//		}

		if (validateCommand("adb") == false) {
			//throw new RuntimeException("adb not in PATH");
			throw new RipperRuntimeException(AndroidRipperStarter.class, "validateEnvorinment", "adb executable not in PATH!");
		}

		if (validateCommand("emulator") == false) {
			//throw new RuntimeException("emulator not in PATH");
			throw new RipperRuntimeException(AndroidRipperStarter.class, "validateEnvorinment", "emulator executable not in PATH!");
		}
	}

	protected boolean validateCommand(String cmd) {
		try {
			Process proc = Runtime.getRuntime().exec(cmd);
			try {
				proc.destroy();
			} catch (Exception ex) {
			}
			return true;
		} catch (IOException e1) {
			// e1.printStackTrace();
			return false;
		}
	}

	protected void createAPKs(String testSuitePath, String appPackage, String appMainActivity, String extractorClass,
			String toolsPath, String debugKeyStorePath, String autAPK, String tempPath) {
		// replace strings
		println("Editing 'Configuration.java'");
		replaceStringsInFile(
				testSuitePath + "/smali/it/unina/android/ripper/configuration/Configuration.smali.template",
				testSuitePath + "/smali/it/unina/android/ripper/configuration/Configuration.smali", appPackage,
				appMainActivity);
		println("Editing 'AndroidManifest.xml'");
		replaceStringsInFile(testSuitePath + "/AndroidManifest.xml.template", testSuitePath + "/AndroidManifest.xml",
				appPackage, appMainActivity);

		try {
			println("Cleaning apks...");
			println("Building AndroidRipper...");

			execCommand("java -jar " + toolsPath + "apktool.jar b " + testSuitePath + " -o " + tempPath + "/ar.apk");

			println("Signing AndroidRipper...");

			execCommand("jarsigner -verbose -sigalg SHA1withRSA -digestalg SHA1 -keystore " + debugKeyStorePath
					+ "/debug.keystore -storepass android -keypass android " + tempPath + "/ar.apk androiddebugkey");
			execCommand("zipalign 4 " + tempPath + "/ar.apk " + tempPath + "/ripper.apk");

			Files.copy(FileSystems.getDefault().getPath(autAPK),
					FileSystems.getDefault().getPath(tempPath + "/temp.apk"),
					(CopyOption) StandardCopyOption.REPLACE_EXISTING);

			println("Signing AUT...");
			ZipUtils.deleteFromZip(tempPath + "/temp.apk");
			execCommand("jarsigner -verbose -sigalg SHA1withRSA -digestalg SHA1 -keystore " + debugKeyStorePath
					+ "/debug.keystore -storepass android -keypass android " + tempPath
					+ "/temp.apk androiddebugkey");
			execCommand("jarsigner -verify " + tempPath + "/temp.apk");
			execCommand("zipalign -v 4 " + tempPath + "/temp.apk " + tempPath + "/aut.apk");
			
		} catch (Throwable t) {
			throw new RipperRuntimeException(AndroidRipperStarter.class, "createAPKs", "apk build failed", t);
		}
	}

	protected void replaceStringsInFile(String templateFilePath, String outputFilePath, String appPackage,
			String appMainActivity) {
		try {

			Path templatePath = Paths.get(templateFilePath);
			Path outPath = Paths.get(outputFilePath);
			Charset charset = StandardCharsets.UTF_8;

			String content = new String(Files.readAllBytes(templatePath), charset);
			appPackage = appPackage.replaceAll("\\$", "\\\\\\$");
			content = content.replaceAll("%%_PACKAGE_NAME_%%", appPackage);
			appMainActivity = appMainActivity.replaceAll("\\$", "\\\\\\$");
			content = content.replaceAll("%%_CLASS_NAME_%%", appMainActivity);

			Files.write(outPath, content.getBytes(charset));

		} catch (Exception ex) {
			throw new RipperRuntimeException(AndroidRipperStarter.class, "replaceStringsInFile", ex.getMessage(), ex);
		}
	}

	protected boolean checkAVD(String avdName) {
		try {
			Process proc = Runtime.getRuntime().exec(OSSpecific.getAndroidListAVDCommand());
			BufferedReader stdInput = new BufferedReader(new InputStreamReader(proc.getInputStream()));
			String s;
			while ((s = stdInput.readLine()) != null) {
				if (s.contains("Name: ")) {
					String name = s.substring(s.indexOf("Name: ") + 6).trim();

					if (name.equals(avdName)) {
						return true;
					}
				}
			}
		} catch (IOException e1) {
			throw new RipperRuntimeException(AndroidRipperStarter.class, "replaceStringsInFile", e1.getMessage(), e1);
		}

		return false;
	}

	public static void execCommand(String cmd) {
		execCommand(cmd, true);
	}

	public static void execCommand(String cmd, boolean wait) {
		try {
			final Process p = Runtime.getRuntime().exec(shell_CMD + cmd);

			Thread t = new Thread() {
				public void run() {
					try {
						String line = "";
						BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));
						while ((line = input.readLine()) != null) {
							System.out.println(line);
						}
						input.close();
					} catch (Exception ex) {
						// ex.printStackTrace();
					}
				}
			};
			t.start();
			p.waitFor();
		} catch (Exception ex) {
			throw new RipperRuntimeException(AndroidRipperStarter.class, "execCommand", ex.getMessage(), ex);
		}
	}

	public static String[] extractAPKInfos(String apk) {
		String[] ret = new String[2];
		ret[0] = null;
		ret[1] = null;

		try {
			final Process p = Runtime.getRuntime().exec("aapt dump badging " + apk);

			String line = "";
			BufferedReader aaptReader = new BufferedReader(new InputStreamReader(p.getInputStream()));
			String contentLine = "";
			while ((contentLine = aaptReader.readLine()) != null) {
				// System.out.println(contentLine);

				if (ret[0] == null && contentLine.startsWith("package:")) {
					int ind = contentLine.indexOf("name=") + "name='".length();
					ret[0] = contentLine.substring(ind, contentLine.indexOf('\'', ind + 1)).trim();
				}

				if (ret[1] == null && contentLine.startsWith("launchable-activity:")) {
					int ind = contentLine.indexOf("name=") + "name='".length();
					ret[1] = contentLine.substring(ind, contentLine.indexOf('\'', ind + 1)).trim();
				}

				if (ret[0] != null && ret[1] != null) {
					break;
				}
			}

			try {
				aaptReader.close();
			} catch (Throwable t) {

			}

			try {
				p.waitFor();
			} catch (Throwable t) {

			}

			if (ret[1] == null) {
				final Process p2 = Runtime.getRuntime().exec("aapt dump xmltree " + apk + " AndroidManifest.xml");
				BufferedReader reader = new BufferedReader(new InputStreamReader(p2.getInputStream()));
				line = "";
				String mainActivity = null;
				while ((line = reader.readLine()) != null) {
					if (line.contains("targetActivity")) {
						mainActivity = line.trim().split("\"")[1];
						System.out.println(mainActivity);
					} else if (line.contains("android.intent.category.LAUNCHER")) {
						break;
					}
				}

				try {
					reader.close();
				} catch (Throwable t) {

				}

				try {
					p2.waitFor();
				} catch (Throwable t) {

				}

				ret[1] = mainActivity;
			}

		} catch (Exception ex) {
			throw new RipperRuntimeException(AndroidRipperStarter.class, "extractAPKInfos", ex.getMessage(), ex);
		}

		return ret;
	}

	private static String sanitizePath(String pathToSanitize) {
		String path = new File(pathToSanitize).getAbsolutePath();
		if (File.separatorChar != '/') {
			path = path.replace(File.separatorChar, '/');
		}

		return path;
	}

	public AbstractDriver getDriver() {
		return this.driver;
	}
	
	public String howMuchSleepINeed(String mainActivityClass) {
		
		try {
			String simpleClassName = mainActivityClass.substring(mainActivityClass.lastIndexOf('.') + 1).toLowerCase();
			
			if (RipperStringUtils.stringContainsItemFromList(simpleClassName, new String[] {"splash", "welcome", "intro", "loading", "logo"})) {
				return "10000";
			}
		} catch (Throwable t) {
			
		}
		
		return "3000";
	}
}
