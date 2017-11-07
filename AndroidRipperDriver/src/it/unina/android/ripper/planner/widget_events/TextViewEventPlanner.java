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

import it.unina.android.ripper.utils.RipperStringUtils;
import it.unina.android.shared.ripper.constants.InteractionType;
import it.unina.android.shared.ripper.model.state.WidgetDescription;
import it.unina.android.shared.ripper.model.task.Task;
import it.unina.android.shared.ripper.model.task.TaskList;
import it.unina.android.shared.ripper.model.transition.Input;

import java.util.ArrayList;

/**
 * Planner for TEXT_VIEW Widget
 * 
 * @author Nicola Amatucci - REvERSE
 * 
 */
public class TextViewEventPlanner extends WidgetEventPlanner {
	
	/**
	 * Constructor
	 *  
	 * @param widgetDescription widget
	 */
	public TextViewEventPlanner(WidgetDescription widgetDescription) {
		super(widgetDescription);
	}
	
	/* (non-Javadoc)
	 * @see it.unina.android.ripper.planner.widget_events.WidgetEventPlanner#tap(it.unina.android.ripper.model.Task, java.util.ArrayList, java.lang.String[])
	 */
	@Override
	protected TaskList tap(Task currentTask, ArrayList<Input> inputs, String... options)
	{
		TaskList t = new TaskList();

		if (mWidget.isClickable() && mWidget.getValue() != null && mWidget.getValue().equals("") == false) {
			t.add(new Task(currentTask, mWidget, InteractionType.CLICK_ON_TEXT, inputs, RipperStringUtils.quoteRegExSpecialChars(mWidget.getValue())));
		} else if (mWidget.isClickable() && mWidget.getName() != null && mWidget.getName().equals("") == false) {
			t.add(new Task(currentTask, mWidget, InteractionType.CLICK_ON_TEXT, inputs, RipperStringUtils.quoteRegExSpecialChars(mWidget.getName())));
		}

		return t;
	}

}
