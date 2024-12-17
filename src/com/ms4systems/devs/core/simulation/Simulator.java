package com.ms4systems.devs.core.simulation;

import java.io.Serializable;
import java.net.URI;
import java.util.ArrayList;

import com.ms4systems.devs.core.message.MessageBag;
import com.ms4systems.devs.core.model.AtomicModel;
import com.ms4systems.devs.events.SimulationEventListener;



public interface Simulator extends Serializable {

	/**
	 * Initialize the simulator and its model to begin simulation at simulationTime
	 * @param initialSimulationTime
	 * @return The time of the next scheduled event for this simulator
	 */
	void initialize(double initialSimulationTime);
	
	/***
	 * Tell the simulator to run the next transition currently scheduled
	 */
	void executeNextEvent(double time);
	
	/***
	 * Retrieve the output associated with the currently scheduled next transition
	 * @return A list of output messages generated by the currently scheduled next transition.
	 * 	This may be empty for non-internal or non-message generating transitions, but should never be null.
	 */
	MessageBag computeOutput();
	
	/***
	 * Schedules input to be received by the simulator at some future time
	 * @param input Input to process (the time property must be set to time that messages should be processed)
	 * @param time The time that this input should be received (must be non-negative and less than nextEventTime)
	 * 
	 */
	void processInput(double time, MessageBag input);
	
	/**
	 * @return The time of the next scheduled event.  May be Double.POSITIVE_INFINITY if nothing is scheduled.
	 */
	double getNextEventTime();
	
	/**
	 * @return The time of the last event that was processed.  Will be 0 if no events have been processed.
	 */
	double getLastEventTime();
	
	/***
	 * @return The current time of the simulation - must be correct if used by any model during an
	 * 	event.  E.g. a model should always be able to call getSimulator().getCurrentTime() during
	 * 	getOutput, transitions, etc. and get the current simulation time.
	 */
	double getCurrentTime();
	
	/**
	 * @return The model being simulated
	 */
	AtomicModel getAtomicModel();
	/**
	 * Sets the atomic model to simulate.  This cannot be changed after initialization.
	 * @param model
	 */
	void setAtomicModel(AtomicModel model);
	
	/**
	 * @return The parent coordinator managing this simulator, or null if this is the root simulator
	 */
	Coordinator getParent();
	/**
	 * Sets the parent coordinator for this simulator.  This cannot be changed after initialization.
	 * @param parent
	 */
	void setParent(Coordinator parent);
	
	/**
	 * @return True if this simulator has been initialized, false if not.
	 */
	boolean isInitialized();
	
	/***
	 * @return The unique URI identifying this simulator
	 */
	URI getURI();
	
	/***
	 * @return A list of all components within this simulator
	 */
	ArrayList<URI> getAllContents();
	
	/***
	 * @return The simulation in which the simulator is participating. If this is
	 * not the root simulator it should delegate to getParent().getSimulation()
	 */
	Simulation getSimulation();
	
	/***
	 * NOTE - Can only be called on the root simulator!
	 * @param simulation Set the simulation in which the simulator is participating.
	 */
	void setSimulation(Simulation simulation);
	
	void addSimulationEventListener(SimulationEventListener eventListener, boolean addRecursively);

	public abstract void modelMessage(String message);

	public abstract void clearCachedOutput();

	/** 
	 * The same as process input, but the simulator will silently discard any messages not 
	 * sent to a valid input port.  A coordinator will rebroadcast this to all child simulators.
	 * 
	 * @param time
	 * @param injectedInput
	 */
	void injectInput(double time, MessageBag input, MessageBag injectedInput);
}
