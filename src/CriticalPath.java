import java.util.Scanner;
import java.util.*;


public class CriticalPath {
    static Scanner scan = new Scanner(System.in);
    public ArrayList<Task> tasks = new ArrayList<>();
    public int projectDuration = 0;
    public List<List<String>> criticalPaths = new ArrayList<>();
    public Task finalTask;

    public Task findTaskByName(String name) {
        for (Task task : tasks) {
            if (task.name.equals(name)) {
                return task;
            }
        }
        return null;
    }

    public int computeEarliestStart(Task task) {
        if (task.es > 0 || task.dependencies.isEmpty()) {
            return task.es;
        }

        int maxDependencyES = 0;
        for (String dep : task.dependencies) {
            Task depTask = findTaskByName(dep);
            if (depTask != null) {
                int depES = computeEarliestStart(depTask) + depTask.hours;
                if (depES > maxDependencyES) {
                    maxDependencyES = depES;
                }
            }
        }
        task.es = maxDependencyES; // Set task's earliest start time
        return task.es;
    }

    public void computeLatestStart(Task task, int projectEnd) {
        if (task.ls < projectEnd) {
            return;
        }

        task.ls = projectEnd - task.hours;

        for (Task t : tasks) {
            if (t.dependencies.contains(task.name)) {
                computeLatestStart(t, projectEnd);
                int potentialLS = t.ls - task.hours;
                if (potentialLS < task.ls) {
                    task.ls = potentialLS;
                }
            }
        }

        task.slack = task.ls - task.es;
    }

    public void findCriticalPaths() {

        for (Task task : tasks) {
            if (task.dependencies.isEmpty()) {
                List<String> path = new ArrayList<>();
                findCriticalPathRecursive(task, path, finalTask);
            }
        }
    }

    // Recursive method to traverse and collect critical paths
    public void findCriticalPathRecursive(Task task, List<String> currentPath, Task finalTask) {
        currentPath.add(task.name);

        boolean hasCriticalSuccessor = false;
        for (Task t : tasks) {
            if (t.dependencies.contains(task.name) && t.slack == 0) {
                hasCriticalSuccessor = true;
                findCriticalPathRecursive(t, new ArrayList<>(currentPath), finalTask);
            }
        }

        // Add the current path if it ends at the final task and all tasks in the path are critical
        if (!hasCriticalSuccessor && currentPath.get(currentPath.size() - 1).equals(finalTask.name)) {
            boolean isPathCritical = true;
            for (String taskName : currentPath) {
                Task t = findTaskByName(taskName);
                if (t != null && t.slack > 0) { // Check if any task has slack
                    isPathCritical = false;
                    break;
                }
            }
            if (isPathCritical) {
                criticalPaths.add(new ArrayList<>(currentPath));
            }
        }
    }

    public void criticalPath() {
        System.out.println("Enter number of tasks: ");
        int n = scan.nextInt();
        scan.nextLine();

        for (int i = 0; i < n; i++) {
            System.out.println("Enter task name: ");
            String name = scan.nextLine();
            System.out.println("Enter hours: ");
            int hours = scan.nextInt();
            scan.nextLine();
            System.out.println("Enter number of dependencies: ");
            int dependenciesNum = scan.nextInt();
            scan.nextLine();

            ArrayList<String> dependencies = new ArrayList<>();
            for (int j = 0; j < dependenciesNum; j++) {
                System.out.println("Enter dependency: ");
                dependencies.add(scan.nextLine());
            }
            Task addedTask = new Task(name, hours, dependencies);
            if(i==n-1){
                finalTask=addedTask;
            }
            tasks.add(addedTask);
        }

        for (Task task : tasks) {
            computeEarliestStart(task);
        }

        for (Task task : tasks) {
            int taskEnd = task.es + task.hours;
            if (taskEnd > projectDuration) {
                projectDuration = taskEnd;
            }
        }

        for (Task task : tasks) {
            computeLatestStart(task, projectDuration);
        }

        findCriticalPaths();

        System.out.println("The critical path(s):");
        if (criticalPaths.isEmpty()) {
            System.out.println("No critical paths found.");
        } else {
            for (List<String> path : criticalPaths) {
                System.out.println(String.join(" -> ", path));
            }
        }
    }

}
