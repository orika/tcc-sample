package com.mogujie.tcc.demo.impl;

import com.mogujie.tcc.DefaultParticipant;
import com.mogujie.tcc.demo.IPayment;
import com.mogujie.tcc.error.ParticipantException;
import com.mogujie.tesla.api.annotation.Tesla;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Tesla
@Service
public class Payment extends DefaultParticipant implements IPayment {

    private Bank bank = new Bank();

    private Map<Long, String> cache = new HashMap<Long, String>();

    public Payment() {
        try {
            initMethods("com.mogujie.tcc.demo.IPayment");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void pay(Long uuid, String person) {
        Bank.Customer customer = bank.getCustomer(person);
        if (customer == null)
            throw new RuntimeException(person + " is not in the bank");
        customer.pay(uuid);
    }

    public void reserve(Long uuid, String person, int money) {
        Bank.Customer customer = bank.getCustomer(person);
        if (customer == null)
            throw new RuntimeException(person + " is not in the bank");
        customer.reserve(uuid, money);
        cache.put(uuid, person);
    }

//	public void cancel(Long uuid, String person) {
//		if (person == null)
//			return;
//		Bank.Customer customer = bank.getCustomer(person);
//		if (customer == null)
//			throw new RuntimeException(person + " is not in the bank");
//		LogUtil.log(person + " cancel " + uuid + " paying");
//		customer.reserveBack(uuid);
//	}

    public void expire(Long uuid, String person) {
        if (person == null)
            return;
        Bank.Customer customer = bank.getCustomer(person);
        if (customer == null)
            throw new RuntimeException(person + " is not in the bank");
        LogUtil.log(person + " expire " + uuid + " paying");
        customer.reserveBack(uuid);
    }

    @Override
    public void cancel(Long uuid) throws ParticipantException {
        String person = cache.get(uuid);
        if (person == null)
            return;
        Bank.Customer customer = bank.getCustomer(person);
        if (customer == null)
            throw new RuntimeException(person + " is not in the bank");
        LogUtil.log(person + " cancel " + uuid + " paying");
        customer.reserveBack(uuid);
        cache.remove(uuid);
    }

    @Override
    public void confirm(Long uuid) throws ParticipantException {
        pay(uuid, cache.get(uuid));
        cache.remove(uuid);
    }

    @Override
    public void expired(Long uuid) throws ParticipantException {
        expire(uuid, cache.get(uuid));
        cache.remove(uuid);
    }

    @Override
    public boolean isConfirmed(Long uuid) {
        return !cache.containsKey(uuid);
    }
}
