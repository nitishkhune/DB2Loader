package com.sjsu.db2;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public class UnixPipe
{
	FileChannel fc;
	int multiTables[] = new int[1];
	String filesep = System.getProperty("file.separator");
	String fileName, OUTPUT_DIR = ".", pipeName;
	int pipeBuffer = 131072, fileBuffer = 8192;
	
	public UnixPipe(String fileName, String output)
	{
		this.fileName = fileName;
		this.OUTPUT_DIR = output;
		multiTables[0] = 0;
	}
	
	private void log(String message)
	{
		System.out.println(message);		
	}
	
	public void runPipe()
	{
		int bytesReturn;
		//pipeName = OUTPUT_DIR + "data" + filesep + pipeName + ".pipe";
	pipeName = "/home/nitish2008/workspace/Db2Loader/abj";
	
		
		File pipeFile = new File(pipeName);
pipeFile.deleteOnExit();
		if (!pipeFile.exists())
		{
			try
			{
				Runtime.getRuntime().exec("mkfifo " + pipeFile.getAbsolutePath());
			} catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		FileOutputStream fos = null;
		try
		{
			if (multiTables[0] == 0)
			{
				fos = new FileOutputStream(pipeFile);
				fc = fos.getChannel();        					
			} else
			{
				fc = fc; // Use same file channel for parallel unload of the same table
			}
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		try
		{
			File f1 = new File(this.fileName);
			InputStream in = new FileInputStream(f1);
			log("Sending data to the pipe");
			byte[] buf = new byte[fileBuffer];
		    int len;
		    while ((len = in.read(buf)) > 0)
		    {
		    	bytesReturn = fc.write(ByteBuffer.wrap(buf));		    	
				log("Sent " + len + "/" + bytesReturn + " bytes to the pipe");
				if (bytesReturn == -1)
				{
					log("Error Writing to pipe " + pipeName);
				}					    
		    }
		    in.close();
			log("Writing to the pipe completed.");
			Process p2 = Runtime.getRuntime().exec("cat "+pipeName);
			System.out.println("p2 created");
			//new InputStreamReader(p1.getInputStream())
			//new InputStreamReader(p2.getInputStream())
			BufferedReader reader = new BufferedReader(new InputStreamReader(p2.getInputStream())); 
			String line; 
			while((line = reader.readLine()) != null) 
			{ 
					System.out.println(line);
				
			}
			//System.out.println("Pipe Created");
			 
			
		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) throws IOException, InterruptedException 
	{
		String output = ".";
		String fileName = "/home/nitish2008/Downloads/pipeload/db2tabledata.txt";
		UnixPipe testPipe = new UnixPipe(fileName, output);
		testPipe.runPipe();
	}
}
