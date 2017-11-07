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

/**
 * The Process is ended after a predefined number of fired events
 * 
 * @author Nicola Amatucci - REvERSE
 *
 */
public class TestingTimeBasedTerminationCriterion implements TerminationCriterion {

	/**
	 * AbstractDriver instance
	 */
	AbstractDriver driver;
	
	/**
	 * Maximum Number of Events to be fired
	 */
	public static boolean terminated = false;
	
	public static long millisec = 0;
	/**
	 * Constructor
	 * 
	 * @param MAX_EVENTS Maximum Number of Events to be fired
	 */
	public TestingTimeBasedTerminationCriterion(long millisec) {
		super();
		TestingTimeBasedTerminationCriterion.millisec = millisec;
		TestingTimeBasedTerminationCriterion.terminated = false;
	}
	
	/* (non-Javadoc)
	 * @see it.unina.android.ripper.termination.TerminationCriterion#init(it.unina.android.ripper.driver.AbstractDriver)
	 */
	@Override
	public void init(AbstractDriver driver) {
		this.driver=driver;
	}

	/* (non-Javadoc)
	 * @see it.unina.android.ripper.termination.TerminationCriterion#check()
	 */
	@Override
	public boolean check() {
		System.out.println("Testing Time = "+driver.getTestingTime());
		return (this.driver.getTestingTime() >= millisec);
	}

}
