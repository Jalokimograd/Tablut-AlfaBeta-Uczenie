package Gra;

import java.awt.GridLayout;
import java.awt.Color;
import javax.swing.JFrame;

public class View
{
	Game gm;
	//table of our Buttons
	public MyButton[][] jb = new MyButton[9][9];
	JFrame jf;
	public View()
	{
		//set our Frame
		jf = new JFrame("Tablut");
		jf.setBounds(100, 100, 450, 450);
		jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		jf.setResizable(false);
		jf.getContentPane().setLayout(new GridLayout(9, 9, 0, 0));
		jf.setVisible(true);
		//create Buttons, and add it to Container
		for(int i=0;i<9;i++)
		{
			for(int j=0;j<9;j++)
			{
				jb[i][j]=new MyButton(i,j);
				jf.getContentPane().add(jb[i][j]);
			}
		}
	}
	//methods to set button's colour
	public void setOrange(int x, int y)
	{
		MyButton b=jb[x][y];
		b.setBackground(Color.orange);
	}
	public void setGray(int x, int y)
	{
		MyButton b=jb[x][y];
		b.setBackground(Color.gray);
	}
	public void setKing(int x, int y)
	{
		MyButton b=jb[x][y];
		b.setBackground(Color.cyan);
	}
	public void setWhite(int x, int y)
	{
		MyButton b=jb[x][y];
		b.setBackground(Color.white);
	}
	public void setBlack(int x, int y)
	{
		MyButton b=jb[x][y];
		b.setBackground(Color.black);
	}
	//methods to show view of our frame
	public void show_view()
	{
		jf.show();
	}
}

		
	



