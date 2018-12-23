package com.marcos.relatorio.util;

public class Log {

	public static void info (Class<?> classe, String mensagen) {
		System.out.print(classe.getSimpleName() + ": " + mensagen);		
	}
	
	public static void infoln (Class<?> classe, String mensagen) {
		System.out.println(classe.getSimpleName() + ": " + mensagen);		
	}
		
	public static void infof (Class<?> classe, String mensagen, Object...args ) {
		System.out.printf(classe.getSimpleName() + ": " + mensagen, args);		
	}
}
