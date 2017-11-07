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

package it.unina.android.ripper.termination;

import it.unina.android.ripper.driver.AbstractDriver;
import it.unina.android.shared.ripper.model.task.TaskList;

/**
 * The Process is ended when no more events are in the Task List
 * 
 * @author Nicola Amatucci - REvERSE
 *
 */
public class EmptyActivityStateListTerminationCriterion implements TerminationCriterion {

	/**
	 * Task List instance
	 */
	TaskList mTaskList;

	/*
	 * Constructor
	 */
	public EmptyActivityStateListTerminationCriterion() {
		super();
	}

	/* (non-Javadoc)
	 * @see it.unina.android.ripper.termination.TerminationCriterion#init(it.unina.android.ripper.driver.AbstractDriver)
	 */
	@Override
	public void init(AbstractDriver driver) {	
		this.mTaskList = driver.getScheduler().getTaskList();
	}
	
	/* (non-Javadoc)
	 * @see it.unina.android.ripper.termination.TerminationCriterion#check()
	 */
	@Override
	public boolean check() {
		return mTaskList.isEmpty();
	}

}
