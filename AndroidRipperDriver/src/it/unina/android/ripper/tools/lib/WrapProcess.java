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

package it.unina.android.ripper.tools.lib;

import java.io.BufferedReader;
import java.io.Flushable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.nio.CharBuffer;
import java.util.HashSet;
import java.util.Set;

/** Extends Process by wrapping one.
 * 
 * @author Nicola Amatucci - REvERSE
 *
 */
public class WrapProcess {

	private final static Appendable DEV_NULL = new Appendable() {

		public Appendable append(CharSequence csq) throws IOException { return this; }
		public Appendable append(char c) throws IOException { return this; }
		public Appendable append(CharSequence csq, int start, int end) throws IOException { return this; }
		
	};
	
	private final static Set<WrapProcess> processes = new HashSet<WrapProcess>();
	
	static {
		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
				for (WrapProcess process : processes) {
					try {
						process.destroy();
					} catch (Exception ignored) {}
				}
			}
		});
	}
	
	private final Process process;

	public WrapProcess(Process process) {
		processes.add(this);
		this.process = process;		
	}
	
	public void destroy() {
		process.destroy();
	}

	public int exitValue() {
		return process.exitValue();
	}

	public WrapProcess waitFor() throws InterruptedException {
		process.waitFor();
		return this;
	}
	
	public WrapProcess waitForSuccess() throws InterruptedException {
		int exitValue = waitFor().exitValue();
		if (exitValue != 0) throw new RuntimeException("Tool return " + exitValue);
		return this;
	}

	private Writer wrap(OutputStream sink) {
		return new OutputStreamWriter(sink);
	}
	
	private Reader wrap(InputStream sink) {
		return new InputStreamReader(sink);
	}
	
	public OutputStream getStdin() {
		return process.getOutputStream();
	}

	public Writer getStdinWriter() {
		return wrap(process.getOutputStream());
	}
	
	public InputStream getStdout() {
		return process.getInputStream();
	}
	
	public BufferedReader getStdoutReader() {
		return new BufferedReader(wrap(process.getInputStream()));
	}
	
	public InputStream getStderr() {
		return process.getErrorStream();
	}
	
	public BufferedReader getStderrReader() {
		return new BufferedReader(wrap(process.getErrorStream()));
	}
	
	private void connect(final Readable source, final Appendable sink) {
		Thread thread = new Thread(new Runnable() {
			public void run() {
				CharBuffer cb = CharBuffer.wrap(new char [256]);
				try {
					while (source.read(cb) != -1) {
						cb.flip();
						sink.append(cb);
						cb.clear();
					}

					if (sink instanceof Flushable) {
						((Flushable)sink).flush();
					}
				} catch (IOException e) { /* prolly broken pipe, just die */ }
			}
		});
		thread.setDaemon(true);
		thread.start();
	}
	
	public WrapProcess connectStdin(Readable source) {
		connect(source, wrap(getStdin()));
		return this;
	}
		
	public WrapProcess connectStdin(InputStream source) {
		return connectStdin(wrap(source));
	}
	
	public WrapProcess connectStdout(Appendable sink) {
		connect(wrap(getStdout()), sink);
		return this;
	}
	
	public WrapProcess connectStderr(Appendable sink) {
		connect(wrap(getStderr()), sink);
		return this;
	}

	public WrapProcess discardStdout() {
		return connectStdout(DEV_NULL);
	}
	
	public WrapProcess discardStderr() {
		return connectStderr(DEV_NULL);
	}
	
	public WrapProcess forwardIO() {
		connectStdin(System.in);
		return forwardOutput();
	}
	
	public WrapProcess forwardOutput() {
		connectStdout(System.out);
		connectStderr(System.err);
		return this;
	}
	
}
