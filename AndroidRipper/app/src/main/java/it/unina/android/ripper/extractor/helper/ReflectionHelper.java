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

package it.unina.android.ripper.extractor.helper;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import android.annotation.SuppressLint;
import android.util.Log;

/**
 * Method that exploit Java Reflection API 
 * 
 * @author Nicola Amatucci - REvERSE
 *
 */
public class ReflectionHelper
{
	/**
	 * Log TAG
	 */
	public static final String TAG = "ReflectionHelper";
	
	/**
	 * Check if a class implement an interface
	 * 
	 * @param className CanonicalName of the class
	 * @param interfaceName CanonicalName of the interface
	 * @return
	 * @throws ClassNotFoundException
	 */
	public static boolean implementsInterface(String className, String interfaceName) throws ClassNotFoundException
	{
		Class<?> myClass = (Class<?>) Class.forName(className);
		return ReflectionHelper.implementsInterface(myClass, interfaceName);
	}
	
	/**
	 * Check if a class implement an interface
	 * 
	 * @param myClass Class class
	 * @param interfaceName CanonicalName of the interface
	 * @return esito della ricerca
	 */
	public static boolean implementsInterface(Class<?> myClass, String interfaceName)
	{
		for (Class<?> myInterface : myClass.getInterfaces())
		{
			if (myInterface.getCanonicalName().equals(interfaceName))
				return true;
		}
		return false;
	}
	
	/**
	 * Scan a class and its variables searching for something implementing an interface.
	 * 
	 * @param className CanonicalName of the class
	 * @param interfaceName CanonicalName of the interface
	 * @return
	 * @throws ClassNotFoundException
	 */
	public static boolean scanClassForInterface(String className, String interfaceName) throws ClassNotFoundException
	{
		Class<?> myClass = (Class<?>) Class.forName(className);
		return ReflectionHelper.scanClassForInterface(myClass, interfaceName);
	}
	
	/**
	 * Scan a class and its variables searching for something implementing an interface.
	 * 
	 * @param myClass Class class
	 * @param interfaceName CanonicalName of the interface
	 * @return
	 * @return
	 */
	public static boolean scanClassForInterface(Class<?> myClass, String interfaceName)
	{
		if ( ReflectionHelper.implementsInterface(myClass, interfaceName))
		{
			Log.v(TAG, "Found interface : " + interfaceName + " in " + myClass.getCanonicalName());
			return true;
		}

		for(Field field : myClass.getDeclaredFields() )
		{
			Class<?> fieldClass = field.getType();
								
			if ( ReflectionHelper.implementsInterface(fieldClass, interfaceName))
			{
				Log.v(TAG, "Found field implements : " + interfaceName  + " in " + fieldClass.getCanonicalName());
				return true;
			}
			
			if (fieldClass.getCanonicalName().equals(interfaceName))
			{
				Log.v(TAG, "Found field inline definition : " + interfaceName  + " in " + fieldClass.getCanonicalName());
				return true;
			}
		}
		
		Log.v(TAG, "Not found : " + interfaceName  + " in " + myClass.getCanonicalName());
		return false;
	}
		
	/**
	 * Using Java Reflection API obtains the set of listeners of a View
	 * 
	 * @param view View to reflect
	 * @return 	HashMap<String, Boolean>: key=name of the method, value=esists?
	 */
	@SuppressLint("NewApi")
	public static HashMap<String, Boolean> reflectViewListeners(android.view.View view)
    {
		HashMap<String, Boolean> ret = new HashMap<String, Boolean>();
		
		if (android.os.Build.VERSION.SDK_INT <= android.os.Build.VERSION_CODES.GINGERBREAD_MR1) {
		     ret = reflectViewListenersLegacy(view);
		} else {
			ret = reflectViewListenersNew(view);
		}
		
		return ret;
    }
	
	private static HashMap<String, Boolean> reflectViewListenersLegacy(android.view.View view)
    {
		HashMap<String, Boolean> ret = new HashMap<String, Boolean>();
		
		ret.put( "OnFocusChangeListener", checkIfFieldIsSet(view, "android.view.View", "mOnFocusChangeListener") );
		
		ret.put( "OnClickListener",
					checkIfFieldIsSet(view, "android.view.View", "mOnClickListener")
				||	checkIfFieldIsSet(view, "android.view.View", "mOnTouchListener")
		);
		
		ret.put( "OnLongClickListener",
					checkIfFieldIsSet(view, "android.view.View", "mOnLongClickListener")
				||	checkIfFieldIsSet(view, "android.view.View", "mOnCreateContextMenuListener")
		);
		
		ret.put( "OnKeyListener", checkIfFieldIsSet(view, "android.view.View", "mOnKeyListener") );
		
		if (view instanceof android.widget.TextView) //EditText
		{
			ret.put( "TextChangedListener", checkIfArrayListFieldIsSet(view, "android.widget.TextView", "mListeners") );
		}
		
		if (view instanceof android.widget.AbsListView) //ListView
		{
			ret.put( "OnScrollListener", checkIfFieldIsSet(view, "android.widget.AbsListView", "mOnScrollListener") );			
			ret.put( "OnItemSelectedListener", checkIfFieldIsSet(view, "android.widget.AdapterView", "mOnItemSelectedListener") );
			ret.put( "OnItemClickListener", checkIfFieldIsSet(view, "android.widget.AdapterView", "mOnItemClickListener") );
			ret.put( "OnItemLongClickListener", checkIfFieldIsSet(view, "android.widget.AdapterView", "mOnItemLongClickListener") );			
		}
		
		if (view instanceof android.view.ViewGroup)
		{
			ret.put( "OnHierarchyChangeListener", checkIfFieldIsSet(view, "android.view.ViewGroup", "mOnHierarchyChangeListener") );
			ret.put( "AnimationListener", checkIfFieldIsSet(view, "android.view.ViewGroup", "mAnimationListener") );
		}
		
    	return ret;
    }
	
	private static HashMap<String, Boolean> reflectViewListenersNew(android.view.View view)
    {
		HashMap<String, Boolean> ret = new HashMap<String, Boolean>();
		
		//Object o = getPrivateField("android.view.View.ListenerInfo", fieldName, o);
		//Object mListenerInfo = getPrivateField("android.view.View", "mListenerInfo", view);
		Object mListenerInfo = callGetterMethod("android.view.View", "getListenerInfo", view);
		
		ret.put( "OnFocusChangeListener", checkIfFieldIsSet(mListenerInfo, "android.view.View$ListenerInfo", "mOnFocusChangeListener") );
		
		ret.put( "OnClickListener",
					checkIfFieldIsSet(mListenerInfo, "android.view.View$ListenerInfo", "mOnClickListener")
				||	checkIfFieldIsSet(mListenerInfo, "android.view.View$ListenerInfo", "mOnTouchListener")
		);
		
		ret.put( "OnLongClickListener",
					checkIfFieldIsSet(mListenerInfo, "android.view.View$ListenerInfo", "mOnLongClickListener")
				||	checkIfFieldIsSet(mListenerInfo, "android.view.View$ListenerInfo", "mOnCreateContextMenuListener")
		);
		
		ret.put( "OnKeyListener", checkIfFieldIsSet(mListenerInfo, "android.view.View$ListenerInfo", "mOnKeyListener") );
		ret.put( "OnHoverListener", checkIfFieldIsSet(mListenerInfo, "android.view.View$ListenerInfo", "mOnHoverListener") );
		ret.put( "OnDragListener", checkIfFieldIsSet(mListenerInfo, "android.view.View$ListenerInfo", "mOnDragListener") );
		
		if (view instanceof android.widget.TextView) //EditText
		{
			ret.put( "TextChangedListener", checkIfArrayListFieldIsSet(view, "android.widget.TextView", "mListeners") );
		}
		
		if (view instanceof android.widget.AbsListView) //ListView
		{
			ret.put( "OnScrollListener", checkIfFieldIsSet(view, "android.widget.AbsListView", "mOnScrollListener") );			
			ret.put( "OnItemSelectedListener", checkIfFieldIsSet(view, "android.widget.AdapterView", "mOnItemSelectedListener") );
			ret.put( "OnItemClickListener", checkIfFieldIsSet(view, "android.widget.AdapterView", "mOnItemClickListener") );
			ret.put( "OnItemLongClickListener", checkIfFieldIsSet(view, "android.widget.AdapterView", "mOnItemLongClickListener") );			
		}
		
		if (view instanceof android.view.ViewGroup)
		{
			ret.put( "OnHierarchyChangeListener", checkIfFieldIsSet(view, "android.view.ViewGroup", "mOnHierarchyChangeListener") );
			ret.put( "AnimationListener", checkIfFieldIsSet(view, "android.view.ViewGroup", "mAnimationListener") );
		}
		
    	return ret;
    }
	
	/**
	 * Check if a field of a class is set
	 * 
	 * @param o Class Instance
	 * @param baseClass (Parent) Class
	 * @param fieldName Field Name
	 * @return
	 */
	public static boolean checkIfFieldIsSet(Object o, String baseClass, String fieldName)
	{
		java.lang.reflect.Field field;
		
		try
    	{
			//TODO: cache
			Class<?> viewObj = Class.forName(baseClass);
			field = viewObj.getDeclaredField(fieldName);
			field.setAccessible(true);
			
			boolean ret = (field.get(o) != null);
			Log.v(TAG, o.getClass().getCanonicalName() + " > " + fieldName + " FOUND | " + ((ret)?"ACTIVE":"NOT ACTIVE") );
			return ret;			
    	}
		catch (Exception e)
		{
			Log.e(TAG, e.toString());
		}
		
		Log.v(TAG, o.getClass().getCanonicalName() + " > " + fieldName + " NOT FOUND");
		
		return false;
	}
	
	/**
	 * Check if an ArrayList field of a class is set
	 * 
	 * @param o Class Instance
	 * @param baseClass (Parent) Class
	 * @param fieldName Field Name
	 * @return
	 */
	public static boolean checkIfArrayListFieldIsSet(Object o, String baseClass, String fieldName)
	{
		java.lang.reflect.Field field;
		
		try
    	{
			//TODO: cache
			Class<?> viewObj = Class.forName(baseClass);
			field = viewObj.getDeclaredField(fieldName);
			field.setAccessible(true);
			
			ArrayList arrayListField = (ArrayList) field.get(o);
			
			if (arrayListField != null)
			{
				if (arrayListField.size() > 0)
				{
					Log.v(TAG, o.getClass().getCanonicalName() + " > " + fieldName + " FOUND | ACTIVE" );
					return true;
				}
				else
				{
					Log.v(TAG, o.getClass().getCanonicalName() + " > " + fieldName + " FOUND | NOT ACTIVE" );
				}
			}
			else
			{
				Log.v(TAG, o.getClass().getCanonicalName() + " > " + fieldName + " FOUND | NULL" );
				return false;
			}
    	}
		catch (Exception e)
		{
			Log.e(TAG, e.toString());
		}
		
		Log.v(TAG, o.getClass().getCanonicalName() + " > " + fieldName + " NOT FOUND");
		
		return false;
	}
	
	/**
	 * Get the value of a private field
	 * 
	 * @param canonicalClassName CanonicalName of the class
	 * @param fieldName Field Name
	 * @param o Class Instance
	 * @return
	 */
	public static Object getPrivateField(String canonicalClassName, String fieldName, Object o)
	{
		try
		{
			Class<?> viewObj = Class.forName(canonicalClassName);
			Field field = viewObj.getDeclaredField(fieldName);
			field.setAccessible(true);
			return field.get(o);
		}
		catch(Exception ex)
		{
			Log.e(TAG, ex.toString());
		}
		
		return null;
	}
	
	public static Object callGetterMethod(String canonicalClassName, String methodName, android.view.View o) {
		try
		{
			Method method = Class.forName(canonicalClassName).getDeclaredMethod(methodName);
			method.setAccessible(true);
			return method.invoke(o);
		}
		catch(Exception ex)
		{
			Log.e(TAG, ex.toString());
		}
		
		return null;
	}
	
	/**
	 * Check if the class has a declared method
	 * 
	 * TODO: check the signature of the method instead of the name only
	 * 
	 * @param c Class class
	 * @param methodName Method Name
	 * @return
	 */
	public static boolean hasDeclaredMethod(Class<?> c, String methodName)
	{
		try
		{
			for ( Method m : c.getDeclaredMethods() )
				if (m.getName().equals(methodName))
					return true;
		}
		catch(Exception ex)
		{
			Log.e(TAG, ex.toString());
		}
		
		return false;
	}
	
	/**
	 * Check if the class is a descendant of another class
	 * 
	 * @param descendant Descendant Class
	 * @param ancestor Ancestor Class
	 * @return
	 */
	public static boolean isDescendant(Class<?> descendant, Class<?> ancestor)
	{
		return ancestor.isAssignableFrom(descendant);
	}
}
