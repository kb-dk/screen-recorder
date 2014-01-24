import java.util.*;
import java.io.*;


public class Parser  {



    private File dir;



    Parser(File dir) {
        this.dir = dir;

    }

    private ArrayList<Action> parseCommand(String command, long time) {

        ArrayList<Action> actions = new ArrayList<Action>();
        if(command.startsWith("click")) {
            String[] numbers = command.trim().split(" ");
            Action action1 = new Action();
            action1.assignCommand((long)time, "xdotool mousemove " + numbers[1] + " " + numbers[2]);
            action1.setWait(1);
            actions.add(action1);
            Action action2 = new Action();
            action2.assignCommand((long)time, "xdotool click 1");
            action2.setWait(1);
            actions.add(action2);
        }

        if(command.startsWith("open")) {
            Action action1 = new Action();
            // Åbner den præcis samme kommando uden at vente (f.eks. en browser)
            //action1.assignCommand((long)time, command.substring(5).trim() + " > /dev/null 2>&1");
            String filename = ""+UUID.randomUUID().hashCode();
            // Stupid IntelliJ
            File file = new File(".");
            try {



                file = new File(dir + "/scripts/"+filename);
                while(file.exists()) {
                    filename = ""+UUID.randomUUID().toString();
                    file = new File(dir +"/scripts/"+filename);
                }

                // if file doesnt exists, then create it
                if (!file.exists()) {
                    file.createNewFile();
                }

                FileWriter fw = new FileWriter(file.getAbsoluteFile());
                BufferedWriter bw = new BufferedWriter(fw);
                bw.write("#! /bin/bash" + "\n");
                bw.write(command.substring(5).trim() + " > /dev/null 2>&1");
                bw.close();

                //TODO: Better implementation than this!
                if(command.substring(5).trim().startsWith("firefox")) {
                    action1.setOpens("firefox");
                }
                if(command.substring(5).trim().startsWith("gnome-terminal")) {
                    action1.setOpens("gnome-terminal");
                }

            } catch (IOException e) {
                e.printStackTrace();
            }


            action1.assignCommand((long)time, "bash " + file.getAbsolutePath());
            action1.setWait(0);
            actions.add(action1);

        }

        if(command.startsWith("paste")) {
            Action action1 = new Action();
            // Åbner den præcis samme kommando uden at vente (f.eks. en browser)
            //action1.assignCommand((long)time, command.substring(5).trim() + " > /dev/null 2>&1");
            String filename = ""+UUID.randomUUID().hashCode();
            // Stupid IntelliJ
            File file = new File(".");
            try {



                file = new File(dir + "/scripts/"+filename);
                while(file.exists()) {
                    filename = ""+UUID.randomUUID().toString();
                    file = new File(dir +"/scripts/"+filename);
                }

                // if file doesnt exists, then create it
                if (!file.exists()) {
                    file.createNewFile();
                }

                FileWriter fw = new FileWriter(file.getAbsoluteFile());
                BufferedWriter bw = new BufferedWriter(fw);
                bw.write("#! /bin/bash" + "\n");
                bw.write("echo \"" +command.substring(6).trim().replaceAll("\"","\\\"") + "\" | xclip -sel clip" + " > /dev/null 2>&1");
                bw.close();


            } catch (IOException e) {
                e.printStackTrace();
            }


            action1.assignCommand((long)time, "bash " + file.getAbsolutePath());
            action1.setWait(1);
            actions.add(action1);

            Action action2 = new Action();
            action2.assignCommand((long)time, "xdotool key Ctrl+v");
            action2.setWait(1);
            actions.add(action2);


        }

        if(command.startsWith("write")) {
            Action action1 = new Action();
            String[] tobetyped = command.substring(6).trim().split(" ");

            action1.setWait(1);
            // TODO: Make a tree or some fancy structure for this
            for(int i = 0; i < tobetyped.length; i++) {
                String[] tobetyped2 = tobetyped[i].split("\\?");

                for(int j = 0; j < tobetyped2.length; j++) {
                    action1 = new Action(1);
                    action1.assignCommand((long)time, "xdotool type " + tobetyped2[j]);
                    actions.add(action1);
                    if(j != tobetyped2.length-1) {
                        action1 = new Action(1);
                        action1.assignCommand((long)time, "xdotool key Shift_L+plus");
                        actions.add(action1);
                    }
                }
                if(i != tobetyped.length-1) {
                    action1 = new Action(1);
                    action1.assignCommand((long)time, "xdotool key space");
                    actions.add(action1);
                }
            }
                    /*
                    action1 = new Action(1);
                    action1.assignCommand((long)time, "xdotool type " + tobetyped[tobetyped.length-1]);
                    actions.add(action1);
                    */

        }

        if(command.startsWith("scroll")) {
            Action action1 = new Action();
            String direction = "Down"; // Down
            String[] commands = command.trim().split(" ");
            if(commands[1].equals("up")) {
                direction = "Up";
            }
            int times = 1;
            if(commands.length == 3) {
                times = Integer.parseInt(commands[2]);
            }
            action1.assignCommand((long)time,"xdotool key " + direction);
            action1.setWait(1);
            for(int i = 0; i < times; i++) {
                actions.add(action1);
            }
        }

        if(command.startsWith("type")) {
            Action action1 = new Action();
            action1.assignCommand((long)time,"xdotool key " + command.substring(5).trim());
            action1.setWait(1);
            actions.add(action1);


        }
        return actions;

    }


    public ArrayList<Action> parse(String string) {
        //ArrayList<Action> actions = new ArrayList<Action>();

        int split = string.indexOf(" ");
        long time = Long.parseLong(string.substring(0,split).trim());
        String command = string.substring(split).trim();

        /*
        if(command.startsWith("click")) {
            String[] numbers = command.trim().split(" ");
            Action action1 = new Action();
            action1.assignCommand((long)time, "xdotool mousemove " + numbers[1] + " " + numbers[2]);
            action1.setWait(1);
            actions.add(action1);
            Action action2 = new Action();
            action2.assignCommand((long)time, "xdotool click 1");
            action2.setWait(1);
            actions.add(action2);
        }

        if(command.startsWith("open")) {
            Action action1 = new Action();
            // Åbner den præcis samme kommando uden at vente (f.eks. en browser)
            //action1.assignCommand((long)time, command.substring(5).trim() + " > /dev/null 2>&1");
            String filename = ""+UUID.randomUUID().hashCode();
            // Stupid IntelliJ
            File file = new File(".");
            try {



                file = new File(dir + "/scripts/"+filename);
                while(file.exists()) {
                    filename = ""+UUID.randomUUID().toString();
                    file = new File(dir +"/scripts/"+filename);
                }

                // if file doesnt exists, then create it
                if (!file.exists()) {
                    file.createNewFile();
                }

                FileWriter fw = new FileWriter(file.getAbsoluteFile());
                BufferedWriter bw = new BufferedWriter(fw);
                bw.write("#! /bin/bash" + "\n");
                bw.write(command.substring(5).trim() + " > /dev/null 2>&1");
                bw.close();

                //TODO: Better implementation than this!
                if(command.substring(5).trim().startsWith("firefox")) {
                    action1.setOpens("firefox");
                }
                if(command.substring(5).trim().startsWith("gnome-terminal")) {
                    action1.setOpens("gnome-terminal");
                }

            } catch (IOException e) {
                e.printStackTrace();
            }


            action1.assignCommand((long)time, "bash " + file.getAbsolutePath());
            action1.setWait(0);
            actions.add(action1);

        }

        if(command.startsWith("write")) {
            Action action1 = new Action();
            String[] tobetyped = command.substring(6).trim().split(" ");

            action1.setWait(1);
            // TODO: Make a tree or some fancy structure for this
            for(int i = 0; i < tobetyped.length; i++) {
                String[] tobetyped2 = tobetyped[i].split("\\?");

                for(int j = 0; j < tobetyped2.length; j++) {
                    action1 = new Action(1);
                    action1.assignCommand((long)time, "xdotool type " + tobetyped2[j]);
                    actions.add(action1);
                    if(j != tobetyped2.length-1) {
                        action1 = new Action(1);
                        action1.assignCommand((long)time, "xdotool key Shift_L+plus");
                        actions.add(action1);
                    }
                }
                if(i != tobetyped.length-1) {
                    action1 = new Action(1);
                    action1.assignCommand((long)time, "xdotool key space");
                    actions.add(action1);
                }
            }

            //action1 = new Action(1);
            //action1.assignCommand((long)time, "xdotool type " + tobetyped[tobetyped.length-1]);
            //actions.add(action1);


        }

        if(command.startsWith("scroll")) {
            Action action1 = new Action();
            String direction = "Down"; // Down
            String[] commands = command.trim().split(" ");
            if(commands[1].equals("up")) {
                direction = "Up";
            }
            int times = 1;
            if(commands.length == 3) {
                times = Integer.parseInt(commands[2]);
            }
            action1.assignCommand((long)time,"xdotool key " + direction);
            action1.setWait(1);
            for(int i = 0; i < times; i++) {
                actions.add(action1);
            }
        }

        if(command.startsWith("type")) {
            Action action1 = new Action();
            action1.assignCommand((long)time,"xdotool key " + command.substring(5).trim());
            action1.setWait(1);
            actions.add(action1);


        }
        */
        return parseCommand(command,time);
    }

    public ArrayList<Action> parseEvery(String line, long startTime, long endTime) {
        ArrayList<Action> temp = new ArrayList<Action>();
        System.out.println("StartTime = " + startTime + ", endTime = " + endTime + " and difference = " + (endTime-startTime));
        long runningTime = (endTime - startTime)/1000;
        String arrayLine[] = line.split(" ");
        assert(arrayLine[1].trim().equals("every"));
        long start = Long.parseLong(arrayLine[0]);
        long everyTime = Long.parseLong(arrayLine[2]);
        long count = start;
        String command = line.substring((arrayLine[0].trim() + " " + arrayLine[1].trim() + " " + arrayLine[2].trim()).length()).trim();
        while(count < runningTime) {
            temp.addAll(parseCommand(command,count));
            count+=everyTime;
        }


        return temp;
    }

    // TODO: REWRITE
    public ArrayList<String> makeCommand(String string) {
        ArrayList<String> strings = new ArrayList<String>();
        Collections.addAll(strings, string.split(" "));
        return strings;

    }





}
