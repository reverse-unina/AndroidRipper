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

package it.unina.android.ripper.planner.widget_events;

import it.unina.android.shared.ripper.constants.InteractionType;
import it.unina.android.shared.ripper.model.state.WidgetDescription;
import it.unina.android.shared.ripper.model.task.Task;
import it.unina.android.shared.ripper.model.task.TaskList;
import it.unina.android.shared.ripper.model.transition.Input;

import java.util.ArrayList;

/**
 * Planner for SPINNER Widget
 * 
 * @author Nicola Amatucci - REvERSE
 *
 */
public class SpinnerEventPlanner extends WidgetEventPlanner {

	/**
	 * Max items to be considered
	 */
	int MAX_INTERACTIONS = 9;
	
	/**
	 * Constructor
	 * 
	 * @param widgetDescription Widget
	 * @param MAX_INTERACTIONS Max items to be considered
	 */
	public SpinnerEventPlanner(WidgetDescription widgetDescription, int MAX_INTERACTIONS) {
		super(widgetDescription);
		this.MAX_INTERACTIONS = MAX_INTERACTIONS;
	}
	
	/* (non-Javadoc)
	 * @see it.unina.android.ripper.planner.widget_events.WidgetEventPlanner#tap(it.unina.android.ripper.model.Task, java.util.ArrayList, java.lang.String[])
	 */
	@Override
	protected TaskList tap(Task currentTask, ArrayList<Input> inputs, String... options) {
		TaskList t = new TaskList();
		int count = mWidget.getCount() != null?mWidget.getCount():0;
		for (int i = 1; i <= Math.min(count, MAX_INTERACTIONS); i++)
			t.addNewTaskForWidget(currentTask, mWidget, InteractionType.SPINNER_SELECT, inputs, Integer.toString(i));
		return t;
	}

}
