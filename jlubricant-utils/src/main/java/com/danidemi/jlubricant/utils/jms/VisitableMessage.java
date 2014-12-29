package com.danidemi.jlubricant.utils.jms;

import javax.jms.BytesMessage;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import javax.jms.StreamMessage;
import javax.jms.TextMessage;

public class VisitableMessage<R> {
	
	private Message message;
	
	public VisitableMessage(Message message) {
		super();
		this.message = message;
	}

	public R accept(MessageVisitor<R> messageVisitor) throws Exception {
		if(message instanceof BytesMessage){
			return messageVisitor.onBytesMessage((BytesMessage)message);
		}else if(message instanceof MapMessage){
			return messageVisitor.onMapMessage((MapMessage)message);
		}else if(message instanceof ObjectMessage){
			return messageVisitor.onObjectMessage((ObjectMessage)message);
		}else if(message instanceof StreamMessage){
			return messageVisitor.onStreamMessage((StreamMessage)message);
		}else if(message instanceof TextMessage){
			return messageVisitor.onTextMessage((TextMessage)message);
		}else{
			throw new UnsupportedOperationException("Unknown message of type " + message.getClass());
		}
		
	}
	
}
