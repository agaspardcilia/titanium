package utils.rcon;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class RconAnswerReceiver implements Runnable {

	private Lock lock;
	private Condition hasAnswer;


	private RconClient rcon;

	private ArrayList<String> receivedAnswers;

	public RconAnswerReceiver(RconClient rcon) {
		this.rcon = rcon;
		this.receivedAnswers = new ArrayList<>();
		this.lock = new ReentrantLock();
		hasAnswer = lock.newCondition();
	}


	@Override
	public void run() {
		Packet received = null;

		while (true) {
			try {
				received = rcon.read();
				receivedAnswers.add(received.getBody());
				lock.lock();
				try {
					hasAnswer.signal();

				} finally {
					lock.unlock();

				}
			} catch (IOException e) {
				System.out.println("Stream corrupted!");
				e.printStackTrace();
				break;
			}
		}

	}



	public String getNextAnswer() throws InterruptedException {
		String result;
		lock.lock();
		try {
			hasAnswer.await();
			result = receivedAnswers.remove(0);
		} finally {
			lock.unlock();

		}
		return result;
	}
}
