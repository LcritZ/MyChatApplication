package View;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.swing.*;
import javax.swing.border.Border;

import Model.ChatBean;

import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.ActionEvent;
//import javax.swing.BorderFactory;
//import javax.swing.ImageIcon;
//import javax.swing.JFrame;
//import javax.swing.JLabel;
//import javax.swing.JList;
//import javax.swing.JPanel;
//import javax.swing.JProgressBar;
//import javax.swing.JTextArea;
//import javax.swing.ListCellRenderer;

class CellRenderer extends JLabel implements ListCellRenderer {
	public CellRenderer() {
		setOpaque(true);
	}

	@Override
	public Component getListCellRendererComponent(JList list, Object value,
			int index, boolean isSelected,
			boolean cellHasFocus) {setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));// 加入宽度为5的空白边框

//			if (value != null) {
//				setText(value.toString());
//				setIcon(new ImageIcon("images//1.jpg"));
//			}
			if (isSelected) {
				setBackground(new Color(255, 255, 153));// 设置背景色
				setForeground(Color.black);
			} else {
				// 设置选取与取消选取的前景与背景颜色.
				setBackground(Color.white); // 设置背景色
				setForeground(Color.black);
			}
			setEnabled(list.isEnabled());
			setFont(new Font("sdf", Font.ROMAN_BASELINE, 13));
			setOpaque(true);
			return this;
	}
}

class UUListModel extends AbstractListModel{
	
	private Vector vs;
	
	public UUListModel(Vector vs){
		this.vs = vs;
	}

	@Override
	public Object getElementAt(int index) {
		// TODO Auto-generated method stub
		return vs.get(index);
	}

	@Override
	public int getSize() {
		// TODO Auto-generated method stub
		return vs.size();
	}
	
}

public class ChatRoom extends JFrame{
	private static final long serialVersionUID = 6129126482250125466L;

	private static JPanel contentPane;
	private static Socket clientSocket;
	private static ObjectOutputStream oos;
	private static ObjectInputStream ois;
	private static String name;
	private static JTextArea textArea;
	private static AbstractListModel listmodel;
	private static JList list_1;
	private static Vector onlines;
	private static JTextArea textArea_1;
	private static JTextArea textArea_2;
	private static JButton button;
	private static JButton button_1;
	private static JScrollPane scrollPane;
	
	//private JList list_1;
	
	public ChatRoom (String u_name, Socket client){
		
		name = u_name;
		clientSocket = client;
		onlines = new Vector();
		SwingUtilities.updateComponentTreeUI(this);
		
		try {
			UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException
				| UnsupportedLookAndFeelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		setTitle(name);
		setResizable(false);
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		setBounds(200,200,600,500);
		contentPane = new JPanel(){
			private static final long serialVersionUID = 1L;
			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				g.drawImage(new ImageIcon("images\\聊天室1.jpg").getImage(), 0, 0,
						getWidth(), getHeight(), null);
			}
		};
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		textArea_1 = new JTextArea();
		textArea_1.setEditable(false);
		textArea_1.setLineWrap(true);
		textArea_1.setWrapStyleWord(true);
		
		textArea_1.setBounds(10, 10, 336, 231);
		contentPane.add(textArea_1);
		
		
		//消息编辑
		textArea_2 = new JTextArea();
		textArea_2.setBounds(10, 277, 336, 52);
		contentPane.add(textArea_2);
		textArea_2.setLineWrap(true);
		textArea_2.setWrapStyleWord(true);
		
		button = new JButton("发送");
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				String info = textArea_2.getText();
				List sendlist = list_1.getSelectedValuesList();
				
				if (sendlist.size()<1) {
					JOptionPane.showMessageDialog(contentPane, "选择聊天对象");
					return;					
				}
				if(info.equals("")){
					JOptionPane.showMessageDialog(contentPane, "消息为空");
					return;
				}
				
				ChatBean clientbean = new ChatBean();
				clientbean.setType(1);
				clientbean.setName(name);
				String time = new SimpleDateFormat
						("yyyy-mm-dd HH:mm:ss").format(new Date());
				clientbean.setTimer(time);
				clientbean.setInfo(info);
				HashSet set = new  HashSet();
				set.addAll(sendlist);
				clientbean.setClients(set);
				
				textArea_1.append(time+"我:"+"\n"+info+"\n");
				sendMessage(clientbean);
				textArea_2.setText(null);
				textArea_2.requestFocus();
			}
		});
		button.setBounds(250, 368, 93, 23);
		getRootPane().setDefaultButton(button);
		contentPane.add(button);
		
		button_1 = new JButton("取消");
		button_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				textArea_2.setText(null);
				textArea_2.requestFocus();
			}
		});
		button_1.setBounds(121, 368, 93, 23);
		contentPane.add(button_1);
		
		listmodel = new UUListModel(onlines);
		list_1 = new JList();
		list_1.setCellRenderer(new CellRenderer());
		list_1.setOpaque(false);
		Border etch = BorderFactory.createEtchedBorder();
		
		list_1.setBounds(377, 57, 167, 184);
		//contentPane.add(list_1);
		
		scrollPane = new JScrollPane(list_1);
		scrollPane.setBounds(579, 290, -222, -242);
		scrollPane.setOpaque(false);
		scrollPane.getViewport().setOpaque(false);
		contentPane.add(scrollPane);
		
		
		
		JLabel label = new JLabel("在线用户：");
		label.setBounds(377, 10, 138, 32);
		contentPane.add(label);
		
		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				super.windowClosing(e);
				int result = JOptionPane.showConfirmDialog(contentPane, "是否确定离开");
				if (result == 0) {
					ChatBean clientbean = new ChatBean();
					clientbean.setType(-1);
					clientbean.setName(name);
					clientbean.setTimer(new SimpleDateFormat
						("yyyy-mm-dd HH:mm:ss").format(new Date()));
					sendMessage(clientbean);
				}
			}
		});
		list_1.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				// TODO Auto-generated method stub
				List sendlist = list_1.getSelectedValuesList();
				
				HashSet<String> set = new HashSet<String>();
				set.addAll(sendlist);
				//cli
			}
		});
		
	}
	
	class ClientInputThread extends Thread{
		@Override
		public void run() {
			// TODO Auto-generated method stub
			try {
			while(true){
				ois = new ObjectInputStream(clientSocket.getInputStream());
				final ChatBean serverbean = (ChatBean) ois.readObject();
				switch (serverbean.getType()) {
				case 0:{
					onlines.clear();
					HashSet<String> clients = serverbean.getClients();
					Iterator<String> cliite = clients.iterator();
					while(cliite.hasNext()){
						String cli = cliite.next();
						if (name.equals(cli)) {
							onlines.add(cli);
						}else {
							onlines.add(cli);
							
						}
					}
					listmodel = new UUListModel(onlines);
					list_1.setModel(listmodel);
					//aau2.play();
					textArea_1.append(serverbean.getInfo()+"\n");
					textArea_1.selectAll();
				 }
				case -1:return ;
				case 1:{
					textArea_1.append(serverbean.getTimer()+"\n"+serverbean.getInfo());
					break;
				}
				default:
					break;
				}
				}
			}catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}finally{
				if (clientSocket != null) {
					try {
						clientSocket.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				System.exit(0);
			}
			}
		}
	
	
	
	
	protected void sendMessage(ChatBean clientbean) {
		try {
			oos = new ObjectOutputStream(clientSocket.getOutputStream());
			oos.writeObject(clientbean);
			oos.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
	}
}
