package it.unina.android.ripper.tools.tcpdump;

import it.unina.android.ripper.driver.device.AbstractDevice;
import it.unina.android.ripper.tools.actions.Actions;

public class TcpdumpDumper extends Thread {

	private String filename = null;
	Process p;
	AbstractDevice device;
	String mPackageName = null;
	
	public TcpdumpDumper(String packageName, String outPath, AbstractDevice device,
			Long sequenceNumber) {
		super();
		mPackageName = packageName;
		this.device = device;
		if (sequenceNumber != null) {
			this.filename = outPath + packageName + "_" + sequenceNumber + ".pcap";
		} else {
			this.filename = outPath + packageName + ".pcap";
		}
	}

	public void run() {
		try {
			System.out.println("TcpDump: " + mPackageName);
			p = Runtime.getRuntime().exec("adb -s "+device.getName()+" shell tcpdump -X -n -tt -vvv -w /sdcard/tcpdump.pcap");
		} catch (Exception ex) {
			System.out.println(ex.getMessage());
		}
	}

	public void pull() {
		try {
			if (p != null)
				p.destroy();
		} catch (Throwable t) {
		}
		Actions.pull("/sdcard/tcpdump.pcap", filename);
	}
}
