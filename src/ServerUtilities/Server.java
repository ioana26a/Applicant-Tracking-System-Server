package ServerUtilities;

import Helper.DBHelper;

import javax.swing.*;
import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
        private static volatile boolean isRunning = false;
        private static ServerSocket serverSocket;
        public static JTextArea log;
        private static final int PORT = 1234;
        private static List<Socket> clients = new ArrayList<>();
        public static void main(String[] args) {
                show();
        }
        public static void startServer(int port) {
                isRunning = true;
                try {
                        serverSocket = new ServerSocket(port);
                        log.append("Server is listening on port " + port + "\n");
                        DBHelper.connect();
                        while (isRunning) {
                                try {
                                        Socket socket = serverSocket.accept();
                                        clients.add(socket);
                                        log.append("New client connected: " + socket.getPort() + "\n");
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
                                if(!clients.isEmpty()){
                                        for (Socket clientSocket : clients) {
                                                try {
                                                        OutputStream out = clientSocket.getOutputStream();
                                                        out.write("Server is shutting down. Goodbye!\n".getBytes());
                                                        out.flush();
                                                } catch (IOException e) {
                                                        System.err.println("Error sending shutdown message to client: " + e.getMessage());
                                                }
                                        }
                                }
                                serverSocket.close();
                                log.append("Server stopped.");
                        } catch (IOException e) {
                                System.err.println("Error closing server socket: " + e.getMessage());
                        }
                }
        }
        public static void show() {
                JFrame frame = new JFrame("Server Control");
                frame.setSize(600, 400);
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.setLayout(null);

                log = new JTextArea();
                log.setEditable(false);
                JScrollPane scrollPane = new JScrollPane(log);
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
                                log.append("Server pornit...\n");
                        } else {
                                log.append("Serverul este deja pornit!\n");
                        }
                });
                stopButton.addActionListener(e -> {
                        if (isRunning) {
                                stopServer();
                                statusLabel.setText("Server Status: Oprit");
                                log.append("Server oprit.\n");
                        } else {
                                log.append("Serverul este deja oprit!\n");
                        }
                });

                clearButton.addActionListener(e -> log.setText(""));
                frame.setVisible(true);
        }
}
