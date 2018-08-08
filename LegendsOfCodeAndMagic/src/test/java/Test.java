import com.sun.org.apache.regexp.internal.RE;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by 666666 on 2018/8/8.
 */
public class Test {
    public static void main(String[] args) {
        List<Integer> list = new ArrayList<>();
        list.add(5);
        list.add(4);
        list.add(6);
        list.add(3);
        List<Integer> collect = list.stream().sorted((o1, o2) -> -1).collect(Collectors.toList());
        System.out.println(collect);
    }

}
