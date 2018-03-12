import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.Properties;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

public class Start {
	private String serverIp;
	private String serverPort;
	private String serverUsername;
	private String serverPassword;
	private LinkedList<ClientSSH> readers;
	private LinkedList<ClientSSH> writers;
	private int numberOfAccesses;

	public void runSSH(String host, String user, String password, String command) {

		int port = 22;

		try {
			JSch jsch = new JSch();
			Session session = jsch.getSession(user, host, port);
			session.setPassword(password);

			session.setConfig("StrictHostKeyChecking", "no");
			System.out.println("Establishing Connection...");
			session.connect();

			System.out.println("Connection established.");
			System.out.println("Creating SFTP Channel.");

			ChannelSftp sftpChannel = (ChannelSftp) session.openChannel("sftp");
			sftpChannel.connect();
			System.out.println("SFTP Channel created.");

			ChannelExec channelExec = (ChannelExec) session.openChannel("exec");
			channelExec.setCommand(command);
			channelExec.connect();

		} catch (Exception e) {
			System.err.print(e);
		}
	}

	public void runSSH(LinkedList<ClientSSH> clients, String command,int offset) {

		int port = 22;

		for (int i = 0; i < clients.size(); i++) {
			
			ClientSSH client = clients.get(i);
			
			try {
				JSch jsch = new JSch();
				Session session = jsch.getSession(client.getUsername(), client.getHost(), port);
				session.setPassword(client.getPassword());

				session.setConfig("StrictHostKeyChecking", "no");
				System.out.println("Establishing Connection...");
				session.connect();

				System.out.println("Connection established.");
				System.out.println("Creating SFTP Channel.");

				ChannelSftp sftpChannel = (ChannelSftp) session.openChannel("sftp");
				sftpChannel.connect();
				System.out.println("SFTP Channel created.");

				ChannelExec channelExec = (ChannelExec) session.openChannel("exec");
				channelExec.setCommand(command+" "+(i+offset));
				channelExec.connect();

			} catch (Exception e) {
				System.err.print(e);
			}

		}
	}

	public void parseConfiguration() {
		Properties prop = new Properties();
		InputStream input = null;

		try {
			input = new FileInputStream("system.properties");
			prop.load(input);

			serverIp = prop.getProperty("RW.server");
			serverUsername = prop.getProperty("RW.server.username");
			serverPassword = prop.getProperty("RW.server.password");
			serverPort = prop.getProperty("RW.server.port");

			System.out.println(serverIp);

			int numberOfReaders = Integer.valueOf(prop.getProperty("RW.numberOfReaders"));
			readers = new LinkedList<ClientSSH>();
			for (int i = 0; i < numberOfReaders; i++) {
				ClientSSH cl = new ClientSSH();
				cl.setHost(prop.getProperty("RW.reader" + i));
				cl.setUsername(prop.getProperty("RW.reader" + i + ".username"));
				cl.setPassword(prop.getProperty("RW.reader" + i + ".password"));
				readers.add(cl);
			}

			int numberOfWriters = Integer.valueOf(prop.getProperty("RW.numberOfWriters"));
			writers = new LinkedList<ClientSSH>();
			for (int i = 0; i < numberOfWriters; i++) {
				ClientSSH cl = new ClientSSH();
				cl.setHost(prop.getProperty("RW.writer" + i));
				cl.setUsername(prop.getProperty("RW.writer" + i + ".username"));
				cl.setPassword(prop.getProperty("RW.writer" + i + ".password"));
				writers.add(cl);
			}

			numberOfAccesses = Integer.valueOf(prop.getProperty("RW.numberOfAccesses"));

		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

	}

	public void runServer() {
		String command = "java MainServer "+readers.size()+" "+writers.size()+" "+numberOfAccesses + " " + serverIp+" "+serverPort;
		runSSH(serverIp, serverUsername, serverPassword, command);
	}

	public void runClientReader() {
		String command = "java ReaderClient " + serverIp + " " + serverPort +" " + numberOfAccesses;
		runSSH(readers, command,0);
	}
	
	public void runClientWriter() {
		String command = "java WriterClient " + serverIp +" " + serverPort +" " + numberOfAccesses;
		runSSH(writers, command,readers.size());
	}

	public static void main(String[] args) {
		Start st = new Start();
		st.parseConfiguration();
		st.runServer();
		st.runClientReader();
		st.runClientWriter();
		System.exit(0);
		
	}

}