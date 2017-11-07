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
 * Generic RuntimeException
 * 
 * @author Nicola Amatucci - REvERSE
 *
 */
public class RipperRuntimeException extends RuntimeException {

	private Class<?> sourceClass;
	private String method;
	private String ripperMessage;
	
	public RipperRuntimeException(Class<?> sourceClass, String method, String message, Throwable cause) {
		super(message, cause);
		this.sourceClass = sourceClass;
		this.method = method;
		this.ripperMessage = message;
	}

	public RipperRuntimeException(Class<?> sourceClass, String method, String message) {
		super();
		this.sourceClass = sourceClass;
		this.method = method;
		this.ripperMessage = message;
	}

	public Class<?> getSourceClass() {
		return sourceClass;
	}

	public void setSourceClass(Class<?> sourceClass) {
		this.sourceClass = sourceClass;
	}

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public String getRipperMessage() {
		return ripperMessage;
	}

	public void setRipperMessage(String ripperMessage) {
		this.ripperMessage = ripperMessage;
	}

	public String getSourceClassName() {
		if (this.sourceClass != null) {
			return this.sourceClass.getSimpleName();
		} else {
			return "?";
		}
	} 
	
}
