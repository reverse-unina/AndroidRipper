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

package it.unina.android.ripper.utils;

/**
 * String utils
 * 
 * @author Nicola Amatucci - REvERSE
 *
 */
public class RipperStringUtils {
	public static String quoteRegExSpecialChars(String s) {
		if (s != null) {
			return s.replaceAll("[\\<\\(\\[\\{\\\\\\^\\-\\=\\$\\!\\|\\]\\}\\)‌​\\?\\*\\+\\.\\>]", "\\\\$0");
		} else {
			return null;
		}
	}

	public static boolean stringContainsItemFromList(String inputStr, String[] items) {
		for (int i = 0; i < items.length; i++) {
			if (inputStr.contains(items[i])) {
				return true;
			}
		}
		return false;
	}
}
