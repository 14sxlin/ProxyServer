/**
 * Created by sparr on 2017/8/5.
 */
public class Person {
    private String name;
    private String addr;
    static int age = 0;
    static {
        age += 1;
    }
    Person(String name){
        this.name = name;
    }

    Person(String name,String addr){
        this(name);
        this.addr = addr;
    }


}
