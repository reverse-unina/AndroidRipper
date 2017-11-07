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

import static it.unina.android.shared.ripper.constants.XMLModelTags.ACTIVITY;
import static it.unina.android.shared.ripper.constants.XMLModelTags.ACTIVITY_CLASS;
import static it.unina.android.shared.ripper.constants.XMLModelTags.ACTIVITY_HANDLES_KEYPRESS;
import static it.unina.android.shared.ripper.constants.XMLModelTags.ACTIVITY_ID;
import static it.unina.android.shared.ripper.constants.XMLModelTags.ACTIVITY_IS_ROOT_ACTIVITY;
import static it.unina.android.shared.ripper.constants.XMLModelTags.ACTIVITY_IS_TABACTIVITY;
import static it.unina.android.shared.ripper.constants.XMLModelTags.ACTIVITY_MENU;
import static it.unina.android.shared.ripper.constants.XMLModelTags.ACTIVITY_NAME;
import static it.unina.android.shared.ripper.constants.XMLModelTags.ACTIVITY_TABS_COUNT;
import static it.unina.android.shared.ripper.constants.XMLModelTags.ACTIVITY_TITLE;
import static it.unina.android.shared.ripper.constants.XMLModelTags.ACTIVITY_UID;
import static it.unina.android.shared.ripper.constants.XMLModelTags.EVENT_INTERACTION;
import static it.unina.android.shared.ripper.constants.XMLModelTags.EVENT_VALUE;
import static it.unina.android.shared.ripper.constants.XMLModelTags.FIRED_EVENT;
import static it.unina.android.shared.ripper.constants.XMLModelTags.INPUT;
import static it.unina.android.shared.ripper.constants.XMLModelTags.INPUT_TYPE;
import static it.unina.android.shared.ripper.constants.XMLModelTags.INPUT_VALUE;
import static it.unina.android.shared.ripper.constants.XMLModelTags.LISTENER;
import static it.unina.android.shared.ripper.constants.XMLModelTags.LISTENER_CLASS;
import static it.unina.android.shared.ripper.constants.XMLModelTags.LISTENER_PRESENT;
import static it.unina.android.shared.ripper.constants.XMLModelTags.WIDGET;
import static it.unina.android.shared.ripper.constants.XMLModelTags.WIDGET_CLASS;
import static it.unina.android.shared.ripper.constants.XMLModelTags.WIDGET_COUNT;
import static it.unina.android.shared.ripper.constants.XMLModelTags.WIDGET_ENABLED;
import static it.unina.android.shared.ripper.constants.XMLModelTags.WIDGET_ID;
import static it.unina.android.shared.ripper.constants.XMLModelTags.WIDGET_INDEX;
import static it.unina.android.shared.ripper.constants.XMLModelTags.WIDGET_NAME;
import static it.unina.android.shared.ripper.constants.XMLModelTags.WIDGET_R_ID;
import static it.unina.android.shared.ripper.constants.XMLModelTags.WIDGET_SIMPLE_TYPE;
import static it.unina.android.shared.ripper.constants.XMLModelTags.WIDGET_VALUE;
import static it.unina.android.shared.ripper.constants.XMLModelTags.WIDGET_VISIBLE;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import it.unina.android.shared.ripper.model.state.ActivityDescription;
import it.unina.android.shared.ripper.model.state.WidgetDescription;
import it.unina.android.shared.ripper.model.task.Task;
import it.unina.android.shared.ripper.model.transition.Event;
import it.unina.android.shared.ripper.model.transition.Input;

public class XMLRipperInput implements RipperInput {

	@Override
	public ActivityDescription inputActivityDescription(Element activityElement) {
		ActivityDescription ret = null;
		
		if (activityElement != null) {
			ret = new ActivityDescription();
			
			try {
				ret.setId( activityElement.getAttribute(ACTIVITY_ID) );
			} catch (Exception ex) {
				//System.out.println(ex.getMessage());
			}
			
			try {
				ret.setUid( activityElement.getAttribute(ACTIVITY_UID) );
			} catch (Exception ex) {
				//System.out.println(ex.getMessage());
			}
			
			ret.setTitle(activityElement.getAttribute(ACTIVITY_TITLE));
			ret.setName(activityElement.getAttribute(ACTIVITY_NAME));
			ret.setClassName(activityElement.getAttribute(ACTIVITY_CLASS));
			ret.setHasMenu(activityElement.getAttribute(ACTIVITY_MENU)
					.equalsIgnoreCase("TRUE"));
			ret.setHandlesKeyPress(activityElement.getAttribute(
					ACTIVITY_HANDLES_KEYPRESS).equalsIgnoreCase("TRUE"));
			ret.setHandlesLongKeyPress(activityElement.getAttribute(
					ACTIVITY_HANDLES_KEYPRESS).equalsIgnoreCase("TRUE"));
			ret.setIsTabActivity(activityElement.getAttribute(
					ACTIVITY_IS_TABACTIVITY).equalsIgnoreCase("TRUE"));
			ret.setIsRootActivity(activityElement.getAttribute(
					ACTIVITY_IS_ROOT_ACTIVITY).equalsIgnoreCase("TRUE"));
			
			try
			{
				ret.setTabsCount( Integer.parseInt(activityElement.getAttribute(ACTIVITY_TABS_COUNT)));
			} catch(Throwable t){
				ret.setTabsCount(0);
			}
			
			NodeList childNodes = activityElement.getChildNodes();

			for (int index = 0; index < childNodes.getLength(); index++) {

				Node node = (Node) childNodes.item(index);
				
				//System.out.println("1)"+node.getNodeName());
				
				if (node.getNodeType() == Node.ELEMENT_NODE)
				{
					
					Element e = (Element)node;
					//System.out.println("2)"+e.getNodeName());
					if (e.getNodeName().equals(LISTENER)) {

						ret.addListener(
								e.getAttribute(LISTENER_CLASS),
								e.getAttribute(LISTENER_PRESENT).equalsIgnoreCase(
										"TRUE"));

					} else if (e.getNodeName().equals(WIDGET)) {

						WidgetDescription wd = this.inputWidgetDescription(e);
						ret.addWidget(wd);
						
					}
				}
			}
		} else {
			// malformed xml
		}
		
		return ret;
	}
	
	@Override
	public ActivityDescription inputActivityDescription(String description) {
		
		//System.out.println(description);
		
		ActivityDescription ret = null;

		try {
			//ret = new ActivityDescription();
			
			ByteArrayInputStream bs = new ByteArrayInputStream(description.getBytes("UTF-8"));
			InputSource is = new InputSource(bs);
	        is.setEncoding("UTF-8");
	        
			Document doc = DocumentBuilderFactory.newInstance()
					.newDocumentBuilder()
					.parse(is);
			Element root = doc.getDocumentElement();
			if (root.getTagName().equals(ACTIVITY)) {
			
				ret = this.inputActivityDescription(root);

			} else {
				
				NodeList activityElements = root.getElementsByTagName(ACTIVITY);

				if (activityElements != null && activityElements.getLength() > 0) {
					Element activityElement = (Element) activityElements.item(0);

					ret = this.inputActivityDescription(activityElement);

				} else {
					// malformed xml
				}
			}
			
		} catch (Exception ex) {
			ex.printStackTrace();
			//System.out.println(ex.getMessage());
		}

		return ret;
	}

	public WidgetDescription inputWidgetDescription(Element e) {
		WidgetDescription wd = null;
		
		if (e != null) {
			
			wd = new WidgetDescription();
			
			wd.setId(e.getAttribute(WIDGET_ID));
			wd.setClassName(e.getAttribute(WIDGET_CLASS));
			wd.setName(e.getAttribute(WIDGET_NAME));
			wd.setTextualId(e.getAttribute(WIDGET_R_ID));
			wd.setSimpleType(e.getAttribute(WIDGET_SIMPLE_TYPE));
			wd.setValue(e.getAttribute(WIDGET_VALUE));
			wd.setEnabled(e.getAttribute(WIDGET_ENABLED)
					.equalsIgnoreCase("TRUE"));
			wd.setVisible(e.getAttribute(WIDGET_VISIBLE)
					.equalsIgnoreCase("TRUE"));
			
			wd.setIndex(Integer.parseInt(e.getAttribute(WIDGET_INDEX)));
			
			if (e.getAttribute(WIDGET_COUNT) != null && e.getAttribute(WIDGET_COUNT).equals("") == false)
				wd.setCount(Integer.parseInt(e.getAttribute(WIDGET_COUNT)));
			
			NodeList widgetChildNodes = e.getElementsByTagName(LISTENER);
			//System.out.println(widgetChildNodes.getLength());
			for (int index2 = 0; index2 < widgetChildNodes.getLength(); index2++) {
				Node node2 = (Node) widgetChildNodes.item(index2);
				if (node2.getNodeType() == Node.ELEMENT_NODE)
				{
						Element e2 = (Element)node2;
						wd.addListener(
								e2.getAttribute(LISTENER_CLASS),
								e2.getAttribute(LISTENER_PRESENT).equalsIgnoreCase(
										"TRUE"));
				}
			}
			
		}
	
		return wd;
	}
	
	public Event inputEvent(Element e) {
		Event event = null;
		
		if (e != null) {
		
			event = new Event();
			ArrayList<Input> inputs = new ArrayList<Input>();
			
			event.setInteraction(e.getAttribute(EVENT_INTERACTION));
			event.setValue(e.getAttribute(EVENT_VALUE));
			
			NodeList childNodes = e.getChildNodes();
			
			for (int index = 0; index < childNodes.getLength(); index++) {

				Node node = (Node) childNodes.item(index);
				if (node.getNodeType() == Node.ELEMENT_NODE) {
					
					Element ce = (Element)node;
					
					if (ce.getNodeName().equals(WIDGET)) {
						WidgetDescription wd = this.inputWidgetDescription(ce);
						event.setWidget(wd);
					}
					
					if (ce.getNodeName().equals(INPUT)) {
						
						Input input = this.inputInput(ce);
						inputs.add(input);
					}
					
				}
			}
			
			event.setInputs(inputs);
		}
		
		return event;
	}
	
	public Input inputInput(Element e) {
		Input input = null;
		
		if (e != null) {
			input = new Input();
			
			input.setInputType(e.getAttribute(INPUT_TYPE));
			input.setValue(e.getAttribute(INPUT_VALUE));
			
			NodeList childNodes = e.getChildNodes();
			
			for (int index = 0; index < childNodes.getLength(); index++) {

				Node node = (Node) childNodes.item(index);
				if (node.getNodeType() == Node.ELEMENT_NODE) {
					
					Element ce = (Element)node;
					
					if (ce.getNodeName().equals(WIDGET)) {
						WidgetDescription wd = this.inputWidgetDescription(ce);
						input.setWidget(wd);
						break;
					}
				}
			}
			
		}
		
		return input;
	}
	
	public ArrayList<ActivityDescription> loadActivityDescriptionList(String sourceURI) {
		ArrayList<ActivityDescription> ret = new ArrayList<ActivityDescription>();
		
		NodeList nList = null;
		try {

			File fXmlFile = new File(sourceURI);
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(fXmlFile);

			doc.getDocumentElement().normalize();

			nList = doc.getElementsByTagName(ACTIVITY);
			
			for (int i = 0; i < nList.getLength(); i++) {
	
				Node nNode = nList.item(i);
	
				if (nNode.getNodeType() == Node.ELEMENT_NODE) {
					Element eElement = (Element) nNode;
					ActivityDescription ad = this.inputActivityDescription(eElement);
					ret.add(ad);
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			//System.out.println(e.getMessage());
		}
		
		return ret;
	}
	
	public ArrayList<ActivityDescription> loadActivityDescriptionList(InputStream is) {
		ArrayList<ActivityDescription> ret = new ArrayList<ActivityDescription>();
		
		NodeList nList = null;
		try {

			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(is);

			doc.getDocumentElement().normalize();

			nList = doc.getElementsByTagName(ACTIVITY);
			
			for (int i = 0; i < nList.getLength(); i++) {
	
				Node nNode = nList.item(i);
	
				if (nNode.getNodeType() == Node.ELEMENT_NODE) {
					Element eElement = (Element) nNode;
					ActivityDescription ad = this.inputActivityDescription(eElement);
					ret.add(ad);
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			//System.out.println(e.getMessage());
		}
		
		return ret;
	}
	
	public Task loadTask(String sourceURI) {
		Task task = new Task();
				
		try {

			File fXmlFile = new File(sourceURI);
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(fXmlFile);

			if ( doc != null ) {
				doc.getDocumentElement().normalize();
				
				NodeList eventNodes = doc.getDocumentElement().getChildNodes();
				for (int i = 0; i < eventNodes.getLength(); i++) {
					Node eventNode = eventNodes.item(i);
					if (eventNode.getNodeType() == Node.ELEMENT_NODE) {
					
						Element eventElement = (Element)eventNode;
						if (eventElement != null) {
							if (eventElement.getTagName().equals(FIRED_EVENT)) {
								Event event = this.inputEvent(eventElement);
								if (event != null) {
									task.add(event);
								}
							}
						}
					}
				}
				
			}

		} catch (Exception e) {
			e.printStackTrace();
			//System.out.println(e.getMessage());
		}
		
		return task;
	}
}
