package io;

import java.io.IOException;
import java.io.PrintStream;

public abstract class PrintStreamHandler {


	public abstract PrintStream getPrintStream();
	
	
	public abstract void close() throws IOException;
	

}
