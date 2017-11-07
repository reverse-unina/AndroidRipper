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

package it.unina.android.ripper.driver.random;

import java.io.IOException;
import java.util.ArrayList;

import it.unina.android.ripper.driver.AbstractDriver;
import it.unina.android.ripper.driver.exception.AckNotReceivedException;
import it.unina.android.ripper.driver.exception.NullMessageReceivedException;
import it.unina.android.ripper.driver.exception.RipperRuntimeException;
import it.unina.android.ripper.net.RipperServiceSocket;
import it.unina.android.ripper.planner.Planner;
import it.unina.android.ripper.scheduler.Scheduler;
import it.unina.android.ripper.termination.TerminationCriterion;
import it.unina.android.ripper.tools.actions.Actions;
import it.unina.android.shared.ripper.constants.InteractionType;
import it.unina.android.shared.ripper.input.RipperInput;
import it.unina.android.shared.ripper.model.state.ActivityDescription;
import it.unina.android.shared.ripper.model.task.Task;
import it.unina.android.shared.ripper.model.task.TaskList;
import it.unina.android.shared.ripper.model.transition.Event;
import it.unina.android.shared.ripper.model.transition.IEvent;
import it.unina.android.shared.ripper.net.Message;
import it.unina.android.shared.ripper.net.MessageType;
import it.unina.android.shared.ripper.output.RipperOutput;

/**
 * RandomDriver
 * 
 * @author Nicola Amatucci - REvERSE
 *
 */

public class RandomDriver extends AbstractDriver {
	/**
	 * Number of random events to trigger
	 */
	public int NUM_EVENTS = 1000;

	/**
	 * Number of events for each session
	 * 
	 * 0 = until NUM_EVENTS
	 */
	public int NUM_EVENTS_PER_SESSION = 0;

	/**
	 * Number of events between each coverage.ec retrieval
	 * 
	 * 0 = no coverage
	 */
	public int COVERAGE_FREQUENCY = 100;

	/**
	 * Random Seed
	 */
	public long RANDOM_SEED = System.currentTimeMillis();

	/**
	 * Number of events between each xml log creation
	 * 
	 * 0 = until end of the session
	 */
	public int NEW_LOG_FREQUENCY = 0;

	/**
	 * 
	 * @param scheduler
	 * @param planner
	 * @param ripperInput
	 * @param ripperOutput
	 * @param terminationCriterion
	 */
	public RandomDriver(Scheduler scheduler, Planner planner, RipperInput ripperInput, RipperOutput ripperOutput,
			TerminationCriterion terminationCriterion) {
		super();

		this.scheduler = scheduler;
		this.planner = planner;
		this.ripperInput = ripperInput;
		this.ripperOutput = ripperOutput;

		this.addTerminationCriterion(terminationCriterion);

		// TODO: move
		// Planner.CAN_GO_BACK_ON_HOME_ACTIVITY = false;

		// TODO: create coverage dir
	}

	/**
	 * 
	 * @param scheduler
	 * @param planner
	 * @param ripperInput
	 * @param ripperOutput
	 * @param terminationCriteria
	 */
	public RandomDriver(Scheduler scheduler, Planner planner, RipperInput ripperInput, RipperOutput ripperOutput,
			ArrayList<TerminationCriterion> terminationCriteria) {
		super();

		this.scheduler = scheduler;
		this.planner = planner;
		this.ripperInput = ripperInput;
		this.ripperOutput = ripperOutput;

		for (TerminationCriterion tc : terminationCriteria) {
			this.addTerminationCriterion(tc);
		}

		// TODO: create coverage dir
	}

	/**
	 * Main Ripping Loop - Random Implementation
	 */
	@Override
	public void rippingLoop() {
		//reset counters
		nEvents = 0;
		nTasks = 0;
		nFails = 0;
		nRestart = 0;

		boolean bootstrap = false;

		long t1 = System.currentTimeMillis();

		long startup_time = 0;

		do {
			nRestart++;

			if (nRestart > RESTART_THRESHOLD && nEvents == 0) {
				throw new RipperRuntimeException(RandomDriver.class, "rippingLoop", "Restart Treshold Exceeded!");
			}
			
			//startup process
			long startup_time_t1 = System.currentTimeMillis();
			
			if (device.isStarted() == false) {
				startupDevice();
				device.setStarted(true);
			}
			
			// socket
			if (rsSocket == null) {
				rsSocket = new RipperServiceSocket(device.getIpAddress(), SERVICE_HOST_PORT);
			} else if (rsSocket != null && (rsSocket.getSocket() == null || (rsSocket.getSocket() != null && rsSocket.getSocket().isClosed()))) {
				rsSocket.disconnect();
				rsSocket = new RipperServiceSocket(device.getIpAddress(), SERVICE_HOST_PORT);
			}
			
			this.startup();
			startup_time += System.currentTimeMillis() - startup_time_t1;
			
			if (running && started) {
				this.startTestingTimeCounter();
				createLogFileAtCurrentTimeMillis();

				if (bootstrap == false) {
					bootstrap = true;
				}
				
				try {

					do {						
						if(this.getTestingTimeStarted()==false){
							this.startTestingTimeCounter();
						}
											
						this.ifIsPausedDoPause();
						
						notifyRipperLog("Alive...");
						if (Actions.checkCurrentForegroundActivityPackage(AUT_PACKAGE) == false || rsSocket.isAlive() == false) {
							notifyRipperLog("... NOT Alive!");
							break;
						}

						ActivityDescription activity = null;
						do {
							activity = describeActivity();
							if (activity != null && activity.getClassName() != null && activity.getClassName().equals("com.google.android.gms.ads.AdActivity")) {
								try {
									notifyRipperLog("Google ADS Activity detected : BACK!");
									executeEvent(new Event(InteractionType.BACK, null, null, null));
								} catch (AckNotReceivedException e) {
									// TODO Auto-generated catch block
									//e.printStackTrace();
								} catch (NullMessageReceivedException e) {
									// TODO Auto-generated catch block
									//e.printStackTrace();
								}
								Actions.sleepMilliSeconds(1000);
							}
						} while (activity == null || (activity != null && activity.getWidgets().size() == 0));
						
						if (activity != null) {
							
							this.appendLineToLogFile(this.ripperOutput.outputActivityDescription(activity));

							TaskList plannedTasks = plan(null, activity);

							scheduler.clear();
							scheduler.addTasks(plannedTasks);

							// schedule
							Task t = scheduler.nextTask();

							if (t == null) {
								notifyRipperLog("No scheduled task!");

								appendLineToLogFile("\n<error type=\"nothing_scheduled\" />\n");
								continue; // nothing to do
							}

							//execute
							Message msg = this.executeTask(t);
							
							Actions.sleepMilliSeconds(SLEEP_AFTER_EVENT);
							
							//handle execution result
							if (msg == null || running == false) {
								// do nothing
								notifyRipperLog("msg == null || running == false");
							} else {

								if (msg != null && msg.isTypeOf(MessageType.ACK_MESSAGE)) {

									nTasks++;
									nEvents++;

								} else if ((msg != null && msg.isTypeOf(MessageType.FAIL_MESSAGE))) {

									nFails++;
									this.appendLineToLogFile("\n<failure type=\"fail_message\" />\n");

								} else {

									notifyRipperLog("executeTask(): something went wrong?!?");
									this.appendLineToLogFile("\n<error type='executeTask' />\n");

								}

							}
	
							
	
							if (NUM_EVENTS_PER_SESSION > 0 && (nEvents % NUM_EVENTS_PER_SESSION == 0)) {
								notifyRipperLog("session limit reached : " + nEvents + "|" + NUM_EVENTS_PER_SESSION);
								break;
							}
	
							if (NEW_LOG_FREQUENCY > 0 && (nEvents % NEW_LOG_FREQUENCY == 0)) {
								endLogFile();
								createLogFileAtCurrentTimeMillis();
							}
						
						}

						this.ifIsPausedDoPause();
						
						this.stopTestingTimeCounter();
						
					} while (running && this.checkTerminationCriteria() == false);

				} catch (Throwable ex) {
					
					if (ex instanceof RipperRuntimeException) {
						RipperRuntimeException rex = (RipperRuntimeException)ex;
						if (rex.getRipperMessage().equals("Game/WebView Detected!")) {
							this.handleEndOfLoop();
							throw rex;
						}
					}					
					//ex.printStackTrace();
					System.out.println(ex.getMessage());
				}

				endLogFile();
				if(this.getTestingTimeStarted()){
					this.stopTestingTimeCounter();
				}
			}

			Actions.sleepMilliSeconds(SLEEP_AFTER_TASK);
			
			notifyRipperLog("End message...");
			rsSocket.sendMessage(Message.getEndMessage());
			try {
				if (Actions.checkCurrentForegroundActivityPackage(AUT_PACKAGE)) {
					waitAck();
					
					while (Actions.isRipperActive()) {
						Actions.sleepMilliSeconds(500);
					}
				}
			} catch (AckNotReceivedException e) {
			} catch (NullMessageReceivedException e) {
				//Actions.setRipperActive(false);
			}
			
			this.endRipperTask(false, true);
			
			this.ifIsPausedDoPause();
			
		} while (running && this.checkTerminationCriteria() == false);

		this.handleEndOfLoop();
		
		long executionTime = System.currentTimeMillis() - t1;

		this.notifyRipperLog("Execution Time: " + executionTime);

		String reportXML = "<?xml version=\"1.0\"?><report>\n";
		reportXML += "<seed>" + RANDOM_SEED + "</seed>\n";
		reportXML += "<events>" + nEvents + "</events>\n";
		reportXML += "<execution_time>" + executionTime + "</execution_time>\n";
		reportXML += "<restart>" + nRestart + "</restart>\n";
		reportXML += "<failure>" + nFails + "</failure>\n";
		reportXML += "<tasks>" + nTasks + "</tasks>\n";
		reportXML += "<startup_time>" + startup_time + "</startup_time>\n";
		reportXML += "</report>";

		writeReportFile(reportXML);
		
		this.notifyRipperEnded();
	}

	/**
	 * Returns the Message related to the execution of the last event of the
	 * task or null if an ack message is not received
	 * 
	 * @param t
	 *            Task
	 * @return Message
	 */
	protected Message executeTask(Task t) {
		Message msg = null;

		if (t != null && t.size() > 0) {

			IEvent evt = t.get(0);
			try {
				this.appendLineToLogFile(this.ripperOutput.outputFiredEvent(evt));
				msg = executeEvent(evt);
			} catch (AckNotReceivedException e1) {
				msg = null;
				notifyRipperLog("executeTask(): AckNotReceivedException"); // failure
				this.appendLineToLogFile("\n<error type='AckNotReceivedException' />\n");
			} catch (NullMessageReceivedException e2) {
				msg = null;
				notifyRipperLog("executeTask(): NullMessageReceivedException"); // failure
				this.appendLineToLogFile("\n<error type='NullMessageReceivedException' />\n");
				Actions.setRipperActive(false);
			}

			Actions.sleepMilliSeconds(SLEEP_AFTER_EVENT);

		}

		return msg;
	}
	
	protected ActivityDescription describeActivity() throws IOException {
		// describe
		ActivityDescription activity = null;
		do {
			if (activity != null) {
				Actions.sleepMilliSeconds(500);
			}
			activity = getCurrentDescriptionAsActivityDescription();
		} while (activity == null || (activity != null && activity.getWidgets().size() == 0));
		
		return activity;
	}
}
