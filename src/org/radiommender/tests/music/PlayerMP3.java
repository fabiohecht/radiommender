/**
 * Copyright 2012 CSG@IFI
 * 
 * This file is part of Radiommender.
 * 
 * Radiommender is free software: you can redistribute it and/or modify it under the terms of the GNU General 
 * Public License as published by the Free Software Foundation, either version 3 of the License, or (at your 
 * option) any later version.
 * 
 * Radiommender is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the 
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License 
 * for more details.
 * 
 * You should have received a copy of the GNU General Public License along with Radiommender. If not, see 
 * http://www.gnu.org/licenses/.
 * 
 */
package org.radiommender.tests.music;

import java.io.File;
import java.io.IOException;
 
import javax.sound.sampled.*;

public class PlayerMP3 {
	
	public static void main(String[] args) {
			AudioInputStream din = null;
			try {
				File file = new File("/home/bkey/test.mp3");
				AudioInputStream in = AudioSystem.getAudioInputStream(file);
				AudioFormat baseFormat = in.getFormat();
				AudioFormat decodedFormat = new AudioFormat(
						AudioFormat.Encoding.PCM_SIGNED,
						baseFormat.getSampleRate(), 16, baseFormat.getChannels(),
						baseFormat.getChannels() * 2, baseFormat.getSampleRate(),
						false);
				din = AudioSystem.getAudioInputStream(decodedFormat, in);
				DataLine.Info info = new DataLine.Info(SourceDataLine.class, decodedFormat);
				SourceDataLine line = (SourceDataLine) AudioSystem.getLine(info);
				if(line != null) {
					line.open(decodedFormat);
					byte[] data = new byte[4096];
					// Start
					line.start();
					
					int nBytesRead;
					while ((nBytesRead = din.read(data, 0, data.length)) != -1) {	
						line.write(data, 0, nBytesRead);
					}
					// Stop
					line.drain();
					line.stop();
					line.close();
					din.close();
				}
				
			}
			catch(Exception e) {
				e.printStackTrace();
			}
			finally {
				if(din != null) {
					try { din.close(); } catch(IOException e) { }
				}
			}
		}
	 
}
	

