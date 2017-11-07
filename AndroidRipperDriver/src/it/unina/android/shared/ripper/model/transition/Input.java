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

package it.unina.android.shared.ripper.model.transition;

import java.io.Serializable;

import it.unina.android.shared.ripper.model.state.WidgetDescription;

/**
 * Input
 * 
 * @author Nicola Amatucci - REvERSE
 *
 */
public class Input implements Serializable {
	
	private WidgetDescription widget;
	private String value;
	private String inputType;
	
	public Input()
	{
		super();
	}

	public Input(WidgetDescription widget, String inputType, String value) {
		super();
		this.widget = widget;
		this.value = value;
		this.inputType = inputType;
	}

	public WidgetDescription getWidget() {
		return widget;
	}

	public void setWidget(WidgetDescription widget) {
		this.widget = widget;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getInputType() {
		return inputType;
	}

	public void setInputType(String inputType) {
		this.inputType = inputType;
	}

	@Override
	public String toString()
	{
		return ((widget!=null)?widget.toString():"[widget=null]") + "[inputType="+inputType+"][value="+value+"]";
	}
	
	public String toXMLString()
	{
		String xml = new String("");
		xml += "<input " +					
					"inputType=\""+inputType+"\" " +
					"value=\""+value+"\" " +
				">\n";
		
		if (widget != null)
			xml += widget.toXMLString();
		else
			xml += "<widget id=\"null\" type=\"null\" />\n";
		
		xml += "</input>\n";
		return xml;
	}
}
