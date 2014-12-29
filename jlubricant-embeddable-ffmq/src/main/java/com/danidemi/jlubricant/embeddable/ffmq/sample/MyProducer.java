package com.danidemi.jlubricant.embeddable.ffmq.sample;

import static java.lang.String.format;

import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.QueueConnection;
import javax.jms.QueueConnectionFactory;
import javax.jms.QueueSession;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.naming.Context;

public class MyProducer {
	
	private Context context;
	private int numOfMsgs = 3;
	private String name;
	private String destinationName;

	public MyProducer(String name, Context context, int num, String destinationName) {
		this.context = context;
		this.name = name;
		this.numOfMsgs = num;
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
					
					MessageProducer producer = queueSession.createProducer(queue);
					for(int i=0; i<numOfMsgs; i++){
						TextMessage textMessage = queueSession.createTextMessage();
						String body = format("msg #%s/%s", name, i);
						textMessage.setText(body);
						producer.send( textMessage );
						System.out.println( format("sent \"%s\"", body) );
						Thread.yield();
						Thread.sleep(1000);
					}
					producer.close();
					
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