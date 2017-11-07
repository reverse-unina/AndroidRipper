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

package it.unina.android.shared.ripper.model.state;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Abstraction of a GUI Interface, an Android Activity
 * 
 * @author Nicola Amatucci - REvERSE
 *
 */

public class ActivityDescription implements Serializable
{
	String id; //given by comparator
	String uid; //given by comparator
	String title;
	String name;
	Class<?> activityClass;
	String className;
	Boolean hasMenu;
	Boolean handlesKeyPress;
	Boolean handlesLongKeyPress;
	Boolean isTabActivity;
	int tabsCount;
	int currentTab;
	Boolean isRootActivity;
	
	HashMap<String, Boolean> listeners;
	ArrayList<String> supportedEvents;
	
	ArrayList<WidgetDescription> widgets;
	
	public ActivityDescription() {
		super();
		this.listeners = new HashMap<String, Boolean>();
		this.supportedEvents = new ArrayList<String>();
		this.widgets = new ArrayList<WidgetDescription>();
		this.hasMenu = false;
		this.handlesKeyPress = false;
		this.handlesLongKeyPress = false;
		this.isTabActivity = false;
		this.isRootActivity = false;
	}
	
	public void addWidget(WidgetDescription widget)
	{
		this.widgets.add(widget);
	}
	
	public String getId() {
		return id;
	}

	public void setUid(String id) {
		this.uid = id;
	}
	
	public String getUid() {
		return uid;
	}

	public void setId(String id) {
		this.id = id;
	}
	
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Class<?> getActivityClass() {
		return activityClass;
	}

	public void setActivityClass(Class<?> activityClass) {
		this.activityClass = activityClass;
	}
	
	public ArrayList<WidgetDescription> getWidgets()
	{
		return this.widgets;
	}
	
	public Boolean hasMenu() {
		return hasMenu;
	}

	public void setHasMenu(Boolean hasMenu) {
		this.hasMenu = hasMenu;
	}
	
	public Boolean handlesKeyPress() {
		return handlesKeyPress;
	}

	public void setHandlesKeyPress(Boolean handlesKeyPress) {
		this.handlesKeyPress = handlesKeyPress;
	}

	public Boolean handlesLongKeyPress() {
		return handlesLongKeyPress;
	}

	public void setHandlesLongKeyPress(Boolean handlesLongKeyPress) {
		this.handlesLongKeyPress = handlesLongKeyPress;
	}

	public Boolean isTabActivity() {
		return isTabActivity;
	}

	public void setIsTabActivity(Boolean isTabActivity) {
		this.isTabActivity = isTabActivity;
	}

	public void setWidgets(ArrayList<WidgetDescription> widgets) {
		this.widgets = widgets;
	}

	public HashMap<String, Boolean> getListeners() {
		return listeners;
	}

	public void addListener(String key, Boolean value)
	{
		this.listeners.put(key, value);
	}
	
	public void setListeners(HashMap<String, Boolean> listeners) {
		this.listeners = listeners;
	}
	
	public ArrayList<String> getSupportedEvents() {
		return supportedEvents;
	}

	public void setSupportedEvents(ArrayList<String> supportedEvents) {
		this.supportedEvents = supportedEvents;
	}

	public void addSupportedEvent(String key)
	{
		this.supportedEvents.add(key);
	}

	public String getClassName() {
		if (this.activityClass != null) {
			return this.activityClass.getCanonicalName();
		} else {
			return className;
		}
	}

	public void setClassName(String className) {
		this.className = className;
	}
	
	public Boolean getHasMenu() {
		return hasMenu;
	}

	public Boolean getHandlesKeyPress() {
		return handlesKeyPress;
	}

	public Boolean getHandlesLongKeyPress() {
		return handlesLongKeyPress;
	}

	public Boolean getIsTabActivity() {
		return isTabActivity;
	}

	public int getTabsCount() {
		return tabsCount;
	}

	public void setTabsCount(int tabsCount) {
		this.tabsCount = tabsCount;
	}
	
	public int getCurrentTab() {
		return currentTab;
	}

	public void setCurrentTab(int currentTab) {
		this.currentTab = currentTab;
	}

	public boolean hasOrientationListener()
	{
		return (isListenerActive("OrientationListener"));
	}
	
	public boolean hasLocationListener()
	{
		return (isListenerActive("LocationListener"));
	}
	
	public boolean hasSensorListener()
	{
		return (isListenerActive("SensorListener") || isListenerActive("SensorEventListener"));
	}
	
	public boolean hasListener(String listenerName)
	{
		return listeners.containsKey(listenerName);
	}
	
	public boolean isListenerActive(String listenerName)
	{
		return hasListener(listenerName) && listeners.get(listenerName);
	}
		
	public Boolean isRootActivity() {
		return isRootActivity;
	}

	public void setIsRootActivity(Boolean isRootActivity) {
		this.isRootActivity = isRootActivity;
	}

	@Override
	public String toString()
	{
		String ret = new String("");
		
		ret += "[title="+this.title+"]\n";
		
		if (this.activityClass!=null)
			ret += "[class="+this.activityClass.getCanonicalName()+"]\n";
		else
			ret += "[class="+this.className+"]\n";
		
		for (WidgetDescription wd : this.widgets)
			ret += "\t"+wd.toString()+"\n";	
		
		return ret;
	}
}
