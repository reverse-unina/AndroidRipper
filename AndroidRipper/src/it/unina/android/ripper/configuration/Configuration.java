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

package it.unina.android.ripper.configuration;

import it.unina.android.ripper.RipperTestCase;

/**
 * Configuration
 * 
 * @author Nicola Amatucci - REvERSE
 *
 */
public class Configuration implements IConfiguration
{
	/**
	 * AUT Package
	 */
	public static String PACKAGE_NAME = "%%_PACKAGE_NAME_%%";
	
	/**
	 * AUT Class Name
	 */
	public static String CLASS_NAME = "%%_CLASS_NAME_%%";
	
	/**
	 * Ripper Package
	 */
	public static String RIPPER_PACKAGE = RipperTestCase.class.getPackage().getName();
	
	/**
	 * Sleep time after an event
	 */
	public static int SLEEP_AFTER_EVENT = 1000;
	
	/**
	 * Sleep time on throbber
	 */
	public static int SLEEP_ON_THROBBER = 3000;
	
	/**
	 * Sleep time on restart
	 */
	public static int SLEEP_AFTER_RESTART = 1000;
	
	/**
	 * Sleep time after task
	 */
	public static int SLEEP_AFTER_TASK = 1000;
	
	/**
	 * Ripper Configuration File
	 */
	public final static String CONFIGURATION_FILE = "configuration.xml";	
	
	/**
	 * Aut Class Object
	 */
	public static Class<?> autActivityClass;
	
	/**
	 * Load configuration
	 */
	static {
		//Prefs.setMainNode(RIPPER_PACKAGE);
		//Prefs.updateMainNode();

		try {
			autActivityClass = Class.forName(CLASS_NAME);
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
	}
}
