package it.unina.android.ripper.tools.strace;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;

import it.unina.android.ripper.driver.device.AbstractDevice;

public class AdbStraceDumper extends StraceDumper {

	/**
	 * Device
	 */
	String packageName;

	/**
	 * Destination File Name
	 */
	// String pcapFilename;
	String straceFilename;
	String straceCutFilename;
	String netstatFilename;
	
	AbstractDevice device;

	Process p = null;
		
	/**
	 * Constructor
	 * 
	 * @param deviceName
	 * 
	 * @param device
	 *            device name
	 */
	public AdbStraceDumper(String packageName, String outPath, AbstractDevice device, long sequenceNumber) {
		super();
		this.packageName = packageName;
		this.straceFilename = outPath + packageName + "_" + sequenceNumber + ".strace";
		this.straceCutFilename = outPath + packageName + "_" + sequenceNumber + "_cut.strace";
		//this.netstatFilename = outPath + packageName + "_" + sequenceNumber + ".netstat";
		this.device = device;
	}

	@Override
	public void strace() {

		String pid = null;
		try {
			do {
				if (device.needsSu() == false) {
					p = Runtime.getRuntime().exec("adb -s " + device.getName() + " shell ps " + AdbStraceDumper.this.packageName);
				} else {
					p = Runtime.getRuntime().exec("adb -s " + device.getName() + " su -c shell ps " + AdbStraceDumper.this.packageName);
				}
				BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));				
				String line = "";
				// skip first row, table header
				while ((line = input.readLine()) != null) {
					//System.out.println(line);
					if (line.contains(AdbStraceDumper.this.packageName)) {
						line = line.trim().replaceAll(" +", " ");
						pid = line.split(" ")[1];
						break;
					}
				}
				p.waitFor();
				input.close();
			} while (pid == null);

			System.out.println("Tracing PID: " + pid);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println(e.getMessage());
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			System.out.println(e.getMessage());
		}

		if (pid != null) {
			

			
			try {
				if (device.needsSu() == false) {
					p = Runtime.getRuntime().exec("adb -s " + device.getName()
							+ " shell strace -f -ttt -xx -p " + pid + " -e trace=connect,getsockname -yy");
				} else {
					p = Runtime.getRuntime().exec("adb -s " + device.getName()
							+ " shell su -c strace -f -ttt -xx -p " + pid + " -e trace=connect,getsockname -yy");
				}
				String line = "";
				BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));
				FileWriter fw = new FileWriter(AdbStraceDumper.this.straceFilename);
				FileWriter fwCut = new FileWriter(AdbStraceDumper.this.straceCutFilename);
				
				
				//boolean firstLine = true;
				while ((line = input.readLine()) != null) {
					
					if ((line.contains("getsockname") && (line.contains("TCP") || line.contains("UDP")))
							|| (line.contains("connect") && line.contains("TCP"))
							|| (line.contains("sendto") && line.contains("UDP"))
							|| ((line.contains("getsockname") || line.contains("connect") || line.contains("sendto"))
									&& line.contains("resumed"))) {						
						fwCut.write(line + "\n");
						fwCut.flush();
					}
					fw.write(line + "\n");
					fw.flush();
				}
				p.waitFor();
				input.close();
				fw.close();
				fwCut.close();
			} catch (Exception ex) {
				ex.printStackTrace();
				//System.out.println(ex.getMessage());
			}
		}

	}

	@Override
	public void stopProcess() {
		try {
			if (p != null)
				p.destroy();
		} catch (Throwable t) {
		}
	}

}