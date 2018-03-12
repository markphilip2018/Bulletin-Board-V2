import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.LinkedList;

public class WriterClient {

	public static void main(String[] args) {

		LinkedList<String> clientReader = new LinkedList<>();
		String serverIp =  args[0];
		int port = Integer.valueOf(args[1]);
		int numberOfAccesses =  Integer.valueOf(args[2]);
		int clientNumber =  Integer.valueOf(args[3]);

		try {

			RMIInterface board = (RMIInterface) Naming.lookup("rmi://"+serverIp+":"+port+"/MyServer");

			for (int i = 0; i < numberOfAccesses; i++) {
				int[] data = board.writeData(clientNumber);
				clientReader.add(data[0]+" "+data[1]);
				Long start = System.currentTimeMillis();
				while (System.currentTimeMillis() - start < (Math.random() * 3000)) {
				}
			}
			

		} catch (MalformedURLException | RemoteException | NotBoundException e) {

			e.printStackTrace();
		}
		
		String filename = "log" + clientNumber + ".txt";
		PrintWriter writer = null;
		
		try {
			writer = new PrintWriter(filename, "UTF-8");
		} catch (FileNotFoundException |UnsupportedEncodingException e) {
			e.printStackTrace();
		} 
		
		writer.println("Client type: Writer");
		writer.println("Client Name: "+clientNumber);
		writer.println("rSeq sSeq ");
		
		for (int i = 0; i < clientReader.size(); i++) {
			writer.println(clientReader.get(i));
		}
		writer.close();

	}

}
