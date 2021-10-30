import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

public class Remote extends JFrame implements ActionListener{
	private static final long serialVersionUID = 1L;
	private String serverMessage;
	private JButton forwardBtn, backwardBtn, leftBtn, rightBtn, selectBtn;
	
	private Socket serverConnection;
	private DataOutputStream out;
	private DataInputStream in;
	private String figure;
	private int port;
	
	
	public Remote(){
		super("Remote Control");
		this.figure = "";
		this.port = 0;
		try {	
			getContentPane().setLayout(null);
			JPanel p = new JPanel();
			p.setBounds(0, 0, 500, 350);
			p.setLayout(null);
			
			forwardBtn = new JButton();
			forwardBtn.setText("Up");
			forwardBtn.setBounds(130, 20, 100, 30);
			forwardBtn.addActionListener(this);
			getContentPane().add(forwardBtn);
			
			backwardBtn = new JButton();
			backwardBtn.setText("Down");
			backwardBtn.setBounds(130, 120, 100, 30);
			backwardBtn.addActionListener(this);
			getContentPane().add(backwardBtn);

			rightBtn = new JButton();
			rightBtn.setText("Rigth");
			rightBtn.setBounds(235, 70, 100, 30);
			rightBtn.addActionListener(this);
			getContentPane().add(rightBtn);
			
			leftBtn = new JButton();
			leftBtn.setText("Left");
			leftBtn.setBounds(25, 70, 100, 30);
			leftBtn.addActionListener(this);
			getContentPane().add(leftBtn);			
			
			selectBtn = new JButton();
			selectBtn.setText("Select");
			selectBtn.setBounds(130, 70, 100, 30);
			selectBtn.addActionListener(this);
			getContentPane().add(selectBtn);
			
			JLabel portLabel;
			portLabel = new JLabel("SERVER PORT");
			portLabel.setHorizontalAlignment(SwingConstants.CENTER);
			portLabel.setBounds(25, 160, 100, 34);
			add(portLabel);
			
			JTextField portField;
			portField = new JTextField();
			portField.setColumns(10);
			portField.setBounds(135, 160, 100, 30);
			add(portField);
			
			JButton portBtn = new JButton();
			portBtn.setText("Set Ports");
			portBtn.setBounds(240, 160, 70, 30);
			portBtn.addActionListener(new ActionListener() {
				@Override
	            public void actionPerformed(ActionEvent e) {
					try {
						port = Integer.parseInt(portField.getText());
						figure = port % 2 == 0 ? "cross" : "circle";
						
						serverConnection = new Socket("localhost", port);
						out = new DataOutputStream(serverConnection.getOutputStream());
						in = new DataInputStream(serverConnection.getInputStream());
						
						System.out.println("Remote > Socket established to port " + port);
					} catch (Exception e1) {
						e1.printStackTrace();
					}	
	            }
	        });		
			add(portBtn);
		}catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		Remote remotePanel = new Remote();
		remotePanel.setVisible(true);
		remotePanel.setLocation(100, 100);
		remotePanel.setSize(370, 250);
		remotePanel.setResizable(false);
		remotePanel.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		String operation = null;
		if (e.getSource() == forwardBtn) {
			operation = "Up";
		}else if (e.getSource() == backwardBtn) {
			operation = "Down";
		}else if (e.getSource() == leftBtn) {
			operation = "Left";
		}else if (e.getSource() == rightBtn) {
			operation = "Right";
		}else if (e.getSource() == selectBtn) {
			operation = "Select";
		}
		
		try {
			String origin = "Remote";
			String data = origin + "|" + figure + "|" + operation;
			out.writeUTF(data);
			serverMessage = in.readUTF();
			System.out.println("Remote > Server response: " + serverMessage);
		} catch (NumberFormatException | IOException e1) {
			e1.printStackTrace();
		}
	}
}
