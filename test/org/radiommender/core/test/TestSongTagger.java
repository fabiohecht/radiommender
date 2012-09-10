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
package org.radiommender.core.test;

import org.junit.Test;
import org.radiommender.overlay.Overlay;
import org.radiommender.tagger.SongTagger;


public class TestSongTagger {
	
	@Test
	public void testGetWords() {
		SongTagger songTagger = new SongTagger(new Overlay());
		
		System.out.println(songTagger.getWords("The quick brown fox jumps over the lazy dog"));
		System.out.println(songTagger.getWords("The / \"quick b@rown fox jum0ps 69 over the lazy d\\og"));
	}
}
