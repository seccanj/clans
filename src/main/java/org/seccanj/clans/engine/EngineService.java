package org.seccanj.clans.engine;

import org.kie.api.runtime.KieSession;
import org.seccanj.clans.Clans;
import org.seccanj.clans.gui.GuiContext;
import org.seccanj.clans.model.World;

public class EngineService extends Thread {

    private World world;

	private KieSession kSession;
	
	private Clans gui;
	
    public EngineService(World world, KieSession kSession, Clans gui) {
        this.world = world;
		this.kSession = kSession;
		this.gui = gui;
    }

    @Override
    public void run() {
        EngineTask task = new EngineTask(world, kSession);

    	boolean endOfLife = false;
    	
    	do {
	        task.run();
	     
        	GuiContext guiContext = task.getGuiContext();
            gui.drawFrame(guiContext);
	        
            endOfLife = guiContext.isEndOfLife();
	        
    	} while (!endOfLife);
    }
}