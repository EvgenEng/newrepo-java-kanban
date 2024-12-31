package manager;

import interfaces.HistoryManager;
import task.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryHistoryManager implements HistoryManager {

    private final Map<Integer, Node> nodeMap = new HashMap<>(); // Хранение узлов по ID задачи

    private Node head; // Голова двусвязного списка

    private Node tail; // Хвост двусвязного списка

    // Узел двусвязного списка
    private static class Node {
        private Task task;
        private Node next;
        private Node prev;

        Node(Task task) {
            this.task = task;
        }
    }

    @Override
    public void add(Task task) {
        if (task == null) {
            return;
        }

        // Удаляем задачу из списка, если она уже есть
        remove(task.getId());

        // Добавляем задачу в конец списка
        Node newNode = new Node(task);
        linkLast(newNode);

        // Обновляем HashMap
        nodeMap.put(task.getId(), newNode);
    }

    @Override
    public void remove(int id) {
        // Удаляем задачу из истории
        nodeMap.remove(id); // Удаляем узел из HashMap
        Node node = nodeMap.get(id); // Получаем узел из HashMap
        if (node != null) {
            removeNode(node); // Удаляем узел из списка
        }
    }

    @Override
    public List<Task> getHistory() {
        List<Task> history = new ArrayList<>();
        Node current = head;
        while (current != null) {
            history.add(current.task);
            current = current.next;
        }
        return history;
    }

    // Добавление узла в конец списка
    private void linkLast(Node node) {
        if (tail == null) { // Если список пуст
            head = node;
            tail = node;
        } else {
            tail.next = node;
            node.prev = tail;
            tail = node;
        }
    }

    // Удаление узла из списка
    private void removeNode(Node node) {
        if (node == head && node == tail) { // Если это единственный элемент
            head = null;
            tail = null;
        } else if (node == head) { // Если это голова
            head = node.next;
            if (head != null) {
                head.prev = null;
            }
        } else if (node == tail) { // Если это хвост
            tail = node.prev;
            if (tail != null) {
                tail.next = null;
            }
        } else { // Если это элемент в середине
            Node prevNode = node.prev;
            Node nextNode = node.next;
            if (prevNode != null) {
                prevNode.next = nextNode;
            }
            if (nextNode != null) {
                nextNode.prev = prevNode;
            }
        }
    }
}