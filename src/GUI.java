import java.awt.EventQueue;

import javax.swing.JFrame;
import java.awt.Font;

import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import java.awt.Window.Type;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JTextPane;


public class GUI{


	private JFrame frmCommunicator;
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					GUI window = new GUI();
					window.frmCommunicator.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public GUI() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		//Main App window
		frmCommunicator = new JFrame();
		frmCommunicator.setType(Type.UTILITY);
		frmCommunicator.setTitle("QComm");
		frmCommunicator.setFont(new Font("Arial Rounded MT Bold", Font.PLAIN, 12));
		frmCommunicator.setBounds(100, 100, 657, 471);
		frmCommunicator.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmCommunicator.getContentPane().setLayout(null);

		//Scrollable list pane
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(50, 36, 539, 358);
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		frmCommunicator.getContentPane().add(scrollPane);

		//Text Pane corresponding to scrollable area
		final JTextPane textPane = new JTextPane();
		textPane.setFont(new Font("Arial Rounded MT Bold", Font.PLAIN, 13));
		textPane.setEditable(false);
		scrollPane.setViewportView(textPane);
		textPane.setVisible(true);

		//Sub Classes for updating purposes
		class EventSource extends Observable implements Runnable {
			@Override
			public void run() {
				while(true){
					String old = Xbee.message;
					int oldint = Xbee.errors;
					while(old.equals(Xbee.message) &&  oldint >= Xbee.errors);
					setChanged();
					notifyObservers(Xbee.message);
				}
			}
		}

		class ResponseHandler implements Observer {
			public void update(Observable obj, Object arg) {
				if(textPane.getText().length()==0){
					textPane.setText(Xbee.message);
				}
				else{
					textPane.setText(Xbee.message+"\n\n"+textPane.getText());
				}
			}
		}
		//Observer and Responder
		EventSource eventSource = new EventSource();
		ResponseHandler responseHandler = new ResponseHandler();
		eventSource.addObserver(responseHandler);
		Thread stringObserver = new Thread(eventSource);
		stringObserver.start();
		Xbee xbee = new Xbee();
		xbee.connect("COM11");
		Thread timer = new Thread(Timer.CLOCK);
		timer.start();
	}

}