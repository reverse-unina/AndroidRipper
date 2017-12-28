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

import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.os.Build;
import android.os.RemoteException;
import it.unina.android.ripper_service.util.ProcessNameGrabber;

/**
 * AndroidRipperService Stub
 * 
 * @author Nicola Amatucci - REvERSE
 *
 */
public class AndroidRipperServiceStub extends IAndroidRipperService.Stub {

	AndroidRipperService service = null;
	ActivityManager mActivityManager;

	public AndroidRipperServiceStub(AndroidRipperService service, ActivityManager activityManager) {
		this.service = service;
		this.mActivityManager = activityManager;
	}

	@Override
	public void send(Map message) throws RemoteException {
		this.service.send((Map<String, String>) message);
	}

	@Override
	public void register(IAnrdoidRipperServiceCallback cb) throws RemoteException {
		this.service.register(cb);

	}

	@Override
	public void unregister(IAnrdoidRipperServiceCallback cb) throws RemoteException {
		this.service.unregister(cb);
	}

	@SuppressLint("NewApi")
	@Override
	public String getForegroundProcess() throws RemoteException {
		if (Build.VERSION.SDK_INT < 21) {
			List<ActivityManager.RunningTaskInfo> taskInfo = this.mActivityManager.getRunningTasks(1);
			ComponentName componentInfo = taskInfo.get(0).topActivity;
			return componentInfo.getPackageName();
		} else {
//			String process = ProcessNameGrabber.getForegroundApp();
//			if (process != null) {
//				return process.replaceAll("[^a-zA-Z0-9]", "");
//			}
			return ProcessNameGrabber.getForegroundApp2();
		}
	}

}
