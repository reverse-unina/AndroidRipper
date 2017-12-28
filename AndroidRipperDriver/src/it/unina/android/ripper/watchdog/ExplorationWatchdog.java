/**
 * The MIT License
 *
 * Copyright (c) 2014-2017 REvERSE, REsEarch gRoup of Software Engineering @ the University of Naples Federico II, http://reverse.dieti.unina.it/
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 *
 **/

package it.unina.android.ripper.watchdog;

import java.util.ArrayList;

import it.unina.android.ripper.comparator.IComparator;
import it.unina.android.ripper.comparator.WidgetPropertiesComparator;
import it.unina.android.shared.ripper.model.state.ActivityDescription;
import it.unina.android.shared.ripper.model.task.Task;

public class ExplorationWatchdog {

	/* DEBUG FUNCTIONS ;-) */
	public static boolean DEBUG = true;
	public static final String TAG = "ExplorationWatchdog";
	public static void debug(String s) { if (DEBUG)	System.out.println("["+TAG+"]"+s); }
	public static void debug(boolean condition, String s) { if (DEBUG && condition)	System.out.println("["+TAG+"]"+s); }
	/* END OF DEBUG FUNCTIONS ;-) */
	
	public static int EXPLORATION_WATCHDOG_THRESHOLD = 25;
	
	class CountActivityListElement {
		public ActivityDescription ad = null;
		public Integer counter = null;
		
		public CountActivityListElement(ActivityDescription ad) {
			this.ad = ad;
			counter = 0;
		}
	}
	
	ArrayList<CountActivityListElement> countActivityList = null;
	
	private static ExplorationWatchdog wDog = null;
	
	public static ExplorationWatchdog getInstance() {
		if (wDog == null) {
			wDog = new ExplorationWatchdog();
		}
		
		return wDog;
	}
	
	IComparator comparator = null;
	
	private ExplorationWatchdog() {
		super();
		comparator = new WidgetPropertiesComparator();
		countActivityList = new ArrayList<CountActivityListElement>();
		this.reset();
	}
	
	public synchronized void reset() {
		debug("Reset Watchdog!");
		countActivityList.clear();
	}
	
	public synchronized boolean check(ActivityDescription ad, Task t) {
		
		CountActivityListElement e = containsActivity(ad);
		if (e != null) {
			debug("Activity: " + e.ad.getClassName() + " | " + (e.counter+1)); //counter is incremented in the next line ;-)
			return (e.counter++ > EXPLORATION_WATCHDOG_THRESHOLD);	
		} else {
			//TODO: reset();
			debug("Activity: " + ad.getClassName() + " | 0");
			countActivityList.add(new CountActivityListElement(ad));
		}
		
		return false;
	}
	
	private CountActivityListElement containsActivity(ActivityDescription activity1)
	{
		for (CountActivityListElement activity2 : countActivityList)
		{
			if (activity1 == null || activity2.ad == null)
				continue;
			
			if ((Boolean)this.comparator.compare(activity1, activity2.ad))
				return activity2;
		}
				
		return null;
	}
}
