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

package it.unina.android.ripper_service.net.packer;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * Message Packer/UnPacker class
 * 
 * @author Nicola Amatucci - REvERSE
 *
 */
public class MessagePacker {

	/**
	 * Pack Map into a byte array
	 * 
	 * @param map Map to pack
	 * @return packed Map
	 */
	public static byte[] pack(Map map) {
		if (map != null) {
			try {
				JsonObject jsonObject = new JsonObject();

				for (Object k : map.keySet()) {
					String value = (String) map.get(k);
					jsonObject.addProperty((String) k, value);
				}

				//System.out.println(jsonObject.toString());
				
				return jsonObject.toString().getBytes();
			} catch (Throwable t) {
				t.printStackTrace();
			}
		}

		return null;
	}

	/**
	 * UnPack Map from byte array
	 * 
	 * @param b byte array
	 * @return UnPacked Map
	 */
	@SuppressWarnings("unchecked")
	public static Map unpack(byte[] b) {
		if (b != null) {
			String s = new String(b);

			try {
				Gson gson = new Gson();
				JsonParser parser = new JsonParser();
				JsonObject o = parser.parse(s).getAsJsonObject();
				Map<String, Object> map = new HashMap<String, Object>();
				
				map = (Map<String,Object>) gson.fromJson(o, map.getClass());
				
				return map;

			} catch (Throwable t) {
				t.printStackTrace();
			}

		}

		return null;
	}
}
