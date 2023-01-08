package history;

import java.util.*;

import tasks.Task;


public class InMemoryHistoryManager implements HistoryManager {
    private final LinkedListHistory linkedListHistory = new LinkedListHistory();

    @Override
    public void add(Task task) {
        Node node = linkedListHistory.getNodeFromMap(task.getId());
        if (node != null) {
            linkedListHistory.removeNode(node);
        }
        linkedListHistory.linkLast(task);
    }

    @Override
    public List<Task> getHistory() {
        return linkedListHistory.getTasks();
    }

    @Override
    public void remove(List<Task> listForDelete) {
        for (Task task : listForDelete) {
            Node node = linkedListHistory.getNodeFromMap(task.getId());
            if (node != null) {
                linkedListHistory.removeNode(node);
                linkedListHistory.removeNodeFromMap(task.getId());
            }
        }
    }

    private static class Node {
        Task data;
        Node next;
        Node prev;

        Node(Node prev, Task data, Node next) {
            this.data = data;
            this.next = next;
            this.prev = prev;
        }
    }

    private class LinkedListHistory {
        private Node head;
        private Node tail;
        private int size = 0;
        private Map<Integer, Node> map = new HashMap<>();

        public void linkLast(Task elem) {
            final Node old = tail;
            final Node newNode = new Node(tail, elem, null);
            tail = newNode;
            if (old == null) {
                head = newNode;
            } else {
                old.next = newNode;
            }
            size++;
            map.put(newNode.data.getId(), newNode);
        }

        public void removeNode(Node node) {
            final Node prev = node.prev;
            final Node next = node.next;
            if (prev != null) {
                prev.next = next;
            } else {
                head = next;
            }
            if (next != null) {
                next.prev = prev;
            } else {
                tail = prev;
            }
            node = null;
            size--;
        }

        public Node getNodeFromMap(int id) {
            return map.get(id);
        }

        public void removeNodeFromMap(int id) {
            map.remove(id);
        }

        private List<Task> getTasks() {
            List<Task> taskList = new ArrayList<>();
            for (Node x = head; x != null; x = x.next) {
                taskList.add(x.data);
            }
            return taskList;
        }

    }

}
