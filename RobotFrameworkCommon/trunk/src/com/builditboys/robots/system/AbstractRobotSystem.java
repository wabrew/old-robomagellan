package com.builditboys.robots.system;

import java.io.IOException;

import com.builditboys.robots.communication.AbstractProtocol.ProtocolRoleEnum;
import com.builditboys.robots.communication.AbstractLink;
import com.builditboys.robots.communication.AbstractProtocol;
import com.builditboys.robots.communication.LinkPortInterface;
import com.builditboys.robots.communication.MasterLink;
import com.builditboys.robots.driver.RobotDriverProtocol;
import com.builditboys.robots.infrastructure.DistributionList;
import com.builditboys.robots.infrastructure.ParameterInterface;
import com.builditboys.robots.infrastructure.ParameterServer;
import com.builditboys.robots.infrastructure.StringParameter;
import com.builditboys.robots.time.LocalTimeSystem;
import com.builditboys.robots.time.TimeSyncProtocol;

/*
The concept.

The robot system is responsible for getting things started up and shut down. 
AbstractRobotSystem takes care of most of the basic setup and shut down.  You
make subclasses that do more specific setup and shut down.  You can have several
layers in the hierarchy.  For example, the class WindowsRobotSystem does windows
specific setup and shutdown but thats about it.  For your specific robot, you
probably need to extend WindowsRobotSystem to actually do something by creating
a thread that embodies the robot's mission or top-level.

Each layer in the hierarchy has responsibilities.  A leaf layer creates an
instance of itself, stuffs it in INSTANCE and then calls its own copy of 
startRobotSystem.  This method does some specific setup and then calls
super.startRobotSystem.

Similar situation exists with stopRobotSystem.

*/


public abstract class AbstractRobotSystem implements ParameterInterface {

	// Holds the single robot system instance.
	// Not a final but pretty close, gets set in when a subclass is created
	// and cannot be set again.
	private static AbstractRobotSystem INSTANCE;
	

	private static final SystemNotification START1_NOTICE = SystemNotification.newStart1Notification();
	private static final SystemNotification START2_NOTICE = SystemNotification.newStart2Notification();
	private static final SystemNotification START3_NOTICE = SystemNotification.newStart3Notification();
	private static final SystemNotification ESTOP_NOTICE = SystemNotification.newEstopNotification();
	private static final SystemNotification STOP_NOTICE = SystemNotification.newStopNotification();

	private static final int ROBOT_SYSTEM_PHASE_WAIT = 200;

	protected DistributionList systemDistList = SystemNotification.getDistributionList();

	// --------------------------------------------------------------------------------
	
	public static AbstractRobotSystem getInstance() {
		return INSTANCE;
	}

	protected static void setInstance(AbstractRobotSystem instance) {
		if (INSTANCE == null) {
			INSTANCE = instance;
		}
		else {
			throw new IllegalStateException("Robot system instance is already set");
		}
	}
	
	// --------------------------------------------------------------------------------
	// Build
	
	protected void build () throws IOException {
		ParameterServer.addParameter(this);
	}
	
	// --------------------------------------------------------------------------------
	// Start
	
	protected void start () throws InterruptedException, IOException {
		// set up local time first, many things depend on it including notifications
		LocalTimeSystem.startLocalTimeNow();
		System.out.println("Local time initialized");

	}
		
	// --------------------------------------------------------------------------------
	// Stop
	
	protected synchronized void stop() throws InterruptedException, IOException {
	}

	// --------------------------------------------------------------------------------
	// Destroy
	
	protected void destroy () {
	}

	// --------------------------------------------------------------------------------
	// Let other stuff stop the robot system

	public static void stopTheRobotSystem () throws InterruptedException, IOException {
		AbstractRobotSystem.getInstance().stop();
	}
	
	public static void safeStopTheRobotSystem () {
		try {
			stopTheRobotSystem();
		} catch (Exception e) {
			System.out.println("Stop Exception");
			e.printStackTrace();
		}
	}

	// --------------------------------------------------------------------------------

	public static void stopTheRobotSystemRunnable () {
		TheRobotSystemStopper stopper = new TheRobotSystemStopper();
		Thread thread = new Thread(stopper, "Robot Stopper");
		System.out.println("Starting " + "Abstract Robot Stopper" + " thread");
		thread.start();
	}
	
	private static class TheRobotSystemStopper implements Runnable {
		public void run() {
			safeStopTheRobotSystem();
		}
	}
	
	// --------------------------------------------------------------------------------

	public static void acknowledgeRobotSystemError (String threadName, Exception e) {
		INSTANCE.acknowledgeRobotSystemErrorI(threadName, e);
	}
	
	public void acknowledgeRobotSystemErrorI (String threadName, Exception e) {
		System.out.println("Exception in thread " + threadName + ": " + e);
		e.printStackTrace();
		ESTOP_NOTICE.publish(INSTANCE, systemDistList);
	}
	
	// --------------------------------------------------------------------------------
	// For the parameter server
	
	public String getName () {
		return "ROBOT_SYSTEM";
	}
	
	public static AbstractRobotSystem getParameter (String key) {
		return (AbstractRobotSystem) ParameterServer.getParameter(key);
	}
	
	public static AbstractRobotSystem maybeGetParameter (String key) {
		return (AbstractRobotSystem) ParameterServer.maybeGetParameter(key);
	}
	
	// --------------------------------------------------------------------------------

}
