class Siri {
    public int num = 0;

    public synchronized void numPlus() {
        num++;
    }

    public synchronized void readNumSync() {
        num ++;
        System.out.println("sync read number is : " + num);
    }

    public void readNum() {
        num ++;
        System.out.println("number is : " + num);
    }
}
public class ThreadTest3 {

    public static void main(String[] args) {
        Siri siri = new Siri();
        new Thread(() -> {
            for (; siri.num < 1000; siri.num++) {
                siri.numPlus();
                System.out.println(Thread.currentThread().getName() + " siri is : " + siri.num);
            }
        }, "T1").start();
        while(siri.num < 1000) {
            siri.readNum();
        }
        siri.readNumSync();
        while (Thread.activeCount() > 2) {
            Thread.yield();
        }
    }
}
