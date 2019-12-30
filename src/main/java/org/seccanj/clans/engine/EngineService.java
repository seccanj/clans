package org.seccanj.clans.engine;

import org.kie.api.runtime.KieSession;
import org.seccanj.clans.ClansJavaFx;
import org.seccanj.clans.gui.GuiContext;
import org.seccanj.clans.model.World;

import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;

public class EngineService extends Service<GuiContext> {

    private World world;

	private KieSession kSession;
	
	private ClansJavaFx gui;

    public EngineService(World world, KieSession kSession, ClansJavaFx gui) {
        this.world = world;
		this.kSession = kSession;
		this.gui = gui;
    }

    protected Task<GuiContext> createTask() {
        EngineTask task = new EngineTask(world, kSession);
        
        task.setOnSucceeded((WorkerStateEvent event) -> {
            // another hook - callback lambda
            //System.out.println("In set on Succeded");
            
        	GuiContext guiContext = (GuiContext) event.getSource().getValue();
            gui.drawFrame(guiContext);
        });
        
        task.setOnFailed((WorkerStateEvent event) -> {
            // another hook - callback lambda
            //System.out.println("In Failed");
        });
        
        return task;
    }

    @Override
    protected void scheduled() {
        //System.out.println("In Scheduled");
    	super.scheduled();
    }
    
    private void started() {
        //System.out.println("In Started");
	}
    
    @Override
    protected void running() {
        //System.out.println("In Running");
    	super.running();
    }
    
    @Override
    protected void succeeded() {
        //System.out.println("In Succeded");
    	super.succeeded();
    }
}