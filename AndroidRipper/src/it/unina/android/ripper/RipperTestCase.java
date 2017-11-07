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

package it.unina.android.ripper;

import java.lang.reflect.Method;
import java.util.Map;

import com.robotium.solo.Solo;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.test.ActivityInstrumentationTestCase2;
import android.util.Log;
import it.unina.android.ripper.automation.IAutomation;
import it.unina.android.ripper.automation.RipperAutomation;
import it.unina.android.ripper.automation.robot.IRobot;
import it.unina.android.ripper.automation.robot.RobotiumWrapperRobot;
import it.unina.android.ripper.configuration.Configuration;
import it.unina.android.ripper.extractor.IExtractor;
import it.unina.android.ripper.extractor.SimpleNoValuesExtractor;
import it.unina.android.ripper.extractor.screenshoot.IScreenshotTaker;
import it.unina.android.ripper.extractor.screenshoot.RobotiumScreenshotTaker;
import it.unina.android.ripper.log.Debug;
import it.unina.android.shared.ripper.model.state.ActivityDescription;
import it.unina.android.shared.ripper.net.Message;
import it.unina.android.shared.ripper.net.MessageType;
import it.unina.android.shared.ripper.output.XMLRipperOutput;
import it.unina.android.ripper_service.IAndroidRipperService;
import it.unina.android.ripper_service.IAnrdoidRipperServiceCallback;

/**
 * AndroidRipperTestCase
 * 
 * Communicates with the AndroidRipperDriver and executes events on the AUT.
 * 
 * @author Nicola Amatucci - REvERSE
 *
 */
@SuppressLint("NewApi")
public class RipperTestCase extends ActivityInstrumentationTestCase2 {

	/**
	 * Log TAG
	 */
	public static final String TAG = "RipperTestCase";

	/**
	 * Robot Component Instance
	 */
	IRobot robot = null;

	/**
	 * Automation Component Instance
	 */
	IAutomation automation = null;

	/**
	 * Extractor Component Instance
	 */
	IExtractor extractor = null;

	/**
	 * ScreenshotTaker Component Instance
	 */
	IScreenshotTaker screenshotTaker = null;

	/**
	 * Test Case Running Status
	 */
	private boolean testRunning = true;

	/**
	 * Android ActivityManager
	 */
	ActivityManager mActivityManager;

	/**
	 * Android Context
	 */
	Context mContext;

	/**
	 * Ready to Operate Status
	 */
	private boolean readyToOperate = false;

	/**
	 * Constructor
	 */
	public RipperTestCase() {
		super(Configuration.autActivityClass);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.test.ActivityInstrumentationTestCase2#setUp()
	 */
	@Override
	protected void setUp() throws Exception {
		super.setUp();

		mContext = this.getInstrumentation().getContext();
		mActivityManager = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);

		bindCommunicationServices();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.test.ActivityInstrumentationTestCase2#tearDown()
	 */
	@Override
	protected void tearDown() throws Exception {

		unbindCommunicationServices();

		if (this.automation != null) {
			final Activity theActivity = this.automation.getCurrentActivity();

			try {
				this.automation.finalizeRobot();
				
				runTestOnUiThread(new Runnable(){
					@Override
					public void run() {
						theActivity.finish();
					}
				});
				
			} catch (Throwable e) {
				e.printStackTrace();
			}
			//theActivity.finish();
		}

		try {
			this.unbindCommunicationServices();
		} catch (Throwable tr) {
		}

		super.tearDown();
	}

	/**
	 * Get Automation Component Instance
	 * 
	 * @return
	 */
	public IAutomation getAutomation() {
		return this.automation;
	}

	/**
	 * Operation done after restart
	 */
	public void afterRestart() {
		automation.setActivityOrientation(Solo.PORTRAIT);
		sleepAfterTask();
		automation.waitOnThrobber();

		// TODO: precrawling
		Debug.info(this, "Ready to operate after restarting...");
	}

	/**
	 * Main AndroidRipperTestCase Loop
	 */
	public void testApplication() {

		this.robot = new RobotiumWrapperRobot(this);
		this.automation = new RipperAutomation(this.robot);
		this.extractor = new SimpleNoValuesExtractor(this.robot);
		this.screenshotTaker = new RobotiumScreenshotTaker(this.robot);

		this.afterRestart();
		readyToOperate = true;

		// loops until test is in running status
		while (this.testRunning)
			this.robot.sleep(500);
	}

	/**
	 * Sleep After Task Completion
	 */
	private void sleepAfterTask() {
		automation.sleep(Configuration.SLEEP_AFTER_TASK);
	}

	/**
	 * IAndroidRipperService instance
	 */
	IAndroidRipperService mService = null;

	/**
	 * ServiceConnection to the Android Service
	 */
	private ServiceConnection mConnection = new ServiceConnection() {
		public void onServiceConnected(ComponentName className, IBinder service) {
			mService = IAndroidRipperService.Stub.asInterface(service);

			try {
				mService.register(mCallback);
			} catch (RemoteException e) {
				e.printStackTrace();
			}

		}

		public void onServiceDisconnected(ComponentName className) {
			mService = null;
		}
	};

	/**
	 * IAnrdoidRipperServiceCallback
	 */
	IAnrdoidRipperServiceCallback mSecondaryService = null;
	private ServiceConnection mSecondaryConnection = new ServiceConnection() {
		public void onServiceConnected(ComponentName className, IBinder service) {
			mSecondaryService = IAnrdoidRipperServiceCallback.Stub.asInterface(service);
		}

		public void onServiceDisconnected(ComponentName className) {
			mSecondaryService = null;
		}
	};

	/**
	 * Bind to AndroidRipperService
	 */
	private void bindCommunicationServices() {
		Intent bindIntent = new Intent(".IAndroidRipperService");
		bindIntent.setClassName("it.unina.android.ripper_service",
				"it.unina.android.ripper_service.AndroidRipperService");
		this.mContext.bindService(bindIntent, mConnection, Context.BIND_AUTO_CREATE);
		// Log.v("CIAOCIAOCIAO", "bindCommunicationServices()1");

		bindIntent = new Intent(".IAnrdoidRipperServiceCallback");
		bindIntent.setClassName("it.unina.android.ripper_service",
				"it.unina.android.ripper_service.AndroidRipperService");
		this.mContext.bindService(bindIntent, mSecondaryConnection, Context.BIND_AUTO_CREATE);
		// Log.v("CIAOCIAOCIAO", "bindCommunicationServices()2");
	}

	/**
	 * Unbind from AndroidRipperService
	 */
	private void unbindCommunicationServices() {
		try {
			mService.unregister(mCallback);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		this.mContext.unbindService(mConnection);
		this.mContext.unbindService(mSecondaryConnection);
	}

	/**
	 * IAnrdoidRipperServiceCallback
	 * 
	 * Handles Messages from AndroidRipperDriver
	 */
	private IAnrdoidRipperServiceCallback mCallback = new IAnrdoidRipperServiceCallback.Stub() {

		@Override
		public void receive(Map message) throws RemoteException {

			Message msg = new Message(message);

			Debug.info("Recived message: " + msg.getType());

			if (readyToOperate == false) // skip
			{
				Log.v(TAG, "Not ready to operate!");

				if (msg.isTypeOf(MessageType.PING_MESSAGE)) {
					Message m = Message.getWaitForAppReadyMessage();
					m.addParameter("index", msg.get("index"));
					mService.send(m);
				}

				return;
			}

			if (msg.isTypeOf(MessageType.CONFIG_MESSAGE)) {
				// config
				Message m = Message.getAckMessage();
				m.addParameter("index", msg.get("index"));
				mService.send(m);
			} else if (msg.isTypeOf(MessageType.PING_MESSAGE)) {
				Message m = Message.getPongMessage();
				m.addParameter("index", msg.get("index"));
				mService.send(m);

			} else if (msg.isTypeOf(MessageType.DESCRIBE_MESSAGE)) {
				try {
					getInstrumentation().waitForIdleSync();

					Activity activity = getActivity();

					if (activity != null) {
						XMLRipperOutput o = new XMLRipperOutput();
						XMLRipperOutput.RUN_IN_THREAD = false;

						Message retMsg = Message.getDescribeMessage();
						retMsg.addParameter("index", msg.get("index"));

						ActivityDescription ad = extractor.extract();
						String s = o.outputActivityDescription(ad);
						retMsg.addParameter("xml", s);

						mService.send(retMsg);
					} else {
						Log.v(TAG, "DSC : wait activity null");
						Message retMsg = Message.getDescribeMessage();
						retMsg.addParameter("wait", "wait");
						retMsg.addParameter("index", msg.get("index"));
						mService.send(message);
					}

				} catch (Throwable t) {
					t.printStackTrace();
					Message retMsg = Message.getFailMessage();
					retMsg.addParameter("index", msg.get("index"));
					mService.send(message);
				}
			} else if (msg.isTypeOf(MessageType.INPUT_MESSAGE)) {

				Integer widgetId = Integer.parseInt(msg.get("widgetId"));
				String inputType = msg.get("inputType");
				String value = msg.get("value");
				
				String eventUID = msg.get("eventUID");
				String count = msg.get("count");
				long time = System.currentTimeMillis();
				Log.v(TAG, "START_INPUT["+eventUID+"|"+count+"+|"+System.currentTimeMillis()+"]");
				
				try {
					automation.setInput(widgetId, inputType, value);
					Message m = Message.getAckMessage();
					m.addParameter("index", msg.get("index"));
					mService.send(m);
				} catch (Throwable t) {
					t.printStackTrace();
					Message retMsg = Message.getFailMessage();
					retMsg.addParameter("index", msg.get("index"));
					mService.send(message);
				}
				
				Log.v(TAG, "END_INPUT["+eventUID+"|"+count+"|"+System.currentTimeMillis()+"]");
				
			} else if (msg.isTypeOf(MessageType.EVENT_MESSAGE)) {
				String widgetId = msg.get("widgetId");
				String widgetIndexString = msg.get("widgetIndex");
				Integer widgetIndex = (widgetIndexString != null) ? Integer.parseInt(widgetIndexString) : null;
				String widgetName = msg.get("widgetName");
				String widgetType = msg.get("widgetType");
				String eventType = msg.get("eventType");
				String value = msg.get("value");

				String uid = msg.get("uid");
				Log.v(TAG, "START_EVENT["+uid+"|"+System.currentTimeMillis()+"]");
				
				try {
					automation.fireEvent(widgetId, widgetIndex, widgetName, widgetType, eventType, value);
					Message m = Message.getAckMessage();
					m.addParameter("index", msg.get("index"));
					mService.send(m);
				} catch (Throwable t) {
					t.printStackTrace();
					Message retMsg = Message.getFailMessage();
					retMsg.addParameter("index", msg.get("index"));
					mService.send(message);
				}
				Log.v(TAG, "END_EVENT["+uid+"|"+System.currentTimeMillis()+"]");
			} else if (msg.isTypeOf(MessageType.END_MESSAGE)) {
				mService.send(Message.getAckMessage());
				testRunning = false;
			} else {
				Message m = Message.getNAckMessage();
				m.addParameter("index", msg.get("index"));
				mService.send(m);
			}
		}

	};
}
