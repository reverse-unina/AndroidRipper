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

package it.unina.android.ripper.automation;

import static it.unina.android.shared.ripper.constants.InteractionType.BACK;
import static it.unina.android.shared.ripper.constants.InteractionType.CHANGE_ORIENTATION;
import static it.unina.android.shared.ripper.constants.InteractionType.DOUBLE_ORIENTATION;
import static it.unina.android.shared.ripper.constants.InteractionType.CLICK;
import static it.unina.android.shared.ripper.constants.InteractionType.CLICK_ON_TEXT;
import static it.unina.android.shared.ripper.constants.InteractionType.CLICK_MENU_ITEM;
import static it.unina.android.shared.ripper.constants.InteractionType.FOCUS;
import static it.unina.android.shared.ripper.constants.InteractionType.LIST_LONG_SELECT;
import static it.unina.android.shared.ripper.constants.InteractionType.LIST_SELECT;
import static it.unina.android.shared.ripper.constants.InteractionType.LONG_CLICK;
import static it.unina.android.shared.ripper.constants.InteractionType.OPEN_MENU;
import static it.unina.android.shared.ripper.constants.InteractionType.PRESS_KEY;
import static it.unina.android.shared.ripper.constants.InteractionType.SCROLL_DOWN;
import static it.unina.android.shared.ripper.constants.InteractionType.SET_BAR;
import static it.unina.android.shared.ripper.constants.InteractionType.SPINNER_SELECT;
import static it.unina.android.shared.ripper.constants.InteractionType.SWAP_TAB;
import static it.unina.android.shared.ripper.constants.InteractionType.TYPE_TEXT;
import static it.unina.android.shared.ripper.constants.InteractionType.WRITE_TEXT;
import static it.unina.android.shared.ripper.constants.SimpleType.BUTTON;
import static it.unina.android.shared.ripper.constants.SimpleType.LIST_VIEW;
import static it.unina.android.shared.ripper.constants.SimpleType.MENU_ITEM;
import it.unina.android.ripper.automation.robot.IRobot;
import it.unina.android.ripper.configuration.Configuration;
import it.unina.android.shared.ripper.constants.InteractionType;
import it.unina.android.ripper.log.Debug;

import java.util.ArrayList;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContextWrapper;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TabHost;

import com.robotium.solo.Solo;

/**
 * Automation Component Implementation
 * 
 * @author Nicola Amatucci - REvERSE
 *
 */
@SuppressLint("NewApi")
public class RipperAutomation implements IAutomation {

	/**
	 * Robot Implementation
	 */
	IRobot robot = null;
	
	/**
	 * Constructor
	 * 
	 * @param robot Robot Implementation
	 */
	public RipperAutomation(IRobot robot)
	{
		this.robot = robot;
	}
	
	/* (non-Javadoc)
	 * @see it.unina.android.ripper.automation.IAutomation#fireEvent(java.lang.String, java.lang.Integer, java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 */
	public void fireEvent(String widgetId, Integer widgetIndex, String widgetName, String widgetType, String eventType, String value)
	{
		try {
			robot.sync();
		} catch(Throwable t) {
			
		}
		
		if (eventType != null && eventType.equals("") == false)
		{
			if (	eventType.equals(InteractionType.BACK)
				|| 	eventType.equals(InteractionType.SCROLL_DOWN)
				||	eventType.equals(InteractionType.CHANGE_ORIENTATION)
				||	eventType.equals(InteractionType.DOUBLE_ORIENTATION)
				||	eventType.equals(InteractionType.OPEN_MENU))
			{
				this.fireEventOnView(null, eventType, null);
			}
			else if (eventType.equals(InteractionType.CLICK_ON_TEXT))
			{
				this.fireEventOnView(null, eventType, value);
			}
			else
			{
				View v = null;
				
				if (widgetIndex < robot.getViews().size())
					v = robot.getViews().get(widgetIndex);
				
				if (v != null && robot.checkWidgetEquivalence(v, Integer.parseInt(widgetId), widgetType, widgetName))
				{
					fireEventOnView(v, eventType, value);
				}
				else if (widgetId.equals("-1"))
				{
					fireEvent(widgetName, widgetType, eventType, value);
				}
				else
				{
					fireEvent (Integer.parseInt(widgetId), widgetName, widgetType, eventType, value);
				}
			}
			
		}
	
		
	}

	/**
	 * Inject Interaction using Robot Instance
	 * 
	 * @param v Widget
	 * @param interactionType Type of Interaction
	 * @param value Interaction Parameter
	 */
	protected void injectInteraction (View v, String interactionType, String value) {
		if (v!=null)
			this.robot.requestView(v);
		
		if (interactionType.equals(CLICK))
		{
			this.robot.click (v);
		}		
		else if (interactionType.equals(CLICK_MENU_ITEM))
		{
			this.robot.click (v);
		}
		else if (interactionType.equals(FOCUS))
		{
			this.robot.click (v);
		}
		else if (interactionType.equals(LONG_CLICK))
		{
			this.robot.longClick(v);
		}
		else if (interactionType.equals(BACK))
		{
			this.robot.goBack();
		}
		else if (interactionType.equals(CHANGE_ORIENTATION))
		{
			this.robot.changeOrientation();
		}
		else if (interactionType.equals(DOUBLE_ORIENTATION))
		{
			this.robot.changeOrientation();
			this.robot.sleep(1000);
			this.robot.changeOrientation();
		}
		else if (interactionType.equals(CLICK_ON_TEXT))
		{
			this.robot.clickOnText(value);
		}
		else if (interactionType.equals(PRESS_KEY))
		{
			//this.robot.sendKey(Integer.parseInt(value));
			robot.getInstrumentation().sendCharacterSync(Integer.parseInt(value));
		}
		else if (interactionType.equals(OPEN_MENU))
		{
			//this.robot.sendKey(Solo.MENU);
			robot.getInstrumentation().sendCharacterSync(Solo.MENU);
		}
		else if (interactionType.equals(SCROLL_DOWN))
		{
			this.robot.scrollDown();
		}
		else if (interactionType.equals(SWAP_TAB) && (value!=null))
		{
			if (v instanceof TabHost) {
				this.robot.swapTab ((TabHost)v, value);
			} else {
				this.robot.swapTab (value);
			}
		}
		else if (ListView.class.isInstance(v) && interactionType.equals(LIST_SELECT))
		{
			this.robot.selectListItem((ListView)v, value);
		}
		else if (ListView.class.isInstance(v) && interactionType.equals(LIST_LONG_SELECT))
		{
			this.robot.selectListItem((ListView)v, value, true);
		}
		else if (Spinner.class.isInstance(v) && interactionType.equals(SPINNER_SELECT))
		{
			this.robot.selectSpinnerItem((Spinner)v, value);
		}
		else if (EditText.class.isInstance(v) && interactionType.equals(TYPE_TEXT))
		{
			this.robot.typeText((EditText)v, value);
		}
		else if (EditText.class.isInstance(v) && interactionType.equals(WRITE_TEXT))
		{
			this.robot.writeText((EditText)v, value);
		}
		else if (ProgressBar.class.isInstance(v) && interactionType.equals(SET_BAR))
		{
			this.robot.setProgressBar((ProgressBar)v, Integer.parseInt(value));
		}
		else
		{
			return;
		}
	}
	
	/* (non-Javadoc)
	 * @see it.unina.android.ripper.automation.IAutomation#fireEvent(int, int, java.lang.String, java.lang.String)
	 */
	@Override
	public void fireEvent (int widgetId, int widgetIndex, String widgetType, String eventType) {
		//fireEvent(widgetId, widgetIndex, widgetType, eventType, null);
		fireEvent(widgetId, widgetIndex, "", widgetType, eventType);
	}
	
	/* (non-Javadoc)
	 * @see it.unina.android.ripper.automation.IAutomation#fireEvent(int, int, java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public void fireEvent (int widgetId, int widgetIndex, String widgetName, String widgetType, String eventType) {
		fireEvent(widgetId, widgetIndex, widgetName, widgetType, eventType, null);
	}
	
	/* (non-Javadoc)
	 * @see it.unina.android.ripper.automation.IAutomation#fireEvent(int, java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public void fireEvent (int widgetIndex, String widgetName, String widgetType, String eventType) {
		fireEvent(widgetIndex, widgetName, widgetType, eventType, null);
	}
	
	/* (non-Javadoc)
	 * @see it.unina.android.ripper.automation.IAutomation#fireEvent(int, int, java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public void fireEvent (int widgetId, int widgetIndex, String widgetName, String widgetType, String eventType, String value)
	{
		this.robot.updateWidgets();
		View v = this.robot.findWidgetByIndexOrEquivalenceOrId(widgetId, widgetIndex, widgetName, widgetType);		
		fireEventOnView (v, eventType, value);
	}
	
	/* (non-Javadoc)
	 * @see it.unina.android.ripper.automation.IAutomation#fireEvent(int, java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public void fireEvent (int widgetIndex, String widgetName, String widgetType, String eventType, String value)
	{
		this.robot.updateWidgets();
		
		Debug.info(this, "Firing event: type= " + eventType + " value= " + value);
		
		View v = null;
		if (eventType.equals(BACK) || eventType.equals(SCROLL_DOWN))
		{
			fireEventOnView(null, eventType, null);
			return;
		}
		else if (eventType.equals(CLICK_ON_TEXT))
		{			
			fireEventOnView(null, eventType, value);
			return;
		}
		
		if (widgetType.equals(BUTTON))
		{
			v = this.robot.getButton(widgetName);
		}
		else if (widgetType.equals(MENU_ITEM))
		{
			v = this.robot.getText(widgetName);
		}
		else if (widgetType.equals(LIST_VIEW))
		{
			v = this.robot.getCurrentListViews().get(0);
		}
		
		if (v == null) {
			for (View w: this.robot.getAllWidgets()) {
				if (w instanceof Button) {
					Button candidate = (Button) w;
					if (candidate.getText().equals(widgetName)) {
						v = candidate;
					}
				}
				if (v!=null) break;
			}
		}
		
		fireEventOnView (v, eventType, value);
	}
	
	/* (non-Javadoc)
	 * @see it.unina.android.ripper.automation.IAutomation#fireEvent(java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 */
	public void fireEvent (String widgetName, String widgetType, String eventType, String value)
	{
		this.robot.updateWidgets();
		
		View v = null;
		if (widgetType.equals(BUTTON)) {
			v = robot.getButton(widgetName);
		} else if (widgetType.equals(MENU_ITEM)) {
			v = robot.getText(widgetName);
		}
		if (v == null) {
			for (View w: robot.getAllWidgets()) {
				if (w instanceof Button) {
					Button candidate = (Button) w;
					if (candidate.getText().equals(widgetName)) {
						v = candidate;
					}
				}
				if (v!=null) break;
			}
		}
		fireEventOnView(v, eventType, value);
	}
	
	/**
	 * Fire Event on View
	 * 
	 * @param v Widget
	 * @param eventType Type of Event
	 * @param value Parameter of the Event
	 */
	protected void fireEventOnView (View v, String eventType, String value) {
		injectInteraction(v, eventType, value);
		this.robot.sleep(Configuration.SLEEP_AFTER_EVENT);
		waitOnThrobber();
	}
	
	/* (non-Javadoc)
	 * @see it.unina.android.ripper.automation.IAutomation#setInput(int, java.lang.String, java.lang.String)
	 */
	@Override
	public void setInput (int widgetId, String interactionType, String value)
	{
		this.robot.updateWidgets();
		
		View v = this.robot.getWidget(widgetId);
		
		if (v == null)
			v = this.robot.getCurrentActivity().findViewById(widgetId);
		
		/*
		if (v == null) {
			for (View w: getAllWidgets()) {
				if (w instanceof Button || w instanceof RadioGroup) {
					if (!robot.getType(w).equals(widgetType)) continue;
					v = (detectName(w).equals(widgetName))?w:null;
				}
				if (v!=null) break;
			}
		}
		*/
		
		injectInteraction(v, interactionType, value);
	}
	
	
	/* (non-Javadoc)
	 * @see it.unina.android.ripper.automation.IAutomation#restart()
	 */
	@Override
	public void restart()
	{
		ContextWrapper main = new ContextWrapper(robot.getCurrentActivity());
		Intent i = main.getBaseContext().getPackageManager().getLaunchIntentForPackage(main.getBaseContext().getPackageName() );
		i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK );
		main.startActivity(i);
	}
	
	/* (non-Javadoc)
	 * @see it.unina.android.ripper.automation.IAutomation#waitOnThrobber()
	 */
	@Override
	public void waitOnThrobber()
	{
		int sleepTime = Configuration.SLEEP_ON_THROBBER;
		if (sleepTime==0) return;
		
		boolean flag;
		do
		{
			flag = false;
			ArrayList<ProgressBar> bars = this.robot.getCurrentProgressBars();
			for (ProgressBar b: bars)
			{
				if (b.isShown() && b.isIndeterminate())
				{
					Debug.info(this, "Waiting on Progress Bar #" + b.getId());
					flag = true;
					this.robot.sleep(500);
					sleepTime-=500;
				}
			}
		} while (flag && (sleepTime>0));
		
		this.robot.sync();
	}

	/* (non-Javadoc)
	 * @see it.unina.android.ripper.automation.IAutomation#setActivityOrientation(int)
	 */
	@Override
	public void setActivityOrientation(int orientation) {
		this.robot.setActivityOrientation(orientation);
		
	}

	/* (non-Javadoc)
	 * @see it.unina.android.ripper.automation.IAutomation#sleep(int)
	 */
	@Override
	public void sleep(int time) {
		this.robot.sleep(time);
	}

	/* (non-Javadoc)
	 * @see it.unina.android.ripper.automation.IAutomation#getCurrentActivity()
	 */
	@Override
	public Activity getCurrentActivity() {
		return this.robot.getCurrentActivity();
	}

	/* (non-Javadoc)
	 * @see it.unina.android.ripper.automation.IAutomation#finalizeRobot()
	 */
	@Override
	public void finalizeRobot() throws Throwable {
		this.robot.finalizeRobot();
	}

	/* (non-Javadoc)
	 * @see it.unina.android.ripper.automation.IAutomation#getRobot()
	 */
	@Override
	public IRobot getRobot() {
		return this.robot;
	}
}
