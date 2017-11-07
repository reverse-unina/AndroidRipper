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

package it.unina.android.shared.ripper.constants;

/**
 * String Constants representing Output Model TAGs
 * 
 * @author Nicola Amatucci - REvERSE
 *
 */
public class XMLModelTags {
	public static final String ROOT = "root";
	
	public static final String ACTIVITY = "a";	
	public static final String ACTIVITY_TITLE = "ti";
	public static final String ACTIVITY_CLASS = "cl";
	public static final String ACTIVITY_NAME = "na";
	public static final String ACTIVITY_MENU = "me";
	public static final String ACTIVITY_HANDLES_KEYPRESS = "ky";
	public static final String ACTIVITY_HANDLES_LONG_KEYPRESS = "lky";
	public static final String ACTIVITY_IS_TABACTIVITY = "tb";
	public static final String ACTIVITY_TABS_COUNT = "tb_c";
	public static final String ACTIVITY_ID = "id";
	public static final String ACTIVITY_UID = "uid";
	public static final String ACTIVITY_IS_ROOT_ACTIVITY = "root";	
	
	public static final String LISTENER = "ls";
	public static final String LISTENER_CLASS = "c";
	public static final String LISTENER_PRESENT = "p";
	
	public static final String SUPPORTED_EVENT = "se";
	public static final String SUPPORTED_EVENT_TYPE = "ty";
	
	public static final String WIDGET = "w";
	public static final String WIDGET_ID = "id";
	public static final String WIDGET_INDEX = "in";
	public static final String WIDGET_CLASS = "cl";
	public static final String WIDGET_SIMPLE_TYPE = "st";
	
	public static final String WIDGET_TEXT_TYPE = "tt";
	public static final String WIDGET_NAME = "na";
	public static final String WIDGET_ENABLED = "en";
	public static final String WIDGET_VISIBLE = "vi";
	
	public static final String WIDGET_VALUE = "va";
	public static final String WIDGET_COUNT = "co";
	
	public static final String WIDGET_R_ID = "rid";
		
	public static final String EVENT = "ev";
	public static final String EVENT_INTERACTION = "in";
	public static final String EVENT_VALUE = "va";
	public static final String EVENT_UID = "uid";
	
	public static final String INPUT = "in";
	public static final String INPUT_TYPE = "ty";
	public static final String INPUT_VALUE = "va";
	
	public static final String MANUAL_SEQUENCE = "ms";
	public static final String EXECUTED_MANUAL_SEQUENCE = "ex_ms";
	public static final String MANUAL_SEQUENCE_STEP = "st";
	
	public static final String TASK = "ta";
	
	public static final String STEP = "st";
	public static final String FINAL_ACTIVITY = "fa";
	
	public static final String EXTRACTED_EVENTS = "ee";
	public static final String FIRED_EVENT = "fe";
	
	public static final String FIRST_STEP = "bt";
	
	public static final String DESCRIPTION = "ad";
}
