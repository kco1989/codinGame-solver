import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;

/**
 * Created by lvsw on 2018/8/4.
 */
public class Test {
    public static void main(String[] args) {
        List<String> all = new ArrayList<>();
        all.add("gg1");
        all.add("gg2");
        all.add("gg3");
        all.add("gg4");

        List<String> sub = new ArrayList<>();
        sub.add("gg3");
        sub.add("gg4");
        sub.add("gg45");
        all.removeAll(sub);
        System.out.println(all);
        all.addAll(sub);
        System.out.println(all);
//        System.out.println(-1 / 3 * 3);
//        PriorityQueue<Player1.ProiortyAction> integers = new PriorityQueue<>();
//        integers.add(new Player1.ProiortyAction(0, 1, 1));
//        integers.add(new Player1.ProiortyAction(0, 5, 5));
//        integers.add(new Player1.ProiortyAction(0, 8, 8));
//        integers.add(new Player1.ProiortyAction(0, 3, 3));
//        integers.remove(new Player1.ProiortyAction(8, 8));
//        System.out.println(integers.poll());
//        System.out.println(integers.poll());
//        System.out.println(integers.poll());

    }
}
