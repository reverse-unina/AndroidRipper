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

import it.unina.android.shared.ripper.constants.InteractionType;
import it.unina.android.shared.ripper.model.state.WidgetDescription;

/**
 * Clickable Widget Input Planner  
 *  
 * @author Nicola Amatucci - REvERSE
 *
 */
public class ClickableWidgetInputPlanner extends WidgetInputPlanner {

	public ClickableWidgetInputPlanner(WidgetDescription widget) {
		super(widget, InteractionType.CLICK, false);
	}
	
}
