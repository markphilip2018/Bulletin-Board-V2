import java.rmi.Naming;

public class MainServer {

	public static void main(String[] args) {
		
		try {
			
			int readersSize = Integer.valueOf(args[0]);
			int writersSize = Integer.valueOf(args[1]);
			int numberOfAccess = Integer.valueOf(args[2]);
			int totalConnection = (readersSize + writersSize) * numberOfAccess;
			String serverIp = args[3];
			String port = args[4];
			System.setProperty("java.rmi.server.hostname", serverIp);
			Naming.rebind("rmi://"+serverIp+":"+port+"/MyServer",new RMIServer(readersSize,writersSize,numberOfAccess,totalConnection));  	
			//System.setProperty("java.rmi.server.hostname", "192.168.1.187");
			System.out.println("Server is ready");
		
		} catch (Exception e) {
		
    		System.out.println("[System] Server failed: " + e);
		
		}
		
		System.out.println("end of server file");
	}

}
