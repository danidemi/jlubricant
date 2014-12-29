package com.danidemi.jlubricant.embeddable.ffmq.sample;

import static java.lang.String.format;

import javax.jms.JMSException;
import javax.jms.Message;
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

public class MySyncReader {
	
	private Context context;
	private String name;
	private String destinationName;
	
	public MySyncReader(String name, Context context, String destinationName) {
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
					
					queueConnection.start();
					
					boolean goOn = true;
					while(goOn) {
						Message receive = null;
						try{
							receive = queueReceiver.receive();
							
							String msg = (String) new VisitableMessage(receive).accept( new RejectAllMessageVisitor<String>(){
								public String onTextMessage(TextMessage message) throws JMSException { return message.getText(); };
							} );
							
							System.out.println(format("Receiver %s received message \"%s\"", name, msg));								
						}catch(Exception e){
							goOn = false;
						}
						if(receive == null){
							goOn = false; // the consumer closed
						}
					}
					queueReceiver.close();
					
					if(queueSession.getTransacted()){
						queueSession.commit();			
					}
					
					queueConnection.close();
					
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}).start();
	}		
}