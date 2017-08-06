import java.util.ArrayList;

/**
 * Created by sparr on 2017/8/5.
 */
public class Test {

    public static void main(String[] args) {
        Object obj = new ArrayList<String>();
        System.out.println(obj.getClass());

        Person person = new Person("hello","addr");
        Person person1  = new Person("hello1");
        Person person2 = new Person("hello2","addr");
        System.out.println(Person.age);
    }


}
