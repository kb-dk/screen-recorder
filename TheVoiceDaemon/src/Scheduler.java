import java.io.File;

public class Scheduler {


	public static void main(String[] args) {


        init();
        File dir = new File(Constants.folder);
        Runner runner = new Runner(dir);
		runner.run();
	
	}

    private static void init() {
        File folder = new File(Constants.folder);
        if(!folder.exists()) {
            folder.mkdirs();
        }

        File debug = new File(Constants.debugFolder);
        if(!debug.exists()) {
            debug.mkdirs();
        }

        File jobFolder = new File(Constants.jobFolder);
        if(!jobFolder.exists()) {
            jobFolder.mkdirs();
        }

        File finishedJobs = new File(Constants.finishedJobs);
        if(!finishedJobs.exists()) {
            finishedJobs.mkdirs();
        }

        /*File allJobs = new File(Constants.allJobs);
        if(!allJobs.exists()) {
            allJobs.createNewFile();
        } */

        File doneRecordings = new File(Constants.doneRecording);
        if(!doneRecordings.exists()) {
            doneRecordings.mkdirs();
        }

        File tempRecordings = new File(Constants.tempRecording);
        if(!tempRecordings.exists()) {
            tempRecordings.mkdirs();
        }

    }




}
