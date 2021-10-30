import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import javax.swing.*;
import java.awt.Image;

public class Screen extends JFrame {
	JLabel[][] mlab;
	final int ROWS = 3, COLS = 3;
	String tikTakToeTable[][] = new String[ROWS][COLS];
	int actualCol = 0, actualRow = 0;
	final int SCALABLE_CONSTANT = 150;
	
	private int port, opponentScreenServerPort;
	private boolean isServerConfigured = false;
	private boolean isOpponnentServerSocketOpen = false;
	private ServerSocket serverSocket;
	private Socket opponentServerSocket;
	private DataInputStream opponentIn;
	private DataOutputStream opponentOut;
	
	public Screen() {
		this.port = 0;
		this.opponentScreenServerPort = 0;
		setLayout(null);
		JPanel p = new JPanel();
		p.setBounds(40, 120, 400, 200);
		p.setLayout(null);

		mlab = new JLabel[ROWS][COLS];
		for (int i = 0; i < COLS; i++) {
			for (int j = 0; j < ROWS; j++) {
				mlab[i][j] = new JLabel();
				mlab[i][j].setBounds(SCALABLE_CONSTANT/10 + (i * SCALABLE_CONSTANT), SCALABLE_CONSTANT/10 + (j * SCALABLE_CONSTANT), SCALABLE_CONSTANT, SCALABLE_CONSTANT);
				ImageIcon imagem = new ImageIcon(Screen.class.getResource("/imagenes/empty.png"));
				Image imag = imagem.getImage().getScaledInstance(mlab[i][j].getWidth(), mlab[i][j].getHeight(),
						Image.SCALE_DEFAULT);
				mlab[i][j].setIcon(new ImageIcon(imag));
				add(mlab[i][j]);
			}
		}
		
		JLabel opponentPortLabel;
		opponentPortLabel = new JLabel("OPPONENT SERVER PORT");
		opponentPortLabel.setHorizontalAlignment(SwingConstants.CENTER);
		opponentPortLabel.setBounds(20, 520, 100, 34);
		add(opponentPortLabel);
		
		JTextField opponentPortField;
		opponentPortField = new JTextField();
		opponentPortField.setColumns(10);
		opponentPortField.setBounds(130, 520, 100, 30);
		add(opponentPortField);
		
		JLabel portLabel;
		portLabel = new JLabel("SERVER PORT");
		portLabel.setHorizontalAlignment(SwingConstants.CENTER);
		portLabel.setBounds(20, 480, 100, 34);
		add(portLabel);
		
		JTextField portField;
		portField = new JTextField();
		portField.setColumns(10);
		portField.setBounds(130, 480, 100, 30);
		add(portField);
		
		JButton portBtn = new JButton();
		portBtn.setText("Set Port");
		portBtn.setBounds(240, 480, 100, 30);
		portBtn.addActionListener(new ActionListener() {
			@Override
            public void actionPerformed(ActionEvent e) {
				try {
					port = Integer.parseInt(portField.getText());
					opponentScreenServerPort = Integer.parseInt(opponentPortField.getText());
					
					serverSocket = new ServerSocket(port);					
					isServerConfigured = true;
					
					System.out.println("> Server socket opened in " + port + " port");
				} catch (Exception e1) {
					e1.printStackTrace();
				}	
            }
        });		
		add(portBtn);
		init();
	}
	
	public static void main(String[] args) {
		Screen screen = new Screen();
		screen.setVisible(true);
		screen.setLocation(250, 100);
		screen.setSize(550, 650);
		screen.setResizable(false);
		screen.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		screen.openServerPortListener();
	}
	
	public void init() {
		for (int i = 0; i < COLS; i++) {
			for (int j = 0; j < ROWS; j++) {
				tikTakToeTable[i][j] = "empty";
			}
		}
		setSquareValue(actualCol, actualRow, "emptySelected");								
	}
	
	public void openServerPortListener() {
		try {			
			while(true){
				Thread.sleep(200);
				if(isServerConfigured) {
					System.out.println("> Looking for client sockets on port " + port);
					Socket clientSocket = serverSocket.accept();
				 	Reader_Thread serverReaderThread = new Reader_Thread(this, clientSocket);
				 	serverReaderThread.start();
				}else {
					System.out.println("> Waiting to set configuration");
				}
			}
		} catch (Exception e) {		
			 System.err.println(e);
		}
	}
	
	public void handleMessage(String data, DataOutputStream out) {
		try {
			if(!isOpponnentServerSocketOpen) {
				opponentServerSocket = new Socket("localhost", opponentScreenServerPort);
				opponentIn = new DataInputStream(opponentServerSocket.getInputStream());
				opponentOut = new DataOutputStream(opponentServerSocket.getOutputStream());
				isOpponnentServerSocketOpen = true;
			}
			
			String[] dividedData = data.split("\\|");
			String origin = dividedData[0];
			String figure  =  dividedData[1];
			String operation = dividedData[2];
			if(operation.equals("Select")) {
				select(figure);
			}else {		
				move(operation);	
			}
			out.writeUTF("handleMessage > Data executed: " + data);
					
			if(origin.equals("Remote")) {
				String newOrigin = "Server";
				String newData = newOrigin + "|" + figure + "|" + operation;
				opponentOut.writeUTF(newData);
				String opponentServerMessage = opponentIn.readUTF();
			}
		} catch (IOException e) {		
			 System.err.println(e);
		}
	}
	
	
	
	public void move(String moveDirection) {
		try {		
			int prevRow = actualRow, prevCol = actualCol;
			boolean isMovementAllowed = true;
			
			if(moveDirection.equals("Up") && actualRow > 0) {
				actualRow -= 1;
			}else if(moveDirection.equals("Down") && actualRow < 2) {
				actualRow += 1;
			}else if(moveDirection.equals("Right") && actualCol < 2) {
				actualCol += 1;
			}else if(moveDirection.equals("Left") && actualCol > 0) {
				actualCol -= 1;
			}else { 
				isMovementAllowed = false;
			}
			
			if(isMovementAllowed) {
				String prevValue = getValue(prevCol,prevRow);
				if(prevValue.contains("empty")) {
					setSquareValue(prevCol, prevRow, "empty");																
				}else if(prevValue.contains("circle")) {
					setSquareValue(prevCol, prevRow, "circle");								
				}else if(prevValue.contains("cross")) {
					setSquareValue(prevCol, prevRow, "cross");								
				}else {}
				
				String actValue = getValue(actualCol, actualRow);
				if(actValue.contains("empty")) {
					setSquareValue(actualCol, actualRow, "emptySelected");																
				}else if(actValue.contains("circle")) {
					setSquareValue(actualCol, actualRow, "circleSelected");								
				}else if(actValue.contains("cross")) {
					setSquareValue(actualCol, actualRow, "crossSelected");								
				}else {}			
			}
		} catch (Exception e) {
			System.out.println(e);
		}
	}
	
	public void select(String clientFigure) {
		try {	
			String actValue = getValue(actualCol, actualRow);

			if(actValue.contains("empty")) {
								
				if(clientFigure.equals("circle")) {
					setSquareValue(actualCol, actualRow, "circleSelected");								
				}else if(clientFigure.equals("cross")) {
					setSquareValue(actualCol, actualRow, "crossSelected");								
				}else {}
				
			}else if(actValue.contains("circle")) {
				setSquareValue(actualCol, actualRow, "circleInvalid");								
			}else if(actValue.contains("cross")) {
				setSquareValue(actualCol, actualRow, "crossInvalid");								
			}else {}
			
			checkGameStatus();
			
		} catch (Exception e) {
			System.out.println(e);
		}
	}
	
	public void checkGameStatus() {
		String winner = null;
		
		for (int i = 0; i < COLS; i++) {
			int sumRow = 0;
			int sumCol = 0;
			for (int j = 0; j < ROWS; j++) {
				String actualValueInRow = tikTakToeTable[i][j];
				String actualValueInCol = tikTakToeTable[j][i];
				
				if(actualValueInRow.contains("cross")) {
					sumRow += 1;
				}else if(actualValueInRow.contains("circle")) {
					sumRow -= 1;
				}
				
				if(actualValueInCol.contains("cross")) {
					sumCol += 1;
				}else if(actualValueInCol.contains("circle")) {
					sumCol -= 1;
				}
			}
			if(Math.abs(sumRow) == 3) {
				winner = sumRow > 0 ? "cross" : "circle";
				break;
			}
			if(Math.abs(sumCol) == 3) {
				winner = sumCol > 0 ? "cross" : "circle";
				break;
			}
		}
		if(winner == null) {
			if(tikTakToeTable[0][0].contains("cross") && tikTakToeTable[1][1].contains("cross") && tikTakToeTable[2][2].contains("cross")) {
				winner = "cross";
			}else if(tikTakToeTable[2][0].contains("cross") && tikTakToeTable[1][1].contains("cross") && tikTakToeTable[2][0].contains("cross")) {
				winner = "cross";
			}else if(tikTakToeTable[0][0].contains("circle") && tikTakToeTable[1][1].contains("circle") && tikTakToeTable[2][2].contains("circle")) {
				winner = "circle";
			}else if(tikTakToeTable[2][0].contains("circle") && tikTakToeTable[1][1].contains("circle") && tikTakToeTable[2][0].contains("circle")) {
				winner = "circle";
			}
		}
		
		if(winner != null) {
			System.out.println("> WINNER IS " + winner);
			System.out.println("> WINNER IS " + winner);
			System.out.println("> WINNER IS " + winner);
		}
	}

	public void showTableValues() {
		System.out.println("> Actual table values: ");
		for (int i = 0; i < COLS; i++) {
			for (int j = 0; j < ROWS; j++) {
				System.out.print(tikTakToeTable[i][j] + " | ");
			}
			System.out.println();
		}
	}
	
	public void showActualPos() {
		System.out.println("> Actual pos: [" + actualRow + "][" + actualCol + "]");
	}
	
	public String getValue(int row, int col) {
		return tikTakToeTable[row][col];
	}
	
	public void setSquareValue(int row, int col, String figure) {
		String imagePath = "/imagenes/" + figure + ".png";
		
		ImageIcon imagem = new ImageIcon(Screen.class.getResource(imagePath));
		Image imag = imagem.getImage().getScaledInstance(mlab[row][col].getWidth(), mlab[row][col].getHeight(),
				Image.SCALE_DEFAULT);
		mlab[row][col].setIcon(new ImageIcon(imag));
		tikTakToeTable[row][col] = figure;
	}
}