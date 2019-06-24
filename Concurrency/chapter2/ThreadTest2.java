public class ThreadTest2 implements Runnable{

    private int number = 1;

    @Override
    public void run(){
        for(; number < 100; number++){
            System.out.print(Thread.currentThread().getName() + " " + number + "\n");
        }
    }

    public static void main(String[] args){
        Runnable r = new ThreadTest2();
        Thread t1 = new Thread(r, "T1");
        Thread t2 = new Thread(r, "T2");
        t1.start();
        t2.start();
    }
}
