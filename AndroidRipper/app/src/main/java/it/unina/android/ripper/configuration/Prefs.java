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

package it.unina.android.ripper.configuration;

import static it.unina.android.ripper.configuration.Configuration.*;

import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.prefs.InvalidPreferencesFormatException;
import java.util.prefs.Preferences;

import android.util.Log;

/**
 * Preferences from XML
 * 
 * @author Nicola Amatucci - REvERSE
 *
 */
public class Prefs {
	
	private static Preferences prefs;
	private static boolean notFound = false;
	private Preferences localPrefs;
	private Class<?> resources;
	private static String mainNode = RIPPER_PACKAGE;
	
	public Prefs (Preferences p) {
		this.localPrefs = p;
	}
	
	public Prefs (String node) {
		this (node, Configuration.class);
	}	

	public Prefs (String node, IConfiguration resources) {
		this (node, resources.getClass());
	}	

	public Prefs (String node, Class<? extends IConfiguration> resources) {
		this.localPrefs = loadNode(node);
		this.resources = resources;
	}	
	
	public static void setMainNode (String node) {
		mainNode = node;
	}
	
	public static Preferences getMainNode () {
		if (notFound) return null;
		if (prefs == null) {
			loadMainNode();
		}
		return prefs;
	}

	public static void loadMainNode() {
		loadMainNode (mainNode);
	}
	
	public static void loadMainNode (String node) {
		String path = "/data/data/" + RIPPER_PACKAGE + "/files/"+ CONFIGURATION_FILE;
		InputStream is = null;

		if (!(new File(path).exists())) {
			Log.i("androidripper", "Preferences file not found.");
			notFound = true;
			return;
		}
		
		Log.i("androidripper", "Preferences file found.");
		try {
			is = new BufferedInputStream(new FileInputStream(path));
			Preferences.importPreferences(is);
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidPreferencesFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		prefs = Preferences.userRoot().node(node);
	}
	
	public static Preferences loadNode (String localNode) {
		Log.d("androidripper","Loading node " + localNode);
		if (getMainNode() == null) return null;
		return getMainNode().node(localNode);
	}
	
	public boolean hasPrefs() {
		return localPrefs!=null;
	}
	
	public void updateResources() {
		if (!hasPrefs()) return;
		for (Field f: resources.getFields()) {			
		    if (Modifier.isFinal(f.getModifiers())) continue;
		    try {
				updateValue (f);
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public int getInt (Field parameter) throws IllegalArgumentException, IllegalAccessException {
		return localPrefs.getInt(parameter.getName(), parameter.getInt(parameter));
	}

	public long getLong (Field parameter) throws IllegalArgumentException, IllegalAccessException {
		return localPrefs.getLong(parameter.getName(), parameter.getLong(parameter));
	}

	public boolean getBoolean (Field parameter) throws IllegalArgumentException, IllegalAccessException {
		return localPrefs.getBoolean(parameter.getName(), parameter.getBoolean(parameter));
	}

	public String getString (Field parameter) throws IllegalArgumentException, IllegalAccessException {
		return localPrefs.get(parameter.getName(), parameter.get("").toString());
	}
	
	public static String fromArray (Field parameter, int index) {
		return parameter.getName() + "[" + index + "]";
	}
		
	public String[] getStringArray (Field parameter) throws IllegalArgumentException, IllegalAccessException {
		List<String> theList = new ArrayList<String>();
		int index = 0;
		String value;
		boolean found = false;
		while ((value = localPrefs.get(fromArray(parameter, index), null)) != null) {
			found = true;
			theList.add(value);
			index++;
		}
		String tmp[] = new String [theList.size()];
		return (found)?theList.toArray(tmp):null;
	}

	public int[] getIntArray (Field parameter) throws IllegalArgumentException, IllegalAccessException {
		String[] value = getStringArray (parameter);
		if (value == null) return null;
		int[] ret = new int[value.length];
		for (int i=0; i<value.length; i++) {
			ret[i] = Integer.parseInt(value[i]);
		}
		return ret;
	}
	
	protected void setArray (Field parameter) throws IllegalArgumentException, IllegalAccessException {
		setArray(parameter, parameter.getType());		
	}
	
	protected void setArray (Field parameter, Class<?> type) throws IllegalArgumentException, IllegalAccessException {
//		Log.e("androidripper","Sono qui");
		Class<?> component = type.getComponentType();
		if (component.equals(String.class)) {
//			Log.e("androidripper","Sono qua");
			String[] strings = getStringArray(parameter);
			if (strings!=null) {
//				Log.e("androidripper","Sono quo");
				parameter.set (parameter, strings);					
			}
		} else if (component.equals(int.class)) {
			int[] numbers = getIntArray(parameter);
			if (numbers!=null) {
				parameter.set (parameter, numbers);					
			}
		}
	}

	protected void updateValue (Field parameter) throws IllegalArgumentException, IllegalAccessException {
		Log.v("androidripper", "Updating value " + parameter.getName());
		Class<?> type = parameter.getType();
		String before = (parameter.get("") != null)?parameter.get("").toString():null;
		if (type.equals(int.class)) {
			parameter.setInt (parameter, getInt (parameter));
		} else if (type.equals(long.class)) {
			parameter.setLong (parameter, getLong (parameter));
		} else if (type.equals(String.class)) {
			parameter.set (parameter, getString (parameter));
		} else if (type.equals(boolean.class)) {
			parameter.setBoolean (parameter, getBoolean (parameter));
		} else if (type.isArray()) {
			setArray (parameter, type);
		} else {
			return;
		}
		Object o = parameter.get("");
		String after = (o==null)?"null":o.toString();
		if (!after.equals(before)) {
			if (!type.isArray()) {
				Log.d("androidripper", "Updated value of parameter " + parameter.getName() + " to " + after + " (default = " + before + ")");
			} else {
				Log.d("androidripper", "Updated values of array parameter " + parameter.getName());
			}
		}
	}
	
	public static void updateMainNode () {
		updateNode("", Configuration.class);
	}

	public static void updateNode (String node, Class<? extends IConfiguration> resources) {
		Log.d("androidripper", "Updating node " + node);
		Prefs p = new Prefs (node, resources);
		p.updateResources();
	}

}
