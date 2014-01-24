import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;


/**
 * This class is the boss of one job.
 *
 * @author Mads Ravn
 * @version 1.0
 * @since Oct 12, 2012
 */


public class ProcessController {


    Process p;
    DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
    ArrayList<Process> processes;

    ProcessController() {
        processes = new ArrayList<Process>();
    }
    /**
     * Executes a command in the prompt. The command is built from a list of strings
     *
     * @param strings command to execute
     * @return 0 (successful)
     */


    public int start(List <String> strings, int wait) {
        try {
            ProcessBuilder pb = new ProcessBuilder(strings);
            pb.redirectErrorStream(true);

            Calendar CNow = Calendar.getInstance();
            System.out.print("[" + dateFormat.format(CNow.getTime()) + "] ");
            for(String s : strings) {
                System.out.print(s + " ");
            }
            System.out.print("and wait = " + wait);
            System.out.println("");

            p = pb.start();
            // removed due to piping
            /*InputStreamReader isr = new  InputStreamReader(p.getInputStream());
            BufferedReader br = new BufferedReader(isr);

            String lineRead;
            while ((lineRead = br.readLine()) != null) {
                System.out.println(lineRead);
            }*/
            processes.add(p);
            if(wait == 1) {
                int rc = p.waitFor();
            }
        } catch(Exception e) {
            System.err.println(e);
        }

        return 0;
    }

    public int kill() {
        p.destroy();
        return 0;
    }

    public int kill(String s) {
        ArrayList<String> temp = new ArrayList<String>();
        temp.add("killall");
        temp.add(s);
        start(temp,0);
        return 0;
    }

    public int startRecording(String name) {
        ArrayList<String> temp = new ArrayList<String>();
        temp.add("bash");
        temp.add("record.sh");
        temp.add(name);
        start(temp,0);
        return 0;
    }

    public int stopRecording() {
        ArrayList<String> temp = new ArrayList<String>();
        temp.add("killall");
        temp.add("ffmpeg");
        start(temp,0);
        for(Process p : processes) {
            try {
                if(p != null) {
                    p.destroy();
                }
            } catch (Exception e) {
                System.err.println(e);
            }
        }
        return 0;
    }

    public int click(int x, int y) {
        ArrayList<String> temp = new ArrayList<String>();
        temp.add("xdotool");
        temp.add("mousemove");
        temp.add(""+x);
        temp.add(""+y);
        start(temp,1);
        temp = new ArrayList<String>();
        temp.add("xdotool");
        temp.add("click");
        temp.add(""+1);
        start(temp,1);
        return 0;
    }


    public int command(String command, int wait) {
        ArrayList<String> temp = new ArrayList<String>();
        Collections.addAll(temp, command.split(" "));
        start(temp,wait);
        return 0;
    }



}
