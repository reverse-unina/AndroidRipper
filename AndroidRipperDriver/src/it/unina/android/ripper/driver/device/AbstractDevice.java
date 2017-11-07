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

package it.unina.android.ripper.driver.device;

/**
 * Abstracts an Android Device
 * 
 * @author Nicola Amatucci - REvERSE
 *
 */
import it.unina.android.ripper.tools.actions.Actions;

public abstract class AbstractDevice {
	
	protected boolean deviceStarted = false;
	protected String name = null;
	protected boolean needsSu = false;
	protected boolean rooted = false;
	
	public AbstractDevice(String name) {
		super();
		this.name = name;
		this.needsSu = false;
		this.rooted = false;
	}
	
	public AbstractDevice(String name, boolean needsSu) {
		super();
		this.name = name;
		this.needsSu = needsSu;
	}
	
	public void waitForDevice() {
		System.out.println("Waiting for Device...");
		Actions.waitForDevice();
		System.out.println("Device online!");
	}

	public boolean isStarted() {
		return deviceStarted;
	}

	public void setStarted(boolean b) {
		this.deviceStarted = b;
	}
	
	public void unlockDevice() {
		Actions.unlockDevice();
	}

	public String getName() {
		return name;
	}

	public boolean isVirtualDevice() {
		return false;
	}
	
	public boolean needsSu() {
		return needsSu;
	}

	public void setNeedsSu(boolean needsSu) {
		this.needsSu = needsSu;
	}
	
	public boolean isRooted() {
		return rooted;
	}

	public void setRooted(boolean rooted) {
		this.rooted = rooted;
	}

	public abstract void start();
	public abstract void stop();

	public abstract String getIpAddress();

}
