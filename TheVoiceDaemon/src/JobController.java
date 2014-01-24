import java.util.*;
import java.io.*;


public class JobController {


    File folder;
    File jobFolder;
    HashMap<File, Job> jobs;

    public JobController() {
        folder = new File(".");
        jobFolder = new File("jobs");
        jobs = new HashMap<File, Job>();
    }


    public JobController(File watchFolder) {
        folder = watchFolder;
        jobFolder = new File(watchFolder + "/jobs/");
        jobs = new HashMap<File, Job>();
    }

    public boolean updateNeeded(ArrayList<File> fileArrayList, Iterator it) {

        int length = 0;
        while(it.hasNext()) {
            length++;
            Map.Entry pairs = (Map.Entry)it.next();
            if(!fileArrayList.contains(pairs.getKey())) {
                return true;
            }
        }
        if(length==fileArrayList.size()) {
            return false;
        }
        return true;
    }

    //TODO: REWRITE INTO TWO METHODS: ONE STARTING AND ONE RUNNING
    public void checkFolder() {
        Iterator it = jobs.entrySet().iterator();
        File[] files = jobFolder.listFiles();
        ArrayList<File> fileList = new ArrayList<File>();
        boolean changed = false;
        if(files != null) {
            for(int i = 0; i < files.length; i++) {
                //System.out.println(files[i]);
                if(files[i].isFile() && files[i].getAbsolutePath().endsWith(".job")) {
                    fileList.add(files[i]);
                    //System.out.println(files[i] + " was added");
                }
            }
            if(updateNeeded(fileList,it)) {
                ArrayList<File> toBeRemoved = new ArrayList<File>();
                ArrayList<File> toBeUpdated = new ArrayList<File>();
                while(it.hasNext()) {
                    Map.Entry pairs = (Map.Entry)it.next();
                    if(fileList.contains(pairs.getKey())) {
                        fileList.remove(pairs.getKey());
                    } else {
                        System.out.println("Fjernede " + pairs.getKey() + " fra fileList");
                        //jobs.remove(pairs.getKey());
                        toBeRemoved.add((File)pairs.getKey());
                    }

                    if(((Job)pairs.getValue()).getLastModified() != ((File)pairs.getKey()).lastModified()) {
                        toBeUpdated.add((File)pairs.getKey());
                    }

                }

                for(File f : toBeRemoved) {
                    jobs.remove(f);
                    // lastModified findes åbenbart stadig efter filen er blevet slettet
                    // Sikkert grundet at den skal kunne kaldes selvom filen endnu ikke er skrevet til disk
                    toBeUpdated.remove(f);
                    changed = true;
                }

                for(File f : fileList) {
                    System.out.println("Tilføjede " + f + " til jobs køen");
                    jobs.put(f, parseFile(f));
                    changed = true;
                }

                for(File f : toBeUpdated) {
                    jobs.put(f, parseFile(f));
                    System.out.println("Opdaterede " + f + ".");
                    changed = true;
                }
            }
            changed = true;
        }
        /*  No longer necessary due to updateNeeded - now we know the update is actually needed without counting
        primitively.
        try {
            if(files != null) {
                if(files.length != count(Constants.allJobs)) {
                    changed = true;
                }
            }
        }
        catch (Exception e) {
            System.err.println("Something went wrong");
            e.printStackTrace();
        } */
        if(changed == true){

            try {
                File file = new File(Constants.allJobs);

                // if file doesnt exists, then create it
                if (!file.exists()) {
                    file.createNewFile();
                }

                FileWriter fw = new FileWriter(file.getAbsoluteFile());
                BufferedWriter bw = new BufferedWriter(fw);

                it = jobs.entrySet().iterator();
                while(it.hasNext()) {
                    Map.Entry pairs = (Map.Entry)it.next();
                    bw.write("" + ((File)pairs.getKey()).getName() + ":" + ((Job)pairs.getValue()).getStart() + "-" + ((Job)pairs.getValue()).getStop() + "\n");
                }

                bw.close();
                fw.close();

            } catch (IOException e) {
                e.printStackTrace();
            }

        }

    }

    // http://stackoverflow.com/questions/453018/number-of-lines-in-a-file-in-java
    private int count(String filename) throws IOException {
        LineNumberReader  lnr = new LineNumberReader(new FileReader(filename));
        lnr.skip(Long.MAX_VALUE);
        int linenumber =  lnr.getLineNumber();
        lnr.close();
        return linenumber;
    }

    public Job parseFile(File file) {
        ArrayList<String> list = new ArrayList<String>();

        if(file.exists()) {
            try {
                BufferedReader input = new BufferedReader(new FileReader(file));
                String line;
                while ((line = input.readLine()) != null) {
                    list.add(line);
                }
                input.close();
            } catch (Exception e) {
                System.err.println(e);
            }
        }
        Job job = new Job();
        String[] splitFilename = file.getAbsolutePath().split("/");
        job.setName(splitFilename[splitFilename.length-1]);


        job.setLastModified(file.lastModified());
        Parser parser = new Parser(folder);
        LinkedList<Action> everyActions = new LinkedList<Action>();
        ArrayList<String> everyString = new ArrayList<String>();
        for(String s : list) {
            String sTrimmed = s.trim();
            if(!sTrimmed.startsWith("#") && sTrimmed.length() > 0) {
                if(sTrimmed.startsWith("start") || sTrimmed.startsWith("stop")) {
                    Calendar curr = Calendar.getInstance();
                    assert(sTrimmed.split(" ").length == 2);
                    String action = sTrimmed.split(" ")[0];
                    String[] when = sTrimmed.split(" ")[1].split("-");

                    curr.set(Integer.parseInt(when[5]), Integer.parseInt(when[4])-1, Integer.parseInt(when[3]),
                            Integer.parseInt(when[0]), Integer.parseInt(when[1]), Integer.parseInt(when[2]));
                    System.out.println(action + " " + curr.getTime().getTime());
                    if(action.equals("start")) {
                        job.setStart(curr.getTime().getTime());
                    } else {
                        job.setStop(curr.getTime().getTime());
                    }

                } else {
                    if(!sTrimmed.split(" ")[1].equals("every")) {
                        job.addCommand(parser.parse(s));
                    } else {
                        everyString.add(sTrimmed);
                    }

                }
            }


        }
        for(String s: everyString) {
            everyActions.addAll(parser.parseEvery(s,job.getStart(),job.getStop()));
        }
        //job.print();
        job.merge(everyActions);
        job.printToFile();
        return job;
    }


    //Itererer gennem jobs og ser om nogle af dem skal startes.
    public void checkJobs() {
        checkJobs(10000);


    }
    // wait in milliseconds
    public void checkJobs(int wait) {
        long now = Calendar.getInstance().getTime().getTime();
        Iterator it = jobs.entrySet().iterator();
        while(it.hasNext()) {
            Map.Entry pairs = (Map.Entry)it.next();
            // Skal jobbet være startet om wait/1000 sekunder, så start det.
            if((now+wait)>((Job)pairs.getValue()).getStart() && now <((Job) pairs.getValue()).getStop()) {

                System.out.println("Starting Job: " + (File)pairs.getKey());
                ((Job)pairs.getValue()).run(((File)pairs.getKey()).getName());
            }
        }

    }
}

