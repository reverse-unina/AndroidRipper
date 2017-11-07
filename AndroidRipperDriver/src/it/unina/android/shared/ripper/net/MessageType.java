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

package it.unina.android.shared.ripper.net;

/**
 * String Constants representing Message Types
 * 
 * @author Nicola Amatucci - REvERSE
 *
 */
public class MessageType {
	public static final String CONFIG_MESSAGE = "CFG";
	public static final String EVENT_MESSAGE = "EVT";
	public static final String INPUT_MESSAGE = "INP";
	public static final String DESCRIBE_MESSAGE = "DSC";
	public static final String END_MESSAGE = "END";
	public static final String ACK_MESSAGE = "ACK";
	public static final String NACK_MESSAGE = "NACK";
	public static final String PING_MESSAGE = "PING";
	public static final String PONG_MESSAGE = "PONG";
	public static final String FAIL_MESSAGE = "FAIL";
	public static final String CRASH_MESSAGE = "CRASH";
	public static final String COVERAGE_MESSAGE = "COVER";
	public static final String HOME_MESSAGE = "HOME";
	public static final String USER_TEST_MESSAGE = "TEST";
	public static final String TOTAL_NUMBER_OF_TEST_CASE_MESSAGE = "NUMTC";
	public static final String EXECUTE_TEST_CASE_MESSAGE = "ETC";
	public static final String WAIT_APP_READY = "WAIT_APP_READY";
}
