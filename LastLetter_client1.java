
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;


/**
 * A client for the Last letter game, modified and extended from the class
 * presented in Deitel and Deitel "Java How to Program" book. I made a bunch of
 * enhancements and rewrote large sections of the code. In particular I created
 * the LLP (Last letter Protocol) which is entirely text based. Here are the
 * strings that are sent:
 *
*  Client -> Server                 Server -> Client
 *  -----------------------         ----------------
 *  "A" and player's Answer           VALID_Ans
    will have come after.             OtherPlayerInputWord 
 *  QUIT                              VICTORY
 *                                    DEFEAT
 *                                    MESSAGE <text>
 */

public class Group9_LastLetter_client1 {

    
 private JFrame frame = new JFrame("las letter");
  private JLabel messageLabel = new JLabel();
   private JLabel messageLabelWin = new JLabel();
    private JLabel messageLabelLos = new JLabel();
    private static JTextField text_field_1;
    private static JButton button;
    
    
    public String answer = "";
    
    private static int PORT = 8901;
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
  
    
      public Group9_LastLetter_client1(String serverAddress) throws Exception {

        // Setup networking
        socket = new Socket(serverAddress, PORT);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new PrintWriter(socket.getOutputStream(), true);

        // Layout GUI
        text_field_1 = new JTextField();
        Font newFont = new Font("Serif", Font.BOLD, 25); 
       messageLabel.setFont(newFont);
       messageLabel.setForeground(Color.black);
       
       
       messageLabelWin.setFont(newFont);
       messageLabelWin.setForeground(Color.GREEN);
      
       messageLabelLos.setFont(newFont);
       messageLabelLos.setForeground(Color.RED);
       
        Font newFont2 = new Font("Serif", Font.BOLD, 30);
       text_field_1.setFont(newFont2);
       text_field_1.setForeground(Color.BLUE);
       
        frame.add(messageLabel);
        messageLabel.setBackground(Color.lightGray);
        messageLabel.setVisible(true);
        button = new JButton("Send");
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                if (text_field_1.getText() != null) {
                    answer = text_field_1.getText();
                    text_field_1.setText("");
                    out.println("A" + answer);
                   
                }

            }
        }
        );
        

    }

 /**
     * The main thread of the client will listen for messages from the server.
     * The first message will be a "WELCOME" message in which we receive our
     * mark. Then we go into a loop listening for "VALID_Ans",
     * "OPPONENT_Input", "VICTORY", "DEFEAT", "QUIT" or "MESSAGE"
     * messages, and handling each message appropriately. The "VICTORY",and
     * "DEFEAT"  ask the user whether or not to play another game. If
     * the answer is no, the loop is exited and the server is sent a "QUIT"
     * message. If an QUIT message is recevied then the loop will exit
     * and the server will be sent a "QUIT" message also.
     */
	 
    public void play() throws Exception {
        String response;
        try {
            response = in.readLine();
            if (response.startsWith("WELCOME")) {
                char mark = response.charAt(8);
              
                frame.setTitle("LAST LATTER GAME - Player " + mark);
            }
            while (true) {
                response = in.readLine();
                if (response.startsWith("VALID_Ans")) {
                    messageLabel.setText("true correct, please wait");
                   
                } 
                else if (response.startsWith("OPPONENT_input")) {
                    messageLabel.setText("Opponent enter this word " + response.substring(15, response.length())
                            + ", your turn");
                    
                }
                else if (response.startsWith("DEFEAT")) {
                  
                   messageLabel.setText("");
                    frame.add(messageLabelLos);
                    messageLabelLos.setText("You lose");
                    break;
                    
                }
                else if (response.startsWith("VICTORY")) {
                
                 messageLabel.setText("");
                    frame.add(messageLabelWin);
                    messageLabelWin.setText("You win");
                    break;
                    
                } 
                else if (response.startsWith("MESSAGE")) {
                    messageLabel.setText(response.substring(8));
                }
            }
            out.println("QUIT");
        } finally {
            socket.close();
        }
    }
/**
 * check if player want to playe agine 
 * @return boolran
 */
    private boolean wantsToPlayAgain() {
        int response = JOptionPane.showConfirmDialog(frame,
                "Want to play again?",
                "Last Letter is Fun Fun ",
                JOptionPane.YES_NO_OPTION);
     
        frame.dispose();
        messageLabelLos.setText("");
       messageLabelWin.setText("");
        return response == JOptionPane.YES_OPTION;
    }

    public static void main(String[] args) throws Exception {
        
         while (true) {
            String serverAddress = (args.length == 0) ? "localhost" : args[1];
            
            Group9_LastLetter_client1 client = new Group9_LastLetter_client1 (serverAddress);
            
            client.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            client.frame.setSize(700, 300);
            client.frame.setBackground(Color.green);
            client.frame.setVisible(true);  
            client.frame.setResizable(false);
             client.frame.setLayout(null);
             client.messageLabel.setBounds(40,20,700,40);
             client.messageLabelWin.setBounds(40,20,600,40);
             client.messageLabelLos.setBounds(40,20,600,40);
            client.text_field_1.setBounds(40, 100, 300, 50);
            button.setBounds(500, 100, 100, 50);
             client.frame.add(text_field_1);
             client.frame.add(button);
            client.play();
            if (!client.wantsToPlayAgain()) {
                break;
            }
        }
    }
    
}
