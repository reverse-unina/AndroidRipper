package it.unina.android.ripper.tools.strace;

public abstract class StraceDumper extends Thread {

	public StraceDumper() {
		super();
	}
	
	@Override
	public void run() {
		strace();
	}
	
	public abstract void strace();
	public abstract void stopProcess();
	
}
