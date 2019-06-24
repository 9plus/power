import java.util.concurrent.TimeUnit;

// 定义一个Data对象，用来保存自增的数据number。
class Data {
    //volatile int number = 0;
    int number = 0;

    public void addPlusPlus() {
        this.number ++;
    }
}

/**
 * 在这个类中，在T1线程种不断自增number，在主线程中不断打印number，发现在T1线程中循环的时候，时不时
 * 主线程的值就会发生该表，这代码着两个线程时不时就会同步一下，并非一定要到synchronized块结束才同步。
 * synchronized的作用就是保证同步，保证同步的反义词是不保证同步，而不是保证不同步。所以在synchronized
 * 块一结束，会立刻同步一次结果。 然后退出主线程的循环
 * 
 * 一个问题是如果T1线程循环的次数很少，synchronized块中自增时，主线程不打印了。事实上两者时并行执行的。
 * 10000的量级才能看出来。
 */
public class ThreadTest1 {
    public static void main(String[] args) {

        Data data = new Data();

        // 创建T1线程进行number的自增
        new Thread(() -> {
            try {
                TimeUnit.SECONDS.sleep(1);
                synchronized (data) {
                    while(data.number <= 10000) {
                        System.out.println("number is : " + data.number);
                        data.addPlusPlus();
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }, "T1").start();

        while (data.number <= 10000) {
            // 主线程一直读取number的值
            System.out.println(data.number);
        }
        System.out.println("number is : " + data.number);

        while (Thread.activeCount() > 2) {
            Thread.yield();
        }
    }
}
