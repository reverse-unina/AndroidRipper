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

package it.unina.android.ripper.installer;

public class OSSpecific {

	private static String OS = System.getProperty("os.name").toLowerCase();
	 
	public static String getShellCommand() {
 
		//System.out.println(OS);
 
		if (isWindows()) {
			return "cmd /C ";
		} else if (isMac()) {
			//TODO
		} else if (isUnix()) {
			return "";
		} else if (isSolaris()) {
			//TODO
		}
			
		//return "sh -c";
		return "";
	}
	
	public static String[] getAndroidListAVDCommand() {
		 
		//System.out.println(OS);
 
		if (isWindows()) {
			return new String[]{"cmd","/C","android.bat list avd"};
		} else if (isMac()) {
			//TODO
		} else if (isUnix()) {
			//TODO
		} else if (isSolaris()) {
			//TODO
		}
			
		return new String[]{"android","list", "avd"};
	}
 
	public static boolean isWindows() {
		return (OS.indexOf("win") >= 0);
	}
 
	public static boolean isMac() {
		return (OS.indexOf("mac") >= 0);
	}
 
	public static boolean isUnix() {
		return (OS.indexOf("nix") >= 0 || OS.indexOf("nux") >= 0 || OS.indexOf("aix") > 0 );
	}
 
	public static boolean isSolaris() {
		return (OS.indexOf("sunos") >= 0);
	}
	public static String getOS(){
		if (isWindows()) {
			return "win";
		} else if (isMac()) {
			return "osx";
		} else if (isUnix()) {
			return "uni";
		} else if (isSolaris()) {
			return "sol";
		} else {
			return "err";
		}
	}

}
