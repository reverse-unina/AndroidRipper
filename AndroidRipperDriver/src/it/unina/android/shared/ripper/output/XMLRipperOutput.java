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

package it.unina.android.shared.ripper.output;

import static it.unina.android.shared.ripper.constants.XMLModelTags.ACTIVITY;
import static it.unina.android.shared.ripper.constants.XMLModelTags.ACTIVITY_CLASS;
import static it.unina.android.shared.ripper.constants.XMLModelTags.ACTIVITY_HANDLES_KEYPRESS;
import static it.unina.android.shared.ripper.constants.XMLModelTags.ACTIVITY_HANDLES_LONG_KEYPRESS;
import static it.unina.android.shared.ripper.constants.XMLModelTags.ACTIVITY_ID;
import static it.unina.android.shared.ripper.constants.XMLModelTags.ACTIVITY_IS_ROOT_ACTIVITY;
import static it.unina.android.shared.ripper.constants.XMLModelTags.ACTIVITY_IS_TABACTIVITY;
import static it.unina.android.shared.ripper.constants.XMLModelTags.ACTIVITY_MENU;
import static it.unina.android.shared.ripper.constants.XMLModelTags.ACTIVITY_NAME;
import static it.unina.android.shared.ripper.constants.XMLModelTags.ACTIVITY_TABS_COUNT;
import static it.unina.android.shared.ripper.constants.XMLModelTags.ACTIVITY_TITLE;
import static it.unina.android.shared.ripper.constants.XMLModelTags.ACTIVITY_UID;
import static it.unina.android.shared.ripper.constants.XMLModelTags.DESCRIPTION;
import static it.unina.android.shared.ripper.constants.XMLModelTags.EVENT;
import static it.unina.android.shared.ripper.constants.XMLModelTags.EVENT_INTERACTION;
import static it.unina.android.shared.ripper.constants.XMLModelTags.EVENT_UID;
import static it.unina.android.shared.ripper.constants.XMLModelTags.EVENT_VALUE;
import static it.unina.android.shared.ripper.constants.XMLModelTags.EXTRACTED_EVENTS;
import static it.unina.android.shared.ripper.constants.XMLModelTags.FIRED_EVENT;
import static it.unina.android.shared.ripper.constants.XMLModelTags.FIRST_STEP;
import static it.unina.android.shared.ripper.constants.XMLModelTags.INPUT;
import static it.unina.android.shared.ripper.constants.XMLModelTags.INPUT_TYPE;
import static it.unina.android.shared.ripper.constants.XMLModelTags.INPUT_VALUE;
import static it.unina.android.shared.ripper.constants.XMLModelTags.LISTENER;
import static it.unina.android.shared.ripper.constants.XMLModelTags.LISTENER_CLASS;
import static it.unina.android.shared.ripper.constants.XMLModelTags.LISTENER_PRESENT;
import static it.unina.android.shared.ripper.constants.XMLModelTags.STEP;
import static it.unina.android.shared.ripper.constants.XMLModelTags.SUPPORTED_EVENT;
import static it.unina.android.shared.ripper.constants.XMLModelTags.SUPPORTED_EVENT_TYPE;
import static it.unina.android.shared.ripper.constants.XMLModelTags.TASK;
import static it.unina.android.shared.ripper.constants.XMLModelTags.WIDGET;
import static it.unina.android.shared.ripper.constants.XMLModelTags.WIDGET_CLASS;
import static it.unina.android.shared.ripper.constants.XMLModelTags.WIDGET_COUNT;
import static it.unina.android.shared.ripper.constants.XMLModelTags.WIDGET_ENABLED;
import static it.unina.android.shared.ripper.constants.XMLModelTags.WIDGET_ID;
import static it.unina.android.shared.ripper.constants.XMLModelTags.WIDGET_INDEX;
import static it.unina.android.shared.ripper.constants.XMLModelTags.WIDGET_NAME;
import static it.unina.android.shared.ripper.constants.XMLModelTags.WIDGET_R_ID;
import static it.unina.android.shared.ripper.constants.XMLModelTags.WIDGET_SIMPLE_TYPE;
import static it.unina.android.shared.ripper.constants.XMLModelTags.WIDGET_TEXT_TYPE;
import static it.unina.android.shared.ripper.constants.XMLModelTags.WIDGET_VALUE;
import static it.unina.android.shared.ripper.constants.XMLModelTags.WIDGET_VISIBLE;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import it.unina.android.shared.ripper.model.state.ActivityDescription;
import it.unina.android.shared.ripper.model.state.WidgetDescription;
import it.unina.android.shared.ripper.model.task.Task;
import it.unina.android.shared.ripper.model.task.TaskList;
import it.unina.android.shared.ripper.model.transition.Event;
import it.unina.android.shared.ripper.model.transition.IEvent;
import it.unina.android.shared.ripper.model.transition.Input;

public class XMLRipperOutput implements RipperOutput
{
	public static boolean RUN_IN_THREAD = true; 
	
	@Override
	public String outputActivityDescription(ActivityDescription ad) {
		try {
			Document doc = this.buildActivityDescriptionDocument(ad);
			return this.XML2String(doc);
		} catch (ParserConfigurationException pex) {
			pex.printStackTrace();
		}
		
		return null;
	}

	protected Document buildActivityDescriptionDocument(ActivityDescription ad) throws ParserConfigurationException {
		Document doc = DocumentBuilderFactory.newInstance()
				.newDocumentBuilder().newDocument();
		Element activity = doc.createElement(ACTIVITY);
		doc.appendChild(activity);

		activity.setAttribute(ACTIVITY_TITLE, (ad.getTitle()!=null)?ad.getTitle():"");
		activity.setAttribute(ACTIVITY_NAME, (ad.getName()!=null)?ad.getName():"");
		activity.setAttribute(ACTIVITY_CLASS, (ad.getClassName()!=null)?ad.getClassName():"");
		activity.setAttribute(ACTIVITY_MENU, (ad.hasMenu() != null && ad.hasMenu()) ? "TRUE" : "FALSE");
		activity.setAttribute(ACTIVITY_HANDLES_KEYPRESS,
				((ad.handlesKeyPress() != null && ad.handlesKeyPress()) ? "TRUE" : "FALSE"));
		activity.setAttribute(ACTIVITY_HANDLES_LONG_KEYPRESS,
				((ad.handlesLongKeyPress()  != null && ad.handlesLongKeyPress()) ? "TRUE" : "FALSE"));
		activity.setAttribute(ACTIVITY_IS_TABACTIVITY,
				((ad.isTabActivity() != null && ad.isTabActivity()) ? "TRUE" : "FALSE"));
		activity.setAttribute(
				ACTIVITY_TABS_COUNT,
				((ad.isTabActivity() != null && ad.isTabActivity()) ? Integer.toString(ad.getTabsCount()) : "0"));
		activity.setAttribute(ACTIVITY_ID, (ad.getId() != null) ? ad.getId()
				: "");
		activity.setAttribute(ACTIVITY_UID, (ad.getUid() != null) ? ad.getUid()
				: "");
		activity.setAttribute(ACTIVITY_IS_ROOT_ACTIVITY, ((ad.isRootActivity() != null && ad.isRootActivity())  ? "TRUE" : "FALSE") );
		
		HashMap<String, Boolean> listeners = ad.getListeners();
		if (listeners != null) {
			for (String key : listeners.keySet()) {
				Boolean value = listeners.get(key);
	
				Element listener = doc.createElement(LISTENER);
				listener.setAttribute(LISTENER_CLASS, key);
				listener.setAttribute(LISTENER_PRESENT, value ? "TRUE" : "FALSE");
				activity.appendChild(listener);
			}
		}

		ArrayList<String> supportedEvents = ad.getSupportedEvents();
		if (supportedEvents != null) {
			for (String value : supportedEvents) {
				Element supportedEvent = doc.createElement(SUPPORTED_EVENT);
				supportedEvent.setAttribute(SUPPORTED_EVENT_TYPE, value);
				activity.appendChild(supportedEvent);
			}
		}

		ArrayList<WidgetDescription> wds = ad.getWidgets();
		if (wds != null) {
			for (WidgetDescription wd : wds) {
				Element widget = this.buildWidgetDescriptionDocument(wd)
						.getDocumentElement();
				//activity.appendChild( doc.importNode((Node)widget, true) );
				activity.appendChild(importElement(doc, widget)) ;
			}
		}
		
		return doc;
	}
	
	@Override
	public String outputFirstStep(ActivityDescription ad, TaskList t) {
		try {
			Document doc = this.buildFirstStepDocument(ad, t);
			return this.XML2String(doc);
		} catch (ParserConfigurationException pex) {
			pex.printStackTrace();
		}
		
		return null;
	}
	
	private Document buildFirstStepDocument(ActivityDescription ad, TaskList t) throws ParserConfigurationException {
		Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
		Element firstStep = doc.createElement(FIRST_STEP);
		doc.appendChild(firstStep);
		
		Element activity = null;
		if (ad != null) {
			activity = this.buildActivityDescriptionDocument(ad).getDocumentElement();
			//firstStep.appendChild( doc.importNode((Node)activity, true) );
			firstStep.appendChild(importElement(doc, activity)) ;
		} else {
			activity = doc.createElement(ACTIVITY);
			firstStep.appendChild(activity);
		}
		
		Element extractedEvents = null;
		if (t != null) {
			extractedEvents = this.buildExtractedEventsDocument(t, ad).getDocumentElement();
			//firstStep.appendChild( doc.importNode((Node)extractedEvents, true) );
			firstStep.appendChild(importElement(doc, extractedEvents)) ;
		} else {
			extractedEvents = doc.createElement(EXTRACTED_EVENTS);
			firstStep.appendChild(extractedEvents);
		}
		
		return doc;
	}

	@Override
	public String outputWidgetDescription(WidgetDescription wd) {
		
		try {
			Document doc = this.buildWidgetDescriptionDocument(wd);
			return this.XML2String(doc);
		} catch (ParserConfigurationException pex) {
			pex.printStackTrace();
		}
		
		return null;
		
	}
	
	protected Document buildWidgetDescriptionDocument(WidgetDescription wd) throws ParserConfigurationException {
		
		Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
		Element widget = doc.createElement(WIDGET);
		doc.appendChild(widget);
		
		widget.setAttribute(WIDGET_ID, (wd.getId() != null)?Integer.toString(wd.getId()):"");
		widget.setAttribute(WIDGET_CLASS, (wd.getClassName() != null)?wd.getClassName():"");
		
		widget.setAttribute(WIDGET_SIMPLE_TYPE, (wd.getSimpleType() != null)?wd.getSimpleType():"");
		
		if (wd.getTextualId() != null)
			widget.setAttribute(WIDGET_R_ID, wd.getTextualId());
		else
			widget.setAttribute(WIDGET_R_ID, "");
		
		if (wd.getTextType() != null)
			widget.setAttribute(WIDGET_TEXT_TYPE, wd.getTextType().toString());
		else
			widget.setAttribute(WIDGET_TEXT_TYPE, "");
		
		if (wd.getName() != null)
			widget.setAttribute(WIDGET_NAME, wd.getName());
		else
			widget.setAttribute(WIDGET_NAME, "");
		
		if (wd.getValue() != null)
			widget.setAttribute(WIDGET_VALUE, wd.getValue());
		else
			widget.setAttribute(WIDGET_VALUE, "");
		
		if (wd.getCount() != null)
			widget.setAttribute(WIDGET_COUNT, wd.getCount().toString());
		else
			widget.setAttribute(WIDGET_COUNT, "");
		
		if (wd.getIndex() != null)
			widget.setAttribute(WIDGET_INDEX, wd.getIndex().toString());
		else
			widget.setAttribute(WIDGET_INDEX, "");
		
		widget.setAttribute(WIDGET_ENABLED, (wd.isEnabled() != null && wd.isEnabled())?"TRUE":"FALSE");
		widget.setAttribute(WIDGET_VISIBLE, (wd.isVisible() != null && wd.isVisible())?"TRUE":"FALSE");
		
		HashMap<String, Boolean> listeners = wd.getListeners();
		if (listeners != null) {
			for (String key : listeners.keySet())
			{
				Boolean value = listeners.get(key);
				
				Element listener = doc.createElement(LISTENER);
				listener.setAttribute(LISTENER_CLASS, key);
				listener.setAttribute(LISTENER_PRESENT, (value != null && value)?"TRUE":"FALSE");
				widget.appendChild(listener);
			}
		}
		
		return doc;
	}
	
	@Override
	public String outputEvent(IEvent evt) {
		try {
			Document doc = this.buildIEventDescriptionDocument(evt, EVENT);
			return this.XML2String(doc);
		} catch (ParserConfigurationException pex) {
			pex.printStackTrace();
		}
		
		return null;
	}
	
	@Override
	public String outputFiredEvent(IEvent evt) {
		try {
			Document doc = this.buildIEventDescriptionDocument(evt, FIRED_EVENT);
			return this.XML2String(doc);
		} catch (ParserConfigurationException pex) {
			pex.printStackTrace();
		}
		
		return null;
	}

	protected Document buildEventDescriptionDocument(Event e, String TAG) throws ParserConfigurationException {
		Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
		Element event = doc.createElement(TAG);
		doc.appendChild(event);
		
		event.setAttribute(EVENT_INTERACTION, (e.getInteraction() != null)?e.getInteraction():"");
		event.setAttribute(EVENT_VALUE, (e.getValue() != null)?e.getValue():"");
		event.setAttribute(EVENT_UID, Long.toString(e.getEventUID()));
		
		if (e.getWidget() != null) {
			Element widget = null;
			if (e.getWidget() != null) {
				widget = this.buildWidgetDescriptionDocument( e.getWidget() ).getDocumentElement();
				//event.appendChild( doc.importNode((Node)widget, true) );
				event.appendChild(importElement(doc, widget)) ;
			} else {
				widget = doc.createElement(WIDGET); 
				event.appendChild( widget );
			}
		}
		
		if (e.getInputs() != null) {
			for (Input i : e.getInputs())
			{
				Element input = this.buildInputDescriptionDocument(i).getDocumentElement();
				//event.appendChild( doc.importNode((Node)input, true) );
				event.appendChild(importElement(doc, input)) ;
			}
		}
		
		return doc;
	}
	
	protected Document buildInputDescriptionDocument(Input i) throws ParserConfigurationException {
		Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
		Element input = doc.createElement(INPUT);
		doc.appendChild(input);
		
		input.setAttribute(INPUT_TYPE, (i.getInputType()!=null)?i.getInputType():"");
		input.setAttribute(INPUT_VALUE, (i.getValue()!=null)?i.getValue():"");
		
		if (i.getWidget() != null) { 
			Element widget = null;
			if (i.getWidget() != null) {
				widget = this.buildWidgetDescriptionDocument( i.getWidget() ).getDocumentElement();
				//input.appendChild( doc.importNode((Node)widget, true) );
				input.appendChild(importElement(doc, widget)) ;
			} else {
				widget = doc.createElement(WIDGET); 
				input.appendChild( widget );
			}
		}
		
		return doc;
	}
	
	@Override
	public String outputTask(Task t) {
		try {
			Document doc = this.buildTaskDescriptionDocument(t);
			return this.XML2String(doc);
		} catch (ParserConfigurationException pex) {
			pex.printStackTrace();
		}
		
		return null;
	}
	
	protected Document buildTaskDescriptionDocument(Task t) throws ParserConfigurationException {
		Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
		Element task = doc.createElement(TASK);
		doc.appendChild(task);
		
		for (IEvent e : t) {
			
			Element event = null;
			if (e != null) {
				event = this.buildIEventDescriptionDocument(e, EVENT).getDocumentElement();
				//task.appendChild( doc.importNode((Node)event, true) );
				task.appendChild(importElement(doc, event)) ;
			} else {
				event = doc.createElement(EVENT);
				task.appendChild(event);
			}
		}
		
		return doc;
	}

	@Override
	public String outputStep(IEvent e, ActivityDescription a) {
		try {
			Document doc = this.buildStepDocument(e, a);
			return this.XML2String(doc);
		} catch (ParserConfigurationException pex) {
			pex.printStackTrace();
		}
		
		return null;
	}
	
	protected Document buildStepDocument(IEvent e, ActivityDescription a) throws ParserConfigurationException {
		Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
		Element step = doc.createElement(STEP);
		doc.appendChild(step);
		
		Element event = null;
		if (e != null) {
			event = this.buildIEventDescriptionDocument(e, FIRED_EVENT).getDocumentElement();		
			//step.appendChild( doc.importNode((Node)event, true) );
			step.appendChild(importElement(doc, event)) ;
		} else {
			event = doc.createElement(FIRED_EVENT);
			step.appendChild(event);
		}

		Element activity = null;
		if (a != null) {
			activity = this.buildActivityDescriptionDocument(a).getDocumentElement();
			//step.appendChild( doc.importNode((Node)activity, true) );
			step.appendChild(importElement(doc, activity)) ;
		} else {
			activity = doc.createElement(ACTIVITY);
			step.appendChild(activity);
		}
		
		return doc;
	}
	
	@Override
	public String outputActivityDescriptionAndPlannedTasks(ActivityDescription a, TaskList t) {
		try {
			Document doc = this.buildActivityDescriptionAndPlannedDocument(a, t);
			return this.XML2String(doc);
		} catch (ParserConfigurationException pex) {
			pex.printStackTrace();
		}
		
		return null;
	}
	
	private Document buildActivityDescriptionAndPlannedDocument(ActivityDescription a, TaskList t) throws ParserConfigurationException {
		Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
		Element description = doc.createElement(DESCRIPTION);
		doc.appendChild(description);
		
		Element activity = null;
		if (a != null) {
			String id = a.getId();
			activity = this.buildActivityDescriptionDocument(a).getDocumentElement();
			activity.setAttribute("id", (id != null)?id:"");
			//description.appendChild( doc.importNode((Node)activity, true) );
			description.appendChild(importElement(doc, activity)) ;
		} else {
			activity = doc.createElement(ACTIVITY);
			description.appendChild(activity);
		}
		
		Element extractedEvents = null;
		if (t != null) {
			extractedEvents = this.buildExtractedEventsDocument(t, null).getDocumentElement();
			description.appendChild( doc.importNode((Node)extractedEvents, true) );
		} else {
			extractedEvents = doc.createElement(EXTRACTED_EVENTS);
			//description.appendChild(extractedEvents);
			description.appendChild(importElement(doc, extractedEvents)) ;
		}
		
		return doc;
	}

	@Override
	public String outputStepAndPlannedTasks(IEvent e, ActivityDescription a, TaskList t) {
		try {
			Document doc = this.buildStepAndPlannedTasksDocument(e, a, t);
			return this.XML2String(doc);
		} catch (ParserConfigurationException pex) {
			pex.printStackTrace();
		}
		
		return null;
	}
	
	protected Document buildStepAndPlannedTasksDocument(IEvent e, ActivityDescription a, TaskList t) throws ParserConfigurationException {
		Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
		Element step = doc.createElement(STEP);
		doc.appendChild(step);
		
		Element event = null;
		if (e != null) {
			event = this.buildIEventDescriptionDocument((Event)e, FIRED_EVENT).getDocumentElement();
			step.appendChild(importElement(doc, event)) ;
		} else {
			event = doc.createElement(EVENT);
			step.appendChild(event);
		}		

		Element activity = null;
		if (a != null) {
			activity = this.buildActivityDescriptionDocument(a).getDocumentElement();
			//step.appendChild( doc.importNode((Node)activity, true) );
			step.appendChild(importElement(doc, activity)) ;
		} else {
			activity = doc.createElement(ACTIVITY);
			step.appendChild(activity);
		}
		
		Element extractedEvents = null;
		if (t != null) {
			extractedEvents = this.buildExtractedEventsDocument(t, null).getDocumentElement();
			//step.appendChild( doc.importNode((Node)extractedEvents, true) );
			step.appendChild(importElement(doc, extractedEvents)) ;
		} else {
			extractedEvents = doc.createElement(EXTRACTED_EVENTS);
			step.appendChild(extractedEvents);
		}
		
		return doc;
	}
	
	protected Document buildIEventDescriptionDocument(IEvent e, String tag) throws ParserConfigurationException {
		if (e instanceof Event) {
			return this.buildEventDescriptionDocument((Event)e, tag.equals(EVENT)?EVENT:FIRED_EVENT);
		}
		return null;
	}

	
	@Override
	public String outputExtractedEvents(TaskList t) {
		return this.outputExtractedEvents(t, null);
	}
	
	@Override
	public String outputExtractedEvents(TaskList t, ActivityDescription from) {
		try {
			Document doc = this.buildExtractedEventsDocument(t, from);
			return this.XML2String(doc);
		} catch (ParserConfigurationException pex) {
			pex.printStackTrace();
		}
		
		return null;
	}
	
	private Document buildExtractedEventsDocument(TaskList t, ActivityDescription from) throws ParserConfigurationException {
		Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
		Element extractedEvents = doc.createElement(EXTRACTED_EVENTS);
		
		if (from != null) {
			extractedEvents.setAttribute(ACTIVITY_ID, from.getId());
		}
		
		doc.appendChild(extractedEvents);
		
		for (Task task : t) {
			
			IEvent e = task.get( task.size() - 1 );
		
			Element event = null;
			if (e != null) {
				event = this.buildIEventDescriptionDocument((Event)e, EVENT).getDocumentElement();
				//extractedEvents.appendChild( doc.importNode((Node)event, true) );
				extractedEvents.appendChild(importElement(doc, event)) ;
			}
			
		}
		
		return doc;
	}

	/**
	 * Return the String representation of an XML Document
	 * 
	 * @param doc XML Document
	 * @return XML String
	 */
	protected String XML2String(Document doc) {
		
		if (RUN_IN_THREAD == false) {
			try {
				StringWriter stw = new StringWriter();
				
				TransformerFactory tFactory = TransformerFactory.newInstance();
				Transformer serializer = tFactory.newTransformer();
				serializer.setOutputProperty("omit-xml-declaration", "yes");
				//serializer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
				serializer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
				serializer.setOutputProperty(OutputKeys.INDENT, "no");
				
				DOMSource domSrc = new DOMSource(doc);
				StreamResult sResult = new StreamResult(stw);
				serializer.transform(domSrc, sResult);
				
				String ret = stw.toString();
				
				domSrc = null;
				sResult = null;
				stw = null;
				serializer = null;
				
				return ret;

//				TransformerFactory transfac = TransformerFactory.newInstance();
//	            //transfac.setAttribute("indent-number", Integer.valueOf(2));
//	            Transformer trans = transfac.newTransformer();
//	            trans.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
//	            trans.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
//	            trans.setOutputProperty(OutputKeys.INDENT, "yes");
//
//	            //create string from xml tree
//	            StringWriter sw = new StringWriter();
//	            StreamResult result = new StreamResult(sw);
//	            DOMSource source = new DOMSource(doc);
//	            trans.transform(source, result);
//	            String xmlString = sw.toString();
//	            
//	           return xmlString;
				
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			
			return null;
			
		} else {
			try {
				XMLSerializerThread xmlSerializerThread = new XMLSerializerThread(doc);
				xmlSerializerThread.start();
				xmlSerializerThread.join();
				return xmlSerializerThread.getOutput();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			return null;
		}
	}
	
	/**
	 * Import Node into Document
	 * 
	 * @param document XML Document
	 * @param element Node to import
	 * @return Document
	 */
	protected Node importElement(Document document, Element element) {
		try {
			return document.importNode((Node)element, true);
		} catch (DOMException ex) {
			Node newNode = (Node)element.cloneNode(true);
			return document.adoptNode(newNode);
		}
	}
}
