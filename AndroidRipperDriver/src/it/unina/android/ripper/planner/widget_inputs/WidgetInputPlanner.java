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

package it.unina.android.ripper.planner.widget_inputs;

import it.unina.android.shared.ripper.model.state.WidgetDescription;
import it.unina.android.shared.ripper.model.transition.Input;

/**
 * Generic Widget Input Planner  
 *  
 * @author Nicola Amatucci - REvERSE
 *
 */
public class WidgetInputPlanner {

	public static final int RANDOM_VALUE = 0;
	
	protected WidgetDescription mWidget;
	protected boolean mGenerateValues;
	protected String mInteractionType;
	
	public WidgetInputPlanner(WidgetDescription widget, String interactionType, boolean generateValues)
	{
		super();
		this.mWidget = widget;
		this.mGenerateValues = generateValues;
		this.mInteractionType = interactionType;
	}
	
	public boolean canPlanForWidget()
	{
		return mWidget != null && mWidget.isEnabled(); //&& mWidget.getSimpleType() != null && mWidget.getSimpleType().equals("") == false;
	}
	
	public Input getInputForWidget()
	{	
		if (canPlanForWidget())
			if (mGenerateValues)
				return new Input(mWidget, mInteractionType, RandomValuesGenerator.generate());
			else
				return new Input(mWidget, mInteractionType, null);
		else
			return null;
	}
}
