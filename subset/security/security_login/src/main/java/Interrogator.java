public class Interrogator {
    String[] expected = {
            "n:", "Password:", "Last login:", "incorrect", "Connection closed by foreign host.", "Welcome","timed out"
    };

    private TelnetTest telnetTest;
    private static String username ="usernameFail";
    private static String password = "passwordFail";
    private static int maxTries = 6;
    private Report reportHandler;
    private boolean debug = false;

    public Interrogator(
            TelnetTest telnetTest) {
        this.telnetTest = telnetTest;
        reportHandler = new Report("telnet");
    }

    public void receiveData(String data) {

        if (debug) {
            System.out.println(
                    java.time.LocalTime.now() + "receiveDataLen:" + data.length() + "receiveData:" + data);
        }
        if (data != null) {
            validateData(data);
        }
    }

    private int usernameIndex = 0;
    private int passwordCount = 0;
    private int attemptCountz =0;

    public void validateData(String data){
        String trimmedData = data.trim();
        if (trimmedData.contains("timed out")){
            reportHandler.addText("RESULT skip security.login session timed so login can not be tested");
            reportHandler.writeReport();
        }
        if(trimmedData.contains("Welcome")){
            reportHandler.addText("RESULT skip security.login test has accidentally logged in");
            reportHandler.writeReport();
            telnetTest.disconnect();
        }
        else if(trimmedData.endsWith("n:")){
            writeUsername();
        }
        else if(trimmedData.endsWith("assword:")){
            writePassword();
        }
        else if (trimmedData.contains("Maximum")){
            reportHandler.addText("RESULT security.login pass Device has configured a login trap ");
            reportHandler.writeReport();
            telnetTest.disconnect();
        }
    }


    public void writeData(String data) {
        telnetTest.writeData(data);
    }

    public void writePassword(){
        reportHandler.addText("Login attempt: " + attemptCountz);
        attemptCountz++;
        writeData(password.trim()+"\n");
        if(attemptCountz >= maxTries){
                reportHandler.addText("RESULT security.login fail device has not configured a login trap");
                reportHandler.writeReport();
                telnetTest.disconnect();
            }
            else{
                usernameIndex++;
                passwordCount = -1;
            }

        passwordCount++;
    }

    public void writeUsername(){
        if(attemptCountz >= maxTries){
            attemptCountz = 0;

        }
        writeData(username.trim()+"\n");
    }



}
