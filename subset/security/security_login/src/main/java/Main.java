public class Main {
    public static void main(String[] args){
        String host = args[0];
        String port = args[1];
        String protocol = args[2];

//        String host = "127.0.0.1";
//        String port = "32770";
//        String protocol = "telnet";

        SetupTest setupTest = new SetupTest(host,port,protocol);
    }

}
