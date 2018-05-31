
package org.apache.milagro.amcl.FP256BN;  //

import java.util.ArrayList;
import java.util.List;


import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class TestMultithread {

    @Test
    public void testAmclThreads() throws InterruptedException {
        BIG gx = new BIG(ROM.CURVE_Gx);
        BIG gy = new BIG(ROM.CURVE_Gy);
        ECP genG1 = new ECP(gx, gy);

        ECP a = new ECP();
        ECP b = new ECP();
        BIG big = new BIG(5);

        a.copy(genG1);
        a = a.mul(new BIG(5));

        b.copy(genG1);
        b = b.mul(new BIG(25));
        assertTrue(a.mul(big).equals(b));

        ECP c = new ECP();
        ECP d = new ECP();
        BIG bigPrime = new BIG(7);

        c.copy(genG1);
        c = c.mul(new BIG(3));

        d.copy(genG1);
        d = d.mul(new BIG(21));

        assertTrue(c.mul(bigPrime).equals(d));

        List<Thread> runners = new ArrayList<>();

        Thread thread = new Thread(new VerifyAmcl(a, b, big));
        thread.setDaemon(true);
        thread.start();
        runners.add(thread);

        
        Thread threadPrime = new Thread(new VerifyAmcl(c, d, bigPrime));
        threadPrime.setDaemon(true);
        threadPrime.start();
        runners.add(threadPrime);*/

        for (Thread t : runners) {
            t.join();
        }
    }

    private class VerifyAmcl implements Runnable {
        private ECP a, b;
        private BIG i;

        public VerifyAmcl(ECP a, ECP b, BIG i) {
            this.a = a;
            this.b = b;
            this.i = i;
        }

        public void invoke() {
            assertTrue(a.mul(i).equals(b));
        }

        @Override
        public void run() {
            for (int i = 1000; i > 0; --i) {
                invoke();
            }
            final Thread thread = Thread.currentThread();
            System.out.println("Thread " + thread.getName() + " done.");
            thread.interrupt();
        }
    }
}
