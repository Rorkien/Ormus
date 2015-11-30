package com.ormus.lightweight;

public class ZerglingSpawner {
	
	public ZerglingSpawner(int amount) throws InterruptedException {
		for (int i = 0; i < amount; i++) {
			new LightweightClient("zrg" + i, "1234");
			Thread.sleep(60);
		}
	}
	
	public static void main(String[] args) {
		try {
			new ZerglingSpawner(300);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
