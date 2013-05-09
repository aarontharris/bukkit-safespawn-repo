package com.ath.bukkit.safespawn;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.ath.bukkit.safespawn.data.Task;

public class TaskManager {

	private static final long repeatingdelayBetween = 5; // seconds
	private static final long doOncedelayBetween = 5; // millis

	private ScheduledExecutorService repeatingExc;
	private Queue<Task> repeatingTasks = new LinkedList<Task>();

	private ScheduledExecutorService doOnceExc;
	private Queue<Task> doOnceTasks = new ConcurrentLinkedQueue<Task>();

	public TaskManager() {
		reset();
	}

	public void reset() {
		repeatingExc = Executors.newScheduledThreadPool( 1 );
		repeatingExc.scheduleWithFixedDelay( new Runnable() {
			@Override
			public void run() {
				try {
					for ( Task task : repeatingTasks ) { // don't pop them off, we plan to rerun these tasks indefinitely
						try {
							task.run();
						} catch ( Exception e ) {
							Log.error( e );
						}
					}
				} catch ( Exception e ) {
					Log.error( e );
				}
			}
		}, repeatingdelayBetween, repeatingdelayBetween, TimeUnit.SECONDS );

		doOnceExc = Executors.newScheduledThreadPool( 1 );
		doOnceExc.scheduleWithFixedDelay( new Runnable() {
			@Override
			public void run() {
				Task t = doOnceTasks.poll();
				if ( t != null ) {
					t.run();
				}
			}
		}, 0, doOncedelayBetween, TimeUnit.MILLISECONDS );
	}

	public void shutdown() {
		repeatingExc.shutdown();
	}

	public void addRepeatingTask( Task task ) {
		repeatingTasks.add( task );
	}

	public void addNonRepeatingTask( final Task task ) {
		doOnceTasks.add( task );
	}
}
