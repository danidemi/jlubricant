package com.danidemi.jlubricant.embeddable.database.core;

import java.util.ArrayList;
import java.util.List;

public class ObservableAccount implements Account {

	private List<Observer> observers;
	private Account account;

	public ObservableAccount(Account account) {
		observers = null;
		this.account = account;
	}

	public void registerObserver(Observer observer) {
		if(observers==null){
			observers = new ArrayList<>();
		}
		observers.add(observer);
	}

	public void set(BaseAccount account) {
		this.account = account;
		if(observers != null){
			for (Observer observer : observers) {
				observer.update();							
			}
		}
	}

	@Override
	public String getPassword() {
		return account.getPassword();
	}

	@Override
	public String getUsername() {
		return account.getUsername();
	}

}
