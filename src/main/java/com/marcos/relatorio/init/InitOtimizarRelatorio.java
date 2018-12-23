package com.marcos.relatorio.init;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Service;

import com.marcos.relatorio.Main;

@Service	
public class InitOtimizarRelatorio implements BeanPostProcessor {
	
	@Autowired
	private Main main;
		
	@Override
	public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
		int i = main.notificarPreLoader();
		System.out.println("Pos Inicializacao *** " + i + " *** " + bean.getClass().getSimpleName());
		return bean;
	}

	@Override
	public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
		main.notificarPreLoader("" + bean.getClass().getSimpleName());
		System.out.println("Pre Inicializacao --- " + bean.getClass().getSimpleName());
		return bean;
	}

}
