package org.seccanj.clans.gui;

public class FrameRate {
	private long lastTime;
	private long delta;
	private int frames;
	private int frameRate;
	
	public FrameRate() {
		lastTime = System.currentTimeMillis();
	}
	
	public void newFrame() {
		long currentTime  = System.currentTimeMillis();
		delta += currentTime - lastTime;
		lastTime = currentTime;
		frames++;
		
		if(delta > 1000){
			delta -= 1000;
			frameRate = frames;
			frames = 0;
		}
	}
	
	public long getFrameRate() {
		return frameRate;
	}
}
