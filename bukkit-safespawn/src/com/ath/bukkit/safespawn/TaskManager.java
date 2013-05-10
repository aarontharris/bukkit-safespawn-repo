package com.ath.bukkit.safespawn;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.ath.bukkit.safespawn.data.Task;

public class TaskManager {

	private static final long slowrepeatingdelayBetween = 5000; // millis
	private static final long fastrepeatingdelayBetween = 5; // millis
	private static final long doOncedelayBetween = 5; // millis

	private static ExecutorService mainExc = Executors.newFixedThreadPool( 8 ); // work is done here
	private ScheduledExecutorService scheduler; // polls for work at various intervals

	private Queue<Task> slowRepeatingTasks = new LinkedList<Task>();
	private Queue<Task> fastRepeatingTasks = new LinkedList<Task>();
	private Queue<Task> doOnceTasks = new ConcurrentLinkedQueue<Task>();

	public TaskManager() {
		reset();
	}

	public void reset() {
		scheduler = Executors.newScheduledThreadPool( 2 );

		scheduler.scheduleWithFixedDelay( new Runnable() {
			@Override
			public void run() {
				try {
					for ( Task task : slowRepeatingTasks ) { // don't pop them off, we plan to rerun these tasks indefinitely
						try {
							mainExc.execute( task );
						} catch ( Exception e ) {
							Log.error( e );
						}
					}
				} catch ( Exception e ) {
					Log.error( e );
				}
			}
		}, slowrepeatingdelayBetween, slowrepeatingdelayBetween, TimeUnit.MILLISECONDS );

		scheduler.scheduleWithFixedDelay( new Runnable() {
			@Override
			public void run() {
				try {
					for ( Task task : fastRepeatingTasks ) { // don't pop them off, we plan to rerun these tasks indefinitely
						try {
							mainExc.execute( task );
						} catch ( Exception e ) {
							Log.error( e );
						}
					}
				} catch ( Exception e ) {
					Log.error( e );
				}
			}
		}, fastrepeatingdelayBetween, fastrepeatingdelayBetween, TimeUnit.MILLISECONDS );


		scheduler.scheduleWithFixedDelay( new Runnable() { // check for tasks
					@Override
					public void run() {
						Task task = doOnceTasks.poll();
						if ( task != null ) {
							mainExc.execute( task );
						}
					}
				}, 0, doOncedelayBetween, TimeUnit.MILLISECONDS );
	}

	public void shutdown() {
		scheduler.shutdown();
	}

	public void addSlowRepeatingTask( Task task ) {
		slowRepeatingTasks.add( task );
	}

	public void addFastRepeatingTask( Task task ) {
		fastRepeatingTasks.add( task );
	}

	public void addNonRepeatingTask( final Task task ) {
		doOnceTasks.add( task );
	}
}
