package com.danidemi.jlubricant.embeddable.database.core;

import java.util.ArrayList;
import java.util.List;

/**
 * A mutable {@link Account} that is able to inform {@link Observer Observers} when the account changes.
 */
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

	public void set(Account newAccount) {
		this.account = newAccount;
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
