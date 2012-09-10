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

import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

import org.junit.Test;
import org.radiommender.core.Application;
import org.radiommender.model.PlayListEntry;
import org.radiommender.model.Song;
import org.radiommender.playlist.PlayListQueue;


public class TestPlayListQueue {
	
	@Test
	public void testInsertEntriesMulti() {
		
		float[] total = new float[6];

		for (int i=0; i<100; i++) {
			float[] q = testInsertEntries();
			
			int j=0;
			for (float rating : q) {
				total[j] += rating;
				j++;
			}

		}
		
		int j=0;
		for (float rating : total) {
			System.out.println(j+":"+rating);
			j++;
		}

	}
	
	public float[] testInsertEntries() {
		
		PlayListQueue<PlayListEntry> q = new PlayListQueue<PlayListEntry>(6);
		
		PlayListEntry[] e = new PlayListEntry[5];
		
		for (int i=0; i<5; i++) {    		
			float random = Application.random.nextFloat();

			e[i] = new PlayListEntry(new Song(i+"",i+"",i+"",i+""), "ST", random);
			q.offer(e[i]);
		}
		
		q.offer(new PlayListEntry(new Song("A","A","A","A"), "AN", -1F));

		
		float[] out = new float[6];
		for (int i=0; i<6; i++) {
			out[i] = q.poll().getRating();
			System.out.print("	"+i+":"+out[i]);
		}
		System.out.println();

		return out;
	}
	
	/*
	@Test
	public void testInsertEntriesNQ() {
		
		LinkedBlockingQueue<PlayListEntry> q = new LinkedBlockingQueue<PlayListEntry>(5);
		
		PlayListEntry[] e = new PlayListEntry[5];
		
		for (int i=0; i<5; i++) {
			e[i] = new PlayListEntry(new Song(i+"",i+"",i+"",i+""), "XX", 10F-i);
			System.out.println("offer: "+e[i]);
			q.offer(e[i]);
			System.out.println(q.toString());
		}
	}
	*/
}
