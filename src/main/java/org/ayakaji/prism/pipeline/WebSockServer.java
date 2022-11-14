package org.ayakaji.prism.pipeline;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.net.InetSocketAddress;
import java.util.Map;
import java.util.logging.Logger;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

public class WebSockServer extends WebSocketServer {

	private static Logger logger = Logger.getLogger(WebSockServer.class.getName());

	private static class Diagnosis implements Runnable {

		public void run() {
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				logger.warning(e.getMessage());
			}
			Map<Thread, StackTraceElement[]> map = Thread.getAllStackTraces();
			for (StackTraceElement[] stes : map.values()) {
				System.out.println("[");
				for (StackTraceElement ste : stes) {
					System.out.println(
							"   " + ste.getClassName() + "::" + ste.getMethodName() + "(" + ste.getLineNumber() + ")");
				}
				System.out.println("]");
			}
		}

	}

	public static void main(String[] args)
			throws IOException, InterruptedException, ClassNotFoundException, SecurityException, NoSuchMethodException,
			IllegalArgumentException, IllegalAccessException, InvocationTargetException {
//		Class<?> c = Class.forName("org.ayakaji.prism.agent.common.ConnInsight");
//		Method m = c.getMethod("main", String[].class);
//		m.invoke(null, (Object) new String[] {""});
		WebSockServer server = new WebSockServer(8888);
		server.start();
		Thread thread = new Thread(new Diagnosis());
		thread.start();
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		while (true) {
			String in = br.readLine();
			server.broadcast(in);
			if (in.equals("exit")) {
				server.stop(5000);
				break;
			}
		}
	}

	public WebSockServer(int port) {
		super(new InetSocketAddress(port));
	}

	@Override
	public void onOpen(WebSocket conn, ClientHandshake handshake) {
		InetSocketAddress isaClnt = conn.getRemoteSocketAddress();
		if (isaClnt == null)
			logger.warning("Illegal remote socket address!");
		InetSocketAddress isaSrv = conn.getLocalSocketAddress();
		if (isaSrv == null)
			logger.warning("Illegal server socket address!");
		logger.info(isaClnt.getHostName() + ":" + isaClnt.getPort() + " <-----> " + isaSrv.getHostName() + ":"
				+ isaSrv.getPort());
	}

	@Override
	public void onClose(WebSocket conn, int code, String reason, boolean remote) {
		InetSocketAddress isaClnt = conn.getRemoteSocketAddress();
		if (isaClnt == null)
			logger.warning("Illegal remote socket address!");
		InetSocketAddress isaSrv = conn.getLocalSocketAddress();
		if (isaSrv == null)
			logger.warning("Illegal server socket address!");
		logger.info(isaClnt.getHostName() + ":" + isaClnt.getPort() + " <--X--> " + isaSrv.getHostName() + ":"
				+ isaSrv.getPort());
		logger.info("Close Code : " + code);
		if (reason != null && !reason.equals(""))
			logger.info("The reason of closing this connection : " + reason);
		if (remote)
			logger.info("The closing of the connection was initiated by the remote host.");
	}

	@Override
	public void onMessage(WebSocket conn, String message) {
		logger.info("Message : " + message);
	}

	@Override
	public void onError(WebSocket conn, Exception ex) {
		logger.info("Exception : " + ex.getMessage());
	}

	@Override
	public void onStart() {
		logger.info("The server started up successfully!");
	}

}
