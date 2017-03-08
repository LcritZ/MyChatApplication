package View;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import Model.ClientBean;

import java.awt.BorderLayout;
import java.util.HashMap;
import java.util.Properties;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.Socket;
import java.awt.event.ActionEvent;

public class Login extends JFrame{
	private JTextField IDField;
	private JTextField PasswordField;
	private JButton button_load;
	private JButton button_cancle;
	private JButton button_login;
	public static HashMap<String, ClientBean> onlines;
	private JPanel ContentPane;
	
	public static void LoadInfo(Properties userpro,File info) {
		if (!info.exists()) {
			try {
				info.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		try {
			userpro.load(new FileInputStream(info));
		} catch (Exception e) {
			// TODO: handle exception
		}
		
	}
	
	public Login() {
		setTitle("登录聊天室");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(350, 350, 500, 300);
		getContentPane().setLayout(null);
		
		ContentPane = new JPanel();
		ContentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		ContentPane.setBounds(0, 0, 484, 261);
		setContentPane(ContentPane);		
		ContentPane.setLayout(null);
		
		IDField = new JTextField();
		IDField.setBounds(101, 61, 129, 31);
		IDField.setOpaque(false);
		ContentPane.add(IDField);
		IDField.setColumns(10);
		
		JLabel label = new JLabel("账号：");
		label.setHorizontalAlignment(SwingConstants.CENTER);
		label.setBounds(10, 61, 81, 31);
		ContentPane.add(label);
		
		PasswordField = new JPasswordField();
		PasswordField.setBounds(101, 130, 129, 31);
		ContentPane.add(PasswordField);
		PasswordField.setColumns(10);
		
		JLabel label_1 = new JLabel("密码：");
		label_1.setHorizontalAlignment(SwingConstants.CENTER);
		label_1.setBounds(10, 130, 81, 31);
		ContentPane.add(label_1);
		
		button_load = new JButton("登录");
		button_load.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Properties users = new Properties();
				File infoFile = new File("UsersInfo/Info.properties");
				LoadInfo(users, infoFile);
				String userName = IDField.getText();
				if (infoFile.length()!=0) {
					if (users.containsKey(userName)) {
						String user_pwd = new String
								(PasswordField.getText());
						if (user_pwd.equals(users.getProperty(userName))){
							try {
								Socket client = new Socket("127.0.0.1", 49153);
								button_load.setEnabled(false);
								ChatRoom chatRoomframe = new ChatRoom(userName, client);
								chatRoomframe.setVisible(true);
								setVisible(false);
							} catch (Exception e2) {
								// TODO: handle exception
							}
							
						}else {
							JOptionPane.showMessageDialog(ContentPane, "密码错误");
							IDField.setText("");
							PasswordField.setText("");
							IDField.requestFocus();
						}
					}else {
						JOptionPane.showMessageDialog(ContentPane, "账号不存在");
						IDField.setText("");
						PasswordField.setText("");
						IDField.requestFocus();
					}
				}else {
					JOptionPane.showMessageDialog(ContentPane, "账号不存在");
					IDField.setText("");
					PasswordField.setText("");
					IDField.requestFocus();
				}
			}
		});
		button_load.setBounds(117, 201, 93, 23);
		getRootPane().setDefaultButton(button_load);
		ContentPane.add(button_load);
		
		
		button_cancle = new JButton("取消");
		button_cancle.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				IDField.setText("");
				PasswordField.setText("");
				IDField.requestFocus();
			}
		});
		button_cancle.setBounds(282, 134, 93, 23);
		ContentPane.add(button_cancle);
		
		button_login = new JButton("注册");
		button_login.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
			}
		});
		button_login.setBounds(282, 65, 93, 23);
		ContentPane.add(button_login);
		
	}
	protected void errorTip(String str) {
		// TODO Auto-generated method stub
		JOptionPane.showMessageDialog(ContentPane, str, "Error Message",
				JOptionPane.ERROR_MESSAGE);
		IDField.setText("");
		PasswordField.setText("");
		IDField.requestFocus();
	}
}
