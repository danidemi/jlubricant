package com.danidemi.jlubricant.utils.jms;

import javax.jms.BytesMessage;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import javax.jms.StreamMessage;
import javax.jms.TextMessage;

public class RejectAllMessageVisitor<R> implements MessageVisitor<R> {

	public R onBytesMessage(BytesMessage message) {
		fail(message);
		return null;
	}

	public R onMapMessage(MapMessage message) {
		fail(message);
		return null;
	}

	public R onObjectMessage(ObjectMessage message) {
		fail(message);
		return null;
	}

	public R onStreamMessage(StreamMessage message) {
		fail(message);
		return null;
	}

	public R onTextMessage(TextMessage message) throws Exception {
		fail(message);
		return null;
	}
	
	private void fail(Message message) {
		throw new UnsupportedOperationException("Not intended to receive messages of type " + message.getClass() + ".");
	}	

}
