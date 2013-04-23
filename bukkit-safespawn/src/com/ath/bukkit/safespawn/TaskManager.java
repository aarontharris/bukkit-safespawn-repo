package com.ath.bukkit.safespawn;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class TaskManager {

	public static final ScheduledExecutorService schex = Executors.newScheduledThreadPool( 1 );
	public static final long delayBetween = 60; // seconds



	public static abstract class Task implements Runnable {
	}

	public Queue<Task> tasks = new LinkedList<Task>();

	public TaskManager() {
		init();
	}

	public void init() {
		schex.scheduleWithFixedDelay( new Runnable() {
			@Override
			public void run() {
				for ( Task task : tasks ) { // don't pop them off, we plan to rerun these tasks indefinitely
					task.run();
				}
			}
		}, delayBetween, delayBetween, TimeUnit.SECONDS );
	}

	public void shutdown() {
		schex.shutdown();
	}

	public void addTask( Task task ) {
		tasks.add( task );
	}
}
