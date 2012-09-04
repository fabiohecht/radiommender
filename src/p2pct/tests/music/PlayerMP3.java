package p2pct.tests.music;

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
	

