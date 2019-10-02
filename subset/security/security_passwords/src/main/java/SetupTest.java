import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class SetupTest {
    String protocol;
    String hostAddress;
    String port;
    String macAddress;
    String domain;
    private static final int minimumMACAddressLength = 5;
    private static final int addressStartPosition = 0;
    private static final int addressEndPosition = 6;
    private static final int manufacturerNamePosition = 7;
    Map<String, String> macDevices = new HashMap<String, String>();
    private InputStream jsonStream = this.getClass().getResourceAsStream("/defaultPasswords.json");
    ReportHandler reportHandler;
    String[] usernames;
    String[] passwords;
    Gson gsonController = new Gson();

    public void readMacList() {
        try {
            InputStream inputStream = this.getClass().getResourceAsStream("/macList.txt");
            StringBuilder resultStringBuilder = new StringBuilder();
            BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = br.readLine()) != null) {
                resultStringBuilder.append(line).append("\n");
                String macAddress;
                String manufacturer;
                if (line.length() > minimumMACAddressLength) {
                    macAddress = line.substring(addressStartPosition, addressEndPosition);
                    manufacturer = line.substring(manufacturerNamePosition);
                    if (manufacturer.length() > addressStartPosition) {
                        macDevices.put(macAddress, manufacturer);

                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getMacAddress() {
        String formattedMac;

        System.out.println("getting mac");
        try {
            macAddress = macAddress.replace(":", "");
            formattedMac = macAddress.substring(addressStartPosition, addressEndPosition).toUpperCase();
            System.out.println("mac retireved");
            getJsonFile(formattedMac);
        } catch (Exception e) {
            reportHandler.addText("RESULT skip security.passwords Device does not have a valid mac address");
            reportHandler.writeReport();
        }
    }

    public void getJsonFile(String macAddress) {
        JsonObject jsonFileContents = gsonController.fromJson(new InputStreamReader(jsonStream), JsonObject.class);
        JsonObject manufacturer = jsonFileContents.getAsJsonObject(macAddress);
        String jsonUsernames = manufacturer.get("Usernames").getAsString();
        String jsonPasswords = manufacturer.get("Passwords").getAsString();
        usernames = jsonUsernames.split(",");
        passwords = jsonPasswords.split(",");
        if (protocol.equals("ssh")) {
            RunSshTest runSshTest = new RunSshTest(usernames, passwords, hostAddress, port, reportHandler);
            Thread sshThread = new Thread(runSshTest);
            sshThread.start();
        } else {
            createConsoleCommand(usernames, passwords);
        }

    }

    public SetupTest(String protocol, String hostAddress, String port, String macAddress, String domain) {
        this.protocol = protocol;
        this.hostAddress = hostAddress;
        this.port = port;
        this.macAddress = macAddress;
        this.domain = domain;
        this.reportHandler = new ReportHandler(protocol);


        System.out.println("constructor built");
        readMacList();

        getMacAddress();
    }

    private void createConsoleCommand(String[] usernames, String[] passwords) {
        System.out.println("writing command");
        ArrayList<String> usernamesList = new ArrayList<>(Arrays.asList(usernames));
        ArrayList<String> passwordsList = new ArrayList<>(Arrays.asList(passwords));
        String command;
        command = "ncrack ";


        if (protocol.equals("https") || protocol.equals("http")) {
            command += domain + " ";
        }

        command += protocol + "://";
        command += hostAddress + ":";
        command += port + " ";
        command += "--user ";

        StringBuilder str = new StringBuilder(command);

        System.out.println("Num of usernames= " + usernamesList.size());
        System.out.println("Num of passwords= " + passwordsList.size());
        for (String username : usernamesList) {
            if (usernamesList.indexOf(username) != (usernamesList.size() - 1)) {
                str.append(username + ",");
            } else {
                str.append(username + " --pass ");
            }
        }
        for (String password : passwords) {
            if (passwordsList.indexOf(password) != (passwordsList.size() - 1)) {
                str.append(password + ",");
            } else {
                str.append(password + " ");
            }
        }

        String finalCommand = str.toString();
        RunTest runnable = new RunTest(finalCommand, reportHandler);
        System.out.println("command is : " + finalCommand);
        runnable.runCommand(finalCommand);
    }
}
