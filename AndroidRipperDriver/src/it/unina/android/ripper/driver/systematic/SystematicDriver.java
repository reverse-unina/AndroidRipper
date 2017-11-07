/**
* The MIT License
* 
* Copyright (c) 2014-2017 REvERSE, REsEarch gRoup of Software Engineering @ the University of Naples Federico II, http://reverse.dieti.unina.it/
* 
* Permission is hereby granted, free of charge, to any person obtaining a copy
* of this software and associated documentation files (the "Software"), to deal
* in the Software without restriction, including without limitation the rights
* to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
* copies of the Software, and to permit persons to whom the Software is
* furnished to do so, subject to the following conditions:
* 
* The above copyright notice and this permission notice shall be included in
* all copies or substantial portions of the Software.
* 
* THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
* IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
* FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
* AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
* LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
* OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
* THE SOFTWARE.
* 
**/

package it.unina.android.ripper.driver.systematic;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import it.unina.android.ripper.comparator.ActivityStructureComparator;
import it.unina.android.ripper.comparator.IComparator;
import it.unina.android.ripper.driver.AbstractDriver;
import it.unina.android.ripper.driver.exception.AckNotReceivedException;
import it.unina.android.ripper.driver.exception.NullMessageReceivedException;
import it.unina.android.ripper.driver.exception.RipperRuntimeException;
import it.unina.android.ripper.net.RipperServiceSocket;
import it.unina.android.ripper.planner.ConfigurationBasedPlanner;
import it.unina.android.ripper.planner.Planner;
import it.unina.android.ripper.scheduler.BreadthScheduler;
import it.unina.android.ripper.scheduler.Scheduler;
import it.unina.android.ripper.states.ActivityStateList;
import it.unina.android.ripper.termination.EmptyActivityStateListTerminationCriterion;
import it.unina.android.ripper.termination.TerminationCriterion;
import it.unina.android.ripper.tools.actions.Actions;
import it.unina.android.shared.ripper.constants.InteractionType;
import it.unina.android.shared.ripper.input.RipperInput;
import it.unina.android.shared.ripper.input.XMLRipperInput;
import it.unina.android.shared.ripper.model.state.ActivityDescription;
import it.unina.android.shared.ripper.model.task.Task;
import it.unina.android.shared.ripper.model.task.TaskList;
import it.unina.android.shared.ripper.model.transition.Event;
import it.unina.android.shared.ripper.model.transition.IEvent;
import it.unina.android.shared.ripper.net.Message;
import it.unina.android.shared.ripper.net.MessageType;
import it.unina.android.shared.ripper.output.RipperOutput;
import it.unina.android.shared.ripper.output.XMLRipperOutput;

/**
 * ActiveLearning Driver
 * 
 * @author Nicola Amatucci - REvERSE
 *
 */
public class SystematicDriver extends AbstractDriver {

	/**
	 * States List File name
	 */
	public static String STATES_LIST_FILE = "activities.xml";

	/**
	 * ActivityDescription Comparator instance
	 */
	protected IComparator comparator;

	/**
	 * ActivityStateList instance
	 * 
	 * TODO: generalize -> now only activity-based state
	 */
	protected ActivityStateList statesList;

	/**
	 * Constructor. Default Components.
	 */
	public SystematicDriver() {
		this(new BreadthScheduler(), new ConfigurationBasedPlanner(), new XMLRipperInput(),
				new ActivityStructureComparator(),
				new EmptyActivityStateListTerminationCriterion(), new XMLRipperOutput());
	}

	/**
	 * Constructor
	 * 
	 * @param scheduler
	 * @param planner
	 * @param ripperInput
	 * @param comparator
	 * @param terminationCriterion
	 * @param ripperOutput
	 */
	public SystematicDriver(Scheduler scheduler, Planner planner, RipperInput ripperInput, IComparator comparator,
			TerminationCriterion terminationCriterion, RipperOutput ripperOutput) {

		super();

		this.scheduler = scheduler;
		this.planner = planner;
		this.ripperInput = ripperInput;
		this.comparator = comparator;
		this.statesList = new ActivityStateList(this.comparator);
		this.ripperOutput = ripperOutput;

		this.addTerminationCriterion(terminationCriterion);
	}

	/**
	 * Constructor
	 * 
	 * @param scheduler
	 * @param planner
	 * @param ripperInput
	 * @param comparator
	 * @param terminationCriteria
	 * @param ripperOutput
	 */
	public SystematicDriver(Scheduler scheduler, Planner planner, RipperInput ripperInput, IComparator comparator,
			ArrayList<TerminationCriterion> terminationCriteria, RipperOutput ripperOutput) {

		super();

		this.scheduler = scheduler;
		this.planner = planner;
		this.ripperInput = ripperInput;
		this.comparator = comparator;
		this.statesList = new ActivityStateList(this.comparator);
		this.ripperOutput = ripperOutput;

		for (TerminationCriterion tc : terminationCriteria) {
			this.addTerminationCriterion(tc);
		}

	}

	/**
	 * Main Ripping Loop - Active Learning Implementation
	 */
	@Override
	public void rippingLoop() {
		// reset counters
		nEvents = 0;
		nTasks = 0;
		nFails = 0;
		nRestart = 0;

		// init acivities.xml
		initStateDescriptionFile();

		boolean bootstrapDone = false;

		long t1 = System.currentTimeMillis();

		long startup_time = 0;

		do {
			Task t = null;

			nRestart++;
			
			if (nRestart > RESTART_THRESHOLD && nEvents == 0) {
				throw new RipperRuntimeException(SystematicDriver.class, "rippingLoop", "Restart Threshold exceeded!"); 
			}
			
			long startup_time_t1 = System.currentTimeMillis();

			if (device.isStarted() == false) {
				startupDevice();
				device.setStarted(true);
			}

			// socket
			if (rsSocket == null) {
				rsSocket = new RipperServiceSocket(device.getIpAddress(), SERVICE_HOST_PORT);
			} else if (rsSocket != null && (rsSocket.getSocket() == null
					|| (rsSocket.getSocket() != null && rsSocket.getSocket().isClosed()))) {
				rsSocket.disconnect();
				rsSocket = new RipperServiceSocket(device.getIpAddress(), SERVICE_HOST_PORT);
			}

			this.startup();

			try {
				notifyRipperLog("Alive...");
				if (Actions.checkCurrentForegroundActivityPackage(AUT_PACKAGE) == false || rsSocket.isAlive() == false) {
					throw new RipperRuntimeException(SystematicDriver.class, "rippingLoop", "Emulator Killed!");
				}
				
			} catch (Exception ex) {
				throw new RipperRuntimeException(SystematicDriver.class, "rippingLoop", ex.getMessage(), ex);
			}

			startup_time += System.currentTimeMillis() - startup_time_t1;

			if (running && started) {
				createLogFile();
				try {
					
					boolean aTaskHasBeenExecuted = false;
						
					if (bootstrapDone) {
						notifyRipperLog("Scheduled tasks: " + ((this.getScheduler()!=null && this.getScheduler().getTaskList()!=null)?this.getScheduler().getTaskList().size():"NULL"));
						t = this.schedule();

						if (t != null) {
							Message msg = this.executeTask(t);

							Actions.sleepMilliSeconds(SLEEP_AFTER_EVENT);

							if (msg == null || running == false) {
								// do nothing
								notifyRipperLog("msg == null || running == false");
							} else {
								if (msg != null && msg.isTypeOf(MessageType.ACK_MESSAGE)) {

									nTasks++;
									nEvents += t.size();

									aTaskHasBeenExecuted = true;
									
								} else if ((msg != null && msg.isTypeOf(MessageType.FAIL_MESSAGE))) {
									nTasks++;
									nFails++;
									this.appendLineToLogFile("\n<fail />\n");
								} else {
									notifyRipperLog("executeTask(): something went wrong?!?");
									this.appendLineToLogFile("\n<error type='executeTask' />\n");
								}
							}
						}
					}

					
					Actions.sleepMilliSeconds(SLEEP_AFTER_TASK);

					if (bootstrapDone == false || aTaskHasBeenExecuted == true) {
										
						//handle ads
						ActivityDescription ad = getCurrentDescriptionAsActivityDescription();
						do {
							if (ad != null && ad.getClassName() != null && ad.getClassName().equals("com.google.android.gms.ads.AdActivity")) {
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

								this.updateLatestDescriptionAsActivityDescription();
								ad = this.getLastActivityDescription();
								ad.setId(statesList.getEquivalentActivityStateId(ad));
								this.appendLineToLogFile(this.ripperOutput.outputActivityDescription(ad));
								
							}
						} while (ad == null || (ad != null && ad.getWidgets().size() == 0));
						//handle ads
						
						TaskList plannedTasks = new TaskList();
						if (compareAndAddState(getCurrentDescriptionAsActivityDescription())) {
							plannedTasks = plan(t, getLastActivityDescription());
							
							scheduler.addTasks(plannedTasks);

							notifyRipperLog("Scheduled Tasks : " + scheduler.getTaskList().size());
							
							appendStatesDescriptionFile(getLastActivityDescription());							
						}

						// output
						if (plannedTasks == null) {
							plannedTasks = new TaskList();
						}

						ad = getLastActivityDescription();
						ad.setId(statesList.getEquivalentActivityStateId(ad));
						appendLineToLogFile(
								this.ripperOutput.outputActivityDescriptionAndPlannedTasks(ad, plannedTasks));
						
						if (bootstrapDone == false) {
							bootstrapDone = true;
						}
					}
					
				} catch (Throwable throwable) {
					notifyRipperLog("[SystemticDriver.rippingLoop()] " + throwable.getMessage());
				}

				endLogFile();

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

			this.uninstallAPKs(false);

		} while (running && this.checkTerminationCriteria() == false);

		

		closeStateDescriptionFile();

		long executionTime = System.currentTimeMillis() - t1;

		this.notifyRipperLog("Execution Time: " + executionTime);

		String reportXML = "<?xml version=\"1.0\"?><report>\n";
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
	 * Open the XML Description File (no overwrite)
	 */
	protected void initStateDescriptionFile() {
		this.initStateDescriptionFile(false);
	}

	/**
	 * Open the XML Description File
	 * 
	 * @param overwrite
	 */
	protected void initStateDescriptionFile(boolean overwrite) {
		if (overwrite || new File(XML_OUTPUT_PATH + STATES_LIST_FILE).exists() == false) {
			writeStringToFile("<?xml version=\"1.1\"?><states>\n", XML_OUTPUT_PATH + STATES_LIST_FILE);
		}
	}

	/**
	 * Close the XML Description File
	 */
	protected void closeStateDescriptionFile() {
		appendStringToFile("</states>\n", XML_OUTPUT_PATH + STATES_LIST_FILE);
	}

	/**
	 * Append an ActivtyDescription to the XML Description File
	 * 
	 * @param ad
	 */
	protected void appendStatesDescriptionFile(ActivityDescription ad) {
		appendStringToFile("\n" + this.ripperOutput.outputActivityDescription(ad), XML_OUTPUT_PATH + STATES_LIST_FILE);
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
		
		try {
			this.updateLatestDescriptionAsActivityDescription();
		} catch (Exception ex) {
			this.notifyRipperLog("[SystematicDriver.executeTask()] " + ex.getMessage());
			return null;
		}

		ActivityDescription ad = this.getLastActivityDescription();
		ad.setId(statesList.getEquivalentActivityStateId(ad));
		this.appendLineToLogFile(this.ripperOutput.outputActivityDescription(ad));

		
		// for(Event evt : t)
		for (int i = 0; i < t.size(); i++) {
			IEvent evt = t.get(i);
			
			try {

				//handle ads
				do {
					if (ad != null && ad.getClassName() != null && ad.getClassName().equals("com.google.android.gms.ads.AdActivity")) {
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

						this.updateLatestDescriptionAsActivityDescription();
						ad = this.getLastActivityDescription();
						ad.setId(statesList.getEquivalentActivityStateId(ad));
						this.appendLineToLogFile(this.ripperOutput.outputActivityDescription(ad));
						
					}
				} while (ad == null || (ad != null && ad.getWidgets().size() == 0));
				//handle ads

				if (checkBeforeEventStateId(ad, evt)) {
					try {
						msg = executeEvent(evt);
						// output fired event
					} catch (AckNotReceivedException e1) {
						msg = null;
						notifyRipperLog("executeTask(): AckNotReceivedException"); // failure
						this.appendLineToLogFile("\n<error type='AckNotReceivedException' />\n");
					} catch (NullMessageReceivedException e2) {
						Actions.setRipperActive(false);
						msg = null;
						notifyRipperLog("executeTask(): NullMessageReceivedException"); // failure
						this.appendLineToLogFile("\n<error type='NullMessageReceivedException' />\n");
					}

					if (msg == null || running == false) {
						break;
					} else if (msg != null && msg.isTypeOf(MessageType.ACK_MESSAGE)) {

						Actions.sleepMilliSeconds(SLEEP_AFTER_EVENT);

					} else if ((msg != null && msg.isTypeOf(MessageType.FAIL_MESSAGE))) {
						return msg;
					}
				} else {
					// something went wrong
					// throw new BeforeEventStateAssertionFailedException()???
					this.appendLineToLogFile("\n<error type='BeforeEventStateAssertionFailed' />\n");
				}
			} catch (IOException ex) {
				notifyRipperLog("executeTask(): Description IOException");
				this.appendLineToLogFile("\n<error type='IOException' />\n");
			}

			// this.appendLineToLogFile("<end
			// timestamp=\""+System.currentTimeMillis()+"\" />");
		}

		// this.appendLineToLogFile("</task>");

		return msg;
	}

	/**
	 * Compare a ActivityDescription with the ones contained in the statesList
	 * 
	 * @param activity
	 *            ActivityDescription to compare
	 * @return
	 */
	protected boolean compareAndAddState(ActivityDescription activity) {
		notifyRipperLog("\tComparator...");
		if (statesList.containsActivity(activity) == null) {
			// add to visited states
			statesList.addActivity(activity);
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Check if the ActivityDescription is equivalent (using the Comparator) to
	 * the one where the event evt was planned to be performed
	 * 
	 * @param ad
	 *            Activity Description
	 * @param evt
	 *            Event Description
	 * @return
	 * @throws IOException
	 */
	protected boolean checkBeforeEventStateId(ActivityDescription ad, IEvent e) throws IOException {
		
		if (e instanceof Event) {
			Event evt = (Event)e;
			if (evt.getBeforeExecutionStateUID().equals("UNDEFINED"))
				return true;
	
			return ad.getId().equals(evt.getBeforeExecutionStateUID());
		} else { //instanceof ManualSequence
			//TODO
			return true;
		}
	}

	/**
	 * Get the XML Description File name with full path
	 * 
	 * @return
	 */
	public String getStatesListFile() {
		return XML_OUTPUT_PATH + STATES_LIST_FILE;
	}

	/**
	 * Set the XML Description File
	 * 
	 * @param statesListFile
	 */
	public void setStatesListFile(String statesListFile) {
		STATES_LIST_FILE = statesListFile;
	}

	/**
	 * Comparator Instance
	 * 
	 * @return
	 */
	public IComparator getComparator() {
		return comparator;
	}

	/**
	 * ActivityStateList Instance
	 * 
	 * @return
	 */
	public ActivityStateList getStatesList() {
		return statesList;
	}
}
