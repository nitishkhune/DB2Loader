package com.sjsu.db2;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.io.OutputStreamWriter;
import java.io.BufferedWriter;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

public class Db2 extends JFrame implements Runnable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	static { /* works fine! ! */
		System.setProperty("java.awt.headless", "true");
		// System.out.println(java.awt.GraphicsEnvironment.isHeadless());
		/* ---> prints true */
	}

	// private static final Color COLOR_BACKGROUND = Color.WHITE;

	String input1, input2, input3;
	private JLabel item1, item2, item3, item4, item5, item6, item7,heading;
	private JButton button1, button2;
	private JPanel panel;
	private JTextField textfield1, textfield3, textfield4, textfield5;
	private JPasswordField textfield2;
	public String user, password, database;
	JFrame frame = new JFrame("left");
	Connection con = null;
	public String arr[] = new String[100];
	private java.io.InputStream input;

	private java.io.OutputStream output;

	public Db2(java.io.InputStream input, java.io.OutputStream output) {
		this.input = input;
		this.output = output;
	}

	public Db2(java.io.InputStream input, java.io.OutputStream output, Connection con) {
		this.input = input;
		this.output = output;
		this.con = con;
	}
	
	Db2() {
		super("Unzip File and Load into DB2");
		setLayout(new FlowLayout());

		panel = new JPanel();

		panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
		
		heading = new JLabel("Load Data using PIPE in db2");
		heading.setFont(heading.getFont().deriveFont(32.0f));
		add(heading);

		item1 = new JLabel("DB2 Instance Name: ");
		panel.add(item1);
		textfield1 = new JTextField(15);
		panel.add(textfield1);

		input1 = item1.getText();

		item2 = new JLabel("Password: ");
		panel.add(item2);
		textfield2 = new JPasswordField(15);
		panel.add(textfield2);

		input2 = item2.getText();

		item3 = new JLabel("Database Name: ");
		panel.add(item3);
		textfield3 = new JTextField(15);
		panel.add(textfield3);

		input3 = item3.getText();

		item7 = new JLabel("Pipe name: ");
		panel.add(item7);
		textfield5 = new JTextField(15);
		panel.add(textfield5);

		textfield4 = new JTextField(15);
		add(textfield4);
		button1 = new JButton("choose file/directory");
		add(button1);
		Handler handler = new Handler();
		button1.addActionListener(handler);

		button2 = new JButton("Convert");
		add(button2);
		LoadContents loadcontents = new LoadContents();
		button2.addActionListener(loadcontents);

		add(panel);

	}

	public static void main(String args[]) {
		// Linux kbd = new Linux();
		Db2 obj = new Db2();
		obj.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		obj.setSize(500, 300);
		obj.setVisible(true);
	}

	private class Handler implements ActionListener {
		public void actionPerformed(ActionEvent event) {

			JFileChooser fc = new JFileChooser();
			int retVal = fc.showOpenDialog(frame);
			if (retVal == JFileChooser.APPROVE_OPTION) {

				textfield4.setText(fc.getSelectedFile().getAbsolutePath());

			}

		}
	}

	public void dbconnect(String temp, int id) {

		Connection con = null;
		PreparedStatement pstat = null;
		ResultSet rs;
		Statement stmt;

		try {

			try {
				Class.forName("com.ibm.db2.jcc.DB2Driver");
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			// System.out.println("**** Loaded the JDBC driver");

			this.database = "jdbc:db2://127.0.0.1:50000/db2temp";
			this.user = "db2inst1";
			this.password = "sjsu1234";
			con = DriverManager.getConnection(this.database, this.user,
					this.password);

			System.out.println(this.database + "    " + this.user + "   "
					+ this.password);
			con.setAutoCommit(false);
			// String createTableSQL = "CREATE TABLE TEMPTABLE("
			// + "DATA VARCHAR(50) NOT NULL, "
			// + "ID INTEGER(20) NOT NULL"
			// + ")";

			// pstat = con.prepareStatement(createTableSQL);
			// pstat.close();

			pstat = con.prepareStatement("insert into temptable values(?,?)");
			pstat.setString(1, temp);
			pstat.setInt(2, id);
			pstat.executeUpdate();

			// pstat.close();

			// con.close();
			// stmt = con.createStatement();
			// rs = stmt.executeQuery(" ");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} /*
		 * catch (ClassNotFoundException e) { // TODO Auto-generated catch block
		 * e.printStackTrace(); }
		 */

	}

	public void run() {
		try {
			
			PreparedStatement pstat = null;
			// Create 512 bytes buffer
			byte[] b = new byte[50];
			// String[]
			// String[] b = new String[100];
			int read = 1;
			// String a = new String();
			// As long as data is read; -1 means EOF
			int i = 0;
		while (read > -1) {
			
			
			// Read bytes into buffer
			//	byte[] b = new byte[50];
				read = input.read(b, 0, b.length);
			
				arr[i] = new String(b);
	//			
			
//				System.out.println(con);
				pstat = con.prepareStatement("insert into trackdata values(?,?)");
				pstat.setInt(1, i);
				pstat.setString(2, arr[i]);
				
				pstat.executeUpdate();
				i++;
				

			}
			System.out.println("Completed");
			con.commit();
			con.close();
		System.exit(0);
		} catch (Exception e) {
			// Something happened while reading or writing streams; pipe is
			// broken
			throw new RuntimeException("Broken pipe", e);
		} finally {
			try {
				input.close();
			} catch (Exception e) {
			}
			try {
				output.close();
			} catch (Exception e) {
			}
		}
	}

	private class LoadContents implements ActionListener {
		public void actionPerformed(ActionEvent event) {
			Unzip mfe = new Unzip();
			Db2 datab = new Db2();
			String str = textfield4.getText();
			String pipename = textfield5.getText();
			// Piper kbd = new Piper();
			// Systaseem.out.println(str);
			String str1= mfe.unzipFile(str);
			try {
				String urlPrefix = "jdbc:db2:";
				
				Statement stmt;
				ResultSet rs;

				// System.out.println(filename);

				database = urlPrefix + textfield3.getText();
				user = textfield1.getText();

				password = textfield2.getText();
				System.out.println(database + "    " + user + "   " + password);

				try {
					Class.forName("com.ibm.db2.jcc.DB2Driver");
				} catch (ClassNotFoundException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				System.out.println("**** Loaded the JDBC driver");

				try {
					con = DriverManager.getConnection(database, user, password);
				System.out.println(con);
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

				try {
					con.setAutoCommit(false);
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

				System.out
						.println("**** Created a JDBC connection to the data source");

				// System.out.println(str);
				Process p1 = Runtime.getRuntime().exec("mkfifo " + pipename);
				// System.out.println("pipe created");

				p1 = Runtime.getRuntime().exec("cat " + str1 + " > " + pipename);

				Process p2 = Runtime.getRuntime().exec("cat " + pipename);
				// System.out.println("p2 created");
				// new InputStreamReader(p1.getInputStream())
				Db2 pipe = new Db2(p1.getInputStream(), p2.getOutputStream(), con);

				Thread t = new Thread(pipe);
				t.start();

			
							} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				
			}
			/*finally{
				
			System.exit(0);
			}*/
		}
	}
}