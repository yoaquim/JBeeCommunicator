import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JLabel;
import com.jgoodies.forms.factories.DefaultComponentFactory;
import javax.swing.SwingConstants;
import java.awt.Font;
import javax.swing.JProgressBar;
import java.awt.Color;
import java.util.ArrayList;

import javax.swing.JScrollPane;
import javax.swing.JPanel;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.LineBorder;
import javax.swing.JInternalFrame;
import javax.swing.JTextPane;


public class QboComm {

	private JFrame frmQboCommunicator;
	public static Xbee xbee = new Xbee();
	private JInternalFrame internalFrame;
	private JLabel lblConnected;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				QboComm window = new QboComm();
				window.frmQboCommunicator.setVisible(true);
				ConnectionListener callback = new ConnectionListener() {
					public void connectionEstablished() {
						try {
							ArrayList<String> list = new ArrayList<String>();
							list = xbee.fetch();
							DBConnector db = DBConnector.getConnector();
							if(db.connect()){
								for(String s : list){
									db.recycleTransaction(s.substring(1, 5), s.substring(6), s.substring(0, 1) );
								}
							}
							db.disconnect();
							xbee.printDBList();
						} catch (Exception e) {
							e.printStackTrace();
						} 
					}
					public void connectionFailed() {
						//IMPLEMENT
					}
				};
				new ConnectionWorker(callback, window).execute();
			}
		});
	}

	/**
	 * Create the application.
	 */
	public QboComm() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frmQboCommunicator = new JFrame();
		frmQboCommunicator.setTitle("Qbo Communicator");
		frmQboCommunicator.getContentPane().setBackground(new Color(165, 42, 42));
		frmQboCommunicator.getContentPane().setForeground(new Color(211, 211, 211));
		frmQboCommunicator.setBounds(100, 100, 450, 300);
		frmQboCommunicator.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmQboCommunicator.getContentPane().setLayout(null);

		/**
		 * Connection Error Pop-up
		 */
		this.internalFrame = new JInternalFrame("Connection Error");
		internalFrame.setBorder(new LineBorder(new Color(153, 180, 209), 3));
		internalFrame.setBounds(103, 46, 227, 157);
		frmQboCommunicator.getContentPane().add(internalFrame);
		internalFrame.getContentPane().setLayout(null);
		JTextPane txtpnAQboCommunicator = new JTextPane();
		txtpnAQboCommunicator.setText("A Qbo Communicator is not connected. Please connect a Qbo Communicator to continue.");
		txtpnAQboCommunicator.setEditable(false);
		txtpnAQboCommunicator.setBounds(0, 0, 211, 128);
		internalFrame.getContentPane().add(txtpnAQboCommunicator);
		internalFrame.setVisible(false);

		/**
		 * Application Name
		 */
		JLabel lblQboCommunicator = DefaultComponentFactory.getInstance().createTitle("QBO COMMUNICATOR");
		lblQboCommunicator.setForeground(new Color(255, 255, 255));
		lblQboCommunicator.setBackground(new Color(255, 255, 240));
		lblQboCommunicator.setHorizontalAlignment(SwingConstants.CENTER);
		lblQboCommunicator.setBounds(144, 0, 146, 14);
		frmQboCommunicator.getContentPane().add(lblQboCommunicator);

		/**
		 * Connected label, displayed when connected to Xbee device
		 */
		this.lblConnected = DefaultComponentFactory.getInstance().createLabel("CONNECTED");
		lblConnected.setForeground(new Color(255, 255, 255));
		lblConnected.setFont(new Font("Tahoma", Font.BOLD, 14));
		lblConnected.setHorizontalAlignment(SwingConstants.CENTER);
		lblConnected.setBounds(144, 25, 146, 14);
		frmQboCommunicator.getContentPane().add(lblConnected);

		/**
		 * Scroll Panel that displays uploaded data
		 */

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane.setBounds(53, 65, 344, 131);
		frmQboCommunicator.getContentPane().add(scrollPane);

		JPanel panel = new JPanel();
		panel.setBorder(new LineBorder(new Color(255, 255, 255)));
		panel.setBackground(new Color(165, 42, 42));
		scrollPane.setViewportView(panel);

		/**
		 * Progress Bar
		 */
		JProgressBar progressBar = new JProgressBar();
		progressBar.setStringPainted(true);
		progressBar.setFont(new Font("Tahoma", Font.BOLD, 12));
		progressBar.setBackground(new Color(255, 255, 255));
		progressBar.setForeground(new Color(255, 140, 0));
		progressBar.setBounds(53, 214, 344, 25);
		frmQboCommunicator.getContentPane().add(progressBar);
	}

	public void publishError(){
		this.internalFrame.setVisible(true);
		this.lblConnected.setText("DISCONNECTED");
	}

	public void removeError(){
		this.internalFrame.setVisible(false);
		this.lblConnected.setText("CONNECTED");
	}
}
