package com.danidemi.jlubricant.embeddable.database.core;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class ObservableAccountTest {
	
	@Mock BaseAccount initialAccount;
	@Mock BaseAccount newAccount;
	@Mock Observer observer1;
	@Mock Observer observer2;
		
	@Test public void shouldGetUsernameAndPassword() {
		
		// given
		when(initialAccount.getUsername()).thenReturn("u");
		when(initialAccount.getPassword()).thenReturn("p");
		when(newAccount.getUsername()).thenReturn("u2");
		when(newAccount.getPassword()).thenReturn("p2");		
		
		ObservableAccount accountHolder = new ObservableAccount( initialAccount );

		// when
		String username = accountHolder.getUsername();
		String password = accountHolder.getPassword();
		
		// then
		assertThat(username, equalTo("u"));
		assertThat(password, equalTo("p"));
		
		// when
		accountHolder.set(newAccount);
		username = accountHolder.getUsername();
		password = accountHolder.getPassword();	
		
		// then
		assertThat(username, equalTo("u2"));
		assertThat(password, equalTo("p2"));		
		
	}
	
	@Test public void shouldInformTheObserversInTheOrderTheyWhereRegistered(){
		
		// given		
		ObservableAccount accountHolder = new ObservableAccount( initialAccount );
		accountHolder.registerObserver( observer1 );
		accountHolder.registerObserver( observer2 );
		
		// when
		accountHolder.set( newAccount );
		
		// then
		InOrder inOrder = inOrder(observer1, observer2);
		inOrder.verify( observer1 ).update();
		inOrder.verify( observer2 ).update();

	}	

	@Test public void shouldInformTheObserverWhenAccountChange(){
		
		// given		
		ObservableAccount accountHolder = new ObservableAccount( initialAccount );
		accountHolder.registerObserver( observer1 );
		
		// when
		accountHolder.set( newAccount );
		
		// then
		verify( observer1 ).update();
		
	}
	
	@Test public void shouldSupportChangeWhenNoObserversAreRegistered(){
		
		// given		
		ObservableAccount accountHolder = new ObservableAccount( initialAccount );
		
		// when
		accountHolder.set( newAccount );
				
	}	
	
}
