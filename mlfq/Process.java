package mlfq;

public class Process {
    String name;
    int arrival;
    int burst;
    int remaining;
    int waiting = 0;
    int turnaround = 0;

    int completion = 0;
    int response = -1;
    int currentQueue = 0;
    int quantumUsed = 0;

    public Process(String name, int arrival, int burst) {
        this.name = name;
        this.arrival = arrival;
        this.burst = burst;
        this.remaining = burst;
    }

    public boolean execute(int currentTime) {
        if (response == -1) {
            response = currentTime - arrival;
        }
        remaining--;
        quantumUsed++;
        return remaining == 0;
    }

    public void resetQuantum() {
        quantumUsed = 0;
    }

    public boolean isQuantumExhausted() {
        if (currentQueue == 0)
            return quantumUsed >= 4;
        if (currentQueue == 1)
            return quantumUsed >= 8;
        return false;
    }

    public void demote() {
        if (currentQueue < 2) {
            currentQueue++;
            resetQuantum();
        }
    }

    public void boost() {
        currentQueue = 0;
        resetQuantum();
    }

    @Override
    public String toString() {
        return String.format("%s (A:%d B:%d)", name, arrival, burst);
    }
}
