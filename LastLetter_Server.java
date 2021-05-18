
package group9_lastletter;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * A server for a network multi-player Last letter game. Modified and extended
 * from the class presented in Deitel and Deitel "Java How to Program" book. I
 * made a bunch of enhancements and rewrote large sections of the code. The main
 * change is instead of passing *data* between the client and server, I made a
 * LLP ( Last letter protocol) which is totally plain text, so you can test the
 * game with Telnet (always a good idea.) The strings that are sent in LLP are:
 *
 * Client -> Server Server -> Client ----------------------- ----------------
 * "A" and player's Answer VALID_Ans will have come after. OtherPlayerInputWord
 * QUIT VICTORY DEFEAT MESSAGE <text>
 * A second change is that it allows an unlimited number of pairs of players to
 * play.
 */
/**
 *
 * @author Saraa
 */
public class LastLetter_Server  {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException  {
     /**
     *
     * @param args
     * @throws IOException
     *
     * LAST LETTER GAME :The application is a Socket Programming Game which is
     * the last letter game, where the player read a word from the previous
     * player and he has to write a word where the first letter of them must
     * match the last letter of the word of the previous player.
     */
        // TODO code application logic here
        ServerSocket listener = new ServerSocket(8901);
        System.out.println("last letter game  Server is Running");
        try {
            while (true) {
                Game game = new Game();
                Game.Player player1 = game.new Player(listener.accept(), '1');
                Game.Player player2 = game.new Player(listener.accept(), '2');
                player1.setOpponent(player2);
                player2.setOpponent(player1);
                game.currentPlayer = player1;
                player1.start();
                player2.start();
            }
        } finally {
            listener.close();
        }
    }
}

class Game {

    /**
     * A game last lette conatin Test Filed for each player to enter his answer
     * and button to send answer . server have initial word to start game so we
     * use a simple array contain some word and every time game start select on
     * randomly .
     *
     */
    /**
     * array of initial word
     *
     */
    String word[] = {"Sun", "Car", "Appale", "Room", "School", "House", "Time", "Animal", "Parent", "Food",
        "Jeddah", "Teacher", "Mother", "Father", "Sister", "Brother", "Fruit", "Mangores", "Orange", "Door"};

    int index = (int) Math.floor(Math.random() * word.length);
    String firstWord = word[index];

    /**
     * The current player.
     */
    Player currentPlayer;

    /**
     * Returns the player loss .
     */
    public boolean hasLose(String anser) {
        return !anser.isEmpty() && anser.charAt(0) != firstWord.charAt(firstWord.length() - 1);

    }

    /**
     * Called by the player threads when a player tries to check input . This
     * method checks to see if the input is legal: that is, the player
     * requesting the rule of game must be the current player enter word start
     * with last letter of previse player . If the input is legal the other
     * player is notified to input his word
     */
    public synchronized boolean legalAnswer(String word, Player player) {
        if (player == currentPlayer && !word.isEmpty() && word.toLowerCase().charAt(0) == firstWord.toLowerCase().charAt(firstWord.length() - 1)) {
            currentPlayer = currentPlayer.opponent;
            currentPlayer.otherPlayerInputWord(word);
            return true;
        }
        return false;
    }

    /**
     * The class for the helper threads in this multithreaded server
     * application. A Player is identified by a character mark which is either
     * '1' or '2'. For communication with the client the player has a socket
     * with its input and output streams. Since only text is being communicated
     * we use a reader and a writer.
     */
    class Player extends Thread {

        char mark;
        Player opponent;
        Socket socket;
        BufferedReader input;
        PrintWriter output;

        /**
         * Constructs a handler thread for a given socket and mark initializes
         * the stream fields, displays the first two welcoming messages.
         */
        public Player(Socket socket, char mark) {
            this.socket = socket;
            this.mark = mark;
            try {
                input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                output = new PrintWriter(socket.getOutputStream(), true);
                output.println("WELCOME " + mark);
                output.println("MESSAGE Waiting for opponent to connect");
            } catch (IOException e) {
                System.out.println("Player died: " + e);
            }
        }

        /**
         * Accepts notification of who the opponent is to start play the game .
         */
        public void setOpponent(Player opponent) {
            this.opponent = opponent;
        }

        /**
         * Handles the otherPlayerInputWord message.
         */
        public void otherPlayerInputWord(String word) {
            output.println("OPPONENT_input " + word);

        }

        /**
         * The run method of this thread.
         */
        public void run() {
            try {
                // The thread is only started after everyone connects.
                output.println("MESSAGE All players connected");

                // Tell the first player that it is her turn.
                if (mark == '1') {
                    output.println("MESSAGE Your turn , Enter Worde start with Last letter \"  " + firstWord + " \" ");
                }

                // Repeatedly get commands from the client and process them.
                while (true) {
                    String command = input.readLine();

                    /**
                     * "A" letter used in this method means that, if the line
                     * starts with "A" that means player's Answer will have come
                     * after this the letter.
                     */
                    if (command.startsWith("A")) {

                        String answer = command.substring(1, command.length());

                        if (legalAnswer(answer, this)) {
                            output.println("VALID_Ans");
                            firstWord = answer;

                        } else {
                            output.println("DEFEAT");
                            currentPlayer = currentPlayer.opponent;
                            currentPlayer.output.println("VICTORY");
                        }
                    } else if (command.startsWith("QUIT")) {
                        return;
                    }
                }
            } catch (IOException e) {
                System.out.println("Player died: " + e);
            } finally {
                try {
                    socket.close();
                } catch (IOException e) {
                }
            }
        }

    }
}
