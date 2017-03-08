package Server;

import java.io.Closeable;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collection;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;

import Bean.ChatBean;
import Bean.ClientBean;

public class ServerManage {
	private static ServerSocket ss;
	public static Hashtable<String, ClientBean> onlines;
	public ServerManage() {
		try {
			ss = new ServerSocket(49153);
			onlines = new Hashtable<String,ClientBean>();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void start() {
		while(true){
			try {
				Socket acceped = ss.accept();
				new ClientThread(acceped).start();;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
	}
	class ClientThread extends Thread{
		public Socket client;
		public ChatBean bean;
		public ObjectInputStream ois;
		public ObjectOutputStream oos;
		public ClientThread(Socket socket) {
			this.client = socket;
		}
		public void run() {
			try {
				while(true){				
					ois = new ObjectInputStream(client.getInputStream());
					bean = (ChatBean) ois.readObject();	
					
					switch (bean.getType()) {
					case 0://上线
						{
							ClientBean chatclient = new ClientBean();
						
						chatclient.setName(bean.getName());
						chatclient.setSocket(client);
						onlines.put(bean.getName(), chatclient);
						
						ChatBean chatserver = new ChatBean();
						chatserver.setType(0);
						chatserver.setInfo(bean.getName()+"上线了"+bean.getTimer());
						HashSet<String> clientsname = new HashSet<String>();
						clientsname.addAll(onlines.keySet());
						chatserver.setClients(clientsname);
						sendAll(chatserver);
						break;
						}
					case -1:{
						//下线					
						ChatBean offlineclient = new ChatBean();
						offlineclient.setType(-1);
						
						oos = new ObjectOutputStream(client.getOutputStream());
						oos.writeObject(offlineclient);
						oos.flush();
						
						onlines.remove(bean.getName());
						ChatBean chatserver = new ChatBean();
						chatserver.setInfo(bean.getName()+"下线了"+bean.getTimer());
						HashSet<String> clientsname = new HashSet<String>();
						clientsname.addAll(onlines.keySet());
						chatserver.setClients(clientsname);
						sendAll(chatserver);
						return ;						
					}
					case 1:{//聊天
						 ChatBean chatserver = new ChatBean();
						 chatserver.setType(1);
						 chatserver.setClients(bean.getClients());
						 chatserver.setTimer(bean.getTimer());
						 chatserver.setInfo(bean.getInfo());
						 chatserver.setName(bean.getName());
						 
						 break;
					}
					default:
						break;
					}
				} 
			}catch (IOException | ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
			}finally {
				close();
			}
		}
		private void sendMessage(ChatBean chatserver) {
			Set<String> allclients = onlines.keySet();
			Iterator<String> itecli = allclients.iterator();
			HashSet<String> clients = chatserver.getClients();
			while (itecli.hasNext()) {
				String cli = itecli.next();
				if (clients.contains(cli)) {
					Socket client = onlines.get(cli).getSocket();
					ObjectOutputStream oos;
					try {
						oos = new ObjectOutputStream(client.getOutputStream());
						oos.writeObject(chatserver);
						oos.flush();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
				}
			}
		}
		private void sendAll(ChatBean chatserver) {
			Collection<ClientBean> clients = onlines.values();
			Iterator<ClientBean> itecli = clients.iterator();
			ObjectOutputStream oos ;
			while(itecli.hasNext()){
				Socket clisoc = itecli.next().getSocket();
				try {
					oos = new ObjectOutputStream(clisoc.getOutputStream());
					oos.writeObject(chatserver);
					oos.flush();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		public void close() {
			// TODO Auto-generated method stub
			if (oos != null) {
				try {
					oos.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if (ois != null) {
				try {
					ois.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if (client != null) {
				try {
					client.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
}

