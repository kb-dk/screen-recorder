import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class Job {

    private long start, stop, lastModified;
    LinkedList<Action> commands;
    // Auto generated getters/setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    private String name;



    Job() {
        commands = new LinkedList<Action>();

    }

    public void setLastModified(long lastModified) {
        this.lastModified = lastModified;
    }

    public long getLastModified() {
        return lastModified;
    }

    public void setStart(long start) {
        this.start = start;
    }

    public void setStop(long stop) {
        this.stop = stop;
    }

    public long getStart() {
        return start;
    }

    public long getStop() {
        return stop;
    }



    public void addCommand(Action command) {
        // ad or push?
        commands.add(command);
    }

    public void addCommand(ArrayList<Action> command) {

        commands.addAll(command);
    }

    public void run(String name) {

        ArrayList<String> openJobs = new ArrayList<String>();
        long timeElapsed = Calendar.getInstance().getTime().getTime() - start;
        ProcessController pc = new ProcessController();
        pc.startRecording(name);
        while(stop > Calendar.getInstance().getTime().getTime()) {
            // Update time - in seconds
            timeElapsed = (Calendar.getInstance().getTime().getTime() - start) / 1000;


            boolean done = false;
            while(!done) {
                // Check if any of the jobs needs execution
                Action action = commands.peek();
                if(action != null) {
                    if(timeElapsed>action.getStartTime()) {

                        action = commands.remove(); // Same Action
                        pc.start(action.getCommandList(),action.getWait());
                        if(action.opensSomething()) {
                            openJobs.add(action.getOpens());
                        }

                    } else {
                        // Første action skal ikke køres endnu
                        done = true;
                    }
                } else {
                    // null
                    done = true;
                }
            }


            try {
                Thread.sleep(999); // Vi sover lige lidt og venter
            } catch (Exception e) {
                System.err.println(e);
            }

        }
        pc.stopRecording();
        for(String s : openJobs) {
            pc.kill(s);
        }


    }

    // Sorting the list so we can add "every" command to action

    public void merge(LinkedList<Action> actionlist) {
        // Sort the action list according their time of execution
        Collections.sort(actionlist, new Comparator<Action>(){
            public int compare(Action a1, Action a2) {
                return ((Long)a1.getStartTime()).compareTo(a2.getStartTime());
            }
        });
        LinkedList<Action> tempCommands = new LinkedList<Action>();
        while(!commands.isEmpty() && !actionlist.isEmpty() ) {
            if(commands.peekFirst().getStartTime() > actionlist.peekFirst().getStartTime()) {
                tempCommands.add(actionlist.removeFirst());
            } else {
                tempCommands.add(commands.removeFirst());
            }

        }
        // One of the list are empty, hence the rest of the other just have to be appended
        while(!commands.isEmpty()) {
            tempCommands.add(commands.removeFirst());
        }
        while(!actionlist.isEmpty()) {
            tempCommands.add(actionlist.removeFirst());
        }

        // This is the new set of commands.
        commands = tempCommands;

    }


    // Prints the contents of the job in the order of execution
    public void print() {
        LinkedList<Action> tempList = new LinkedList<Action>();
        tempList.addAll(commands);
        Action temp = null;
        while(!tempList.isEmpty()) {
            temp = tempList.removeFirst();
            System.out.println("[" +temp.getStartTime() + "]  " + prettyPrint(temp.getCommandList()));
        }
    }

    // Helper function for the print method
    private String prettyPrint(ArrayList<String> strList) {
        String temp = "";
        for(int i = 0; i < strList.size(); i++) {
            temp += strList.get(i) + " ";
        }
        return temp;
    }
    private String format(int i) {
        if(i > 9) {
            return ""+i;
        }
        return "0"+i;
    }

    private String prettyDate(Calendar c) {
        String tmp = "";
        tmp = tmp + format(c.get(Calendar.DAY_OF_MONTH)) + "-";
        tmp = tmp + format((c.get(Calendar.MONTH)+1)) + "-";
        tmp = tmp + c.get(Calendar.YEAR) + "-";
        tmp = tmp + format(c.get(Calendar.HOUR_OF_DAY)) + "-";
        tmp = tmp + format(c.get(Calendar.MINUTE)) + "-";
        tmp = tmp + format(c.get(Calendar.SECOND));
        return tmp;
    }

    public void printToFile() {
        try {
            File file = new File(Constants.debugFolder + "/" + name);

            // if file doesnt exists, then create it
            if (!file.exists()) {
                file.createNewFile();
            }
            LinkedList<Action> tempList = new LinkedList<Action>();
            tempList.addAll(commands);

            FileWriter fw = new FileWriter(file.getAbsoluteFile());
            BufferedWriter bw = new BufferedWriter(fw);
            Action temp = null;
            Calendar tempCal = Calendar.getInstance();
            tempCal.setTimeInMillis(start);
            bw.write("start: " + prettyDate(tempCal) + "\n");
            while(!tempList.isEmpty()) {
                temp = tempList.removeFirst();
                bw.write("[" +temp.getStartTime() + "]  " + prettyPrint(temp.getCommandList()) + "\n");
            }
            tempCal.setTimeInMillis(stop);
            bw.write("stop: " + prettyDate(tempCal) + "\n");





            bw.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }


}
