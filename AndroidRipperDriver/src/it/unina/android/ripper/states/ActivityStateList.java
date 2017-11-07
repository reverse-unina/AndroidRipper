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

package it.unina.android.ripper.states;

import it.unina.android.ripper.comparator.IComparator;
import it.unina.android.ripper.driver.exception.RipperRuntimeException;
import it.unina.android.shared.ripper.model.state.ActivityDescription;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;

/**
 * List of Activity States traversed during the ripping process. 
 * 
 * @author Nicola Amatucci - REvERSE
 *
 */
public class ActivityStateList extends ArrayList<ActivityDescription> implements Serializable
{
	/**
	 * Comparator Instance
	 */
	IComparator comparator = null;

	/**
	 * Last id assigned to a traversed Activity
	 */
	int lastActivityId = 0;
	
	/**
	 * Constructor
	 * 
	 * @param comparator Comparator instance
	 */
	public ActivityStateList(IComparator comparator)
	{
		super();
		this.comparator = comparator;
	}

	/**
	 * Check if the Activity has been traversed during the ripping process 
	 * 
	 * @param activity1 Activity Description
	 * @return
	 */
	public String containsActivity(ActivityDescription activity1)
	{
		for (ActivityDescription activity2 : this)
		{
			if (activity1 == null || activity2 == null)
				continue;
			
			if ((Boolean)this.comparator.compare(activity1, activity2))
				return activity2.getId();
		}
				
		return null;
	}
	
	/**
	 * Get the Id of an equivalent ActivityDescription, if it exists
	 * 
	 * @param activity1 Activity Description
	 * @return
	 */
	public String getEquivalentActivityStateId(ActivityDescription activity1)
	{
		return this.containsActivity(activity1);
	}
	
	/**
	 * Add an ActivityDescription and sets its id
	 * 
	 * @param a
	 * @return
	 */
	public boolean addActivity(ActivityDescription a)
	{
		a.setId("a"+(++lastActivityId));
		return this.add(a);
	}
	
	/**
	 * Get the last ActivityDescription added
	 * 
	 * @return
	 */
	public ActivityDescription getLatestAdded()
	{
		if (this.size() > 0)
			return this.get(this.size() - 1);
		else
			return null;
	}
	
	/**
	 * Output ActivityDescription to Object File
	 * 
	 * @param fileName Output file
	 */
	public void saveToFile(String fileName) {
		try {
			FileOutputStream fileOut = new FileOutputStream(fileName);
			ObjectOutputStream out = new ObjectOutputStream(fileOut);
			out.writeObject(this);
			out.close();
			fileOut.close();
		} catch(Exception ex) {
			throw new RipperRuntimeException(ActivityStateList.class, "saveToFile", ex.getMessage(), ex);
		}
	}
	
	/**
	 * Read ActivityDescription from Object File
	 * 
	 * @param fileName Input File
	 * @return
	 */
	public static ActivityStateList loadFromFile(String fileName) {
		ActivityStateList t = null;
		
		try {
			FileInputStream fileIn = new FileInputStream(fileName);
			ObjectInputStream in = new ObjectInputStream(fileIn);
			t = (ActivityStateList) in.readObject();
			in.close();
			fileIn.close();
		} catch (Exception ex) {
			throw new RipperRuntimeException(ActivityStateList.class, "loadFromFile", ex.getMessage(), ex);
		}
		
		return t;
	}
	
	public void setComparator(IComparator comparator) {
		this.comparator = comparator;
	}
}
