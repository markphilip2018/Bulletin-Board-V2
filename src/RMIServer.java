import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.LinkedList;

public class RMIServer extends UnicastRemoteObject implements RMIInterface {

	private static int newsData = -1;
	static int counter = 0;
	static int sSeq = 0;
	static int rSeq = 0;
	static LinkedList<String> readerTuples = new LinkedList<String>();
	static LinkedList<String> writerTuples = new LinkedList<String>();
	static int rNum = 0;
	static int readersSize;
	static int writersSize;
	static int numberOfAccess;
	static int totalConnection;

	protected RMIServer(int aReadersSize, int aWritersSize, int aNumberOfAccess, int aTotalConnection)
			throws RemoteException {
		super();
		readersSize = aReadersSize;
		writersSize = aWritersSize;
		numberOfAccess = aNumberOfAccess;
		totalConnection = aTotalConnection;

	}

	@Override
	public int[] readData(int id) throws RemoteException {

		changeRNumBy(1);
		
		int[] readData = new int[3];
		readData[0] = getRSeq();
		
		try {
			Thread.sleep((long) (Math.random() * 1000));
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		int[] data = getSSeqAndData(readData[0], id);
		readData[1] = data[0];
		readData[2] = data[1];
		
		changeRNumBy(-1);
		
//		if(changeAccessNumBy(1) == totalConnection) {
//			closeServer();
//		}
		

		serverlog();
		
		return readData;
	}

	private synchronized int changeRNumBy(int change) {
		rNum += change;
		return rNum;
	}

	private synchronized int changeAccessNumBy(int change) {
		counter += change;
		return counter;
	}

	private synchronized int[] getSSeqAndData(int myRSeq, int id) {

		int mySSeq = getSSeq();

		readerTuples.add( mySSeq + " " + readAndWriteDate(id,true) + " " + id + " " +  changeRNumBy(0));

		return new int[] { mySSeq, newsData };
	}

	private synchronized int getRSeq() {

		return ++rSeq;
	}

	private synchronized int getSSeq() {

		return ++sSeq;
	}
	
	private synchronized int readAndWriteDate(int data , boolean isRead) {
		
		if(isRead) {
			return newsData;
		}else {
			newsData = data;
		}
		return data;
	}

	@Override
	public int[] writeData(int id) throws RemoteException {

		int[] returnData = new int[2];
		returnData[0] = getRSeq();

		try {
			Thread.sleep((long) (Math.random() * 1000));
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		returnData[1] = wirteNewsData(returnData[0], id);

		

//		if(changeAccessNumBy(1) == totalConnection) {
//			closeServer();
//		}
		serverlog();
		
		return returnData;
	}

	private synchronized int wirteNewsData(int myRSeq, int id) {

		int mySSeq = getSSeq();

		writerTuples.add(mySSeq + " " + id + " " + id );

		readAndWriteDate(id, false);
		
		return mySSeq;
	}

	private void serverlog() {
		PrintWriter writer;

		try {
			writer = new PrintWriter("serverlog.txt", "UTF-8");
			writer.println("Readers :");
			writer.println("sSeq oVal rID rNum");
			for (int i = 0; i < readerTuples.size(); i++) {
				writer.println(readerTuples.get(i));
			}
			writer.println("writers :");
			writer.println("sSeq oVal wID");
			for (int i = 0; i < writerTuples.size(); i++) {
				writer.println(writerTuples.get(i));
			}
			writer.close();
		} catch (FileNotFoundException | UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		
	}

	
	/*
	private void closeServer() {

		
		(new ServerEnd()).run();
		
		System.out.println("system ending...");
		
	}


	private class ServerEnd implements Runnable{
		
		public void run() {
			PrintWriter writer;

			try {
				writer = new PrintWriter("serverlog.txt", "UTF-8");
				writer.println("Readers :");
				writer.println("sSeq oVal rID rNum");
				for (int i = 0; i < readerTuples.size(); i++) {
					writer.println(readerTuples.get(i));
				}
				writer.println("writers :");
				writer.println("sSeq oVal wID");
				for (int i = 0; i < writerTuples.size(); i++) {
					writer.println(writerTuples.get(i));
				}
				writer.close();
			} catch (FileNotFoundException | UnsupportedEncodingException e) {
				e.printStackTrace();
			}

			System.exit(0);
	
		}
	}
		
		*/
	
	
	
}
