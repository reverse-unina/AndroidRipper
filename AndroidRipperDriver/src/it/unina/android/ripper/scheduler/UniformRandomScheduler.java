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

import java.util.Random;

import it.unina.android.shared.ripper.model.task.Task;
import it.unina.android.shared.ripper.model.task.TaskList;

/**
 * Randomly chooses the next task.
 *  
 * @author Nicola Amatucci - REvERSE
 *
 */
public class UniformRandomScheduler implements Scheduler {

	/**
	 * Random Seed
	 */
	private long RANDOM_SEED = 1;
	
	/**
	 * Task List
	 */
	protected TaskList taskList;
	
	/**
	 * Random Generator
	 */
	protected Random random;
	
	/**
	 * Constructor
	 * 
	 * @param seed random seed
	 */
	public UniformRandomScheduler(long seed)
	{
		super();
		this.taskList = new TaskList();
		this.RANDOM_SEED = seed;
		random = new Random(RANDOM_SEED);
	}
	
	/* (non-Javadoc)
	 * @see it.unina.android.ripper.scheduler.Scheduler#nextTask()
	 */
	@Override
	public Task nextTask() {
		if (this.taskList.size() > 0)
		{
			int pos = (int)( random.nextInt( taskList.size() ) );
			
			Task t = taskList.get(pos);
			taskList.clear();
			
			return t;
		}
		else
			return null;
	}

	/* (non-Javadoc)
	 * @see it.unina.android.ripper.scheduler.Scheduler#addTask(it.unina.android.ripper.model.Task)
	 */
	@Override
	public void addTask(Task t) {
		this.taskList.add(t);
	}

	/* (non-Javadoc)
	 * @see it.unina.android.ripper.scheduler.Scheduler#addTasks(it.unina.android.ripper.model.TaskList)
	 */
	@Override
	public void addTasks(TaskList taskList) {
		this.taskList.addAll(taskList);
	}

	/* (non-Javadoc)
	 * @see it.unina.android.ripper.scheduler.Scheduler#getTaskList()
	 */
	@Override
	public TaskList getTaskList() {
		return this.taskList;
	}

	@Override
	public void clear() {
		this.taskList.clear();
	}

}
