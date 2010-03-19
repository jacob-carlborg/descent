package scratch.net;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

import melnorme.miscutil.MiscUtil;

public class Socket_Snippet {
	
	
	private static final int SERVER_PORT = 1234;

	public static void main(String[] args) {
		
		
		final Thread serverThread = new Thread() {
			@Override
			public void run() {
				serverRun();
			}
		};
		serverThread.start();
		
		Socket socket;
		try {
			socket = new Socket("localhost", 1000);
		} catch (UnknownHostException e) {
			throw melnorme.miscutil.ExceptionAdapter.unchecked(e);
		} catch (IOException e) {
			throw melnorme.miscutil.ExceptionAdapter.unchecked(e);
		}
		
		
		try {
			int counter = 10000;
			while(counter-- > 0) {
				try {
					
					MiscUtil.sleepUnchecked(2000);
					socket.getOutputStream().write(getBytes("Once upon a time, Mary had a little lamb"));
				} catch (IOException e) {
					System.out.println(" isBound: " + socket.isBound());
					System.out.println(" isConnected: " + socket.isConnected());
					System.out.println(" isClosed: " + socket.isClosed());
					System.out.println(" isInputShutdown: " + socket.isInputShutdown());
					System.out.println(" isOutputShutdown: " + socket.isOutputShutdown());

					System.out.println(" getChannel().isConnected(): " + socket.getChannel().isConnected());
					System.out.println(" getChannel().isOpen(): " + socket.getChannel().isOpen());

					
					e.printStackTrace(System.out);
					if(false)
						throw e;
				}
			}
		} catch (IOException e) {
			throw melnorme.miscutil.ExceptionAdapter.unchecked(e);
		}
		try {
			socket.close();
		} catch (IOException e) {
			throw melnorme.miscutil.ExceptionAdapter.unchecked(e);
		}
		
		try {
			serverThread.join();
		} catch (InterruptedException e) {
			throw melnorme.miscutil.ExceptionAdapter.unchecked(e);
		}
	}

	/** ------- */
	
	private static ServerSocket createServerSocket() {
		try {
			return new ServerSocket(SERVER_PORT);
		} catch (IOException e) {
			throw melnorme.miscutil.ExceptionAdapter.unchecked(e);
		}
	}

	protected static void serverRun() {
		final ServerSocket serverSocket = createServerSocket();
		
		Socket socket;
		try {
			socket = serverSocket.accept();
		} catch (IOException e) {
			throw melnorme.miscutil.ExceptionAdapter.unchecked(e);
		}
		
		
		try {
			while(true) {
				echoServerCharacters(socket);
			}
		} catch (Exception e) {
			socket.isClosed();
			serverPrint("Server Socket exception:" + e);
			closeSocket(socket);
		}
	}

	private static void closeSocket(Socket socket) {
		try {
			socket.close();
		} catch (IOException e) {
			throw melnorme.miscutil.ExceptionAdapter.unchecked(e);
		}
	}

	private static void serverPrint(String msg) {
		System.out.println("SERVER>> " + msg);
	}

	private static void echoServerCharacters(Socket socket) {
		InputStream inputStream;
		try {
			inputStream = socket.getInputStream();
		} catch (IOException e) {
			throw melnorme.miscutil.ExceptionAdapter.unchecked(e);
		}
		
		byte[] bytes = new byte[16];
		int read;
		try {
			read = inputStream.read(bytes);
		} catch (IOException e) {
			throw melnorme.miscutil.ExceptionAdapter.unchecked(e);
		}
		
		if(read == -1) {
			return;
		}
		
		serverPrint("read :" + getString(bytes, read));
		
		OutputStream outputStream;
		try {
			outputStream = socket.getOutputStream();
		} catch (IOException e) {
			throw melnorme.miscutil.ExceptionAdapter.unchecked(e);
		}
		try {
			outputStream.write(bytes, 0, read);
		} catch (IOException e) {
			throw melnorme.miscutil.ExceptionAdapter.unchecked(e);
		}
	}


	
	/* ------------------------------------------------------------ */
	
	private static byte[] getBytes(String string) {
		try {
			return string.getBytes("ASCII");
		} catch (UnsupportedEncodingException e) {
			throw melnorme.miscutil.ExceptionAdapter.unchecked(e);
		}
	}
	
	private static String getString(byte[] bytes, int len) {
		try {
			return new String(bytes, 0, len, "ASCII");
		} catch (UnsupportedEncodingException e) {
			throw melnorme.miscutil.ExceptionAdapter.unchecked(e);
		}
	}

}
