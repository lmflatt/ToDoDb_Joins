/**
 * Created by lee on 9/27/16.
 */
public class ToDoItem {
    int id;
    int user;
    String text;
    boolean isDone;

    public ToDoItem() {
    }

    public ToDoItem(String text) {
        this.text = text;
        this.isDone = false;
    }

    public ToDoItem(int id, int user, String text, boolean isDone) {
        this.id = id;
        this.user = user;
        this.text = text;
        this.isDone = isDone;
    }
}
