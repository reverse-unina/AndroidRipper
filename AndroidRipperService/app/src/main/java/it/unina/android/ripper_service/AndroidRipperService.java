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

package it.unina.android.ripper_service;

import java.util.Map;

import android.app.ActivityManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.util.Log;
import it.unina.android.ripper_service.net.Server;

/**
 * Android Service
 * 
 * - Proxy between AndroidRipperDriver and AndroidRipperTestCase
 * - Can be used to perform privileged operations 
 * 
 * @author Nicola Amatucci - REvERSE
 *
 */
public class AndroidRipperService extends Service {
	/**
	 * Remote Callbacks
	 */
	RemoteCallbackList<IAnrdoidRipperServiceCallback> mCallbacks; // = new
																	// RemoteCallbackList<IAnrdoidRipperServiceCallback>();

	/**
	 * Binder
	 */
	AndroidRipperServiceStub mBinder; // = new AndroidRipperServiceStub(this,
										// this.getApplicationContext());

	/**
	 * TCP Server Instance
	 */
	Server server = null;

	@Override
	public void onCreate() {
		super.onCreate();
		Log.v("AndroidRipperService", "onCreate()");

		mCallbacks = new RemoteCallbackList<IAnrdoidRipperServiceCallback>();
		ActivityManager activityManager = (ActivityManager) this.getSystemService(Context.ACTIVITY_SERVICE);
		mBinder = new AndroidRipperServiceStub(this, activityManager);

		server = new Server(this.mHandler);
		server.startServer();
	}

	@Override
	public IBinder onBind(Intent intent) {
		Log.v("AndroidRipperService", "onBind()");
		return mBinder;
	}

	@Override
	public void onDestroy() {
		Log.v("AndroidRipperService", "onDestroy()");
		server.stopServer();
		super.onDestroy();
	}

	/**
	 * Send a Message via TCP. Used by AndroidRipperTestCase.
	 * 
	 * @param message Message
	 */
	public void send(Map<String, String> message) {
		Log.v("AndroidRipperService", "send()");
		server.send(message);
	}

	/**
	 * Register a CallBack. Used by AndroidRipperTestCase.
	 * 
	 * @param cb
	 */
	synchronized public void register(IAnrdoidRipperServiceCallback cb) {
		if (cb != null) {
			mCallbacks.register(cb);
			Log.v("AndroidRipperService", "register()");
		} else {
			Log.v("AndroidRipperService", "register() fail");
		}
	}

	/**
	 * Unregister a CallBack. Used by AndroidRipperTestCase.
	 * 
	 * @param cb
	 */
	synchronized public void unregister(IAnrdoidRipperServiceCallback cb) {
		if (cb != null) {
			mCallbacks.unregister(cb);
			Log.v("AndroidRipperService", "unregister()");
		} else {
			Log.v("AndroidRipperService", "unregister() fail");
		}
	}

	/**
	 * Unregister all CallBacks.
	 * 
	 * @param cb
	 */
	synchronized public void unregisterAll() {
		mCallbacks.kill();
		mCallbacks = new RemoteCallbackList<IAnrdoidRipperServiceCallback>();
	}

	/**
	 * Forward a message to AndroidRipperTestCase
	 * 
	 * @param message Message
	 */
	synchronized protected void broadcast(Map<String, String> message) {
		Log.v("AndroidRipperService", "broadcast()");
		final int N = mCallbacks.beginBroadcast();
		for (int i = 0; i < N; i++) {
			try {
				mCallbacks.getBroadcastItem(i).receive(message);
			} catch (RemoteException e) {
				// RemoteCallbackList will take care of removing dead objects
			}
		}
		mCallbacks.finishBroadcast();
		Log.v("AndroidRipperService", "broadcast() done");
	}

	/**
	 * Handles AndroidRipperDriver messages
	 */
	private final Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			
			case Server.MSG_TYPE_NOTIFY_RECEIVED:
				Log.v("AndroidRipperService", "mHandler.handleMessage() RECIVED");
				broadcast((Map<String, String>) msg.obj);
				break;

			case Server.MSG_TYPE_NOTIFY_DISCONNECTION:
				Log.v("AndroidRipperService", "mHandler.handleMessage() DISCONNECTED");
				unregisterAll();
				break;
			}
			
		}
	};
}
