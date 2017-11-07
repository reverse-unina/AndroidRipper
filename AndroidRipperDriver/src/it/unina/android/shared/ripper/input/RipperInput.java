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

package it.unina.android.shared.ripper.input;

import java.util.ArrayList;

import org.w3c.dom.Element;

import it.unina.android.shared.ripper.model.state.ActivityDescription;
import it.unina.android.shared.ripper.model.task.Task;

/**
 * Specifies how the element of the model are de-serialized
 * 
 * @author Nicola Amatucci - REvERSE
 *
 */
public interface RipperInput
{
	/**
	 * Specifies how an Activity Description is de-serialized from an XML Element
	 * 
	 * @param description XML Element
	 * @return
	 */
	public ActivityDescription inputActivityDescription(Element description);
	
	/**
	 * Specifies how an Activity Description is de-serialized from a String
	 * 
	 * @param description String description
	 * @return
	 */
	public ActivityDescription inputActivityDescription(String description);
	
	/**
	 * Load an ActivityDescription list from an URI
	 * 
	 * @param sourceURI ActivityDescription list URI
	 * @return
	 */
	public ArrayList<ActivityDescription> loadActivityDescriptionList(String sourceURI);
	
	/**
	 * Load a Task from an URI
	 * 
	 * @param sourceURI
	 * @return
	 */
	public Task loadTask(String sourceURI);
}
