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

package it.unina.android.ripper_service.net;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;

import android.os.Handler;
import android.os.Message;
import android.util.Log;
import it.unina.android.ripper_service.net.packer.MessagePacker;

/**
 * 
 * @author Nicola Amatucci - REvERSE
 *
 */
public class Server extends Thread {
	
	/**
	 * Current Message Index
	 */
	long curIndex = 0;

	/**
	 * Server Port
	 */
	public static int PORT = 18888;
	
	/**
	 * Server Status - Running
	 */
	private boolean running = true;
	
	/**
	 * Server Status - Connected
	 */
	private boolean connected = false;

	/**
	 * Server Socket
	 */
	ServerSocket serverSocket = null;
	
	/**
	 * Socket
	 */
	Socket socket = null;
	
	/**
	 * Socket Data Input Stream
	 */
	DataInputStream dataInputStream = null;
	
	/**
	 * Socket Data Output Stream
	 */
	DataOutputStream dataOutputStream = null;

	/**
	 * Android Service Handler
	 */
	Handler handler = null;

	/**
	 * Constructor
	 * 
	 * @param handler Android Service Handler
	 */
	public Server(Handler handler) {
		super();
		this.handler = handler;
	}

	/**
	 * Start the Server
	 */
	public void startServer() {
		try {
			Log.v("SERVER", "Starting ServerSocket on PORT " + PORT);
			serverSocket = new ServerSocket(PORT);
			Log.v("SERVER", "Started ServerSocket on PORT " + PORT);
			connected = true;
		} catch (IOException e) {
			throw new RuntimeException("Error in Server Socket: " + e.getMessage());
		}

		this.start();
	}

	/**
	 * Stop the Server
	 */
	public void stopServer() {
		Log.v("SERVER", "Stopping ServerSocket on PORT " + PORT);
		this.running = false;

		if (socket != null)
			try {
				socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}

		if (dataInputStream != null)
			try {
				dataInputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}

		if (dataOutputStream != null)
			try {
				dataOutputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}

		this.connected = false;
		Log.v("SERVER", "Stopped ServerSocket on PORT " + PORT);
	}

	@Override
	public void run() {

		try {
			while (running) {
				Log.v("SERVER", "Ready to accept connections on port: " + PORT);
				socket = serverSocket.accept();

				dataInputStream = new DataInputStream(socket.getInputStream());
				dataOutputStream = new DataOutputStream(socket.getOutputStream());

				while (running) {
					Log.v("", "ready");
					byte[] buffer = new byte[24000];
					int c = 0;
					int i = 0;
					while ((c = dataInputStream.read()) != 16 && c != -1) {
						// Log.v("", "read " + c);
						buffer[i++] = (byte) c;
					}

					// connessione caduta
					if (c == -1) {
						Log.v("SERVER", "ServerSocket on PORT " + PORT + ": offline");
						notifyDisconnect();
						break;
					}

					byte[] buffer_trim = new byte[i];
					for (int j = 0; j < i; j++)
						buffer_trim[j] = buffer[j];

					Map<String, String> message = MessagePacker.unpack(buffer_trim);

					if (message != null) {
						String s = message.get("type");
						Log.v("RipperService", s);

						long seqNum = Long.parseLong(message.get("index"));
						if (seqNum >= curIndex) {
							curIndex = seqNum;
							this.notifyReceived(message);
						}
					}

				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}

	/**
	 * Notify Message Received to AndroidRipperTestCase
	 * 
	 * @param line Message Received
	 */
	public void notifyReceived(Map<String, String> line) {
		try {
			Message m = new Message();
			m.obj = line;
			m.what = MSG_TYPE_NOTIFY_RECEIVED;
			handler.handleMessage(m);
		} catch (Throwable tr) {
			tr.printStackTrace();
		}
	}

	/**
	 * Notify Disconnection to AndroidRipperTestCase
	 */
	public void notifyDisconnect() {
		try {
			Message m = new Message();
			m.what = MSG_TYPE_NOTIFY_DISCONNECTION;
			handler.handleMessage(m);
		} catch (Throwable tr) {
			tr.printStackTrace();
		}
	}

	/**
	 * Send Message to AndroidRipperDriver
	 * 
	 * @param message Message
	 */
	public void send(Map<String, String> message) {
		message.put("index", Long.toString(++curIndex));

		byte[] packed = MessagePacker.pack(message);

		if (packed != null) {
			byte[] EOF = { 16 };
			byte[] msg = concatenateByteArrays(packed, EOF);

			try {
				socket.getOutputStream().write(msg);
				socket.getOutputStream().flush();
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		} else {
			throw new RuntimeException("Send failed!");
		}
	}

	/**
	 * Concatenate two byte array
	 * 
	 * @param a
	 * @param b
	 * @return
	 */
	byte[] concatenateByteArrays(byte[] a, byte[] b) {
		byte[] result = new byte[a.length + b.length];
		System.arraycopy(a, 0, result, 0, a.length);
		System.arraycopy(b, 0, result, a.length, b.length);
		return result;
	}

	/**
	 * notifyReceived()
	 */
	public static final int MSG_TYPE_NOTIFY_RECEIVED = 1;
	
	/**
	 * notifyDisconnect()
	 */
	public static final int MSG_TYPE_NOTIFY_DISCONNECTION = 0;
}
