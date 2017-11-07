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

package it.unina.android.ripper_service.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import android.util.Log;

public class ProcessNameGrabber {
	public static final int AID_APP = 10000;

	public static final int AID_USER = 100000;

	public static String getForegroundApp() {
		File[] files = new File("/proc").listFiles();
		int lowestOomScore = Integer.MAX_VALUE;
		String foregroundProcess = null;

		for (File file : files) {
			if (!file.isDirectory()) {
				continue;
			}

			int pid;
			try {
				pid = Integer.parseInt(file.getName());
			} catch (NumberFormatException e) {
				continue;
			}

			try {
				String cgroup = read(String.format("/proc/%d/cgroup", pid));

				String[] lines = cgroup.split("\n");

				if (lines.length != 2) {
					continue;
				}

				String cpuSubsystem = lines[0];
				String cpuaccctSubsystem = lines[1];

				if (!cpuaccctSubsystem.endsWith(Integer.toString(pid))) {
					// not an application process
					continue;
				}

				if (cpuSubsystem.endsWith("bg_non_interactive")) {
					// background policy
					continue;
				}

				String cmdline = read(String.format("/proc/%d/cmdline", pid));

				if (cmdline.contains("com.android.systemui")) {
					continue;
				}

				int uid = Integer.parseInt(cpuaccctSubsystem.split(":")[2].split("/")[1].replace("uid_", ""));
				if (uid >= 1000 && uid <= 1038) {
					// system process
					continue;
				}

				int appId = uid - AID_APP;
				int userId = 0;
				// loop until we get the correct user id.
				// 100000 is the offset for each user.
				while (appId > AID_USER) {
					appId -= AID_USER;
					userId++;
				}

				if (appId < 0) {
					continue;
				}

				// u{user_id}_a{app_id} is used on API 17+ for multiple user
				// account support.
				// String uidName = String.format("u%d_a%d", userId, appId);

				File oomScoreAdj = new File(String.format("/proc/%d/oom_score_adj", pid));
				if (oomScoreAdj.canRead()) {
					int oomAdj = Integer.parseInt(read(oomScoreAdj.getAbsolutePath()));
					if (oomAdj != 0) {
						continue;
					}
				}

				int oomscore = Integer.parseInt(read(String.format("/proc/%d/oom_score", pid)));
				if (oomscore < lowestOomScore) {
					lowestOomScore = oomscore;
					foregroundProcess = cmdline;
				}

			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return foregroundProcess;
	}

	private static String read(String path) throws IOException {
		StringBuilder output = new StringBuilder();
		BufferedReader reader = new BufferedReader(new FileReader(path));
		output.append(reader.readLine());
		for (String line = reader.readLine(); line != null; line = reader.readLine()) {
			output.append('\n').append(line);
		}
		reader.close();
		return output.toString();
	}
	
	public static String getForegroundApp2() {
		try {
			Process process = Runtime.getRuntime().exec("dumpsys activity top");
			BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
			
			String line = reader.readLine();
            process.destroy();
            Log.v("AndroidRipperService", line);
            
            return line.split(" ")[1];
            
		} catch (Exception ex) {
			
		}
		
		return null;
	}
}
