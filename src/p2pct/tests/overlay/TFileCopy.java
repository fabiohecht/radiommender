/**
 * 
 */
package p2pct.tests.overlay;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

/**
 * @author nicolas baer
 *
 */
public class TFileCopy {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		File songFile = new File("/Users/nicolasbar/Desktop/music/Waldeck/Fallen angel.mp3");
		
		// get byte array from file
		FileInputStream fileInputStream;
		try {
			fileInputStream = new FileInputStream(songFile);
			byte[] data = new byte[(int) songFile.length()];
			fileInputStream.read(data);
			fileInputStream.close();
			
			File f = new File("/Users/nicolasbar/Desktop/wicked.mp3");
			f.createNewFile();
			
			FileOutputStream fos = new FileOutputStream(f);
			fos.write(data);
			fos.close();
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		

				
	}

}
