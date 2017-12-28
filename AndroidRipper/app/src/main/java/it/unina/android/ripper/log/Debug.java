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

package it.unina.android.ripper.log;

import android.util.Log;

/**
 * Debug Helper Class
 * 
 * @author Nicola Amatucci - REvERSE
 *
 */
public class Debug
{	
	/**
	 * LOG_LEVEL_NONE
	 */
	public static final int LOG_LEVEL_NONE = 0;
	
	/**
	 * LOG_LEVEL_INFO
	 */
	public static final int LOG_LEVEL_INFO = 1;
	
	/**
	 * LOG_LEVEL_ALL
	 */
	public static final int LOG_LEVEL_ALL = 2;
	
	/**
	 * Current LOG_LEVEL
	 */
	public static int LOG_LEVEL = LOG_LEVEL_ALL;
	
	/**
	 * Debug TAG
	 */
	public static String TAG = "AndroidRipper";
	
	/**
	 * Generic log function (LOG_LEVEL_ALL)
	 * 
	 * @param msg Message
	 */
	public static void log(String msg) {
		if (LOG_LEVEL >= LOG_LEVEL_ALL)
			Log.v(TAG, msg);
	}
	
	/**
	 * Log function (LOG_LEVEL_INFO)
	 * 
	 * @param msg Message
	 */
	public static void info(String msg) {
		if (LOG_LEVEL >= LOG_LEVEL_INFO)
			Log.i(TAG, msg);
	}
	
	/**
	 * Generic log function (LOG_LEVEL_ALL)
	 * 
	 * @param o Object
	 * @param msg Message
	 */
	public static void log(Object o, String msg) {
		log("["+o.getClass().getSimpleName()+"] " + msg);
	}
	
	/**
	 * Log function (LOG_LEVEL_INFO)
	 * 
	 * @param o Object
	 * @param msg Message
	 */
	public static void info(Object o, String msg) {
		info("["+o.getClass().getSimpleName()+"] " + msg);
	}

}
