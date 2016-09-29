import org.h2.tools.Server;

import java.sql.*;
import java.util.ArrayList;
import java.util.Scanner;


public class Main {
    public static void createTables(Connection conn) throws SQLException {
        Statement stmt = conn.createStatement();
        stmt.execute("CREATE TABLE IF NOT EXISTS todos (id IDENTITY, user INT, text VARCHAR, is_done BOOLEAN)");
        stmt.execute("CREATE TABLE IF NOT EXISTS users (id IDENTITY, name VARCHAR)");
    }

    public static void insertToDO(Connection conn, String text, int userId) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("INSERT INTO todos VALUES (null, ?, ?, false)");
        stmt.setInt(1, userId);
        stmt.setString(2, text);
        stmt.execute();
    }

    public static void insertUser(Connection conn, String name) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("INSERT INTO users VALUES (null, ?)");
        stmt.setString(1, name);
        stmt.execute();
    }

    public static ArrayList<ToDoItem> selectToDos(Connection conn) throws SQLException {
        ArrayList<ToDoItem> items = new ArrayList();
        Statement stmt = conn.createStatement();
        ResultSet results = stmt.executeQuery("SELECT * FROM todos INNER JOIN users ON todos.user = users.id");
        while (results.next()) {
            int id = results.getInt("todos.id");
            int user = results.getInt("users.id");
            String text = results.getString("text");
            boolean isDone = results.getBoolean("is_done");
            items.add(new ToDoItem(id, user, text, isDone));
        }
        return items;
    }

    public static ToDoItem selectToDo(Connection conn, int id) throws SQLException {
        ToDoItem item = new ToDoItem();
        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM todos INNER JOIN users ON todos.user = users.id WHERE todos.id = ?");
        stmt.setInt(1, id);
        ResultSet results = stmt.executeQuery();
        while (results.next()) {
            item.id = results.getInt("todos.id");
            item.user = results.getInt("users.id");
            item.text = results.getString("text");
            item.isDone = results.getBoolean("is_done");
        }
        return item;
    }

    public static ArrayList<User> selectUsers(Connection conn) throws SQLException {
        ArrayList<User> users = new ArrayList();
        Statement stmt = conn.createStatement();
        ResultSet results = stmt.executeQuery("SELECT * FROM users");
        while (results.next()) {
            int id = results.getInt("id");
            String name = results.getString("name");
            users.add(new User(id, name));
        }
        return users;
    }

    public static User selectUser(Connection conn, String name) throws SQLException {
        User user = null;
        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM users WHERE name = ?");
        stmt.setString(1,name);
        ResultSet results = stmt.executeQuery();
        if (results.next()) {
            int id = results.getInt("id");
            user = new User(id, name);
        }
        return user;
    }

    public static User selectUser(Connection conn, int id) throws SQLException {
        User user = null;
        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM users WHERE id = ?");
        stmt.setInt(1,id);
        ResultSet results = stmt.executeQuery();
        if (results.next()) {
            String name = results.getString("name");
            user = new User(id, name);
        }
        return user;
    }

    public static void toggleToDo(Connection conn, int id) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("UPDATE todos SET is_done = NOT is_done WHERE id = ?");
        stmt.setInt(1, id);
        stmt.execute();
    }

    public static void deleteToDo(Connection conn, int id) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("DELETE FROM todos WHERE id = ?");
        stmt.setInt(1, id);
        stmt.execute();
    }

    public static void main(String[] args) throws SQLException {
        Server.createWebServer().start();
        Connection conn = DriverManager.getConnection("jdbc:h2:./main");
        createTables(conn);

        Scanner scanner = new Scanner(System.in);

        System.out.println("Type a user name and hit enter");
        String userName = scanner.nextLine();
        User user = selectUser(conn, userName);
        if (user == null) {
            insertUser(conn, userName);
        }

        while (true) {

            printAllToDos(conn);
            System.out.println("\nOptions:");
            System.out.println("[1] Create new user");
            System.out.println("[2] Create new to-do item");
            System.out.println("[3] Mark a to-do item complete or incomplete");
            System.out.println("[4] Delete to-do item");

            String option = scanner.nextLine();

            if (option.equals("1")) {
                System.out.println("Type a user name and hit enter");
                userName = scanner.nextLine();

                user = selectUser(conn, userName);
                if (user == null) {
                    insertUser(conn, userName);
                }
                else {
                    System.out.println("User already exists.");
                }
            }
            else if (option.equals("2")) {
                System.out.println("Type a todo and hit enter");
                String todo = scanner.nextLine();
                System.out.println("Which user is responsible for this item?");
                printAllUsers(conn);
                userName = scanner.nextLine();

                user = selectUser(conn, userName);
                if (user == null) {
                    insertUser(conn, userName);
                }

                insertToDO(conn, todo, user.id);
            }
            else if (option.equals("3")) {
                System.out.println("Type the number of the todo you want to mark complete");
                printAllToDos(conn);
                int id = Integer.valueOf(scanner.nextLine());

                toggleToDo(conn, id);
            }
            else if (option.equals("4")) {
                System.out.println("Type the number of the todo you want to delete");
                printAllToDos(conn);
                int id = Integer.valueOf(scanner.nextLine());

                deleteToDo(conn, id);
            }
            else {
                System.out.println("Invalid option.");
            }
        }
    }

    public static void printAllToDos(Connection conn) throws SQLException {
        ArrayList<ToDoItem> todos = selectToDos(conn);
        for (ToDoItem todo : todos) {
            String checkBox = "[ ]";
            if (todo.isDone) {
                checkBox = "[x]";
            }
            User user = selectUser(conn, todo.user);
            String line = String.format("%d. %s %s : %s", todo.id, checkBox, todo.text, user.name);
            System.out.println(line);
        }
    }

    public static void printAllUsers(Connection conn) throws SQLException {
        ArrayList<User> users = selectUsers(conn);
        for (User user : users) {
            System.out.println(user.name);
        }
    }
}
