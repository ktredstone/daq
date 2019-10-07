import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class Report {

        private String protocol;
        private String report = "";
        private String filePath = "reports/report.txt";
        File reportFile;

        public Report(String protocol) {
            this.protocol = protocol;
            this.filePath = "reports/" + protocol + "_report.txt";
        }

        public void addText(String text) {
            report += text + '\n';
        }

        public void writeReport() {
            reportFile = new File(filePath);
            try {
                reportFile.getParentFile().mkdirs();
                try (BufferedWriter writer = new BufferedWriter(new FileWriter(reportFile))) {
                    writer.write(report);
                }
            } catch (IOException e) {
                System.out.println("Unable to write report");
                System.out.println(e);
            }
        }
}
