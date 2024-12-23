import interfaces.TaskManager;
import manager.Managers;
import task.Epic;
import task.Subtask;
import task.Task;
import task.TaskStatus;

public class Main {
    public static void main(String[] args) {
        TaskManager taskManager = Managers.getDefault();
        // Создание обычных задач
        Task task1 = new Task("Задача 1", "Описание задачи 1");
        Task task2 = new Task("Задача 2", "Описание задачи 2");
        taskManager.createTask(task1);
        taskManager.createTask(task2);

        // Создание эпиков и подзадач
        int epic1Id = 1; // ID для первого эпика
        Epic epic1 = new Epic("Эпик 1", "Описание эпика 1", epic1Id);
        taskManager.createEpic(epic1);

        // Создаем подзадачи с указанием статуса и ID
        Subtask subtask1 = new Subtask("Подзадача 1", "Описание подзадачи 1", epic1, TaskStatus.NEW, 1);
        Subtask subtask2 = new Subtask("Подзадача 2", "Описание подзадачи 2", epic1, TaskStatus.NEW, 2);
        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);

        int epic2Id = 2; // ID для второго эпика
        Epic epic2 = new Epic("Эпик 2", "Описание эпика 2", epic2Id);
        taskManager.createEpic(epic2);

        // Создаем подзадачу с указанием статуса и ID
        Subtask subtask3 = new Subtask("Подзадача 3", "Описание подзадачи 3", epic1, TaskStatus.NEW, 3);
        taskManager.createSubtask(subtask3);

        // Вывод задач
        System.out.println("Все задачи:");
        for (Task task : taskManager.getAllTasks()) {
            System.out.println(task.getTitle() + " - Статус: " + task.getStatus());
        }

        System.out.println("Все подзадачи:");
        for (Subtask subtask : taskManager.getAllSubtasks()) {
            System.out.println(subtask.getTitle() + " - Статус: " + subtask.getStatus());
        }

        System.out.println("Все эпики:");
        for (Epic epic : taskManager.getAllEpics()) {
            System.out.println(epic.getTitle() + " - Статус: " + epic.getStatus());
        }

        // Обновление статусов
        // устанавливаем статусы для задач и подзадач
        task1.setStatus(TaskStatus.DONE); // Устанавливаем статус задачи
        subtask1.setStatus(TaskStatus.DONE); // Устанавливаем статус подзадачи
        subtask2.setStatus(TaskStatus.NEW); // Устанавливаем статус подзадачи

        // Обновляем задачи и подзадачи в TaskManager
        taskManager.updateTask(task1);
        taskManager.updateSubtask(subtask1);
        taskManager.updateSubtask(subtask2);

        // Проверяем обновленные статусы
        System.out.println("Статусы после обновления: ");
        System.out.println("Задача 1 - Статус: " + task1.getStatus());
        System.out.println("Подзадача 1 - Статус: " + subtask1.getStatus());
        System.out.println("Подзадача 2 - Статус: " + subtask2.getStatus());
        System.out.println("Эпик 1 - Статус: " + epic1.getStatus());

        // Удаление задач
        taskManager.deleteTaskById(task1.getId());
        taskManager.deleteEpicById(epic2.getId());
        System.out.println("Списки после удаления: ");
        System.out.println("Все задачи: ");
        for (Task task : taskManager.getAllTasks()) {
            System.out.println(task.getTitle() + " - Статус: " + task.getStatus());
        }
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