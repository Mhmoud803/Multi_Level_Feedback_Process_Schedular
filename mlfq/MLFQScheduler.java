package mlfq;

import java.util.ArrayList;
import java.util.List;

public class MLFQScheduler {
    private ProcessQueue[] queues;
    private List<Process> allProcesses;
    private int currentTime;
    private int contextSwitches;
    private int lastBoostTime;
    private Process currentProcess;

    private static final int BOOST_INTERVAL = 20;

    public MLFQScheduler(List<Process> processes) {
        this.queues = new ProcessQueue[3];
        this.queues[0] = new ProcessQueue(0);
        this.queues[1] = new ProcessQueue(1);
        this.queues[2] = new ProcessQueue(2);

        this.allProcesses = new ArrayList<>(processes);
        this.currentTime = 0;
        this.contextSwitches = 0;
        this.lastBoostTime = 0;
        this.currentProcess = null;
    }

    public void run() {
        System.out.println("=== MLFQ Scheduler Started ===\n");

        while (!isComplete()) {

            if (currentTime - lastBoostTime >= BOOST_INTERVAL && currentTime > 0) {
                performPriorityBoost();
                lastBoostTime = currentTime;
            }

            handleArrivals();

            Process nextProcess = selectNextProcess();

            if (nextProcess != currentProcess) {
                if (nextProcess != null) {
                    contextSwitches++;
                }
                currentProcess = nextProcess;
            }

            if (currentProcess != null) {
                executeProcess(currentProcess);
            } else {

                currentTime++;
            }

            updateWaitingTimes();
        }

        calculateMetrics();
    }

    private void handleArrivals() {
        for (Process p : allProcesses) {
            if (p.arrival == currentTime) {
                queues[0].enqueue(p);
                p.currentQueue = 0;

                if (currentProcess != null && currentProcess.currentQueue > 0) {
                    queues[currentProcess.currentQueue].enqueue(currentProcess);
                    currentProcess = null;
                }
            }
        }
    }

    private Process selectNextProcess() {

        for (int i = 0; i < 3; i++) {
            if (!queues[i].isEmpty()) {
                return queues[i].dequeue();
            }
        }
        return null;
    }

    private void executeProcess(Process p) {
        boolean completed = p.execute(currentTime);
        currentTime++;

        if (completed) {

            p.completion = currentTime;
            currentProcess = null;
        } else if (p.isQuantumExhausted()) {

            p.demote();
            queues[p.currentQueue].enqueue(p);
            currentProcess = null;
        } else if (p.currentQueue < 2) {

            queues[p.currentQueue].enqueue(p);
            currentProcess = null;
        }

    }

    private void performPriorityBoost() {

        List<Process> toBoost = new ArrayList<>();

        for (int i = 1; i < 3; i++) {
            toBoost.addAll(queues[i].getAllProcesses());
            queues[i].clear();
        }

        if (currentProcess != null && currentProcess.currentQueue > 0) {
            toBoost.add(currentProcess);
            currentProcess = null;
        }

        for (Process p : toBoost) {
            p.boost();
            queues[0].enqueue(p);
        }

        if (!toBoost.isEmpty()) {
            System.out.println("[Time " + currentTime + "] Priority boost: " +
                    toBoost.size() + " processes moved to Q0");
        }
    }

    private void updateWaitingTimes() {
        for (Process p : allProcesses) {
            if (p.arrival <= currentTime && p.remaining > 0 && p != currentProcess) {
                p.waiting++;
            }
        }
    }

    private boolean isComplete() {

        for (Process p : allProcesses) {
            if (p.remaining > 0) {
                return false;
            }
        }
        return true;
    }

    private boolean hasRemainingProcesses() {
        for (Process p : allProcesses) {
            if (p.arrival > currentTime) {
                return true;
            }
        }
        return false;
    }

    private void calculateMetrics() {
        for (Process p : allProcesses) {
            p.turnaround = p.completion - p.arrival;
        }
    }

    public void displayResults() {

        System.out.println("=== PROCESS METRICS ===");
        System.out.println(String.format("%-10s %-8s %-8s %-10s %-10s %-10s %-10s",
                "Process", "Arrival", "Burst", "Completion", "Waiting", "Turnaround", "Response"));
        System.out.println("-".repeat(78));

        int totalWaiting = 0;
        int totalTurnaround = 0;
        int totalResponse = 0;

        for (Process p : allProcesses) {
            System.out.println(String.format("%-10s %-8d %-8d %-10d %-10d %-10d %-10d",
                    p.name, p.arrival, p.burst, p.completion,
                    p.waiting, p.turnaround, p.response));

            totalWaiting += p.waiting;
            totalTurnaround += p.turnaround;
            totalResponse += p.response;
        }

        int n = allProcesses.size();
        System.out.println("-".repeat(78));
        System.out.println(String.format("Average: %46s %-10.2f %-10.2f %-10.2f",
                "", (double) totalWaiting / n, (double) totalTurnaround / n, (double) totalResponse / n));

        System.out.println("\n=== SCHEDULER METRICS ===");
        System.out.println("Context Switches: " + contextSwitches);
        System.out.println("Total Time: " + currentTime);
        System.out.println();
    }
}
