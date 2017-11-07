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
 * Planner for a Generic Widget Widget
 * 
 * @author Nicola Amatucci - REvERSE
 *
 */
public class WidgetEventPlanner {

	/**
	 * Configuration of WidgetEventPlanner
	 * 
	 * @author Nicola Amatucci - REvERSE
	 *
	 */
	public static class WidgetEventPlannerConfiguration {
		public boolean doClick = false;
		public boolean doLongClick = false;
		public boolean doFocus = false;
		
		public WidgetEventPlannerConfiguration() {
			super();
		}
		
		public WidgetEventPlannerConfiguration(boolean doTap, boolean doLongTap, boolean doFocus) {
			super();
			this.doClick = doTap;
			this.doLongClick = doLongTap;
			this.doFocus = doFocus;
		}
	}
	
	/**
	 * Widget Description
	 */
	protected WidgetDescription mWidget;
	
	/**
	 * Constructor
	 * 
	 * @param widget Widget Description
	 */
	public WidgetEventPlanner(WidgetDescription widget)
	{
		super();
		this.mWidget = widget;
	}
	
	/**
	 * Plan Tasks for Widget
	 * 
	 * @param current Task Base Task
	 * @param inputs Array of Input Field Setters
	 * @param options Options
	 * @return
	 */
	public TaskList planForWidget(Task currentTask, ArrayList<Input> inputs, String... options)
	{
		TaskList taskList = new TaskList();
		
		if ( this.canPlanForWidget() )
		{
			if (mWidget.isClickable() || mWidget.isListenerActive("OnItemClickListener"))
				taskList.addAll(tap(currentTask, inputs, options));
			
			if (mWidget.isLongClickable() || mWidget.isListenerActive("OnItemLongClickListener"))
				taskList.addAll(longTap(currentTask, inputs, options));
			
			if (mWidget.hasOnFocusChangeListener() || mWidget.isListenerActive("OnFocusListener"))
				taskList.addAll(focus(currentTask, inputs, options));
		}
		
		return taskList;
	}
	
	/**
	 * Plan Tasks for Widget
	 * 
	 * @param current Task Base Task
	 * @param inputs Array of Input Field Setters
	 * @param configuration Configuration
	 * @param options Options
	 * @return
	 */
	public TaskList planForWidget(Task currentTask, ArrayList<Input> inputs, WidgetEventPlannerConfiguration configuration, String... options)
	{
		TaskList taskList = new TaskList();
		
		if ( this.canPlanForWidget() )
		{
			if (mWidget.isClickable() || configuration.doClick)
				taskList.addAll(tap(currentTask, inputs, options));
			
			if (mWidget.isLongClickable() || configuration.doLongClick)
				taskList.addAll(longTap(currentTask, inputs, options));
			
			if (mWidget.hasOnFocusChangeListener() || configuration.doFocus)
				taskList.addAll(focus(currentTask, inputs, options));
		}
		
		return taskList;
	}
	
	/**
	 * Check if the Widget is enabled, visible, ... so that the user can interact with it.
	 * 
	 * @return
	 */
	public boolean canPlanForWidget()
	{
		return mWidget != null && mWidget.isEnabled() && mWidget.isVisible(); // && mWidget.getSimpleType() != null && mWidget.getSimpleType().equals("") == false;
	}
	
	/**
	 * Tap Event
	 * 
	 * @param current Task Base Task
	 * @param inputs Array of Input Field Setters
	 * @param options Options
	 * @return
	 */
	protected TaskList tap(Task currentTask, ArrayList<Input> inputs, String... options)
	{
		TaskList t = new TaskList();
		t.add(new Task(currentTask, mWidget, InteractionType.CLICK, inputs));
		return t;
	}
	
	/**
	 * Long Tap Event
	 * 
	 * @param current Task Base Task
	 * @param inputs Array of Input Field Setters
	 * @param options Options
	 * @return
	 */
	protected TaskList longTap(Task currentTask, ArrayList<Input> inputs, String... options)
	{
		TaskList t = new TaskList();
		t.add(new Task(currentTask, mWidget, InteractionType.LONG_CLICK, inputs));
		return t;
	}
	
	/**
	 * Focus Event
	 * 
	 * @param current Task Base Task
	 * @param inputs Array of Input Field Setters
	 * @param options Options
	 * @return
	 */
	protected TaskList focus(Task currentTask, ArrayList<Input> inputs, String... options)
	{
		TaskList t = new TaskList();
		t.add(new Task(currentTask, mWidget, InteractionType.FOCUS, inputs));
		return t;
	}
}
