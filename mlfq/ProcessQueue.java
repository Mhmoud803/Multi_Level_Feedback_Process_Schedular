package mlfq;

import java.util.LinkedList;

public class ProcessQueue {
    private LinkedList<Process> queue;
    private int level;

    public ProcessQueue(int level) {
        this.queue = new LinkedList<>();
        this.level = level;
    }

    public void enqueue(Process p) {
        queue.addLast(p);
    }

    public Process dequeue() {
        return queue.isEmpty() ? null : queue.removeFirst();
    }

    public Process peek() {
        return queue.isEmpty() ? null : queue.getFirst();
    }

    public boolean isEmpty() {
        return queue.isEmpty();
    }

    public int size() {
        return queue.size();
    }

    public int getLevel() {
        return level;
    }

    public void clear() {
        queue.clear();
    }

    public LinkedList<Process> getAllProcesses() {
        return new LinkedList<>(queue);
    }
}
