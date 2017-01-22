package webcrawler.queue;

import java.util.Collection;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public final class QueueManager {
	
	public static final int MAX_THREADS_WAITING = 2;

	private static final LinkedBlockingQueue<Page> linksQueue = new LinkedBlockingQueue<>();
	private static final CyclicBarrier barrier = new CyclicBarrier(MAX_THREADS_WAITING);

	public static Page pull() throws InterruptedException{
		return linksQueue.take();
	}

	public static void push(Page url){
		linksQueue.add(url);
	}
	
	public static void push(Collection<Page> urls){
		linksQueue.addAll(urls);
	}

	public static boolean isEmpty(){
		return linksQueue.isEmpty();
	}

	public static int getThreadsWaiting(){
		return barrier.getNumberWaiting();
	}
	
	public static void await() throws InterruptedException, BrokenBarrierException{
		barrier.await();
	}
	
	public static void await(long timeout, TimeUnit unit) throws InterruptedException, BrokenBarrierException, TimeoutException{
		barrier.await(timeout, unit);
	}
}
