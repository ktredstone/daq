public class SetupTest {
    String host;
    String port;
    String protocol;
    TelnetTest telnetTest;
    public SetupTest(String host, String port, String protocol){
        this.host = host;
        this.port = port;
        this.protocol = protocol;
        switch(protocol){
            case "telnet":
            telnetTest = new TelnetTest(host,port);
            Thread telnetThread = new Thread(telnetTest);
            telnetThread.start();
        }
    }

}