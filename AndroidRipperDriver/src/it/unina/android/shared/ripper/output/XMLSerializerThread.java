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

import java.io.StringWriter;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;

/**
 * Thread that performs the XML Serialization
 * 
 * @author Nicola Amatucci - REvERSE
 *
 */
public class XMLSerializerThread extends Thread {

	private String output;
	protected Document doc;
	
	public XMLSerializerThread(Document doc) {
		super();
		this.doc = doc;
	}
	
	@Override
	public void run() {
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
			
			this.output = new String(stw.toString());
			
			domSrc = null;
			sResult = null;
			stw = null;
			serializer = null;
			tFactory = null;
			
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	public String getOutput() {
		return output;
	}

}
