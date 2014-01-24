import java.io.*;
import java.util.*;

//TODO: Change class to only check for new files each 10 seconds instead of each reload
public class FileModel {

    private File path;
    private File allJobsPath;
    private File finishedPath;
    private String currentFile;

    public FileModel() {
        this.path = new File(Constants.jobs);
        this.allJobsPath = new File(Constants.allJobs);
        this.finishedPath = new File(Constants.finishedJobs);
        fillHashMap();
        content = "";

    }

    public String getCurrent() {
        return currentFile;
    }

    private HashMap<String, String> times;

    private String content;

    /*
    // Old one
    public List<String> getFiles() {
        ArrayList<String> files = new ArrayList<String>();
        File[] list = path.listFiles();
        for(int i = 0; i < list.length; i++) {
            if(list[i].isFile()) {
                files.add(list[i].getName());
            }
        }
        return files;
    } */

    //TODO: Læs fra mappe eller læs fra fil?
    public List<String> getFiles() {
        ArrayList<String> files = new ArrayList<String>();
        File[] list = path.listFiles();
        fillHashMap();
        for(int i = 0; i < list.length; i++) {
            if(list[i].isFile()) {
                String filename = list[i].getName();
                // Tjek om filen er indekseret af serveren
                //TODO: Pænere
                String collide = "";
                if(checkCollision(filename)) {
                    collide = " COLLISION";
                }

                if(times.get(filename) != null) {
                    String[] startendtimes = times.get(filename).split("-");
                    Calendar cstart = Calendar.getInstance();
                    cstart.setTimeInMillis(Long.parseLong(startendtimes[0]));
                    String timestart = prettyDate(cstart);
                    Calendar cend = Calendar.getInstance();
                    cend.setTimeInMillis((Long.parseLong((startendtimes[1]))));
                    String timeend = prettyDate(cend);

                    String HTML = "<tr><td> <a href=\"edit.do?file=" + filename +"\">" + filename +"</a> </td> <td>" + timestart + " => " + timeend +
                            "</td> <td>" + collide +
                            "</td><td><a href=\"delete.do?file=" + filename
                            + "\" onclick=\"return confirm('Are you sure? Deleting the file will permenantly remove the file')\"> delete </a></td></tr>" ;



                    files.add(HTML);
                }
            }
        }
        return files;
    }

    public List<String> getVideos() {
        ArrayList<String> videos = new ArrayList<String>();
        if(finishedPath.exists()) {
            File[] list = finishedPath.listFiles();
            for(int i = 0; i < list.length; i++) {
                String filename = list[i].getName();
                String HTML = "<tr><td><a href=\"./video/" + filename + "\">" + filename + "</a></td></tr>";
                videos.add(HTML);
            }
        }
        return videos;
    }

    public void saveFile(String file, String fileContent) {
        BufferedWriter writer = null;
        System.out.println("Saving file " + file);
        try
        {
            File savefile = new File(path + "/" + file);
            writer = new BufferedWriter( new FileWriter( savefile));
            writer.write(fileContent.trim());
            System.out.println("Writing " + content + " TO " + savefile);

        }
        catch ( IOException e)
        {
        }
        finally
        {
            try
            {
                if ( writer != null)
                    writer.close( );
            }
            catch ( IOException e)
            {
            }
        }


        loadFile(file);
    }

    public void loadFile(String f) {
        File file = new File(path + "/" + f);
        FileInputStream fis = null;
        BufferedInputStream bis = null;
        BufferedReader dis = null;
        content = "";
        try {
            fis = new FileInputStream(file);

            bis = new BufferedInputStream(fis);
            dis = new BufferedReader(new InputStreamReader(bis));
            String s = "";
            while (dis.ready()) {

                s =dis.readLine().trim();
                content += s + "\n";
            }
        } catch (Exception e) {
            //ouch
        }
        content = content.trim();
        currentFile=f;

    }

    public void deleteFile(String  f) {
        File file = new File(path + "/" + f);
        try {
            if(file.exists()) {
                file.delete();
            }
        } catch (Exception e) {
            System.err.println(e);
        }
    }

    public String getContent() {
        return content.trim();
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

    public void fillHashMap() {
        times = new HashMap<String, String>();
        FileInputStream fis = null;
        BufferedInputStream bis = null;
        BufferedReader dis = null;
        //content = "";
        try {
            fis = new FileInputStream(allJobsPath);

            bis = new BufferedInputStream(fis);
            dis = new BufferedReader(new InputStreamReader(bis));
            String s = "";
            while (dis.ready()) {

                s =dis.readLine().trim();
                times.put(s.split(":")[0],s.split(":")[1]);
            }
        } catch (Exception e) {
            System.err.println(e);
        }

    }

    public boolean checkCollision(String key)
    {

        HashMap<String, String> tempTimes = new HashMap<String, String>();
        fillHashMap();
        tempTimes.putAll(times);
        // eller lav det til at være tempTimes = times. Så får vi kun COLLISION ved den nye i stedet for begge to
        if(tempTimes.get(key) != null) {
            long tempStart = Long.parseLong(tempTimes.get(key).split("-")[0]);
            long tempStop = Long.parseLong(tempTimes.get(key).split("-")[1]);
            tempTimes.remove(key);
            Iterator it = tempTimes.entrySet().iterator();
            boolean ret = false; // return false;

            while(it.hasNext()) {
                Map.Entry pairs = (Map.Entry)it.next();
                String name = (String)pairs.getKey();
                long start = Long.parseLong(((String)pairs.getValue()).split("-")[0]);
                long end = Long.parseLong(((String)pairs.getValue()).split("-")[1]);
                // Compare the two intervals
                boolean condA = tempStart < end;
                boolean condB = tempStop > start;
                ret = ret || (condA && condB); // If we hit one overlap, we change to true



            }


            return ret;
        }
        return false;
    }

    public String getSpace() {
        File space = new File("/");
        String percent = "" + (float)space.getFreeSpace()*100 / space.getTotalSpace();
        return percent.substring(0,5);
    }

    public String getTimespace() {
        File space = new File("/");
        String timeLeft = "" + space.getFreeSpace()/1150000000;
        return timeLeft;
    }

    public String getTime() {
        Calendar calendar = Calendar.getInstance();
        String time = prettyDate(calendar);
        return time;

    }
}
