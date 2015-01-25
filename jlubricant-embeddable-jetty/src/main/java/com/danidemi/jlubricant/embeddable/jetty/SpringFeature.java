package com.danidemi.jlubricant.embeddable.jetty;

import java.util.Map;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.request.RequestScope;
import org.springframework.web.context.request.SessionScope;
import org.springframework.web.context.support.XmlWebApplicationContext;

public class SpringFeature implements Feature, ApplicationContextAware {

	private XmlWebApplicationContext wac;

	@Override
	@SuppressWarnings("deprecation")
	public void setApplicationContext(ApplicationContext applicationContext)
			throws BeansException {
		
		// This is very vey strange.
		// It seems that XmlWebApplicationContext does not resolves beans in the parent
		// if we search them through getBeanNamesForType() or getBeansOfType(),
		// so we override them
		wac = new XmlWebApplicationContext(){
			
			@Override
			public String[] getBeanNamesForType(Class<?> type) {
				String[] beanNamesForType = super.getBeanNamesForType(type);
				if(beanNamesForType==null || beanNamesForType.length == 0){
					beanNamesForType = this.getParent().getBeanNamesForType(type);
				}
				return beanNamesForType;
			}
			
			@Override
			public <T> Map<String, T> getBeansOfType(Class<T> type)
					throws BeansException {
				Map<String, T> beansOfType = super.getBeansOfType(type);
				if(beansOfType==null || beansOfType.isEmpty()){
					beansOfType = this.getParent().getBeansOfType(type);
				}
				return beansOfType;
			}
		};
		wac.setParent( applicationContext );
		wac.setConfigLocation(SpringUtils.fakeEmptyRootContext());
		
		if(!(applicationContext instanceof ConfigurableApplicationContext)){
			throw new IllegalArgumentException("applicationContext cannot be casted to ConfigurableApplicationContext, impossible to register needed scopes.");
		}
		((ConfigurableApplicationContext)applicationContext).getBeanFactory().registerScope("request", new RequestScope());
		((ConfigurableApplicationContext)applicationContext).getBeanFactory().registerScope("session", new SessionScope());
				
	}
	
	@Override
	public void install(EmbeddableJetty embeddableJetty) {
		if(wac == null) throw new IllegalArgumentException("Too early!");
		embeddableJetty.getHandler().addEventListener(new ContextLoaderListener(wac));
		
	}
	
}
