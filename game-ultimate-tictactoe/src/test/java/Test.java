import java.util.ArrayList;
import java.util.List;

/**
 * Created by lvsw on 2018/8/4.
 */
public class Test {
    public static void main(String[] args) {
        Player6.PriorityAction action = Player6.PriorityAction.getPriorityAction(3, 4);
        List<Player6.PriorityAction> actionList = new ArrayList<>();
        actionList.add(action);


        Player6.PriorityAction action2 = Player6.PriorityAction.getPriorityAction(3, 4);
        System.out.println(action == action2);
        actionList.stream().forEach(System.out::println);
    }
}
