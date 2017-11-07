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

package it.unina.android.ripper.driver.exception;

/**
 * Uncaught Exception Handler
 * 
 * @author Nicola Amatucci - REvERSE
 *
 */
public class RipperUncaughtExceptionHandler implements Thread.UncaughtExceptionHandler {

	private boolean printStackTrace = false;
	private Throwable mThrowable = null;
	private boolean exitOnException = false;
	private String packageName = "";
	
	public RipperUncaughtExceptionHandler() {
		super();
	}
	
	public RipperUncaughtExceptionHandler(boolean printStackTrace) {
		super();
		this.printStackTrace = printStackTrace;
	}

	@Override
	public void uncaughtException(Thread t, Throwable e) {
		this.mThrowable = e;
		
		boolean uncaught = false;
		if (e instanceof RipperRuntimeException) {
			RipperRuntimeException ex = (RipperRuntimeException)e;
			System.out.println("[EXCEPTION]["+ex.getSourceClassName()+"."+ex.getMethod()+"] "+ ex.getRipperMessage());
		} else if (e.getCause() instanceof RipperRuntimeException) {
			RipperRuntimeException ex = (RipperRuntimeException)e.getCause();
			System.out.println("[EXCEPTION]["+ex.getSourceClassName()+"."+ex.getMethod()+"] "+ ex.getRipperMessage());
		} else {
			System.out.println("[UNCAUGHT-EXCEPTION] "+e.getMessage());
			uncaught = true;
		}
		
		if (printStackTrace || uncaught) {
			e.printStackTrace();
		}
		
		if (doExitOnException()) {
			System.exit(-1);
		}
	}

	public boolean canPrintStackTrace() {
		return printStackTrace;
	}

	public void setPrintStackTrace(boolean printStackTrace) {
		this.printStackTrace = printStackTrace;
	}

	public Throwable getThrowable() {
		return mThrowable;
	}

	public void setThrowable(Throwable mThrowable) {
		this.mThrowable = mThrowable;
	}

	public boolean doExitOnException() {
		return exitOnException;
	}

	public void setExitOnException(boolean exitOnException) {
		this.exitOnException = exitOnException;
	}
	
	public String getPackageName() {
		return packageName;
	}

	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}

	public String getStringException() {
		Throwable e = getThrowable();
		
		if (e != null) {
			if (e instanceof RipperRuntimeException) {
				RipperRuntimeException ex = (RipperRuntimeException)e;
				return ("[EXCEPTION]["+ex.getSourceClassName()+"."+ex.getMethod()+"] "+ ex.getRipperMessage());
			} else if (e.getCause() instanceof RipperRuntimeException) {
				RipperRuntimeException ex = (RipperRuntimeException)e.getCause();
				return ("[EXCEPTION]["+ex.getSourceClassName()+"."+ex.getMethod()+"] "+ ex.getRipperMessage());
			} else {
				return ("[UNCAUGHT-EXCEPTION] "+e.getMessage());
			}
		} else {
			return null;
		}
	}
}
