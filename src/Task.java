import java.util.ArrayList;
import java.util.Scanner;

public class Task {
    static Scanner scan = new Scanner(System.in);

    public String name;
    public int hours;
    public ArrayList<String> dependencies = new ArrayList<>();
    public int es = 0;
    public int ls = Integer.MAX_VALUE;
    public int slack = 0;

    public Task(String name, int hours, ArrayList<String> dependencies) {
        this.name = name;
        this.hours = hours;
        this.dependencies = dependencies;
    }
}
