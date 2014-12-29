package com.danidemi.jlubricant.embeddable.ffmq.sample;

import static java.lang.String.format;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.Queue;
import javax.jms.QueueConnection;
import javax.jms.QueueConnectionFactory;
import javax.jms.QueueReceiver;
import javax.jms.QueueSession;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.naming.Context;

import com.danidemi.jlubricant.utils.jms.RejectAllMessageVisitor;
import com.danidemi.jlubricant.utils.jms.VisitableMessage;

public class MyAsyncReader implements MessageListener {
	
	private Context context;
	private String name;
	private String destinationName;
	
	public MyAsyncReader(String name, Context context, String destinationName) {
		super();
		this.context = context;
		this.name = name;
		this.destinationName = destinationName;
	}		
	public void start(){
		new Thread(new Runnable() {
			
			public void run() {
				try {
					QueueConnectionFactory connectionFactory = (QueueConnectionFactory)context.lookup("factory/QueueConnectionFactory");
					QueueConnection queueConnection = connectionFactory.createQueueConnection();
					QueueSession queueSession = queueConnection.createQueueSession(false, Session.AUTO_ACKNOWLEDGE);
					Queue queue = queueSession.createQueue(destinationName);
					QueueReceiver queueReceiver = queueSession.createReceiver(queue);
					queueReceiver.setMessageListener(MyAsyncReader.this);
					
					queueConnection.start();
					
					System.out.println("thred terminated");
											
					
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}).start();
	}
	
	public void onMessage(Message message) {
		try {
			String msg = (String) new VisitableMessage(message).accept( new RejectAllMessageVisitor<String>(){
				public String onTextMessage(TextMessage message) throws JMSException { return message.getText(); };
			} );
			System.out.println(format("Receiver %s received message \"%s\"", name, msg));	
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}		
}