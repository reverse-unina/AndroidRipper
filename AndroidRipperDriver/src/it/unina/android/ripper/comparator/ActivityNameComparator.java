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
public class ActivityNameComparator implements IComparator, Serializable {

	/* DEBUG FUNCTIONS ;-) */
	public static boolean DEBUG = false;
	public static final String TAG = "SimpleComparator";
	public static void debug(String s) { if (DEBUG)	System.out.println("["+TAG+"]"+s); }
	public static void debug(boolean condition, String s) { if (DEBUG && condition)	System.out.println("["+TAG+"]"+s); }
	/* END OF DEBUG FUNCTIONS ;-) */
		
	/**
	 * Constructor
	 */
	public ActivityNameComparator()
	{
		super();
	}
	
	/* (non-Javadoc)
	 * @see it.unina.android.ripper.comparator.IComparator#compare(it.unina.android.ripper.model.ActivityDescription, it.unina.android.ripper.model.ActivityDescription)
	 */
	@Override
	public Object compare(ActivityDescription activity1, ActivityDescription activity2)
	{		
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

		return true;
	}

}
