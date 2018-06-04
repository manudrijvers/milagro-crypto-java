
package org.apache.milagro.amcl.FP256BN;  //

import java.util.ArrayList;
import java.util.List;


import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class TestMultithread {

    @Test
    public void testAmclThreads() throws InterruptedException {
        // set generator point
        BIG gx = new BIG(ROM.CURVE_Gx);
        BIG gy = new BIG(ROM.CURVE_Gy);
        ECP genG1 = new ECP(gx, gy);

        // a = 5 * genG1
        ECP a = new ECP();
        a.copy(genG1);
        a = a.mul(new BIG(5));

        // b = 10 * genG1
        ECP b = new ECP();
        b.copy(genG1);
        b = b.mul(new BIG(10));

        // c = 7 * genG1
        ECP c = new ECP();
        c.copy(genG1);
        c = c.mul(new BIG(7));

        // d = 14 * genG1
        ECP d = new ECP();
        d.copy(genG1);
        d = d.mul(new BIG(14));

        List<Thread> runners = new ArrayList<>();

        // run a thread that repeatedly tests a + a = b
        Thread thread = new Thread(new VerifyAmclAdd(a, b));
        thread.setDaemon(true);
        thread.start();
        runners.add(thread);


        // run a thread that repeatedly tests c + c = d
        Thread threadPrime = new Thread(new VerifyAmclAdd(c, d));
        threadPrime.setDaemon(true);
        threadPrime.start();
        runners.add(threadPrime);

        for (Thread t : runners) {
            t.join();
        }
    }

    private class VerifyAmclAdd implements Runnable {
        private ECP a, b;
        private Thread thread;

        public VerifyAmclAdd(ECP a, ECP b) {
            this.a = a;
            this.b = b;
        }

        public void invoke() {
            ECP temp = new ECP();
            temp.copy(a);
            temp.add(a);
            assertTrue(temp.equals(b));
        }

        @Override
        public void run()  {
            try {
                thread = Thread.currentThread();

                for (int i = 10000; i > 0; --i) {
                    invoke();
                }
                System.out.println("Thread " + thread.getName() + " done.");
                thread.interrupt();
            } catch (Exception e) {
                System.out.println("Thread " + thread.getName() + " crashed.");
                thread.interrupt();

            }
        }
    }
}
