import org.junit.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;

import static org.junit.Assert.*;

/**
 * Created by lee on 9/28/16.
 */
public class MainTest {
    // GIVEN
    public Connection startConnection() throws SQLException {
        Connection conn = DriverManager.getConnection("jdbc:h2:mem:test");
        Main.createTables(conn);
        return conn;
    }

    @Test
    public void testUser() throws SQLException {
        Connection conn = startConnection();
        Main.insertUser(conn, "Alice");
        User user = Main.selectUser(conn, "Alice");
        conn.close();

        assertTrue(user != null);
    }

    @Test
    public void testToDoInsertionAndSelection() throws SQLException {
        Connection conn = startConnection();
        Main.insertUser(conn, "Alice");
        Main.insertUser(conn, "Bob");
        User alice = Main.selectUser(conn, "Alice");
        User bob = Main.selectUser(conn, 2);   //// METHOD OVERLOADED TO ACCEPT NAME OR ID
        Main.insertToDO(conn, "thing to do", alice.id);
        Main.insertToDO(conn, "another thing to do", bob.id);

        ArrayList<ToDoItem> todos = Main.selectToDos(conn);
        conn.close();

        assertTrue(todos.size() == 2);
    }

    @Test
    public void testToggleAndDelete() throws SQLException {
        Connection conn = startConnection();
        Main.insertUser(conn, "Alice");
        Main.insertToDO(conn, "thing to do", 1);

        Main.toggleToDo(conn, 1);
        ToDoItem todo = Main.selectToDo(conn, 1);

        assertTrue(todo.isDone == true);

        Main.deleteToDo(conn, 1);
        ArrayList<ToDoItem> todos = Main.selectToDos(conn);
        conn.close();

        assertTrue(todos.size() == 0);
    }
}
