public class TelnetTest {
    String host;
    String port;
    TelnetClient

    public TelnetTest(String host, String port){
        this.host = host;
        this.port = port;
    }

    public void connectTelnetClient() {
        telnetClient = new TelnetClient();
        addOptionHandlers();
        System.out.println("Starting Telnet Connection");
        try {
            telnetClient.connect(host, connectionPort);
            System.out.println("Connected");
        } catch (Exception e) {
            Report reportHandler = new Report();
            reportHandler.addText("RESULT skip security.passwords");
            reportHandler.writeReport("telnet");
            System.err.println(e.getMessage());
            System.out.println("port:" + connectionPort + "ipaddress:" + host);
        }
    }


}
