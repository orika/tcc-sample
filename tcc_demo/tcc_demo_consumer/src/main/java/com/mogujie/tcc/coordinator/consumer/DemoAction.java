package com.mogujie.tcc.coordinator.consumer;

import com.mogujie.tcc.TccManager;
import com.mogujie.tcc.Transaction;
import com.mogujie.tcc.demo.IPayment;
import com.mogujie.tcc.demo.ISale;

import java.util.Random;

public class DemoAction {

    private static final String PERSON = "kevin";
    private TccManager tccManager;
    private IPayment payment;
    private ISale sale;

    public void setTccManager(TccManager tccManager) {
        this.tccManager = tccManager;
    }

    public void setPayment(IPayment payment) {
        this.payment = payment;
    }

    public void setSale(ISale sale) {
        this.sale = sale;
    }

    public void start() throws Exception {
        Random random = new Random();
        int price = sale.getPrice();
        int onSale = sale.getItemCount();
        for (int i = 0; i < Integer.MAX_VALUE && onSale > 0; i++, onSale = sale.getItemCount()) {
            Thread.sleep(2000);
            int buyCount = random.nextInt(10) + 1;
            int money = price * buyCount;
            Transaction tx = tccManager.beginTransaction("shopping");
            long uuid = tx.getUUID();
            try {
                payment.reserve(uuid, PERSON, money);
                sale.reserve(uuid, buyCount);
                if (random.nextInt(10) < 2)
                    throw new RuntimeException("unknown Exception");
                if (random.nextInt(10) < 5) {
                    System.out.println("canceling " + uuid);
                    tx.cancel();
                    continue;
                }
            } catch (Throwable t) {
                System.out.println("waiting expiring " + uuid);
                continue;
            }
            try {
                System.out.println("confirming " + uuid);
                tx.confirm();
            } catch (Throwable t) {
                t.printStackTrace();
            }
        }
    }
}


