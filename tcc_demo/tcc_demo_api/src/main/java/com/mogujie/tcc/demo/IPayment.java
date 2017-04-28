package com.mogujie.tcc.demo;

import com.mogujie.tcc.Participant;


public interface IPayment extends Participant {

    void reserve(Long uuid, String persion, int money);

//	void cancel(Long uuid, String person);

    void pay(Long uuid, String persion);

    boolean isConfirmed(Long uuid);
}
