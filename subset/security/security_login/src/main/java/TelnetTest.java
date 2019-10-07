import org.apache.commons.net.telnet.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class TelnetTest implements Runnable{
    String host;
    int port;
    TelnetClient telnetClient;
    InputStream inputStream;
    OutputStream outputStream;
    Queue<String> receiveQueue = new LinkedList<>();
    Thread readDataThread;
    Thread gatherThread;
    Interrogator interrogator;


    public TelnetTest(String host, String port){
        this.host = host;
        this.port = Integer.parseInt(port);
    }

    public void connectTelnetClient() {
        telnetClient = new TelnetClient();
        addOptionHandlers();
        System.out.println("Starting Telnet Connection");
        try {
            telnetClient.connect(host, port);
            System.out.println("Connected");
        } catch (Exception e) {
            Report reportHandler = new Report("telnet");
            reportHandler.addText("RESULT skip security.login");
            reportHandler.writeReport();
            System.err.println(e.getMessage());
            System.out.println("port:" + port + "ipaddress:" + host);
        }
    }

    public void disconnect() {
        try {
            telnetClient.disconnect();
        } catch (Exception e) {
            System.err.println(e);
        }
    }


    private void readData() {
        int bytesRead = 0;

        inputStream = telnetClient.getInputStream();
        while (telnetClient.isConnected()) {
            try {
                byte[] buffer = new byte[1024];


                bytesRead = inputStream.read(buffer);
                if (bytesRead > 0) {

                    String rawData = normalizeLineEnding(buffer, '\n');
                    receiveQueue.add(rawData);
                } else {
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        System.err.println("InterruptedException readData:" + e.getMessage());
                    }
                }

            } catch (IOException e) {
                System.err.println("Exception while reading socket:" + e.getMessage());
            }
        }
        try {
            inputStream.close();
        }
        catch(IOException e){
            System.err.println("Exception with closing socket:" + e);
        }
    }


    private void gatherData(){
        StringBuilder receiveData = new StringBuilder();
        String receiveDataGathered = "";
        try {
            while (telnetClient.isConnected()) {
                if (receiveQueue.isEmpty()) {
                    Thread.sleep(100);
                } else {
                    Thread.sleep(150);
                    String rxTemp = receiveQueue.poll();
                    receiveData.append(rxTemp);
                    receiveDataGathered = receiveData.toString();
                    System.out.println(receiveDataGathered);
                    System.out.println("-----------END OF DATA-----------");
                    interrogator.receiveData(receiveDataGathered);
                    receiveData.delete(0, receiveDataGathered.length());
                }
            }
        }   catch (InterruptedException e) {
            System.err.println("Exception gatherData:" + e);
        }
    }


    public void writeData(String data) {
        System.out.println("Test Input:   " +data);
        try {
            outputStream = telnetClient.getOutputStream();
            outputStream.write(data.getBytes());
            outputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * * Callback method called when TelnetClient receives an option negotiation command.
     *
     * @param negotiation_code - type of negotiation command received (RECEIVED_DO, RECEIVED_DONT,
     *     RECEIVED_WILL, RECEIVED_WONT, RECEIVED_COMMAND)
     * @param option_code - code of the option negotiated *
     */
    public void receivedNegotiation(int negotiation_code, int option_code) {
        String command = null;
        switch (negotiation_code) {
            case TelnetNotificationHandler.RECEIVED_DO:
                command = "DO";
                break;
            case TelnetNotificationHandler.RECEIVED_DONT:
                command = "DONT";
                break;
            case TelnetNotificationHandler.RECEIVED_WILL:
                command = "WILL";
                break;
            case TelnetNotificationHandler.RECEIVED_WONT:
                command = "WONT";
                break;
            case TelnetNotificationHandler.RECEIVED_COMMAND:
                command = "COMMAND";
                break;
            default:
                command = Integer.toString(negotiation_code); // Should not happen
                break;
        }
        System.out.println("Received " + command + " for option code " + option_code);
    }

    private void addOptionHandlers() {
        TerminalTypeOptionHandler terminalTypeOptionHandler =
                new TerminalTypeOptionHandler("VT100", false, false, true, false);

        EchoOptionHandler echoOptionHandler = new EchoOptionHandler(false, false, false, false);

        SuppressGAOptionHandler suppressGAOptionHandler =
                new SuppressGAOptionHandler(true, true, true, true);

        try {
            telnetClient.addOptionHandler(terminalTypeOptionHandler);
            telnetClient.addOptionHandler(echoOptionHandler);
            telnetClient.addOptionHandler(suppressGAOptionHandler);
        } catch (InvalidTelnetOptionException e) {
            System.err.println(
                    "Error registering option handlers InvalidTelnetOptionException: " + e.getMessage());
        } catch (IOException e) {
            System.err.println("Error registering option handlers IOException: " + e.getMessage());
        }
    }

    private String normalizeLineEnding(byte[] bytes, char endChar) {
        String data = new String(bytes);

        List<Byte> bytesBuffer = new ArrayList<Byte>();

        int countBreak = 0;
        int countESC = 0;

        for (int i = 0; i < bytes.length; i++) {
            if (bytes[i] != 0) {
                switch (bytes[i]) {
                    case 8:
                        // backspace \x08
                        break;
                    case 10:
                        // newLineFeed \x0A
                        countBreak++;
                        bytesBuffer.add((byte) endChar);
                        break;
                    case 13:
                        // carriageReturn \x0D
                        countBreak++;
                        bytesBuffer.add((byte) endChar);
                        break;
                    case 27:
                        // escape \x1B
                        countESC = 2;
                        break;
                    case 33:
                        // character:!
                        break;
                    default:
                        if (countESC == 0) {
                            if (countBreak > 1) {
                                int size = bytesBuffer.size();
                                for (int x = 0; x < countBreak - 1; x++) {
                                    bytesBuffer.remove(size - 1 - x);
                                }
                                countBreak = 0;
                            }
                            bytesBuffer.add(bytes[i]);
                        } else {
                            countESC--;
                        }
                        break;
                }
            }
        }

        String bytesString = "";

        for (Byte byteBuffer : bytesBuffer) {
            bytesString = bytesString + (char) (byte) byteBuffer;
        }

        return bytesString;
    }

    @Override
    public void run() {
        connectTelnetClient();

        interrogator =
                new Interrogator(this);
        Runnable readDataRunnable =
                () -> {
                    readData();
                };
        readDataThread = new Thread(readDataRunnable);
        readDataThread.start();

        Runnable gatherDataRunnable =
                () -> {
                    gatherData();
                };
        gatherThread = new Thread(gatherDataRunnable);
        gatherThread.start();



    }




}