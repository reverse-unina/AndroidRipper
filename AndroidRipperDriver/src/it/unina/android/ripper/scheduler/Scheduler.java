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

package it.unina.android.ripper.scheduler;

import it.unina.android.shared.ripper.model.task.Task;
import it.unina.android.shared.ripper.model.task.TaskList;

/**
 * Scheduler
 *  
 * @author Nicola Amatucci - REvERSE
 *
 */
public interface Scheduler {
	
	/**
	 * Schedule Next Task
	 * 
	 * @return
	 */
	public Task nextTask();
	
	/**
	 * Add a Task to be scheduled
	 * 
	 * @param t Task
	 */
	public void addTask(Task t);
	
	/**
	 * Add a List of Tasks to be scheduled
	 * 
	 * @param taskList List of Tasks
	 */
	public void addTasks(TaskList taskList);
	
	/**
	 * Return the List of Tasks that can be scheduled
	 * 
	 * @return
	 */
	public TaskList getTaskList();
	
	/**
	 * Clear the TaskList
	 */
	public void clear();
}
