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

import it.unina.android.ripper.constants.RipperSimpleType;
import it.unina.android.ripper.log.Debug;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Instrumentation;
import android.test.ActivityInstrumentationTestCase2;
import android.view.Display;
import android.view.Surface;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TabHost;
import android.widget.TextView;

import com.robotium.solo.Solo;


/**
 * Robotium Robot Implementation
 * 
 * @author Nicola Amatucci - REvERSE
 *
 */
@SuppressLint("NewApi")
public class RobotiumWrapperRobot implements IRobot
{
	/**
	 * Robotium Solo
	 */
	private Solo solo = null;
	
	/**
	 * TestCase
	 */
	private ActivityInstrumentationTestCase2<?> testCase = null;
	
	/**
	 * A list of widgets with an id
	 */
	private Map<Integer,View> theViews = null;
	
	/**
	 * A list of all widgets
	 */
	private ArrayList<View> allViews = null;
	
	/**
	 * Reference to the TabHost widget if present
	 */
	private TabHost	tabs = null;
	
	/**
	 * Initialize Robotium
	 * 
	 * @param ActivityInstrumentationTestCase2
	 */
	public RobotiumWrapperRobot(ActivityInstrumentationTestCase2<?> test)
	{
		testCase = test;
		solo = new Solo (testCase.getInstrumentation(), testCase.getActivity());
		
		//init widget lists
		this.theViews = new HashMap<Integer,View>();
		this.allViews = new ArrayList<View>();
	}
	
	/**
	 * Returns Robotium Solo current instance
	 * 
	 * @return Solo
	 */
	public Solo getRobotiumSolo()
	{
		return this.solo;
	}
	
	/* (non-Javadoc)
	 * @see it.unina.android.ripper.automation.robot.IRobot#getInstrumentation()
	 */
	@Override
	public Instrumentation getInstrumentation()
	{
		return testCase.getInstrumentation();
	}
	
	/* (non-Javadoc)
	 * @see it.unina.android.ripper.automation.robot.IRobot#click(android.view.View)
	 */
	@Override
	public void click(View v)
	{
		assertNotNull(v,"Cannot click: the widget does not exist");
		solo.clickOnView(v);
	}

	/* (non-Javadoc)
	 * @see it.unina.android.ripper.automation.robot.IRobot#longClick(android.view.View)
	 */
	@Override
	public void clickMenuItem(String text) {
		solo.clickOnMenuItem(text);
	}
	
	/* (non-Javadoc)
	 * @see it.unina.android.ripper.automation.robot.IRobot#longClick(android.view.View)
	 */
	@Override
	public void longClick (View v)
	{
		assertNotNull(v, "Cannot longClick: the widget does not exist");
		solo.clickLongOnView(v);
	}
	
	/* (non-Javadoc)
	 * @see it.unina.android.ripper.automation.robot.IRobot#clickOnText(java.lang.String)
	 */
	@Override
	public void clickOnText (String text) {
		solo.clickOnText (text);
	}
	
	/* (non-Javadoc)
	 * @see it.unina.android.ripper.automation.robot.IRobot#typeText(android.widget.EditText, java.lang.String)
	 */
	@Override
	public void typeText (EditText v, String value) {
		solo.enterText(v, value);
	}
	
	/* (non-Javadoc)
	 * @see it.unina.android.ripper.automation.robot.IRobot#writeText(android.widget.EditText, java.lang.String)
	 */
	@Override
	public void writeText (EditText v, String value) {
		typeText (v, "");
		typeText (v, value);
	}
	
	/* (non-Javadoc)
	 * @see it.unina.android.ripper.automation.robot.IRobot#pressKey(java.lang.String)
	 */
	@Override
	public void pressKey (String keyCode) {
		pressKey (Integer.parseInt(keyCode));
	}

	/* (non-Javadoc)
	 * @see it.unina.android.ripper.automation.robot.IRobot#pressKey(int)
	 */
	@Override
	public void pressKey (int keyCode) {
		solo.sendKey(keyCode);
	}
	
	/* (non-Javadoc)
	 * @see it.unina.android.ripper.automation.robot.IRobot#sendKey(int)
	 */
	@Override
	public void sendKey(int keyCode) {
		solo.sendKey(keyCode);
	}
	
	/* (non-Javadoc)
	 * @see it.unina.android.ripper.automation.robot.IRobot#goBack()
	 */
	@Override
	public void goBack() {
		solo.goBack();
	}

	/* (non-Javadoc)
	 * @see it.unina.android.ripper.automation.robot.IRobot#openMenu()
	 */
	@Override
	public void openMenu() {
		solo.sendKey(Solo.MENU);
	}

	/* (non-Javadoc)
	 * @see it.unina.android.ripper.automation.robot.IRobot#scrollDown()
	 */
	@Override
	public void scrollDown() {
		solo.scrollDown();
	}
	
	/* (non-Javadoc)
	 * @see it.unina.android.ripper.automation.robot.IRobot#scrollToBottom()
	 */
	@Override
	public void scrollToBottom() {
		solo.scrollToBottom();
	}
	
	/* (non-Javadoc)
	 * @see it.unina.android.ripper.automation.robot.IRobot#scrollToRightSide()
	 */
	public void scrollToRightSide() {
		solo.scrollToSide(Solo.RIGHT);
	}
	
	/* (non-Javadoc)
	 * @see it.unina.android.ripper.automation.robot.IRobot#scrollToLeftSide()
	 */
	public void scrollToLeftSide() {
		solo.scrollToSide(Solo.LEFT);
	}
	
	/* (non-Javadoc)
	 * @see it.unina.android.ripper.automation.robot.IRobot#changeOrientation()
	 */
	@Override
	public void changeOrientation()
	{
		Display display = ((WindowManager) getInstrumentation().getContext().getSystemService(android.content.Context.WINDOW_SERVICE)).getDefaultDisplay();
		int angle = display.getRotation();
		int newAngle = ((angle==Surface.ROTATION_0)||(angle==Surface.ROTATION_180))?Solo.LANDSCAPE:Solo.PORTRAIT;
		solo.setActivityOrientation(newAngle);
	}
	
	/* (non-Javadoc)
	 * @see it.unina.android.ripper.automation.robot.IRobot#selectListItem(android.widget.ListView, java.lang.String)
	 */
	@Override
	public void selectListItem (ListView l, String item) {
		selectListItem (l, item, false);
	}

	/* (non-Javadoc)
	 * @see it.unina.android.ripper.automation.robot.IRobot#selectListItem(android.widget.ListView, java.lang.String, boolean)
	 */
	@Override
	public void selectListItem (ListView l, String item, boolean longClick) {
		selectListItem (l, Integer.valueOf(item), longClick);
	}
	
	/* (non-Javadoc)
	 * @see it.unina.android.ripper.automation.robot.IRobot#selectListItem(android.widget.ListView, int, boolean)
	 */
	@Override
	public void selectListItem (ListView l, int num, boolean longClick) {
		
		/*
		if (l==null) {
			List<ListView> lists = solo.getCurrentViews(ListView.class);
			if (lists.size()>0) {
				l = lists.get(0);
			}
		}
		*/
		
		assertNotNull(l, "Cannon select list item: the list does not exist");
		requestFocus(l);
		Debug.info(this, "Swapping to listview item " + num);
		solo.sendKey(Solo.DOWN);

		final ListView theList = l;
		final int n = Math.min(l.getCount(), Math.max(1,num))-1;
		runOnUiThread(new Runnable() {
			public void run() {
				theList.setSelection(n);
			}
		});
		sync();
		
		if (n<l.getCount()/2) {
			solo.sendKey(Solo.DOWN);
			solo.sendKey(Solo.UP);
		} else {
			solo.sendKey(Solo.UP);			
			solo.sendKey(Solo.DOWN);
		}
		sync();
		
		View v = l.getSelectedView();
		if (longClick) {
			longClick(v);
		} else {
			click (v);
		}
	}
	
	/* (non-Javadoc)
	 * @see it.unina.android.ripper.automation.robot.IRobot#selectSpinnerItem(android.widget.Spinner, java.lang.String)
	 */
	@Override
	public void selectSpinnerItem (Spinner l, String item) {
		selectSpinnerItem (l, Integer.valueOf(item));
	}

	/* (non-Javadoc)
	 * @see it.unina.android.ripper.automation.robot.IRobot#selectSpinnerItem(android.widget.Spinner, int)
	 */
	@Override
	public void selectSpinnerItem (final Spinner s, int num) {
		assertNotNull(s, "Cannon press spinner item: the spinner does not exist");
		Debug.info(this, "Clicking the spinner view");
		click(s);
		sync();
		selectListItem(solo.getCurrentViews(ListView.class).get(0), num, false);
	}
	
	/* (non-Javadoc)
	 * @see it.unina.android.ripper.automation.robot.IRobot#selectRadioItem(android.widget.RadioGroup, java.lang.String)
	 */
	@Override
	public void selectRadioItem (RadioGroup r, String value) {
		selectRadioItem (r, Integer.valueOf(value));
	}

	/* (non-Javadoc)
	 * @see it.unina.android.ripper.automation.robot.IRobot#selectRadioItem(android.widget.RadioGroup, int)
	 */
	@Override
	public void selectRadioItem (final RadioGroup r, int num) {
		if (num<1) assertNotNull(null, "Cannot press radio group item: the index must be a positive number");
		assertNotNull(r, "Cannon press radio group item: the radio group does not exist");
		Debug.info(this, "Selecting from the Radio Group view");
		click(r.getChildAt(num-1));
		sync();
	}
	
	/* (non-Javadoc)
	 * @see it.unina.android.ripper.automation.robot.IRobot#setProgressBar(android.view.View, java.lang.String)
	 */
	@Override
	public void setProgressBar (View v, String value) {
		setProgressBar((ProgressBar)v, Integer.parseInt(value));
	}

	/* (non-Javadoc)
	 * @see it.unina.android.ripper.automation.robot.IRobot#setProgressBar(android.widget.ProgressBar, int)
	 */
	@Override
	public void setProgressBar (ProgressBar v, int value) {
		solo.setProgressBar(v, value);
	}
	
	/* (non-Javadoc)
	 * @see it.unina.android.ripper.automation.robot.IRobot#swapTab(java.lang.String)
	 */
	@Override
	public void swapTab (String tab) {
		swapTab (this.tabs, Integer.valueOf(tab));
	}
	
	/* (non-Javadoc)
	 * @see it.unina.android.ripper.automation.robot.IRobot#swapTab(android.widget.TabHost, java.lang.String)
	 */
	@Override
	public void swapTab (TabHost t, String tab) {
		swapTab (t, Integer.valueOf(tab));
	}
	
	/* (non-Javadoc)
	 * @see it.unina.android.ripper.automation.robot.IRobot#swapTab(android.widget.TabHost, int)
	 */
	@Override
	public void swapTab (final TabHost t, int num) {
		assertNotNull(t, "Cannon swap tab: the tab host does not exist");
		int count = t.getTabWidget().getTabCount();
		ActivityInstrumentationTestCase2.assertTrue("Cannot swap tab: tab index out of bound", num<=count);
		final int n = Math.min(count, Math.max(1,num))-1;
		Debug.info(this, "Swapping to tab " + num);
		click (t.getTabWidget().getChildAt(n));
	}
	
	/* (non-Javadoc)
	 * @see it.unina.android.ripper.automation.robot.IRobot#home()
	 */
	@Override
	public void home() {
		// Scroll listviews up
		final ArrayList<ListView> viewList = solo.getCurrentViews(ListView.class);
		if (viewList.size() > 0) {
			runOnUiThread(new Runnable() {
				public void run() {
					viewList.get(0).setSelection(0);
				}
			});
		}

		// Scroll scrollviews up
		final ArrayList<ScrollView> viewScroll = solo.getCurrentViews(ScrollView.class);
		if (viewScroll.size() > 0) {
			runOnUiThread(new Runnable() {
				public void run() {
					viewScroll.get(0).fullScroll(ScrollView.FOCUS_UP);
				}
			});
		}
	}
	
	/* (non-Javadoc)
	 * @see it.unina.android.ripper.automation.robot.IRobot#requestView(android.view.View)
	 */
	@Override
	public void requestView (final View v) {
		requestView(v, false);
	}
	
	/**
	 * Scroll until the view is on the screen if IN_AND_OUT_OF_FOCUS is enabled or if the force parameter is true
	 * 
	 * @param v
	 * @param force
	 */
	protected void requestView (final View v, boolean force) {
		if (force) {
			home();
			solo.sendKey(Solo.UP); // Solo.waitForView() requires a widget to be focused		
			solo.waitForView(v, 1000, true);
		}
		requestFocus(v);
	}		

	/**
	 * Focus on a View
	 * 	
	 * @param View
	 */
	protected void requestFocus (final View v) {
		runOnUiThread(new Runnable() {
			public void run() {
				v.requestFocus();		
			}
		});
		sync();
	}
	
	/**
	 * Run on Activity Thread (Thread-Safe)
	 * 
	 * @param Runnable
	 */
	protected void runOnUiThread (Runnable action) {
		solo.getCurrentActivity().runOnUiThread(action);		
	}
	
	/* (non-Javadoc)
	 * @see it.unina.android.ripper.automation.robot.IRobot#sync()
	 */
	@Override
	public void sync() {
		getInstrumentation().waitForIdleSync();
	}
	
	/* (non-Javadoc)
	 * @see it.unina.android.ripper.automation.robot.IRobot#wait(int)
	 */
	@Override
	public void wait (int milli) {
		Debug.info(this, "Waiting for " + ((milli>=1000)?(milli/1000 + " sec."):(milli + " msec.")));
		solo.sleep(milli);
	}
	
	/* (non-Javadoc)
	 * @see it.unina.android.ripper.automation.robot.IRobot#getCurrentActivity()
	 */
	@Override
	public Activity getCurrentActivity() {
		return solo.getCurrentActivity();
	}
	
		
	/* (non-Javadoc)
	 * @see it.unina.android.ripper.automation.robot.IRobot#updateWidgets()
	 */
	@Override
	public void updateWidgets () {
		home();
		Debug.info(this, "Retrieving widgets");
		//ArrayList<View> viewList = (isInAndOutFocusEnabled())?solo.getViews():solo.getCurrentViews();
		
		ArrayList<View> viewList = solo.getViews();
		for (View w: viewList)
		{
			String text = (w instanceof TextView)?": "+((TextView)w).getText().toString():"";
			Debug.info(this, "Found widget: id=" + w.getId() + " ("+ w.toString() + ")" + text); // + " in window at [" + xy[0] + "," + xy[1] + "] on screen at [" + xy2[0] + "," + xy2[1] +"]");			
			allViews.add(w);
			if (w.getId()>0) {
				theViews.put(w.getId(), w); // Add only if the widget has a valid ID
			}
			if (w instanceof TabHost) {
				this.tabs = (TabHost)w;
				Debug.info(this, "Found tabhost: id=" + w.getId());
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see it.unina.android.ripper.automation.robot.IRobot#getWidgets()
	 */
	@Override
	public Map<Integer,View> getWidgets () {
		return this.theViews;
	}
	
	/* (non-Javadoc)
	 * @see it.unina.android.ripper.automation.robot.IRobot#getWidget(int)
	 */
	@Override
	public View getWidget (int key) {
		return getWidgets().get(key);
	}
	
	/* (non-Javadoc)
	 * @see it.unina.android.ripper.automation.robot.IRobot#getWidget(int, java.lang.String, java.lang.String)
	 */
	@Override
	public View getWidget (int theId, String theType, String theName) {
		for (View testee: getWidgetsById(theId)) {
			if (checkWidgetEquivalence(testee, theId, theType, theName)) {
				return testee;
			}
		}
		return null;
	}
	
	/* (non-Javadoc)
	 * @see it.unina.android.ripper.automation.robot.IRobot#getWidget(java.lang.String, java.lang.String)
	 */
	@Override
	public View getWidget (String theType, String theName) {
		for (View testee: getWidgetsByType(theType)) {
			if (checkWidgetEquivalence(testee, theType, theName)) {
				return testee;
			}
		}
		return null;
	}
	
	/* (non-Javadoc)
	 * @see it.unina.android.ripper.automation.robot.IRobot#checkWidgetEquivalence(android.view.View, int, java.lang.String, java.lang.String)
	 */
	@Override
	public boolean checkWidgetEquivalence (View testee, int theId, String theType, String theName) {
		return ((theId == testee.getId()) && checkWidgetEquivalence (testee, theType, theName));
	}
	
	/* (non-Javadoc)
	 * @see it.unina.android.ripper.automation.robot.IRobot#checkWidgetEquivalence(android.view.View, java.lang.String, java.lang.String)
	 */
	@Override
	public boolean checkWidgetEquivalence (View testee, String theType, String theName) {
		Debug.info(this, "Retrieved from return list id=" + testee.getId());
		String testeeSimpleType = RipperSimpleType.getSimpleType(testee);
		String testeeType = testee.getClass().getName();
		Debug.info(this, "Testing for type (" + testeeType + ") against the original (" + theType + ")");
		String testeeText = (testee instanceof TextView)?(((TextView)testee).getText().toString()):"";
		
		String testeeName = testeeText;
		if (testee instanceof EditText) {
			CharSequence hint = ((EditText)testee).getHint();
			testeeName = (hint==null)?"":hint.toString();
		}
		
		//		String testeeName = (testee instanceof EditText)?(((EditText)testee).getHint().toString()):testeeText;
		Debug.info(this, "Testing for name (" + testeeName + ") against the original (" + theName + ")");
		if ( ((theType.equals(testeeType)) || (theType.equals(testeeSimpleType)) ) && (theName.equals(testeeName)) ) {
			return true;
		}
		return false;
	}
	
	/* (non-Javadoc)
	 * @see it.unina.android.ripper.automation.robot.IRobot#getWidgetsById(int)
	 */
	@Override
	public ArrayList<View> getWidgetsById (int id) {
		ArrayList<View> theList = new ArrayList<View>();
		for (View theView: getAllWidgets()) {
			if (theView.getId() == id) {
				Debug.info(this, "Added to return list id=" + id);
				theList.add(theView);
			}
		}
		return theList;
	}
	
	/* (non-Javadoc)
	 * @see it.unina.android.ripper.automation.robot.IRobot#getWidgetsByType(java.lang.String)
	 */
	@Override
	public ArrayList<View> getWidgetsByType (String type) {
		ArrayList<View> theList = new ArrayList<View>();
		for (View theView: getAllWidgets()) {
			if (theView.getClass().getName().equals(type)) {
				Debug.info(this, "Added to return list " + type + " with id=" + theView.getId());
				theList.add(theView);
			}
		}
		return theList;
	}
	
	/* (non-Javadoc)
	 * @see it.unina.android.ripper.automation.robot.IRobot#getAllWidgets()
	 */
	@Override
	public ArrayList<View> getAllWidgets () {
		return this.allViews;
	}
	
	/* (non-Javadoc)
	 * @see it.unina.android.ripper.automation.robot.IRobot#clearWidgetList()
	 */
	@Override
	public void clearWidgetList() {
		theViews.clear();
		allViews.clear();
	}

	/**
	 * assertNotNull proxy
	 * 
	 * @param v
	 */
	protected void assertNotNull (final View v)
	{
		ActivityInstrumentationTestCase2.assertNotNull(v);
	}

	/**
	 * assertNotNull proxy
	 * 
	 * @param v
	 */
	protected void assertNotNull (final View v, String errorMessage)
	{
		ActivityInstrumentationTestCase2.assertNotNull(errorMessage, v);
	}

	/* (non-Javadoc)
	 * @see it.unina.android.ripper.automation.robot.IRobot#getButton(java.lang.String)
	 */
	@Override
	public Button getButton(String widgetName) {
		return this.solo.getButton(widgetName);
	}

	/* (non-Javadoc)
	 * @see it.unina.android.ripper.automation.robot.IRobot#getText(java.lang.String)
	 */
	@Override
	public TextView getText(String widgetName) {
		return this.solo.getText(widgetName);
	}

	/* (non-Javadoc)
	 * @see it.unina.android.ripper.automation.robot.IRobot#getCurrentListViews()
	 */
	@Override
	public ArrayList<ListView> getCurrentListViews() {
		return this.solo.getCurrentViews(ListView.class);
	}

	/* (non-Javadoc)
	 * @see it.unina.android.ripper.automation.robot.IRobot#sleep(int)
	 */
	@Override
	public void sleep(int time) {
		this.solo.sleep(time);
	}

	/* (non-Javadoc)
	 * @see it.unina.android.ripper.automation.robot.IRobot#getCurrentProgressBars()
	 */
	@Override
	public ArrayList<ProgressBar> getCurrentProgressBars() {
		return this.solo.getCurrentViews(ProgressBar.class);
	}

	/* (non-Javadoc)
	 * @see it.unina.android.ripper.automation.robot.IRobot#setActivityOrientation(int)
	 */
	@Override
	public void setActivityOrientation(int orientation) {
		solo.setActivityOrientation(orientation);
	}

	/* (non-Javadoc)
	 * @see it.unina.android.ripper.automation.robot.IRobot#finalizeRobot()
	 */
	@Override
	public void finalizeRobot() throws Throwable {
		solo.finalize();
	}
	
	/* (non-Javadoc)
	 * @see it.unina.android.ripper.automation.robot.IRobot#findWidgetByIndexOrEquivalenceOrId(int, int, java.lang.String, java.lang.String)
	 */
	@Override
	public View findWidgetByIndexOrEquivalenceOrId(int widgetId, int widgetIndex, String widgetName, String widgetType)
	{
		View v = null;
		if (widgetIndex<this.getAllWidgets().size()) {
			v = this.getAllWidgets().get(widgetIndex); // Search widget by index
		}
		if ((v!=null) && !this.checkWidgetEquivalence(v, widgetId, widgetType, widgetName)) {
			v = this.getWidget(widgetId, widgetType, widgetName);
		}
		if (v == null) {
			v = this.getWidget(widgetId);
		}
		if (v == null) {
			v = this.getCurrentActivity().findViewById(widgetId);
		}
		
		return v;
	}
	
	
	/* (non-Javadoc)
	 * @see it.unina.android.ripper.automation.robot.IRobot#getViews()
	 */
	@Override
	public ArrayList<View> getViews() {
		return this.solo.getViews();
	}
}
