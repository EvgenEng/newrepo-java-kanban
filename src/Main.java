import interfaces.TaskManager;
import manager.FileBackedTaskManager;
import manager.ManagerSaveException;
import task.Epic;
import task.Subtask;
import task.Task;
import task.TaskStatus;
import manager.HttpTaskServer;
import java.io.File;
import java.io.IOException;

public class Main {
    public static void main(String[] args) throws ManagerSaveException, IOException {
    File dataFile = new File("tasks.csv");
    TaskManager taskManager = new FileBackedTaskManager(dataFile.getAbsolutePath());

    Task task1 = new Task("Задача 1", "Описание задачи 1");
    Task task2 = new Task("Задача 2", "Описание задачи 2");
    taskManager.createTask(task1);
    taskManager.createTask(task2);

    int epic1Id = 1;
    Epic epic1 = new Epic("Эпик 1", "Описание эпика 1", epic1Id);
    taskManager.createEpic(epic1);

    Subtask subtask1 = new Subtask("Подзадача 1", "Описание подзадачи 1", epic1, TaskStatus.NEW, 1);
    Subtask subtask2 = new Subtask("Подзадача 2", "Описание подзадачи 2", epic1, TaskStatus.NEW, 2);
    taskManager.createSubtask(subtask1);
    taskManager.createSubtask(subtask2);

    int epic2Id = 2;
    Epic epic2 = new Epic("Эпик 2", "Описание эпика 2", epic2Id);
    taskManager.createEpic(epic2);

    Subtask subtask3 = new Subtask("Подзадача 3", "Описание подзадачи 3", epic1, TaskStatus.NEW, 3);
    taskManager.createSubtask(subtask3);

    try {
        TaskManager loadedManager = FileBackedTaskManager.loadFromFile(dataFile);
        System.out.println("\n=== Данные, загруженные из файла ===");
        printAllTasks(loadedManager);
    } catch (Exception e) {
        System.out.println("Ошибка загрузки: " + e.getMessage());
    }

    printAllTasks(taskManager);

    task1.setStatus(TaskStatus.DONE);
    subtask1.setStatus(TaskStatus.DONE);
    subtask2.setStatus(TaskStatus.NEW);

    taskManager.updateTask(task1);
    taskManager.updateSubtask(subtask1);
    taskManager.updateSubtask(subtask2);

    System.out.println("Статусы после обновления: ");
    System.out.println("Эпик 1 - Статус: " + epic1.getStatus());

    taskManager.deleteTaskById(task1.getId());
    taskManager.deleteEpicById(epic2.getId());

    System.out.println("Списки после удаления: ");
    printAllTasks(taskManager);

    //Запуск сервера
    System.out.println("Запуск HTTP-сервера...");
    HttpTaskServer server = new HttpTaskServer(taskManager);
    server.start();
    System.out.println("HTTP-сервер запущен!");
    }

private static void printAllTasks(TaskManager manager) {
    System.out.println("Задачи:");
    for (Task task : manager.getAllTasks()) {
        System.out.println(task.getTitle() + " - Статус: " + task.getStatus());
    }

    System.out.println("Эпики:");
    for (Epic epic : manager.getAllEpics()) {
        System.out.println(epic.getTitle() + " - Статус: " + epic.getStatus());
        for (Subtask subtask : manager.getSubtasksForEpic(epic.getId())) {
            System.out.println("--> " + subtask.getTitle() + " - Статус: " + subtask.getStatus());
        }
    }

    System.out.println("Подзадачи:");
    for (Subtask subtask : manager.getAllSubtasks()) {
        System.out.println(subtask.getTitle() + " - Статус: " + subtask.getStatus());
    }

    System.out.println("История:");
    for (Task task : manager.getHistory()) {
        System.out.println(task.getTitle());
    }

    System.out.println("--------------------------------------------------");
    }
}