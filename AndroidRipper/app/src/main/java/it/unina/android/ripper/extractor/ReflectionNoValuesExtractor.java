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

package it.unina.android.ripper.extractor;

import android.annotation.SuppressLint;
import android.view.View;
import android.widget.Checkable;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import it.unina.android.ripper.automation.robot.IRobot;
import it.unina.android.shared.ripper.model.state.WidgetDescription;

/**
 * Uses the JAVA Reflection API to extract information about the current GUI
 * Interface
 * 
 * @author Nicola Amatucci - REvERSE
 *
 */
@SuppressLint("NewApi")
public class ReflectionNoValuesExtractor extends ReflectionExtractor {

	/**
	 * Constructor
	 * 
	 * @param robot
	 *            Robot Instance
	 */
	public ReflectionNoValuesExtractor(IRobot robot) {
		super(robot);
	}

	/**
	 * Set Value of the Widget
	 * 
	 * @param v
	 *            Widget
	 * @param wd
	 *            WidgetDescription instance
	 */
	protected void setValue(View v, WidgetDescription wd) {

		// Checkboxes, radio buttons and toggle buttons -> the value is the
		// checked state (true or false)
		if (v instanceof Checkable) {
			wd.setValue(String.valueOf(((Checkable) v).isChecked()));
		}

		// Textview, Editview et al. -> the value is the displayed text
		if (v instanceof TextView) {
			wd.setValue(((TextView) v).getText().toString());
			// wd.setValue("");
			return;
		}

		// Progress bars, seek bars and rating bars -> the value is the current
		// progress
		if (v instanceof ProgressBar) {
			//wd.setValue(String.valueOf(((ProgressBar) v).getProgress()));
			wd.setValue("");
		}

		if (v instanceof ImageView) {
			ImageView imgView = (ImageView) v;
			// TODO:
		}

	}
}
