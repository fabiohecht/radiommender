package p2pct.tests.music;

public class Player {
	
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		PlayMP3 test = new PlayMP3();
		Thread th = new Thread(test);
		th.start();
		
		
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.out.println("Gonna deactivate after the track");
		
		test.deactivate();
	}
	
	
}
