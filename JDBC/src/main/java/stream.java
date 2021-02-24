import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class stream {
    public static void main(String[] args) {
        List<Integer> l = Arrays.asList(2,35,6,8,42,3);
        List<Integer> sq = l.stream().map(i -> i*i).collect(Collectors.toList());
        System.out.println(sq);
        List<String> s  = Arrays.asList("Nio", "Java", "", "Golang", "");
        long s1 = s.stream().filter(s2 -> s2.isEmpty()).count();
        System.out.println(s1);
    }
}
