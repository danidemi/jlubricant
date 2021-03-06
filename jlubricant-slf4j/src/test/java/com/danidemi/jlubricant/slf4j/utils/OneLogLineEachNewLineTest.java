package com.danidemi.jlubricant.slf4j.utils;

import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;

import org.junit.Assume;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.danidemi.jlubricant.slf4j.utils.CharsToLogMessagesPolicy.Callback;

@RunWith(MockitoJUnitRunner.class)
public class OneLogLineEachNewLineTest {

    @Mock Callback callback;
	@InjectMocks OneLogLineEachNewLine tested;

    @Test public void shouldLogLinesOnWhateverSystem() {

        // when
        tested.onWrite(("this-is-a-line-in-whatever-os" + System.lineSeparator()).toCharArray());

        // then
        verify( callback ).log("this-is-a-line-in-whatever-os");

    }
	
	@Test public void shouldLogOnCloseEvenIfWithoutEndline() {
		
		// when
		tested.onWrite("hello people".toCharArray());
		tested.onClose();
		
		// then
		verify( callback ).log("hello people");
		
	}
	
	@Test public void shouldLogOnceForEachNewLine() {
				
		// when
		tested.onWrite(("hello" + System.lineSeparator() + "world" + System.lineSeparator()).toCharArray());
		
		// then
		InOrder inOrder = inOrder(callback);
		inOrder.verify( callback ).log("hello");
		inOrder.verify( callback ).log("world");
	}	

	@Test public void shouldNotLogIfItDoesNotReceiveANewLine() {
				
		// when
		tested.onWrite("hello world".toCharArray());
		
		// then
		verifyZeroInteractions( callback );
	}
	
}
