package p2pct.tests;

import org.junit.Test;

import p2pct.controller.P2POverlay;
import p2pct.controller.songhandler.SongTagger;

public class TestSongTagger {
	
	@Test
	public void testGetWords() {
		SongTagger songTagger = new SongTagger(new P2POverlay());
		
		System.out.println(songTagger.getWords("The quick brown fox jumps over the lazy dog"));
		System.out.println(songTagger.getWords("The / \"quick b@rown fox jum0ps 69 over the lazy d\\og"));
	}
}
