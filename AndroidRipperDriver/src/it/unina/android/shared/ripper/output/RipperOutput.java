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

package it.unina.android.shared.ripper.output;

import it.unina.android.shared.ripper.model.state.ActivityDescription;
import it.unina.android.shared.ripper.model.state.WidgetDescription;
import it.unina.android.shared.ripper.model.task.Task;
import it.unina.android.shared.ripper.model.task.TaskList;
import it.unina.android.shared.ripper.model.transition.IEvent;

/**
 * Specifies how the element of the model are serialized 
 * 
 * @author Nicola Amatucci - REvERSE
 *
 */
public interface RipperOutput {
	/**
	 * Output ActivityDescription as a String
	 * @param a
	 * @return
	 */
	public String outputActivityDescription(ActivityDescription a);
	
	/**
	 * Output ActivityDescription and TaskList as a String
	 * 
	 * @param a
	 * @param t
	 * @return
	 */
	public String outputActivityDescriptionAndPlannedTasks(ActivityDescription a, TaskList t);
	
	/**
	 * Output WidgetDescription as a String
	 * 
	 * @param a
	 * @return
	 */
	public String outputWidgetDescription(WidgetDescription a);
	
	/**
	 * Output Event as a String
	 * 
	 * @param a
	 * @return
	 */
	public String outputEvent(IEvent a);
	
	/**
	 * Output Fired Event as a String
	 * 
	 * @param evt
	 * @return
	 */
	public String outputFiredEvent(IEvent evt);
	
	/**
	 * Output Task as a String
	 * 
	 * @param a
	 * @return
	 */
	public String outputTask(Task a);
	
	/**
	 * Output TaskList of events extracted from an ActivityDescription as a String
	 * 
	 * @param a
	 * @return
	 */
	public String outputExtractedEvents(TaskList a);
	
	/**
	 * Output TaskList of events extracted from an ActivityDescription and the ActivityDescription as a String
	 * 
	 * @param t
	 * @param from
	 * @return
	 */
	public String outputExtractedEvents(TaskList t, ActivityDescription from);
	
	/**
	 * Output an Event and the ActivityDescription where it is performed as a String
	 * 
	 * @param e
	 * @param a
	 * @return
	 */
	public String outputStep(IEvent e, ActivityDescription a);
	
	/**
	 * Output an Event and the ActivityDescription where it is performed as a String
	 * together with the fireable events planned on the resulting ActivityDescription
	 * (if any)
	 * 
	 * @param e
	 * @param a
	 * @param t
	 * @return
	 */
	public String outputStepAndPlannedTasks(IEvent e, ActivityDescription a, TaskList t);
	
	/**
	 * Output the result of the bootstrap step: ActivityDescription and related TaskList
	 * 
	 * @param ad
	 * @param t
	 * @return
	 */
	public String outputFirstStep(ActivityDescription ad, TaskList t);
}
