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
 * Android Virtual Device Instance
 * 
 * @author Nicola Amatucci - REvERSE
 *
 */
import it.unina.android.ripper.tools.actions.Actions;

public class AndroidVirtualDevice extends AbstractDevice {

	int port = 5554;
	
	public AndroidVirtualDevice(String name, int port) {
		super(name);
		this.name = name;
		this.port = port;
		this.needsSu = false;
		this.rooted = true;
		
		Actions.DEVICE = getName();
	}

	@Override
	public void start() {
		System.out.println("Start AVD...");
		Actions.startEmulatorNoSnapshotLoadSave(name, port);
	}

	@Override
	public void stop() {
		System.out.println("Shutdown AVD...");
		Actions.killEmulator();
		Actions.waitDeviceClosed();
	}

	@Override
	public String getName() {
		return "emulator-"+port;
	}

	@Override
	public String getIpAddress() {
		return "localhost";
	}
	
	@Override
	public boolean isVirtualDevice() {
		return true;
	}
}
