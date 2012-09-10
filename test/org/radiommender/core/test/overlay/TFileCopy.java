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
package org.radiommender.core.test.overlay;

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
