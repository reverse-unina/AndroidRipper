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

package it.unina.android.ripper.net;

import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;

import it.unina.android.ripper.driver.exception.RipperException;
import it.unina.android.ripper.driver.exception.RipperRuntimeException;
import it.unina.android.shared.ripper.model.transition.Event;
import it.unina.android.shared.ripper.model.transition.Input;
import it.unina.android.shared.ripper.net.Message;
import it.unina.android.shared.ripper.net.MessageType;
import it.unina.android.shared.ripper_service.net.packer.MessagePacker;

/**
 * Handles the communication with AndroidRipperService running on the emulator.
 * 
 * @author Nicola Amatucci - REvERSE
 *
 */
public class RipperServiceSocket {
	/**
	 * Log Tag
	 */
	public static final String TAG = "RipperServiceSocket";
	
	/**
	 * Socket Read Timeout
	 */
	public static int READ_TIMEOUT = 20000;
	
	/**
	 * AndroidRipperService host name
	 */
	String host;
	
	/**
	 * AndroidRipperService port
	 */
	int port;
	
	/**
	 * Socket Instance
	 */
	Socket socket = null;
	
	/**
	 * Connection Status
	 */
	boolean connected = false;	
	
	/**
	 * Current Message index (for message sequence verification)
	 */
	long curIndex = 0;
	
	/**
	 * Constructor
	 * 
	 * @param host AndroidRipperService host name
	 * @param port AndroidRipperService port
	 */
	public RipperServiceSocket(String host, int port)
	{
		super();
		if (host != null) {
			this.host = new String(host);
			this.port = port;
		} else {
			throw new RipperRuntimeException(RipperServiceSocket.class, "constructor", "Cannot connect to the device socket (null)!");
		}
	}
	
	/**
	 * Concatenate two array of bytes
	 * 
	 * @param a
	 * @param b
	 * @return
	 */
	private byte[] concatenateByteArrays(byte[] a, byte[] b) {
	    byte[] result = new byte[a.length + b.length]; 
	    System.arraycopy(a, 0, result, 0, a.length); 
	    System.arraycopy(b, 0, result, a.length, b.length); 
	    
	    a = null;
	    b = null;
	    
	    return result;
	}
	
	/**
	 * Connect to AndroidRipperService
	 * @throws RipperException 
	 * 
	 */
	public void connect() throws RipperException
	{
		try {
			this.socket = new Socket(host, port);
			connected = true;
		} catch (IOException e) {
			throw new RipperException(RipperServiceSocket.class, "connect", "Cannot connect to the device socket!");
		}
	}
	
	/**
	 * Connection Status
	 * 
	 * @return
	 */
	public boolean isConnected()
	{
		return this.connected;
	}
	
	public boolean checkSocketConnected() {
		return (this.socket != null && this.socket.isConnected());
	}
	
	/**
	 * Disconnect from AndroidRipperService
	 */
	public void disconnect()
	{
		if (checkSocketConnected())
				try{ this.socket.close(); } catch(Throwable t) { System.out.println("RipperServiceSocket.disconnect(): " + t.getMessage()); }
		
		connected = false;
	}
	
	/**
	 * Low Level Send Messages to AndroidRipperService
	 * 
	 * @param message
	 * @throws IOException
	 */
	private void sendBytes(byte[] message) throws IOException {
		this.socket.getOutputStream().write(message);
		this.socket.getOutputStream().flush();
	}
	
	/**
	 * Low Level Read Messages from AndroidRipperService
	 * 
	 * @param timeout Read Timeout
	 * @param bigBuffer Use a big Buffer?
	 * @return read bytes
	 * @throws IOException
	 */
	private byte[] readBytes(int timeout, boolean bigBuffer) throws IOException
	{
		if (checkSocketConnected() == false)
			return null;
		
		if(bigBuffer) {
			this.socket.setSoTimeout(timeout);
			byte[] buffer = this.readBytesBigBuffer();
			this.socket.setSoTimeout(0);
			return buffer;
		} else {
			this.socket.setSoTimeout(timeout);
			byte[] buffer = this.readBytes();
			this.socket.setSoTimeout(0);
			return buffer;			
		}
	}
	
	/**
	 * Low Level Read Messages from AndroidRipperService without timeout
	 * 
	 * @param bigBuffer Use a big Buffer?
	 * @return read bytes
	 * @throws IOException
	 */
	private byte[] readBytesNoTimeout(boolean bigBuffer) throws IOException
	{
		if (checkSocketConnected() == false)
			return null;

		if(bigBuffer) {
			byte[] buffer = this.readBytesBigBuffer();
			return buffer;
		} else {
			byte[] buffer = this.readBytes();
			return buffer;			
		}
	}

	/**
	 * readBytes() base function
	 * 
	 * @return read bytes
	 * @throws IOException
	 */
	private byte[] readBytes() throws IOException
	{
		if (checkSocketConnected() == false)
			return null;

		byte[] buffer = new byte[10240];
		
		int c = 0;
		int i = 0;
		while((c = this.socket.getInputStream().read()) != 16 && c != -1)
			buffer[i++] = (byte)c;

		//connection closed
		if (c == -1) {
			buffer = null;
			return null;
		}
		
		byte[] buffer_trim = new byte[i];
		for (int j = 0; j < i; j++)
			buffer_trim[j] = buffer[j];
		
		buffer = null;
		
		return buffer_trim;
	}
	
	/**
	 * readBytesBigBuffer() base function
	 * 
	 * @return read bytes
	 * @throws IOException
	 */
	private byte[] readBytesBigBuffer() throws IOException
	{
		if (checkSocketConnected() == false)
			return null;

		byte[] buffer = new byte[1024000];
		
		int c = 0;
		int i = 0;
		while((c = this.socket.getInputStream().read()) != 16 && c != -1)
			buffer[i++] = (byte)c;

		//connection closed
		if (c == -1) {
			buffer = null;
			return null;
		}
		
		byte[] buffer_trim = new byte[i];
		for (int j = 0; j < i; j++)
			buffer_trim[j] = buffer[j];
		
		buffer = null;
		
		return buffer_trim;
	}
	
	/**
	 * High Level Send Messages to AndroidRipperService
	 * 
	 * @param msg Message to Send
	 */
	public void sendMessage(Message msg)
	{
		if (checkSocketConnected() == false)
			return;

		msg.addParameter("index", Long.toString(++curIndex));
		
		byte[] packed = MessagePacker.pack(msg);
		
		if (packed != null)
		{
			byte[] EOF = { 16 };
			byte[] message = concatenateByteArrays(packed, EOF);
			
			try {
				this.sendBytes(message);
			} catch (Throwable e) {
				// TODO Auto-generated catch block
				//System.out.println(e.getMessage());
			}
			
			message = null;
		}
		else
		{
			throw new RuntimeException("RipperServiceSocket: send overflow");
		}
	}
	
	/**
	 * High Level Read Messages from AndroidRipperService
	 * 
	 * @return Read Message
	 * @throws SocketException
	 */
	public Message readMessage() throws SocketException
	{
		//return readMessage(0);
		
		try {
			byte[] buffer = this.readBytes();
			
			if (buffer != null)
			{
//				System.out.println();
//				System.out.println(new String("rcv = \n" + new String(buffer)));
//				System.out.println();
				
				Message msg = new Message(MessagePacker.unpack(buffer));
//				System.out.println("" + msg.getType());
				
				try
				{
					String index = msg.getParameterValue("index");
					long indexLong = Long.parseLong(index);
					
					if (indexLong >= curIndex)
						return msg;
				}
				catch(Throwable t) {
				}
			}
		} catch (java.net.SocketException se) {
			throw se;
		} catch (Throwable e) {
			// TODO Auto-generated catch block
			System.out.println(e.getMessage());
		}
		
		return null;		
	}
	
	/**
	 * High Level Read Messages from AndroidRipperService without timeout
	 * 
	 * @param bigBuffer Use a big Buffer?
	 * @return Read Message
	 * @throws SocketException
	 */
	public Message readMessageNoTimeout(boolean bigBuffer) throws SocketException {

		try {
			byte[] buffer = this.readBytesNoTimeout(bigBuffer);

			if (buffer != null) {
//				System.out.println();
//				System.out.println(new String("rcv = \n" + new String(buffer)));
//				System.out.println();

				Message msg = new Message(MessagePacker.unpack(buffer));
//				System.out.println("" + msg.getType());

				try {
					String index = msg.getParameterValue("index");
					long indexLong = Long.parseLong(index);

					if (indexLong >= curIndex) {
						curIndex = indexLong;
						return msg;
					}
				} catch (Throwable t) {
				}

			}
		} catch (java.net.SocketException se) {
			throw se;
		} catch (java.net.SocketTimeoutException e) {

		} catch (Throwable e) {
			// TODO Auto-generated catch block
			System.out.println(e.getMessage());
		}

		return null;
	}
	
	/**
	 * High Level Read Messages from AndroidRipperService
	 * 
	 * @param timeout Read Timeout
	 * @param bigBuffer Use a big Buffer?
	 * @return Read Message
	 * @throws SocketException
	 */
	public Message readMessage(int timeout, boolean bigBuffer) throws SocketException
	{
		
		try {
			byte[] buffer = this.readBytes(timeout, bigBuffer);
			
			if (buffer != null)
			{
//				System.out.println();
//				System.out.println(new String("rcv = \n" + new String(buffer)));
//				System.out.println();
				
				Message msg = new Message(MessagePacker.unpack(buffer));
//				System.out.println("" + msg.getType());

				try
				{
					String index = msg.getParameterValue("index");
					long indexLong = Long.parseLong(index);
					
					if (indexLong >= curIndex) {
						curIndex = indexLong; 
						return msg;
					}
				}
				catch(Throwable t) {
					System.out.println("RipperServiceSocket.readMessage().t: "+t.getMessage());
				}
				
			}
		} catch (java.net.SocketException se) {
			throw se;
		} catch (java.net.SocketTimeoutException e) {
			
		} catch (Throwable e) {
			// TODO Auto-generated catch block
			//System.out.println(e.getMessage());
			System.out.println("RipperServiceSocket.readMessage().e: "+e.getMessage());
		}
		
		return null;		
	}
	
	/**
	 * Check if AndroidRipperService is responding
	 * 
	 * @return
	 * @throws SocketException
	 */
	public boolean isAlive() throws SocketException
	{
		Message m = null;
		do {
			this.sendMessage(Message.getPingMessage());
			m = this.readMessage(5000, false);
		} while (m != null && m.getType().equals(MessageType.WAIT_APP_READY));
			
		return (m != null);
	}
	
	/**
	 * Send a PING Message
	 * 
	 * @return
	 * @throws SocketException
	 */
	public Message ping() throws SocketException
	{
		this.sendMessage(Message.getPingMessage());
		Message m = this.readMessage(2000, false);
		
		return m;
	}
	
	/**
	 * Send a DESCRIBE Message.
	 * 
	 * Call describe(MAX_RETRY = 5)
	 * 
	 * @return
	 * @throws SocketException
	 */
	public String describe() throws SocketException
	{
		String desc = null;
		
		try {
			desc = describe(3);
		} catch (RuntimeException rex) {
			
			if( desc == null ){
				//single retry
				desc = describe(3);
			}
			
		}
		
		return desc;
	}
	
	/**
	 * Send a DESCRIBE Message.
	 * 
	 * @param MAX_READ_RETRY Maximum number of retry (read timeouts)
	 * @return
	 * @throws SocketException
	 */
	public String describe(int MAX_READ_RETRY) throws SocketException
	{
		Message describeMsg = null;
		this.sendMessage(Message.getDescribeMessage());

		int describeCnt = 0;
		do {			
			describeMsg = this.readMessage(1000, true);
			
			if (describeMsg != null && describeMsg.getType().equals(MessageType.DESCRIBE_MESSAGE) == false)
			{
				//System.out.println("Message != DSC -> " + describeMsg.getType());
				continue;
			}
			
			if (describeMsg != null && describeMsg.getParameterValue("wait") != null)
			{
				try { Thread.sleep(1000); } catch(Throwable tr) {}
				
				this.sendMessage(Message.getDescribeMessage());
				
				continue;
			}
			else if (describeMsg != null && describeMsg.getParameterValue("xml") != null)
			{
				String xml = describeMsg.getParameterValue("xml");
				if (xml != null && xml.length() > 45)
				{
//					if (xml != null)
//						System.out.println(xml);
					
					return xml;
				}
				
				this.sendMessage(Message.getDescribeMessage());
				
//				if (xml != null)
//					System.out.println(xml);
			}
			else
			{
				try { Thread.sleep(1000); } catch(Throwable tr) {}
				
				if (describeCnt++ > MAX_READ_RETRY)
					throw new RuntimeException("RipperServiceSocket: overflow");
				
				//System.out.println("Describe retry " + describeCnt);
				//this.sendMessage(Message.getDescribeMessage());

				continue;
			}
		} while(true);		
	}
	
	/**
	 * Send an EVENT Message
	 * 
	 * @param evt Event instance to send
	 */
	public void sendEvent(Event evt)
	{
		String evtUID = Long.toString(evt.getEventUID());
		
//		if (evt.getInputs() != null && evt.getInputs().size() > 0) {
//			
//			long i = 0;
//			for(Input input : evt.getInputs()) {
//				String count = Long.toString(i++);
//				this.sendMessage(Message.getInputMessage(Integer.toString(input.getWidget().getId()), input.getInputType(), input.getValue(), evtUID, count));
//			}
//			
//		}
		
		this.sendInputs(evtUID, evt.getInputs());
		
		if (evt.getWidget() != null)
			this.sendMessage(Message.getEventMessage((evt.getWidget().getId()!=null)?Integer.toString(evt.getWidget().getId()):"-1", Integer.toString(evt.getWidget().getIndex()), evt.getWidget().getName(), evt.getWidget().getSimpleType(), evt.getInteraction(), evt.getValue(), evtUID));
		else
			this.sendMessage(Message.getEventMessage(null, null, null, null, evt.getInteraction(), evt.getValue(), evtUID));
	}
	
	/**
	 * Send an EVENT Message
	 * 
	 * @param evt Event instance to send
	 */
	public void sendInputs(String evtUID, ArrayList<Input> inputs)
	{
		if (inputs != null && inputs.size() > 0) {
			
			long i = 0;
			for(Input input : inputs) {
				String count = Long.toString(i++);
				this.sendMessage(Message.getInputMessage(Integer.toString(input.getWidget().getId()), Integer.toString(input.getWidget().getIndex()), input.getWidget().getName(), input.getWidget().getSimpleType(), input.getInputType(), input.getValue(), evtUID, count));
			}
			
		}
	}
	
	/**
	 * Returns the Socket Object
	 * 
	 * @return Socket
	 */
	public Socket getSocket() {
		return this.socket;
	}
}
