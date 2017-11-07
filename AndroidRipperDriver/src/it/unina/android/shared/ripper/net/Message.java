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

import java.util.HashMap;
import java.util.Map;

/**
 * Message
 * 
 * @author Nicola Amatucci - REvERSE
 *
 */
public class Message extends HashMap<String, String>
{
	private static final long serialVersionUID = 2199912L;

	/**
	 * @return DESCRIBE_MESSAGE
	 */
	public static Message getDescribeMessage()
	{
		return new Message(MessageType.DESCRIBE_MESSAGE);
	}
	
	/**
	 * @return ACK_MESSAGE
	 */
	public static Message getAckMessage()
	{
		return new Message(MessageType.ACK_MESSAGE);
	}
	
	/**
	 * @return PING_MESSAGE
	 */
	public static Message getPingMessage()
	{
		return new Message(MessageType.PING_MESSAGE);
	}

	/**
	 * @return PONG_MESSAGE
	 */
	public static Message getPongMessage()
	{
		return new Message(MessageType.PONG_MESSAGE);
	}

	/**
	 * @return FAIL_MESSAGE
	 */
	public static Message getFailMessage()
	{
		return new Message(MessageType.FAIL_MESSAGE);
	}

	/**
	 * @return CRASH_MESSAGE
	 */
	public static Message getCrashMessage()
	{
		return new Message(MessageType.CRASH_MESSAGE);
	}

	/**
	 * @return NACK_MESSAGE
	 */
	public static Message getNAckMessage()
	{
		return new Message(MessageType.NACK_MESSAGE);
	}

	/* REGION TEST CASE EXECUTION */
	
	/**
	 * @param i Number of test cas
	 * @return USER_TEST_MESSAGE
	 */
	public static Message getUserTestMessage(String i)
	{
		Message msg = new Message(MessageType.USER_TEST_MESSAGE);
		msg.addParameter("test", i);
		return msg;
	}

	/**
	 * @return TOTAL_NUMBER_OF_TEST_CASE_MESSAGE
	 */
	public static Message getNumTestCaseMessage()
	{
		return new Message(MessageType.TOTAL_NUMBER_OF_TEST_CASE_MESSAGE);
	}
	
	/**
	 * @param string Test Case Runner
	 * @return EXECUTE_TEST_CASE_MESSAGE
	 */
	public static Message getExecuteTestCaseMessage(String string)
	{	
		Message msg = new Message(MessageType.EXECUTE_TEST_CASE_MESSAGE);
		msg.addParameter("runner", string);
		return msg;
	}
	
	/**
	 * @return EXECUTE_TEST_CASE_MESSAGE
	 */
	public static Message getExecuteTestCaseMessage()
	{
		Message msg = new Message(MessageType.EXECUTE_TEST_CASE_MESSAGE);
		return msg;
	}
	
	/* END REGION TEST CASE EXECUTION */
	
	/**
	 * @return END_MESSAGE
	 */
	public static Message getEndMessage()
	{
		return new Message(MessageType.END_MESSAGE);
	}

	/**
	 * @return EVENT_MESSAGE
	 */
	public static Message getEventMessage()
	{
		return new Message(MessageType.EVENT_MESSAGE);
	}

	/**
	 * @return WAIT_APP_READY
	 */
	public static Message getWaitForAppReadyMessage() {
		return new Message(MessageType.WAIT_APP_READY);
	}
	
	/**
	 * @param widgetId Widget id
	 * @param widgetIndex Widget Index
	 * @param widgetName Widget Name
	 * @param widgetType Widget Type
	 * @param eventType Event Type
	 * @param value Value Parameter for the event
	 * @return EVENT_MESSAGE
	 */
	public static Message getEventMessage(String widgetId, String widgetIndex, String widgetName, String widgetType, String eventType, String value, String eventUID)
	{
		Message msg = new Message(MessageType.EVENT_MESSAGE);
		
		msg.addParameter("widgetId", widgetId);
		msg.addParameter("widgetIndex", widgetIndex);
		msg.addParameter("widgetName", widgetName);
		msg.addParameter("widgetType", widgetType);
		msg.addParameter("eventType", eventType);
		msg.addParameter("value", value);
		msg.addParameter("uid", eventUID);
		
		return msg;
	}
	
	/**
	 * @return INPUT_MESSAGE
	 */
	public static Message getInputMessage()
	{
		return new Message(MessageType.INPUT_MESSAGE);
	}
	
	/**
	 * @param widgetId Widget id
	 * @param interactionType Interaction Type
	 * @param value Value for the input field
	 * @return INPUT_MESSAGE
	 */
	public static Message getInputMessage(String widgetId, String widgetIndex, String widgetName, String widgetType, String interactionType, String value, String eventUID, String count)
	{
		Message msg = new Message(MessageType.INPUT_MESSAGE);
		
		msg.addParameter("widgetId", widgetId);
		msg.addParameter("widgetIndex", widgetIndex);
		msg.addParameter("widgetName", widgetName);
		msg.addParameter("widgetType", widgetType);
		msg.addParameter("inputType", interactionType); //TODO: interactionType anche come chiave (anche nel client)
		msg.addParameter("value", value);
		msg.addParameter("eventUID", eventUID);
		msg.addParameter("count", count);
		
		return msg;
	}
	
	/**
	 * @return CONFIG_MESSAGE
	 */
	public static Message getConfigMessage()
	{
		return new Message(MessageType.CONFIG_MESSAGE);
	}

	/**
	 * Constructor - Construct a Message from a Map
	 * 
	 * @param message Map containing the message
	 */
	public Message(Map message)
	{
		super();
		super.putAll(message);
	}
	
	/**
	 * Constructor
	 */
	public Message()
	{
		super();
	}
	
	/**
	 * Constructor - Construct a Message of a specified Type
	 * 
	 * @param type message type
	 */
	public Message(String type)
	{
		super();
		this.setType(type);
	}
	
	/**
	 * Set message type
	 * 
	 * @param type message type
	 */
	public void setType(String type)
	{
		this.put(TYPE_KEY, type);
	}
	
	/**
	 * Set a parameter of the message
	 * 
	 * @param key parameter name
	 * @param value parameter value
	 */
	public void addParameter(String key, String value)
	{
		this.put(key, value);
	}
	
	/**
	 * Get the value of a parameter
	 * 
	 * @param key parameter name
	 * @return
	 */
	public String getParameterValue(String key)
	{
		return this.get(key);
	}
	
	/**
	 * Get Message Type
	 * 
	 * @return message type
	 */
	public String getType()
	{
		return this.get(TYPE_KEY);
	}
	
	/**
	 * Check message type
	 * 
	 * @param type message type
	 * @return
	 */
	public boolean isTypeOf(String type)
	{
		return (this.containsKey(TYPE_KEY) && this.get(TYPE_KEY) != null && this.get(TYPE_KEY).equals(type));		
	}
	
	/**
	 * Name of the parameter containing the type of the message
	 */
	private static final String TYPE_KEY = "type";
}
