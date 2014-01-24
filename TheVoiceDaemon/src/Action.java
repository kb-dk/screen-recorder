import java.util.ArrayList;
import java.util.Collections;

public class Action {

    private long startTime;
    private ArrayList<String> commandList;
    private int wait;
    private String opens;

    public Action() {
        // Een command line deles op i bider
        commandList = new ArrayList<String>();
        wait = 0;
    }
    public Action(int wait) {
        // Een command line deles op i bider
        commandList = new ArrayList<String>();
        this.wait = wait;
    }


    public String getOpens() {
        return opens;
    }

    public boolean opensSomething() {
        if(opens != null) {
            return true;
        }
        return false;
    }

    public void setOpens(String opens) {
        this.opens = opens;
    }


    public void assignCommand(long time, String command) {
        startTime = time;
        Collections.addAll(commandList, command.split(" "));
    }

    public void setStartTime(long time) {
        startTime = time;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setCommandList(ArrayList<String> commands) {
        commandList = commands;
    }

    public ArrayList<String> getCommandList() {
        return commandList;
    }

    public void appendCommandList(String command) {
        commandList.add(command);
    }

    public void setWait(int wait) {
        this.wait = wait;
    }

    public int getWait() {
        return wait;
    }
}
