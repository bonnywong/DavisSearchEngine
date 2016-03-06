package ir;

import java.io.File;

/**
 * Created by swebo_000 on 2016-03-06.
 */
public class TestSearch {
    static DiskIndex disk;
    public static void main(String[] args) {
        /*
        disk = new DiskIndex();
        disk.connectDB("davis.db");
        PostingsList list = disk.getPostingList("i");
        list.printEntries();
        */
        File f = new File("C:/Users/swebo_000/IdeaProjects/DavisSearchEngine/davis.db");
        System.out.println(f.exists());
    }
}
