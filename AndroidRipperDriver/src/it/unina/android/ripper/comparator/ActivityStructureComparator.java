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

package it.unina.android.ripper.comparator;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import it.unina.android.shared.ripper.constants.SimpleType;
import it.unina.android.shared.ripper.model.state.ActivityDescription;
import it.unina.android.shared.ripper.model.state.WidgetDescription;

/**
 * Generic Comparator.
 * 
 * Contains a set of methods to compare two ActivityDescription Instances. 
 * 
 * @author Nicola Amatucci - REvERSE
 * 
 */
public class ActivityStructureComparator implements IComparator, Serializable {

	/* DEBUG FUNCTIONS ;-) */
	public static boolean DEBUG = false;
	public static final String TAG = "SimpleComparator";
	public static void debug(String s) { if (DEBUG)	System.out.println("["+TAG+"]"+s); }
	public static void debug(boolean condition, String s) { if (DEBUG && condition)	System.out.println("["+TAG+"]"+s); }
	/* END OF DEBUG FUNCTIONS ;-) */
	
	protected String[] filteredWidgetsArray = { 
			SimpleType.EDIT_TEXT,
			SimpleType.BUTTON,
			SimpleType.MENU_VIEW,
			SimpleType.DIALOG_VIEW,
			SimpleType.SINGLE_CHOICE_LIST,
			SimpleType.MULTI_CHOICE_LIST,
			SimpleType.WEB_VIEW,
			SimpleType.TAB_HOST,
			SimpleType.LIST_VIEW,
			SimpleType.IMAGE_VIEW,
			SimpleType.TEXT_VIEW,
			SimpleType.AUTOCOMPLETE_TEXTVIEW,
			SimpleType.SEARCH_BAR
	};
	
	protected int fixedNumberOfListElements = 5;
	protected int maxListElementsCount = 5;
	
	/**
	 * Constructor
	 */
	public ActivityStructureComparator()
	{
		super();
	}
	
	/* (non-Javadoc)
	 * @see it.unina.android.ripper.comparator.IComparator#compare(it.unina.android.ripper.model.ActivityDescription, it.unina.android.ripper.model.ActivityDescription)
	 */
	@Override
	public Object compare(ActivityDescription activity1, ActivityDescription activity2)
	{
		//Filter widget (if needed)
		ArrayList<WidgetDescription> filteredWidgets1 = new ArrayList<WidgetDescription>();
		ArrayList<WidgetDescription> filteredWidgets2 = new ArrayList<WidgetDescription>();

		List<String> wList = Arrays.asList(filteredWidgetsArray);
		filteredWidgets1 = this.filterWidgets(activity1, wList);
		filteredWidgets2 = this.filterWidgets(activity2, wList);
		debug("Widgets size1="+ activity1.getWidgets().size() + " - size2=" + activity2.getWidgets().size());
		debug("FilteredWidgets size1="+ filteredWidgets1.size() + " - size2=" + filteredWidgets2.size());
		
		// compare current tab
		if(activity1.isTabActivity() && activity2.isTabActivity())
		{
			
			if (activity1.getCurrentTab() != activity2.getCurrentTab()) {
				debug("compare activity tab number -> false");
				return false;
			} else {
				debug("compare activity tab number -> true");
			}
						
		}
		
		//Compare the title parameter of the ActivityDescription instances	
		if (		(activity1.getTitle() != null && activity2.getTitle() == null) 	
			 ||	(activity1.getTitle() == null && activity2.getTitle() != null)
			 ||	(activity1.getTitle() != null && activity2.getTitle() != null && activity1.getTitle().equals(activity2.getTitle()) == false))
		{			
			debug("compare activity titles -> false");
			return false;
		} else {
			 debug("compare activity titles -> true");
		}
	
		debug("testIfFilteredWidgetsMatch size1="+ filteredWidgets1.size() + " - size2=" + filteredWidgets2.size());
		if (testIfWidgetsListMatch(filteredWidgets1, filteredWidgets2) == false)
		{
			debug("test if FILTERED widgets match -> false");
			return false;
		} else {
			debug("test if FILTERED widgets match -> true");				
		}
		
		return true;
	}

	/**
	 * Filter the Widget array
	 * 
	 * @param activity Activity
	 * @param filteredWidgets List of Widget to Filter
	 * @return
	 */
	private ArrayList<WidgetDescription> filterWidgets(ActivityDescription activity, List<String> filteredWidgets)
	{	
		ArrayList<WidgetDescription> ret = new ArrayList<WidgetDescription>();
		
		for (WidgetDescription wd : activity.getWidgets())
			if (filteredWidgets.contains(wd.getSimpleType()))
				ret.add(wd);
			
		return ret;
	}
	
	/**
	 * Compares two WidgetDescription instances
	 * 
	 * @param w1 Widget
	 * @param w2 Widget
	 * @return
	 */
	protected boolean matchWidget(WidgetDescription w1, WidgetDescription w2)
	{
		if (w1.getId().equals(w2.getId()) == false) {
			debug("compare widget id ("+w1.getId()+","+w2.getId()+") -> false");
			return false;
		} else {
			debug("compare widget id ("+w1.getId()+","+w2.getId()+") -> true");		
		}

		if (w1.getSimpleType().equals(w2.getSimpleType()) == false) {
			debug("compare widget simple type("+w1.getSimpleType()+","+w2.getSimpleType()+") -> false");
			return false;
		} else {
			debug("compare widget simple type("+w1.getSimpleType()+","+w2.getSimpleType()+") -> true");	
		}				
		
		//Compare the item count of two LIST_VIEW WidgetDescription instances		
		if (w1.getSimpleType().equals(SimpleType.LIST_VIEW) && w2.getSimpleType().equals(SimpleType.LIST_VIEW))
		{		
			if (w1.getCount().equals(w2.getCount()) == false) {
				if (w1.getCount() >= maxListElementsCount && w2.getCount() >= maxListElementsCount)
				{
					debug("compare list item count (maxListElementsConut) -> true");	
				}
				else
				{
					debug("compare list item count -> false");
					return false;
				}
			} else {
				debug("compare list item count -> true");		
			}
		}

		//Compare the item count of two MENU_VIEW WidgetDescription instances			
		if (w1.getSimpleType().equals(SimpleType.MENU_VIEW) && w2.getSimpleType().equals(SimpleType.MENU_VIEW))
		{
			if (w1.getCount().equals(w2.getCount()) == false) {
				debug("compare menu item count -> false");
				return false;
			} else {
				debug("compare menu item count -> true");	
			}	
		}

		return true;
	}
	
	/**
	 * Test if the array of WidgetDescription instances of two ActivityDescription instances match
	 * 
	 * @param widgets1 Array of WidgetDescription of the first ActivityDescription instance
	 * @param widgets2 Array of WidgetDescription of the second ActivityDescription instance
	 * @return
	 */
	protected boolean testIfWidgetsListMatch(ArrayList<WidgetDescription> widgets1, ArrayList<WidgetDescription> widgets2)
	{
		/**
		 * Implement a contains() method to verify if the widget is already in the list
		 */
		ArrayList<WidgetDescription> checkedAlready = new ArrayList<WidgetDescription>()
		{
			@Override
			public boolean contains(Object o)
			{
				if (o == null && WidgetDescription.class.isInstance(o) == false)
					return false;
				
				return lookFor((WidgetDescription)o, this);
			}
			
		};
		
		//First Pass of comparison
		for (WidgetDescription w1 : widgets1)
		{
			//if (checkedAlready.contains(w1) == false) {
				if(lookFor(w1, widgets2) == false)
				{
					debug("lookFor(w1, widgets2) no " + w1.getId());
					return false;
				} else {
					debug("lookFor(w1, widgets2) yes " + w1.getId());
					checkedAlready.add(w1);
				}
				
			//}
		}
		
		//Second Pass of comparison
		for (WidgetDescription w2 : widgets2)
		{
			if (checkedAlready.contains(w2) == false) {
				if(lookFor(w2, widgets1) == false)
				{
					debug("lookFor(w2, widgets1) no " + w2.getId());
					return false;
				} else {
					debug("lookFor(w2, widgets1) yes " + w2.getId());
				}
					
			}
		}
	
		return true;
	}
	
	/**
	 * Serach for a WidgetDescription in a list of WidgetDescription
	 * @param w1 WidgetDescription to search for
	 * @param widgets Array of WidgetDescription to search in
	 * @return
	 */
	private boolean lookFor(WidgetDescription w1, ArrayList<WidgetDescription> widgets)
	{
		for (WidgetDescription w2 : widgets)
			if (matchWidget(w1, w2))
				return true;
		
		return false;
	}

}
