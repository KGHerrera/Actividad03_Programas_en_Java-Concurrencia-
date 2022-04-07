
// Fig. 23.13: SharedBufferTest.java
// Application with two threads manipulating an unsynchronized buffer.
import java.security.SecureRandom;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class PruebaActividad {
	public static void main(String[] args) throws InterruptedException {

		// Producer/Consumer Relationship WITHOUT Synchronization
		// create new thread pool
		ExecutorService executorService = Executors.newCachedThreadPool();

		// create UnsynchronizedBuffer to store ints
		Buffer sharedLocation = new UnsynchronizedBuffer();
		System.out.println("Action\t\tValue\tSum of Produced\tSum of Consumed");
		System.out.printf("------\t\t-----\t---------------\t---------------%n%n");

		// execute the Producer and Consumer, giving each
		// access to the sharedLocation
		executorService.execute(new Producer(sharedLocation));
		executorService.execute(new Consumer(sharedLocation));

		executorService.shutdown(); // terminate app when tasks complete
		executorService.awaitTermination(1, TimeUnit.MINUTES);

		// Producer/Consumer Relationship WITH Synchronization
		// create new thread pool
		System.out.println();

		ExecutorService executorService2 = Executors.newCachedThreadPool();
		Buffer sharedLocation2 = new BlockingBuffer();

		executorService2.execute(new Producer2(sharedLocation2));
		executorService2.execute(new Consumer2(sharedLocation2));

		executorService2.shutdown();
		executorService2.awaitTermination(1, TimeUnit.MINUTES);

		System.out.println();

		// Producer/Consumer Relationship: ArrayBlockingQueue

		// create a newCachedThreadPool
		ExecutorService executorService3 = Executors.newCachedThreadPool();

		// create SynchronizedBuffer to store ints
		Buffer sharedLocation3 = new SynchronizedBuffer();

		System.out.printf("%-40s%s\t\t%s%n%-40s%s%n%n", "Operation", "Buffer", "Occupied", "---------",
				"------\t\t--------");

		// execute the Producer and Consumer tasks
		executorService3.execute(new Producer2(sharedLocation3));
		executorService3.execute(new Consumer2(sharedLocation3));

		executorService3.shutdown();
		executorService3.awaitTermination(1, TimeUnit.MINUTES);

	}
}

class Producer2 implements Runnable, Buffer {
	private static final SecureRandom generator = new SecureRandom();
	private final Buffer sharedLocation; // reference to shared object

	// constructor
	public Producer2(Buffer sharedLocation) {
		this.sharedLocation = sharedLocation;
	}

	// store values from 1 to 10 in sharedLocation
	@Override
	public void run() {
		int sum = 0;

		for (int count = 1; count <= 10; count++) {
			try { // sleep 0 to 3 seconds, then place value in Buffer
				Thread.sleep(generator.nextInt(3000)); // random sleep
				sharedLocation.blockingPut(count); // set value in buffer
				sum += count; // increment sum of values
				// System.out.printf("\t%2d%n", sum);
			} catch (InterruptedException exception) {
				Thread.currentThread().interrupt();
			}
		}

		System.out.printf("Producer done producing%nTerminating Producer%n");
	}

	@Override
	public void blockingPut(int value) throws InterruptedException {
		// TODO Auto-generated method stub

	}

	@Override
	public int blockingGet() throws InterruptedException {
		// TODO Auto-generated method stub
		return 0;
	}
}

class Consumer2 implements Runnable {
	private static final SecureRandom generator = new SecureRandom();
	private final Buffer sharedLocation; // reference to shared object

	public Consumer2(Buffer sharedLocation) {
		this.sharedLocation = sharedLocation;

	}

	@Override
	public void run() {
		int sum = 0;

		for (int count = 1; count <= 10; count++) {
			// sleep 0 to 3 seconds, read value from buffer and add to sum
			try {
				Thread.sleep(generator.nextInt(3000));
				sum += sharedLocation.blockingGet();
				// System.out.printf("\t\t\t%2d%n", sum);
			} catch (InterruptedException exception) {
				Thread.currentThread().interrupt();
			}

		}

		System.out.printf("%n%s %d%n%s%n", "Consumer read values totaling", sum, "Terminating Consumer");
	}
}
