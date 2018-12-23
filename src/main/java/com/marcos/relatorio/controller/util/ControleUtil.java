package com.marcos.relatorio.controller.util;

import javafx.event.EventHandler;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;

public class ControleUtil {

	public static void converterEmNumberField(TextField text) {
		text.addEventFilter(KeyEvent.KEY_TYPED, new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent t) {
				char[] a = t.getCharacter().toCharArray();
				char caractere = a[t.getCharacter().toCharArray().length - 1];
				if (!(caractere >= '0' && caractere <= '9')) {
					t.consume();
		        }
			}
			
		});
	}
	
}
