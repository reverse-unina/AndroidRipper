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

package it.unina.android.ripper.automation.robot;

import java.util.ArrayList;
import java.util.Map;

import android.app.Activity;
import android.app.Instrumentation;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TabHost;
import android.widget.TextView;

/**
 * Generic Robot Interface
 * 
 * @author Nicola Amatucci - REvERSE
 *
 */
public interface IRobot {

	/**
	 * Returns Instrumentation
	 * 
	 * @return Instrumentation
	 */
	public abstract Instrumentation getInstrumentation();

	/**
	 * Click on View
	 * 
	 * @param View
	 */
	public abstract void click(View v);

	/**
	 * LongClick on View
	 * 
	 * @param View
	 */
	public abstract void longClick(View v);

	/**
	 * Click On Text
	 * 
	 * @param String
	 */
	public abstract void clickOnText(String text);

	/**
	 * Types text in  EditText
	 * @param EditText
	 * @param String
	 */
	public abstract void typeText(EditText v, String value);

	/**
	 * Writes text in EditText
	 * @param EditText
	 * @param String
	 */
	public abstract void writeText(EditText v, String value);

	/**
	 * Key Press
	 * 
	 * @param String
	 */
	public abstract void pressKey(String keyCode);

	/**
	 * Key Press
	 * 
	 * @param int
	 */
	public abstract void pressKey(int keyCode);

	/**
	 * Key Press
	 * @param menu
	 */
	public abstract void sendKey(int keyCode);

	/**
	 * Goes back
	 */
	public abstract void goBack();

	/**
	 * Opens menu
	 */
	public abstract void openMenu();

	/**
	 * Click MenuItem
	 * 
	 * @param text MenuItem Text
	 */
	public abstract void clickMenuItem(String text);
	
	/**
	 * Scrolls down
	 */
	public abstract void scrollDown();

	/**
	 * Scrolls down to bottom
	 */
	public abstract void scrollToBottom();
	
	/**
	 * Scrolls to right
	 */
	public abstract void scrollToRightSide();
	
	/**
	 * Scrolls to left
	 */
	public abstract void scrollToLeftSide();
	
	/**
	 * Changes orientation
	 */
	public abstract void changeOrientation();

	/**
	 * Selects (Clicks) item from a ListView
	 * 
	 * @param ListView
	 * @param String
	 */
	public abstract void selectListItem(ListView l, String item);

	/**
	 * Selects (Clicks) or Long-Selects (Long-Clicks) item from a ListView
	 * 
	 * @param ListView
	 * @param String
	 * @param boolean
	 */
	public abstract void selectListItem(ListView l, String item,
			boolean longClick);

	/**
	 * Selects (Clicks) or Long-Selects (Long-Clicks) item from a ListView
	 * 
	 * @param ListView
	 * @param int
	 * @param boolean
	 */
	public abstract void selectListItem(ListView l, int num, boolean longClick);

	/**
	 * Spinner interaction
	 * 
	 * @param l
	 * @param item
	 */
	public abstract void selectSpinnerItem(Spinner l, String item);

	/**
	 * Spinner interaction
	 * 
	 * @param s
	 * @param num
	 */
	public abstract void selectSpinnerItem(Spinner s, int num);

	/**
	 * Radio Interaction
	 * 
	 * @param r
	 * @param value
	 */
	public abstract void selectRadioItem(RadioGroup r, String value);

	/**
	 * Radio Interaction
	 * 
	 * @param r
	 * @param num
	 */
	public abstract void selectRadioItem(RadioGroup r, int num);

	/**
	 * Progress Bar interaction
	 * 
	 * @param v
	 * @param value
	 */
	public abstract void setProgressBar(View v, String value);

	/**
	 * Progress Bar interaction
	 * 
	 * @param v
	 * @param value
	 */
	public abstract void setProgressBar(ProgressBar v, int value);

	/**
	 * Tab interaction. Uses tabs preloaded with updateWidgets()
	 * 
	 * @param tab
	 */
	public abstract void swapTab(String tab);

	/**
	 * Tab interaction
	 * 
	 * @param t
	 * @param tab
	 */
	public abstract void swapTab(TabHost t, String tab);

	/**
	 * Tab interaction
	 * 
	 * @param t
	 * @param num
	 */
	public abstract void swapTab(TabHost t, int num);

	/**
	 * Scroll the view to the top. Only works for ListView and ScrollView.
	 * Support for GridView and others must be added
	 */
	public abstract void home();

	/**
	 * Scroll until the view is on the screen if IN_AND_OUT_OF_FOCUS is enabled or if the force parameter is true
	 * 
	 * @param v
	 */
	public abstract void requestView(View v);

	/**
	 * getInstrumentation().waitForIdleSync();
	 */
	public abstract void sync();

	/**
	 * Sleep for milliseconds
	 * 
	 * @param int
	 */
	public abstract void wait(int milli);

	/**
	 * Returns current activity
	 * @return
	 */
	public abstract Activity getCurrentActivity();

	/**
	 * Retrives Widgets and saves a list in two data structures: theViews, allViews
	 */
	public abstract void updateWidgets();

	/**
	 * Get widgets map loaded by updateWidgets() method
	 * 
	 * @return
	 */
	public abstract Map<Integer, View> getWidgets();

	/**
	 * Get widget with an id from the map loaded by updateWidgets() method
	 * 
	 * @return
	 */
	public abstract View getWidget(int key);

	/**
	 * Get widget with an assigned id, type, name
	 * 
	 * @return
	 */
	public abstract View getWidget(int theId, String theType, String theName);

	/**
	 * Gets widget with an assigned id, type
	 * 
	 * @return
	 */
	public abstract View getWidget(String theType, String theName);

	/**
	 * Checks widget for equivalence
	 * 
	 * TODO: should be implemented outside the class
	 * 
	 * @param testee
	 * @param theId
	 * @param theType
	 * @param theName
	 * @return
	 */
	public abstract boolean checkWidgetEquivalence(View testee, int theId,
			String theType, String theName);

	/**
	 * Checks widget for equivalence
	 * 
	 * TODO: should be implemented outside the class
	 * 
	 * @param testee
	 * @param theType
	 * @param theName
	 * @return
	 */
	public abstract boolean checkWidgetEquivalence(View testee, String theType,
			String theName);

	/**
	 * Gets widgets by id
	 * 
	 * @param id
	 * @return
	 */
	public abstract ArrayList<View> getWidgetsById(int id);

	/**
	 * Gets widgets by type
	 * 
	 * @param type
	 * @return
	 */
	public abstract ArrayList<View> getWidgetsByType(String type);

	/**
	 * Gets all loaded widgets
	 * 
	 * @return
	 */
	public abstract ArrayList<View> getAllWidgets();

	/**
	 * Clear widgets list
	 */
	public abstract void clearWidgetList();

	/**
	 * gets button by name
	 * 
	 * @param widgetName
	 * @return
	 */
	public abstract Button getButton(String widgetName);

	/**
	 * gets textview by name
	 * 
	 * @param widgetName
	 * @return
	 */
	public abstract TextView getText(String widgetName);

	/**
	 * gets current listview
	 * 
	 * @return
	 */
	public abstract ArrayList<ListView> getCurrentListViews();

	/**
	 * sleep for n milliseconds
	 * 
	 * @param time
	 */
	public abstract void sleep(int time);

	/**
	 * gets current progress bars
	 * @return
	 */
	public abstract ArrayList<ProgressBar> getCurrentProgressBars();

	/**
	 * set activity orientation
	 * 
	 * @param orientation
	 */
	public abstract void setActivityOrientation(int orientation);

	/**
	 * finalizes robot
	 * 
	 * @throws Excetpion
	 */
	public abstract void finalizeRobot() throws Throwable;

	/**
	 * Find Widget by (in order):
	 * 1) index
	 * 2) equivalence
	 * 3) id in loaded list
	 * 4) id on activity
	 * 
	 * @param widgetId
	 * @param widgetIndex
	 * @param widgetName
	 * @param widgetType
	 * @return
	 */
	public abstract View findWidgetByIndexOrEquivalenceOrId(int widgetId,
			int widgetIndex, String widgetName, String widgetType);

	/**
	 * Get all the views
	 * 
	 * @return
	 */
	public abstract ArrayList<View> getViews();

}