package mlfq;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class MLFQTest {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("╔════════════════════════════════════════════════╗");
        System.out.println("║   MLFQ Scheduler                               ║");
        System.out.println("╚════════════════════════════════════════════════╝\n");

        List<Process> processes = customInput(scanner);

        if (processes != null) {
            System.out.println("\n" + "=".repeat(50));
            System.out.println("Input Processes:");
            for (Process p : processes) {
                System.out.println("  " + p);
            }
            System.out.println("=".repeat(50) + "\n");

            MLFQScheduler scheduler = new MLFQScheduler(processes);
            scheduler.run();
            scheduler.displayResults();
        }

        scanner.close();
    }

    private static List<Process> customInput(Scanner scanner) {
        System.out.print("Enter number of processes: ");
        int n = scanner.nextInt();
        scanner.nextLine();

        List<Process> processes = new ArrayList<>();

        for (int i = 0; i < n; i++) {
            System.out.println("\nProcess " + (i + 1) + ":");
            System.out.print("  Name: ");
            String name = scanner.nextLine();
            System.out.print("  Arrival time: ");
            int arrival = scanner.nextInt();
            System.out.print("  Burst time: ");
            int burst = scanner.nextInt();
            scanner.nextLine();

            processes.add(new Process(name, arrival, burst));
        }

        return processes;
    }
}
