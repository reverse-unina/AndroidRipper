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

package it.unina.android.ripper.planner;

import it.unina.android.shared.ripper.model.state.ActivityDescription;
import it.unina.android.shared.ripper.model.task.Task;
import it.unina.android.shared.ripper.model.task.TaskList;

/**
 * Planner of Tasks
 * 
 * @author Nicola Amatucci - REvERSE
 *
 */
public abstract class Planner
{
	/**
	 * Max Tasks generated for Items contained in LIST_VIEW widget
	 */
	public static int MAX_INTERACTIONS_FOR_LIST = 3;
	
	/**
	 * Max Tasks generated for Items contained in PREFERENCE_LIST_VIEW widget
	 */
	public static int MAX_INTERACTIONS_FOR_PREFERENCES_LIST = 9999;
	
	/**
	 * Max Tasks generated for Items contained in SINGLE_CHOICE_LIST_VIEW widget
	 */
	public static int MAX_INTERACTIONS_FOR_SINGLE_CHOICE_LIST = 3;
	
	/**
	 * Max Tasks generated for Items contained in MULTI_CHOICE_LIST_VIEW widget
	 */
	public static int MAX_INTERACTIONS_FOR_MULTI_CHOICE_LIST = 3;
	
	/**
	 * Max Tasks generated for Items contained in SPINNER widget
	 */
	public static int MAX_INTERACTIONS_FOR_SPINNER = 9;
	
	/**
	 * Max Tasks generated for Items contained in RADIO_GROUP widget
	 */
	public static int MAX_INTERACTIONS_FOR_RADIO_GROUP = 9;
	
	/**
	 * Enable BACK button press events
	 */	
	public static boolean CAN_GO_BACK = true;
	
	/**
	 * Enable CHANGE ORIENTATION event
	 */
	public static boolean CAN_CHANGE_ORIENTATION = false;
	
	/**
	 * Enable MENU button press event
	 */
	public static boolean CAN_OPEN_MENU = true;
	
	/**
	 * Enable SCROLL DOWN event
	 */
	public static boolean CAN_SCROLL_DOWN = false;
	
	/**
	 * Enables SWAP event between TAB VIEWS
	 */
	public static boolean CAN_SWAP_TAB = true;	
	
	/**
	 * Enable HOME event
	 */	
	public static boolean CAN_GO_BACK_ON_HOME_ACTIVITY = true;
	
	/**
	 * ActivityDetector enable
	 */
	public static boolean ACTIVITY_DETECTOR_ENABLE = false;
	
	/**
	 * Plan Tasks
	 * 
	 * @param currentTask starting task
	 * @param activity target ActivityDescription
	 * @return list of tasks
	 */
	public TaskList plan(Task currentTask, ActivityDescription activity) {
		return plan(currentTask ,activity, null);
	}
	
	/**
	 * Plan Tasks
	 * 
	 * @param currentTask starting task
	 * @param activity target ActivityDescription
	 * @param options configuration
	 * @return list of tasks
	 */
	public abstract TaskList plan(Task currentTask, ActivityDescription activity, String ... options);

}
