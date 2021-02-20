package Function;

import Entity.RRSet;
import Entity.Station;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.Set;

public class test {


    public void testStation() {
        Set<RRSet> RRSetSet = new HashSet<>();
        RRSet rrSet1 = new RRSet(1);
        RRSet rrSet2 = new RRSet(2);
        RRSet rrSet3 = new RRSet(3);
        RRSetSet.add(rrSet1);
        RRSetSet.add(rrSet2);
        RRSetSet.add(rrSet3);
        Station station1 = new Station("A");
        Station station2 = new Station("B");

        station1.addRRSet(rrSet1);
        station1.addRRSet(rrSet2);

        station2.addRRSet(rrSet2);
        station2.addRRSet(rrSet3);

        RRSetSet.remove(rrSet1);
        System.out.println("");
    }

    public void test2() {

        long time = System.currentTimeMillis();
        for (int j = 0; j < 1000000000; j++) {
            for (int i = 0; i < 1000000000; i++) {
                for (int k = 0; k < 1000000000; k++) {
                    int n = 1;
                    n++;
                    n *= n;
                    n--;
                }
            }
        }
        time = System.currentTimeMillis() - time;
        System.out.println("Int " + time);


        time = System.currentTimeMillis();
        for (int j = 0; j < 1000000000; j++) {
            for (int i = 0; i < 1000000000; i++) {
                for (int k = 0; k < 1000000000; k++) {
                    double n = 1.0;
                    n++;
                    n *= n;
                    n--;
                }
            }
        }
        time = System.currentTimeMillis() - time;
        System.out.println("double " + time);
    }

    public void test() {
        PriorityQueue<Student> queue = new PriorityQueue<>();
        ArrayList<Student> list = new ArrayList<>();

        for (int i = 0; i < 10; i++) {
            Student student = new Student((int) (Math.random() * 100));
            queue.add(student);
            queue.add(student);
            queue.add(student);
            list.add(student);
        }

        for (int i = 0; i < 10; i++) {
            list.get(i).id = i;
        }

        for (int i = 0; i < 10; i++) {
            System.out.println(queue.poll().id);
        }
    }
}

class Student implements Comparable<Student> {
    public int id;

    public Student(int id) {
        this.id = id;
    }

    @Override
    public int compareTo(Student o) {
        return Integer.compare(o.id, this.id);
    }
}
