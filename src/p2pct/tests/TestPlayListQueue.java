package p2pct.tests;

import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

import org.junit.Test;

import p2pct.P2PChallengeTask;
import p2pct.controller.PlayListQueue;
import p2pct.model.PlayListEntry;
import p2pct.model.Song;

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
			float random = P2PChallengeTask.random.nextFloat();

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
