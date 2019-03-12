package Gra;

import javax.swing.JButton;

public class MyButton extends JButton {
	private final int fieldX;
	private final int fieldY;
	
	public MyButton(int x, int y){
		super();
		fieldX=x;
		fieldY=y;
	}
	
	public int getFieldX(){
		return fieldX;
	}
	
	public int getFieldY(){
		return fieldY;
	}
}