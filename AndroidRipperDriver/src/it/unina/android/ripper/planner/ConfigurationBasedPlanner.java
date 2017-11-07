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

import java.util.ArrayList;
import java.util.HashMap;

import it.unina.android.ripper.planner.widget_events.DrawerListViewEventPlanner;
import it.unina.android.ripper.planner.widget_events.ImageViewEventPlanner;
import it.unina.android.ripper.planner.widget_events.LinearLayoutEventPlanner;
import it.unina.android.ripper.planner.widget_events.ListViewEventPlanner;
import it.unina.android.ripper.planner.widget_events.MenuItemEventPlanner;
import it.unina.android.ripper.planner.widget_events.RadioGroupEventPlanner;
import it.unina.android.ripper.planner.widget_events.SeekBarEventPlanner;
import it.unina.android.ripper.planner.widget_events.SpinnerEventPlanner;
import it.unina.android.ripper.planner.widget_events.TextViewEventPlanner;
import it.unina.android.ripper.planner.widget_events.WidgetEventPlanner;
import it.unina.android.ripper.planner.widget_inputs.ClickableWidgetInputPlanner;
import it.unina.android.ripper.planner.widget_inputs.EditTextInputPlanner;
import it.unina.android.ripper.planner.widget_inputs.SpinnerInputPlanner;
import it.unina.android.ripper.planner.widget_inputs.WidgetInputPlanner;
import it.unina.android.shared.ripper.constants.InteractionType;
import it.unina.android.shared.ripper.constants.SimpleType;
import it.unina.android.shared.ripper.model.state.ActivityDescription;
import it.unina.android.shared.ripper.model.state.WidgetDescription;
import it.unina.android.shared.ripper.model.task.Task;
import it.unina.android.shared.ripper.model.task.TaskList;
import it.unina.android.shared.ripper.model.transition.Input;

/**
 * Planner based on a static configuration. The configuration defines the set of considered
 * interactive widgets and the relative subset of events that can be fired.
 * 
 * This planner does not take into account listeners defined in the WidgetDescription.
 * 
 * @author Nicola Amatucci - REvERSE
 *
 */
public class ConfigurationBasedPlanner extends Planner
{
	/**
	 * Planner Configuration
	 */
	HashMap<String, WidgetEventPlanner.WidgetEventPlannerConfiguration> eventConfiguration;
	
	/**
	 * Widget configured as Input Fields
	 */
	public static String[] inputWidgetList = {
		SimpleType.EDIT_TEXT,
		SimpleType.AUTOCOMPLETE_TEXTVIEW
		//,SimpleType.SPINNER
		//,SimpleType.CHECKBOX
		//,SimpleType.RADIO
		//,SimpleType.TOGGLE
		//,SimpleType.SEEK_BAR
		//,SimpleType.RATING_BAR
		//,SimpleType.FOCUSABLE_EDIT_TEXT
	};
	
	/**
	 * Widget Taken into account by the Planner
	 */
	public static String[] configuredEventWidgetList = {
			SimpleType.BUTTON
			,SimpleType.MENU_ITEM
			,SimpleType.TEXT_VIEW
			,SimpleType.LINEAR_LAYOUT
			//,SimpleType.RELATIVE_LAYOUT
			,SimpleType.CHECKBOX
			,SimpleType.TOGGLE_BUTTON
			,SimpleType.NUMBER_PICKER_BUTTON
			,SimpleType.IMAGE_VIEW
			,SimpleType.PREFERENCE_LIST
			,SimpleType.LIST_VIEW
			,SimpleType.DRAWER_LIST_VIEW
			,SimpleType.SINGLE_CHOICE_LIST
			,SimpleType.MULTI_CHOICE_LIST
			,SimpleType.SPINNER
			,SimpleType.RADIO_GROUP
			,SimpleType.SEEK_BAR
			,SimpleType.RATING_BAR
		};
	
	/**
	 * Check if the Widget is handled by the planner
	 * 
	 * @param widget Widget
	 * @return
	 */
	protected boolean isEventWidgetConfiguredForInteraction(WidgetDescription widget)
	{
		if (widget.getClassName() != null) {
			if(widget.getClassName().startsWith("com.google.android.gms.internal")) {
				return false;
			}
			if(widget.getClassName().startsWith("com.google.android.gms.ads")) {
				return false;
			}
		}
		for (String s : configuredEventWidgetList)
			if(s.equals(widget.getSimpleType()))
				return true;

		return false;
	}
	
	/**
	 * Constructor. Defines the subset of events for each one of the considered widgets.
	 */
	public ConfigurationBasedPlanner() {
		super();
		
		this.eventConfiguration = new HashMap<String, WidgetEventPlanner.WidgetEventPlannerConfiguration>();
		
		this.eventConfiguration.put(SimpleType.BUTTON, new WidgetEventPlanner.WidgetEventPlannerConfiguration(true, false, false));
		this.eventConfiguration.put(SimpleType.MENU_ITEM, new WidgetEventPlanner.WidgetEventPlannerConfiguration(true, false, false));
		this.eventConfiguration.put(SimpleType.TEXT_VIEW, new WidgetEventPlanner.WidgetEventPlannerConfiguration(true, false, false));
		this.eventConfiguration.put(SimpleType.LINEAR_LAYOUT, new WidgetEventPlanner.WidgetEventPlannerConfiguration(true, false, false));
		//this.eventConfiguration.put(SimpleType.RELATIVE_LAYOUT, new WidgetEventPlanner.WidgetEventPlannerConfiguration(false, false, false));
		this.eventConfiguration.put(SimpleType.CHECKBOX, new WidgetEventPlanner.WidgetEventPlannerConfiguration(true, false, false));
		this.eventConfiguration.put(SimpleType.TOGGLE_BUTTON, new WidgetEventPlanner.WidgetEventPlannerConfiguration(true, false, false));
		this.eventConfiguration.put(SimpleType.NUMBER_PICKER_BUTTON, new WidgetEventPlanner.WidgetEventPlannerConfiguration(true, false, false));
		this.eventConfiguration.put(SimpleType.DRAWER_LIST_VIEW, new WidgetEventPlanner.WidgetEventPlannerConfiguration(true, false, false));
		
		this.eventConfiguration.put(SimpleType.IMAGE_VIEW, new WidgetEventPlanner.WidgetEventPlannerConfiguration(true, false, false));
		this.eventConfiguration.put(SimpleType.PREFERENCE_LIST, new WidgetEventPlanner.WidgetEventPlannerConfiguration(true, false, false));
		
		this.eventConfiguration.put(SimpleType.LIST_VIEW, new WidgetEventPlanner.WidgetEventPlannerConfiguration(true, true, false));
		this.eventConfiguration.put(SimpleType.SINGLE_CHOICE_LIST, new WidgetEventPlanner.WidgetEventPlannerConfiguration(true, true, false));
		this.eventConfiguration.put(SimpleType.MULTI_CHOICE_LIST, new WidgetEventPlanner.WidgetEventPlannerConfiguration(true, true, false));
		
		this.eventConfiguration.put(SimpleType.SPINNER, new WidgetEventPlanner.WidgetEventPlannerConfiguration(true, false, false));
		this.eventConfiguration.put(SimpleType.RADIO_GROUP, new WidgetEventPlanner.WidgetEventPlannerConfiguration(true, false, false));
		this.eventConfiguration.put(SimpleType.SEEK_BAR, new WidgetEventPlanner.WidgetEventPlannerConfiguration(true, false, false));
		this.eventConfiguration.put(SimpleType.RATING_BAR, new WidgetEventPlanner.WidgetEventPlannerConfiguration(true, false, false));
	}

	protected TaskList planForWidget(Task currentTask, WidgetDescription widgetDescription, ArrayList<Input> inputs, String... options)
	{
		//excludes widgets used as input and not enabled or not visible widgets
		if (	isInputWidget(widgetDescription) == false &&
				isEventWidgetConfiguredForInteraction(widgetDescription) == true &&
				(widgetDescription.isEnabled() && widgetDescription.isVisible())
		)
		{
			WidgetEventPlanner widgetEventPlanner = null;
			
			if (widgetDescription.getSimpleType().equals(SimpleType.LIST_VIEW))
			{
				widgetEventPlanner = new ListViewEventPlanner(widgetDescription, MAX_INTERACTIONS_FOR_LIST);
			}
			else if (widgetDescription.getSimpleType().equals(SimpleType.DRAWER_LIST_VIEW))
			{
				widgetEventPlanner = new DrawerListViewEventPlanner(widgetDescription);
			}
			else if (widgetDescription.getSimpleType().equals(SimpleType.PREFERENCE_LIST))
			{
				widgetEventPlanner = new ListViewEventPlanner(widgetDescription, MAX_INTERACTIONS_FOR_PREFERENCES_LIST);
			}
			else if (widgetDescription.getSimpleType().equals(SimpleType.SINGLE_CHOICE_LIST))
			{
				widgetEventPlanner =  new ListViewEventPlanner(widgetDescription, MAX_INTERACTIONS_FOR_SINGLE_CHOICE_LIST);
			}
			else if (widgetDescription.getSimpleType().equals(SimpleType.MULTI_CHOICE_LIST))
			{
				widgetEventPlanner =  new ListViewEventPlanner(widgetDescription, MAX_INTERACTIONS_FOR_MULTI_CHOICE_LIST);
			}
			else if (widgetDescription.getSimpleType().equals(SimpleType.SPINNER))
			{
				widgetEventPlanner =  new SpinnerEventPlanner(widgetDescription, MAX_INTERACTIONS_FOR_SPINNER);
			}
			else if (widgetDescription.getSimpleType().equals(SimpleType.RADIO_GROUP))
			{
				widgetEventPlanner =  new RadioGroupEventPlanner(widgetDescription, MAX_INTERACTIONS_FOR_RADIO_GROUP);
			}
			else if (widgetDescription.getSimpleType().equals(SimpleType.TEXT_VIEW))
			{
				widgetEventPlanner =  new TextViewEventPlanner(widgetDescription);
			}
			else if (widgetDescription.getSimpleType().equals(SimpleType.IMAGE_VIEW))
			{
				widgetEventPlanner =  new ImageViewEventPlanner(widgetDescription);
			}

			else if (widgetDescription.getSimpleType().equals(SimpleType.SEEK_BAR) || widgetDescription.getSimpleType().equals(SimpleType.RATING_BAR))
			{
				widgetEventPlanner = new SeekBarEventPlanner(widgetDescription);
			}
			else if (widgetDescription.getSimpleType().equals(SimpleType.MENU_ITEM))
			{
				widgetEventPlanner =  new MenuItemEventPlanner(widgetDescription);
			}
			else if (widgetDescription.getSimpleType().equals(SimpleType.LINEAR_LAYOUT))
			{
				widgetEventPlanner =  new LinearLayoutEventPlanner(widgetDescription);
			}
			else
			{
				widgetEventPlanner =  new WidgetEventPlanner(widgetDescription);
			}
			
			WidgetEventPlanner.WidgetEventPlannerConfiguration config = eventConfiguration.get(widgetDescription.getSimpleType());
			if (config == null)
				config = new WidgetEventPlanner.WidgetEventPlannerConfiguration();
			
			return widgetEventPlanner.planForWidget(
					currentTask,
					inputs,
					config,
					options
			);
		}
		else
		{
			return null; //widget is an input
		}
	}
	
	/* (non-Javadoc)
	 * @see it.unina.android.ripper.planner.Planner#plan(it.unina.android.ripper.model.Task, it.unina.android.ripper.model.ActivityDescription, java.lang.String[])
	 */
	@Override
	public TaskList plan(Task currentTask, ActivityDescription activity, String... options)
	{
		TaskList taskList = new TaskList();
		
		//add activity interactions
		taskList.addAll(this.planForActivity(currentTask, activity, options));

		//generate inputs
		ArrayList<Input> inputs = new ArrayList<Input>();
		for (WidgetDescription wd: activity.getWidgets())
		{
			if (wd.isEnabled() && wd.isVisible()) {
				Input input = getInputForWidget(wd);
				if (input != null)
					inputs.add(input);
			}
		}
				
		//widgets interactions
		for (WidgetDescription wd: activity.getWidgets())
		{
			if (wd.isEnabled() && wd.isVisible())
			{
				TaskList tList = this.planForWidget(currentTask, wd, inputs, options);
				if (tList != null)
					taskList.addAll(tList);
			}
		}	
					
		return taskList;
	}
	
	/**
	 * Check if widget is configured as an Input Field
	 * 
	 * @param widget Widget
	 * @return
	 */
	protected boolean isInputWidget(WidgetDescription widget)
	{
		for (String s : inputWidgetList)
			if(s.equals(widget.getSimpleType()))
				return true;

		return false;
	}
	
	protected Input getInputForWidget(WidgetDescription widgetDescription)
	{
		if (isInputWidget(widgetDescription))
		{
			WidgetInputPlanner widgetInputPlanner = null;
			
			if (widgetDescription.getSimpleType().equals(SimpleType.EDIT_TEXT))
			{

				widgetInputPlanner = new EditTextInputPlanner(widgetDescription);
			}
			else if (widgetDescription.getSimpleType().equals(SimpleType.AUTOCOMPLETE_TEXTVIEW))
			{
				widgetInputPlanner = new EditTextInputPlanner(widgetDescription);
			}
			else if (widgetDescription.getSimpleType().equals(SimpleType.SPINNER))
			{
				widgetInputPlanner = new SpinnerInputPlanner(widgetDescription);
			}
			else if (widgetDescription.getSimpleType().equals(SimpleType.RADIO_GROUP))
			{
				widgetInputPlanner = new SpinnerInputPlanner(widgetDescription);
			}
			else
			{
				widgetInputPlanner = new ClickableWidgetInputPlanner(widgetDescription);
			}
			
			return widgetInputPlanner.getInputForWidget();
		}
		else
		{
			return null; //widget is not an input
		}
	}
	
	/**
	 * Plan Task based on the current ActivityDescription. Add an event to the current task.
	 * 
	 * @param currentTask Current Task
	 * @param activity Target Activity
	 * @param options Configuration
	 * @return
	 */
	protected TaskList planForActivity(Task currentTask, ActivityDescription activity, String... options)
	{
		TaskList taskList = new TaskList();
		
		if (CAN_GO_BACK_ON_HOME_ACTIVITY && activity.isRootActivity())
			taskList.addNewTaskForActivity(currentTask, InteractionType.BACK);
		
		if (CAN_GO_BACK && activity.isRootActivity() == false)
			taskList.addNewTaskForActivity(currentTask, InteractionType.BACK);
		
		if (CAN_CHANGE_ORIENTATION)
			taskList.addNewTaskForActivity(currentTask, InteractionType.CHANGE_ORIENTATION);
		
		if (CAN_OPEN_MENU && activity.hasMenu())
			taskList.addNewTaskForActivity(currentTask, InteractionType.OPEN_MENU);

		if (CAN_SCROLL_DOWN)
			taskList.addNewTaskForActivity(currentTask, InteractionType.SCROLL_DOWN);

		
		if (CAN_SWAP_TAB && activity.isTabActivity())
			for(int i = 1; i <= activity.getTabsCount(); i++)
				taskList.addNewTaskForActivity(currentTask, InteractionType.SWAP_TAB, Integer.toString(i));
			
		return taskList;
	}
}
