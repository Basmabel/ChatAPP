package stream;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

/**
 * This is the main page of the chat application 
 * where a client lands when connecting to the general chat or group chats.
 * 
 * The MainPage is composed of:
 * <ul>
 * <li>A text pane to print messages.</li>
 * <li>A text field to write messages.</li>
 * <li>A button to send messages.</li>
 * <li>A boolean that is indicates if the button was clicked.</li>
 * </ul>
 * 
 * @author Basma BELAHCEN, Ihssane YOUBI et Emma DRAPEAU
 *
 */
@SuppressWarnings("serial")
public class MainPage extends JFrame {
	
	public JTextPane messageLog = new JTextPane();
	public JTextField message = new JTextField();
	public JButton send = new JButton("send");
	public Boolean clicked =false;

	/**
	 * MainPage constructor.
	 * Creates the main page that is displayed when a client can send and receive messages.
	 */
	public MainPage() {
		super("Welcome");
		this.setSize(500, 500);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.setBackground(new Color(31, 160, 85));

		JPanel g = new JPanel();
		BoxLayout box = new BoxLayout(g, BoxLayout.Y_AXIS);
		
		JLabel label = new JLabel("To return to The main menu type \"Return\" At any given time during a conversation");
		label.setAlignmentX(CENTER_ALIGNMENT);

		JScrollPane jp = new JScrollPane(messageLog());
		jp.setPreferredSize(new Dimension(450, 400));

		g.setLayout(box);
		g.add(jp, BorderLayout.CENTER);
		g.add(label);
		g.add(enterMessage());
		g.add(send());
		add(g);
		setVisible(true);
	}

	/**
	 * Creates the text pane where the messages will be printed.
	 * Disables the possibility to edit it.
	 * 
	 * @return The text pane where messages are printed.
	 */
	public JTextPane messageLog() {
		
		messageLog.setAlignmentX(CENTER_ALIGNMENT);
		messageLog.setEditable(false);

		return messageLog;
		
	}
	
	/**
	 * Creates the text field to write messages, configures its listener.
	 * Disables the "Valider" button when the text field is empty,
	 * and enables the button when it is not.
	 * 
	 * @return The text field where we can enter messages
	 */
	public JTextField enterMessage() {

		message = new JTextField();
		message.setPreferredSize(new Dimension(450, 50));
		message.setAlignmentX(CENTER_ALIGNMENT);
		
		message.getDocument().addDocumentListener(new DocumentListener() {
			  
			@Override
			public void changedUpdate(DocumentEvent arg0) {
				enableButton();
				
			}
			@Override
			public void insertUpdate(DocumentEvent arg0) {
				enableButton();
				
			}
			@Override
			public void removeUpdate(DocumentEvent arg0) {
				enableButton();
				
			}
			
			public void enableButton() {
			     if (message.getText().equals(""))
			     {
			    	 send.setEnabled(false);
			     }
			     else
			     {
			    	 send.setEnabled(true);
			     }
			  }
		});
		
		return message;
	}
	
	/**
	 * Creates the "Valider" button, configures its actionPerformed, and disables it.
	 * 
	 * @return The "Valider" button where we click to send a message
	 */
	public JButton send() {

		send = new JButton("Valider");
		send.setAlignmentX(CENTER_ALIGNMENT);
		send.setSize(60, 20);
		send.setPreferredSize(new Dimension(60, 20));
		send.setBackground(Color.LIGHT_GRAY);
		send.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				clicked= true;
				String text = message.getText();
				StyledDocument doc = messageLog.getStyledDocument();
	
				SimpleAttributeSet keyWord = new SimpleAttributeSet();
				StyleConstants.setForeground(keyWord, Color.BLACK);
				StyleConstants.setBackground(keyWord, Color.LIGHT_GRAY);
				StyleConstants.setBold(keyWord, true);

				 try {
					doc.insertString(doc.getLength(), "\n"+text, keyWord );
				} catch (BadLocationException e1) {
					e1.printStackTrace();
				}	
			}
		});
		
		send.setEnabled(false);
		return send;
	}
}
