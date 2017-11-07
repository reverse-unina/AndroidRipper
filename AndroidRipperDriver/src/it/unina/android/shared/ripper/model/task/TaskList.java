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

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import it.unina.android.shared.ripper.model.state.WidgetDescription;
import it.unina.android.shared.ripper.model.transition.Event;
import it.unina.android.shared.ripper.model.transition.IEvent;
import it.unina.android.shared.ripper.model.transition.Input;

/**
 * Task List
 * 
 * @author Nicola Amatucci - REvERSE
 *
 */
public class TaskList extends ArrayList<Task> {

	private static final long serialVersionUID = 1234567890L;

	public void addNewTaskForWidget(Task baseTask, WidgetDescription widget,
			String interaction, ArrayList<Input> inputs) {
		// this.addNewTask(baseTask, new Event(interaction, widget, null,
		// inputs));
		this.add(new Task(baseTask, widget, interaction, inputs));
	}

	public void addNewTaskForWidget(Task baseTask, WidgetDescription widget,
			String interaction, ArrayList<Input> inputs, String value) {
		// this.addNewTask(baseTask, new Event(interaction, widget, value,
		// inputs));
		this.add(new Task(baseTask, widget, interaction, inputs, value));
	}

	public void addNewTaskForActivity(Task baseTask, String interaction) {
		this.addNewTask(baseTask, new Event(interaction, null, null, null));
	}

	public void addNewTaskForActivity(Task baseTask, String interaction,
			String value) {
		this.addNewTask(baseTask, new Event(interaction, null, value, null));
	}
	
	private void addNewTask(Task baseTask, IEvent e) {
		Task task = new Task();

		if (baseTask != null && baseTask.size() > 0)
			task.addAll(baseTask);

		task.add(e);
		this.add(task);
	}

	public void saveToFile(String fileName) {
		try {
			FileOutputStream fileOut = new FileOutputStream(fileName);
			ObjectOutputStream out = new ObjectOutputStream(fileOut);
			out.writeObject(this);
			out.close();
			fileOut.close();
		} catch (Exception ex) {
			//ex.printStackTrace();
		}
	}

	public static TaskList loadFromFile(String fileName) {
		TaskList t = null;
		
		try {
			FileInputStream fileIn = new FileInputStream(fileName);
			ObjectInputStream in = new ObjectInputStream(fileIn);
			t = (TaskList) in.readObject();
			in.close();
			fileIn.close();
		} catch (Exception ex) {
			//ex.printStackTrace();
		}
		
		return t;
	}

//	@Override
//	public boolean add(Task e) {
//		boolean added = super.add(e);
//		
//		if (added) {
//			this.saveToFile("current_TaskList.bin");
//		}
//		
//		return added;
//	}
}
