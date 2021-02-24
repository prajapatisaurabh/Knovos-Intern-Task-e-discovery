public class forkjoin extends Thread{
    @Override
    public  void  run(){
        for (int i=0; i<2; i++){
            try {
                Thread.sleep(500);
                System.out.println("current thread is:"+ Thread.currentThread().getName());
            } catch (InterruptedException e) {
                System.out.println("Exception is caught" + e);
            }
            System.out.println(i);
        }
    }
}
class rutvik{
    public static void main(String[] args) {
        forkjoin t1 = new forkjoin();
        forkjoin t2 = new forkjoin();
        forkjoin t3 = new forkjoin();
        t1.start();
        try{
            System.out.println("current thread" + Thread.currentThread().getName());
            t1.join();
        }
        catch (Exception e){
            System.out.println(e);
        }
        t2.start();
        try{
            System.out.println("current thread" + Thread.currentThread().getName());
            t2.join();
        }
        catch (Exception e){
            System.out.println(e);
        }
        t3.start();
    }
}