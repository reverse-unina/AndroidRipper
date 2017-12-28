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

import it.unina.android.ripper.planner.widget_events.DrawerListViewEventPlanner;
import it.unina.android.ripper.planner.widget_events.ImageViewEventPlanner;
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
 * Planner based on a relevant events on the current ActivityDescription.
 * 
 * This planner take into account listeners defined in the WidgetDescription.
 *
 * NOTA: max_event_len -> if task.size() > MAX don't add :-)
 * 
 * @author Nicola Amatucci - REvERSE
 *
 */
public class HandlerBasedPlanner extends Planner
{	
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
	
	/**
	 * Constructor
	 */
	public HandlerBasedPlanner()
	{
		super();
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
		
		//TODO: set extra inputs
		
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
		
		//TODO: add extra events
		
		return taskList;
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
		
		//TODO: sensors / gps / intents
		//...
		//...
			
		return taskList;
	}
	
	/**
	 * Plan Task based on the current WidgetDescription. Add an event to the current task.
	 * 
	 * @param currentTask Current Task
	 * @param widgetDescription Target Widget
	 * @param inputs Input Fields
	 * @param options Options
	 * @return
	 */
	protected TaskList planForWidget(Task currentTask, WidgetDescription widgetDescription, ArrayList<Input> inputs, String... options)
	{
		//excludes widgets used as input and not enabled or not visible widgets
		if (	isInputWidget(widgetDescription) == false &&
				(widgetDescription.isEnabled() && widgetDescription.isVisible())
		)
		{
			WidgetEventPlanner widgetEventPlanner;
			
			//expandmenu == list
			//numberpicker -> click?
			//auto_complete_text
			//search_bar
			
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
			else if (widgetDescription.getSimpleType().equals(SimpleType.RELATIVE_LAYOUT)) {
				return null;
			}
			else if (widgetDescription.getSimpleType().equals(SimpleType.LINEAR_LAYOUT)) {
				return null;
			}
			else if (widgetDescription.getSimpleType().equals(SimpleType.WEB_VIEW)) {
				return null;
			}
			else
			{
				widgetEventPlanner =  new WidgetEventPlanner(widgetDescription);
			}
			
			return widgetEventPlanner.planForWidget(currentTask, inputs, options);
		}
		else
		{
			return null; //widget is an input
		}
	}
	
	/**
	 * Add Input Field Values for a Widget
	 * 
	 * @param widgetDescription Input Field Widget
	 * @return
	 */
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
}
