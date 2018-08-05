import java.util.PriorityQueue;

/**
 * Created by lvsw on 2018/8/4.
 */
public class Test {
    public static void main(String[] args) {
        System.out.println(-1 / 3 * 3);
        PriorityQueue<Player1.ProiortyAction> integers = new PriorityQueue<>();
        integers.add(new Player1.ProiortyAction(0, 1, 1));
        integers.add(new Player1.ProiortyAction(0, 5, 5));
        integers.add(new Player1.ProiortyAction(0, 8, 8));
        integers.add(new Player1.ProiortyAction(0, 3, 3));
        integers.remove(new Player1.ProiortyAction(8, 8));
        System.out.println(integers.poll());
        System.out.println(integers.poll());
        System.out.println(integers.poll());

    }
}
