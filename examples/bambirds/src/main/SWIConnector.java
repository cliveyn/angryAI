package main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

public class SWIConnector implements Runnable {
	private InputStream in;
	private OutputStream out;
	private InputStream err;
	private BufferedReader br;
	private String pathToSwipl = "";
	private String pathToFunctionsPL = "";
	private Process process = null;
	private String result = "";
	private boolean easyMain = false;

	public SWIConnector(String pathToSwipl, String pathToFunctionsPL) {
		this.pathToFunctionsPL = pathToFunctionsPL;
		this.pathToSwipl = pathToSwipl;
	}

	private void startProcess() {
		try {
			System.out.println("Starting SWIPL ...");
			System.out.println(pathToSwipl);
			String command2 = pathToSwipl + " -O -g consult('" + pathToFunctionsPL.replace("\\", "/") + "').";
			process = Runtime.getRuntime().exec(command2);
			System.out.println(command2);

			Thread.sleep(1000);

			in = process.getInputStream();
			out = process.getOutputStream();
			err = process.getErrorStream();

			Thread thread1 = new Thread(new Runnable() {
				@Override
				public void run() {
					String line;
					BufferedReader r = new BufferedReader(new InputStreamReader(err));
					try {
						while ((line = r.readLine()) != null) {
							System.err.println(line);
						}
					} catch (IOException e) {
					}
				}
			});
			thread1.start();

			if(easyMain){
				writeCommand("easyMain; halt.");
			}else{
				writeCommand("main; halt.");
			}
			easyMain = false;

			br = new BufferedReader(new InputStreamReader(in));

			String line;
			while (process.isAlive()) {
				line = br.readLine();
				if (line != null && line.trim().replace(System.getProperty("line.separator"), "") != "") {
					System.out.println(line);
					synchronized (result) {
						result = line;
					}
				}
			}

			System.out.println(process.exitValue());
		} catch (IOException e) {
			System.out.println("Failed init SWIPL " + e.getMessage());
		} catch (InterruptedException e) {
		}
	}

	@Override
	public void run() {
		while (true) {
            System.out.println("Initiating Prolog start...");
			startProcess();
		}
	}

	public void writeCommand(String command) {
		try {
			if (out == null) {
				return;
			}
			synchronized (result) {
				result = "";
			}

			out.write((command.replace("\\", "/") + System.lineSeparator()).getBytes());
			out.flush();
			System.out.println("Wrote command " + command.replace("\\", "/"));
		} catch (IOException e) {
		}
	}

	public String getResult(long timeOut) {
		long start = System.currentTimeMillis();

		while (true) {
			if(out != null){
				try {
					out.flush();
				} catch (IOException e) {
				}
			}
			synchronized (result) {
				if (result != "") {
					return result;
				}
				if (System.currentTimeMillis() - start > timeOut) {
					System.err.println("No Return from Prolog after "+timeOut+" milliseconds");
					synchronized(process){
						if(process != null){
							process.destroyForcibly();
							easyMain = true;
						}
					}
					try {
						Thread.sleep(3000);
					} catch (InterruptedException e) {
					}
					return "";
				}
			}
		}
	}
}
