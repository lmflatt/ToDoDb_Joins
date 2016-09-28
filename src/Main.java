import org.h2.tools.Server;

import java.sql.*;
import java.util.ArrayList;
import java.util.Scanner;


public class Main {

    public static void insertToDO(Connection conn, String text) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("INSERT INTO todos VALUES (null, ?, false)");
        stmt.setString(1, text);
        stmt.execute();
    }

    public static ArrayList<ToDoItem> selectToDos(Connection conn) throws SQLException {
        ArrayList<ToDoItem> items = new ArrayList();
        Statement stmt = conn.createStatement();
        ResultSet results = stmt.executeQuery("SELECT * FROM todos");
        while (results.next()) {
            int id = results.getInt("id");
            String text = results.getString("text");
            boolean isDone = results.getBoolean("is_done");
            items.add(new ToDoItem(id, text, isDone));
        }
        return items;
    }

    public static void toggleToDo(Connection conn, int id) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("UPDATE todos SET is_done = NOT is_done WHERE id = ?");
        stmt.setInt(1, id);
        stmt.execute();
    }

    public static void main(String[] args) throws SQLException {
        Server.createWebServer().start();
        Connection conn = DriverManager.getConnection("jdbc:h2:./main");
        Statement stmt = conn.createStatement();
        stmt.execute("CREATE TABLE IF NOT EXISTS todos (id IDENTITY, text VARCHAR, is_done BOOLEAN)");

//        ArrayList<ToDoItem> todos = new ArrayList();
        Scanner scanner = new Scanner(System.in);

        while (true) {

            System.out.println("Options:");
            System.out.println("[1] Create new to-do item");
            System.out.println("[2] Toggle to-do item");
            System.out.println("[3] List to-do items");

            String option = scanner.nextLine();

            if (option.equals("1")) {
                System.out.println("Type a todo and hit enter");
                String todo = scanner.nextLine();
//                ToDoItem item = new ToDoItem(todo);
                insertToDO(conn, todo);
            }
            else if (option.equals("2")) {
                System.out.println("Type the number of the todo you want to toggle");
                int itemNum = Integer.valueOf(scanner.nextLine());
//                ToDoItem item = todos.get(itemNum - 1);
//                item.isDone = !item.isDone;
                toggleToDo(conn, itemNum);
            }
            else if (option.equals("3")) {
//                int i = 1;
                ArrayList<ToDoItem> todos = selectToDos(conn);
                for (ToDoItem todo : todos) {
                    String checkBox = "[ ]";
                    if (todo.isDone) {
                        checkBox = "[x]";
                    }
                    String line = String.format("%d. %s %s", todo.id, checkBox, todo.text);
                    System.out.println(line);
//                    i++;
                }
            }
            else {
                System.out.println("Invalid option.");
            }
        }
    }
}
