import java.io.*;
import java.util.*;


public class Runner {

    JobController jobcontroller;

    public Runner(File dir) {
        jobcontroller = new JobController(dir);



    }

    public void run() {
        int waitTime = 2000;
        while(true) {
            // Check for new jobs
            jobcontroller.checkFolder();
            // Move finished recordings
            moveFiles();
            // Do we need to start a job?
            jobcontroller.checkJobs(waitTime);
            try {
                Thread.sleep(waitTime);
            } catch(Exception e) {
                System.err.println(e);
            }

        }
    }

    private void moveFiles() {
        File tempRecording = new File(Constants.tempRecording);
        File[] files = tempRecording.listFiles();
        if(files != null) {
            if(files.length > 0) {
                for(File f: files) {
                    if(f.getName().endsWith(".mkv")) {
                        f.renameTo(new File(Constants.finishedJobs + "/" + f.getName()));
                    }
                }
            }
        }

    }



}
