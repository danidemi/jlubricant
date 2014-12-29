package com.danidemi.jlubricant.utils.jms;

import javax.jms.BytesMessage;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.ObjectMessage;
import javax.jms.StreamMessage;
import javax.jms.TextMessage;

public interface MessageVisitor<R> {

	R onBytesMessage(BytesMessage message);

	R onMapMessage(MapMessage message);

	R onObjectMessage(ObjectMessage message);

	R onStreamMessage(StreamMessage message);

	R onTextMessage(TextMessage message) throws Exception;

}
