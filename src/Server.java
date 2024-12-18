import Helper.DBHelper;

import javax.swing.*;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
        private static volatile boolean isRunning = false;
        private static ServerSocket serverSocket;
        private static final int PORT = 1234;

        public static void main(String[] args) {
                show();
        }
        public static void startServer(int port) {
                isRunning = true;
                try {
                        serverSocket = new ServerSocket(port);
                        System.out.println("Server is listening on port " + port);
                        DBHelper.connect();
                        while (isRunning) {
                                try {
                                        Socket socket = serverSocket.accept();
                                        System.out.println("New client connected: " + socket.getPort());
                                        new ServerThread(socket).start();
                                } catch (IOException e) {
                                        if (isRunning) {
                                                System.err.println("Error accepting client: " + e.getMessage());
                                        }
                                        break;
                                }
                        }
                } catch (IOException ex) {
                        System.err.println("Server exception: " + ex.getMessage());
                        ex.printStackTrace();
                } finally {
                        DBHelper.disconnect();
                        stopServer();
                }
        }
        public static void stopServer() {
                isRunning = false;
                if (serverSocket != null && !serverSocket.isClosed()) {
                        try {
                                serverSocket.close();
                                System.out.println("Server stopped.");
                        } catch (IOException e) {
                                System.err.println("Error closing server socket: " + e.getMessage());
                        }
                }
        }
        public static void show() {
                JFrame frame = new JFrame("Server Control Panel");
                frame.setSize(600, 400);
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.setLayout(null);

                JTextArea logArea = new JTextArea();
                logArea.setEditable(false);
                JScrollPane scrollPane = new JScrollPane(logArea);
                scrollPane.setBounds(20, 20, 550, 200);
                frame.add(scrollPane);

                JLabel statusLabel = new JLabel("Server Status: Oprit");
                statusLabel.setBounds(20, 230, 200, 25);
                frame.add(statusLabel);

                JButton startButton = new JButton("Start Server");
                startButton.setBounds(20, 270, 150, 30);
                frame.add(startButton);

                JButton stopButton = new JButton("Stop Server");
                stopButton.setBounds(200, 270, 150, 30);
                frame.add(stopButton);

                JButton clearButton = new JButton("Clear Logs");
                clearButton.setBounds(380, 270, 150, 30);
                frame.add(clearButton);

                startButton.addActionListener(e -> {
                        if (!isRunning) {
                                ExecutorService executor = Executors.newSingleThreadExecutor();
                                executor.execute(() -> startServer(PORT));
                                executor.shutdown();
                                statusLabel.setText("Server Status: Pornit");
                                logArea.append("Server pornit...\n");
                        } else {
                                logArea.append("Serverul este deja pornit!\n");
                        }
                });
                stopButton.addActionListener(e -> {
                        if (isRunning) {
                                stopServer();
                                statusLabel.setText("Server Status: Oprit");
                                logArea.append("Server oprit.\n");
                        } else {
                                logArea.append("Serverul este deja oprit!\n");
                        }
                });

                clearButton.addActionListener(e -> logArea.setText(""));
                frame.setVisible(true);
        }
}
