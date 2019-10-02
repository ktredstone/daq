import com.jcraft.jsch.Channel;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

import java.util.Properties;

public class RunSshTest implements Runnable {
    ReportHandler reportHandler;
    Session session;
    Channel channel;
    JSch jsch = new JSch();
    String[] usernames;
    String[] passwords;
    String hostAddress;
    int port;
    boolean testFinished = false;
    int passwordIndex = 0;
    int usernameIndex = 0;
    int attempts = -1;

    public RunSshTest(String[] usernames, String[] passwords, String hostAddress, String port, ReportHandler reportHandler) {
        this.usernames = usernames;
        this.passwords = passwords;
        this.hostAddress = hostAddress;
        this.port = Integer.parseInt(port);
        this.reportHandler = reportHandler;
    }

    public void StartTest() {
        while (!testFinished) {
            if (passwordIndex == passwords.length) {
                usernameIndex++;
                passwordIndex = 0;
            }
            if (usernameIndex > usernames.length - 1) {
                testFinished = true;
                reportHandler.addText("RESULT pass security.passwords Default passwords have been changed");
            } else {
                attempts++;
                try {
                    session = jsch.getSession(usernames[usernameIndex], hostAddress, port);
                    session.setPassword(passwords[passwordIndex]);
                    try {
                        Properties config = new Properties();
                        config.put("StrictHostKeyChecking", "no");
                        session.setConfig(config);
                        session.connect();
                        reportHandler.addText("RESULT fail security.passwords Default passwords have not been changed");
                        testFinished = true;
                    } catch (JSchException e) {
                        if (e.toString().contains("Connection refused")) {
                            reportHandler.addText("RESULT skip security.passwords SSH is not enabled on selected device");
                            testFinished = true;
                            break;
                        } else {
                            passwordIndex++;
                        }
                    }
                } catch (JSchException e) {
                    e.printStackTrace();
                }
            }
        }
        reportHandler.writeReport();
        session.disconnect();
    }


    @Override
    public void run() {
        StartTest();
    }
}
