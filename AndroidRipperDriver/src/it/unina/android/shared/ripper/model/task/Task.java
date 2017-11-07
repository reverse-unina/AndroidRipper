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

package it.unina.android.shared.ripper.model.task;


import java.io.Serializable;
import java.util.ArrayList;

import it.unina.android.shared.ripper.model.state.WidgetDescription;
import it.unina.android.shared.ripper.model.transition.Event;
import it.unina.android.shared.ripper.model.transition.IEvent;
import it.unina.android.shared.ripper.model.transition.Input;

/**
 * Task
 * 
 * @author Nicola Amatucci - REvERSE
 *
 */
public class Task extends ArrayList<IEvent> implements Serializable {

	private static final long serialVersionUID = 987654321L;

	public Task()
	{
		super();
	}
	
	public Task(Task baseTask, WidgetDescription widget, String interaction, ArrayList<Input> inputs)
	{
		super();
		
		if (baseTask != null && baseTask.size() > 0)
			this.addAll(baseTask);
		
		this.add(new Event(interaction, widget, null, inputs));
	}
	
	public Task(Task baseTask, WidgetDescription widget, String interaction, ArrayList<Input> inputs, String value)
	{
		super();
		
		if (baseTask != null && baseTask.size() > 0)
			this.addAll(baseTask);
		
		this.add(new Event(interaction, widget, value, inputs));
	}
	
	public void addNewEventForWidget(WidgetDescription widget, String interaction, ArrayList<Input> inputs)
	{
		this.add(new Event(interaction, widget, null, inputs));
	}
	
	public void addNewEventForWidget(WidgetDescription widget, String interaction, ArrayList<Input> inputs, String value)
	{
		this.add(new Event(interaction, widget, value, inputs));
	}

	public void addNewEventForActivity(String interaction)
	{
		this.add(new Event(interaction, null, null, null));
	}
	
	public void addNewEventForActivity(String interaction, String value)
	{
		this.add(new Event(interaction, null, value, null));
	}
}
