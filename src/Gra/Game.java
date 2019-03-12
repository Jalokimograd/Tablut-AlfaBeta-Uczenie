package Gra;

import alphabeta.AI;
import java.awt.event.MouseListener;
import java.awt.event.MouseEvent;
import Algorytmy.MainInterface;

public class Game
{
	public Point[][] tab = new Point[9][9];
	AI white;
	AI black;
	View v;
	MainInterface mInt;
	int t_a;
	int t_b;
	int temp;
	int counter=0;
	boolean mouse_full;
	boolean win=false;
        boolean ai_turn;
        
	public Game(View view, String dec)
	{
			mInt = new MainInterface();
            v=view;
            mouse_full=false;
            switch (dec) {
                case "white":
                    white=new AI(0, true, mInt);
                    black=new AI(1, false, mInt);
                    ai_turn = true;
                    break;
                case "black":
                    white=new AI(0, false, mInt);
                    black=new AI(1, true, mInt);
                    ai_turn = false;
                    break;
                case "comps":
                    white=new AI(0, false, mInt);
                    black=new AI(1, false, mInt);
                    ai_turn = true;
                    break;
                default:
                    white=new AI(0, true, mInt);
                    black=new AI(1, false, mInt);
                    ai_turn = true;
                    break;
            }
            init();
	}
        
        
    //this method checks, if the game is over    
	public boolean checkWin(int playerID)
	{
		int Kx=-1, Ky=-1;
		//white wins, when their king is on the corner
		for(int i=0;i<9;i++)
		{
			for(int j=0;j<9;j++)
			{
				if(tab[i][j].isPawn==3)
				{
					Kx=i;
					Ky=j;
					break;
				}
			}
			if(Kx!=-1)
			{
				break;
			}
		}
                if(Kx==-1 && Ky==-1){
                    win = true;
                    return playerID==1;
                }
		if((Kx==0||Kx==8)&&(Ky==0||Ky==8))
		{
			win = true;
                    return playerID==0;
		}
		
		//black wins, when king is surrounded
		if(Kx==0||Kx==8){
			//by the ending tile
			if(Ky==1&&tab[Kx][Ky+1].isPawn == 1){
				win = true;
                            return playerID==1;
			} else if(Ky==7&&tab[Kx][Ky-1].isPawn == 1){
				win = true;
                            return playerID==1;
			} 
			//other tiles
			if(tab[Kx][Ky-1].isPawn == 1 && tab[Kx][Ky+1].isPawn == 1){
				win = true;
                            return playerID==1;
			}
		} else if(Ky==0||Ky==8){
			//by the ending tile
			if(Kx==1&&tab[Kx+1][Ky].isPawn == 1){
				win = true;
                            return playerID==1;
			} else if(Kx==7&&tab[Kx-1][Ky].isPawn == 1){
				win = true;
                            return playerID==1;
			}
			//other tiles
			if(tab[Kx-1][Ky].isPawn == 1 && tab[Kx+1][Ky].isPawn == 1){
				win = true;
                            return playerID==1;
			}
		}
		//King in the castle
		else if(Kx==4&&Ky==4){
			//surrounded from all sides
			if(tab[Kx-1][Ky].isPawn == 1 && tab[Kx+1][Ky].isPawn == 1 && tab[Kx][Ky-1].isPawn == 1 && tab[Kx][Ky+1].isPawn == 1){
				win = true;
                            return playerID==1;
			} 
		} 
		//King by the castle -- surrounded from three sides
		else if(Kx==4 &&Ky==3){
			if(tab[Kx-1][Ky].isPawn == 1 && tab[Kx+1][Ky].isPawn == 1 && tab[Kx][Ky-1].isPawn == 1){
				win = true;
                            return playerID==1;
			}
		} else if(Kx==4 &&Ky==5){
			if(tab[Kx-1][Ky].isPawn == 1 && tab[Kx+1][Ky].isPawn == 1 && tab[Kx][Ky+1].isPawn == 1){
				win = true;
                            return playerID==1;
			}
		} else if(Kx==3 &&Ky==4){
			if(tab[Kx-1][Ky].isPawn == 1 && tab[Kx][Ky-1].isPawn == 1 && tab[Kx][Ky+1].isPawn == 1){
				win = true;
                            return playerID==1;
			}
		} else if(Kx==5 &&Ky==4){
			if(tab[Kx+1][Ky].isPawn == 1 && tab[Kx][Ky-1].isPawn == 1 && tab[Kx][Ky+1].isPawn == 1){
				win = true;
                            return playerID==1;
			}
		}
		//other cases
		else{
			if((tab[Kx+1][Ky].isPawn == 1 && tab[Kx-1][Ky].isPawn == 1) || (tab[Kx][Ky+1].isPawn == 1 && tab[Kx][Ky-1].isPawn == 1)){
				win = true;
                            return playerID==1;
			}
		}
            return false;
	}	
	
	//this method is brought after click of button
	public void click(MyButton btn)
	{
            int x = btn.getFieldX();
            int y = btn.getFieldY();
			//firstly, we have to take pawn from field
            if(!mouse_full)
            {
                if((counter==1 && tab[x][y].isPawn > 1) ||(counter==0 && tab[x][y].isPawn == 1)){
                    mouse_full=true;
                    t_a=x;
                    t_b=y;
                }
            }
			//next, we have to put this pawn on the other field
            else
            {
                Point[][] temp;
                //player move
                if(counter==0)
                    temp = black.makeMove(tab, t_a, t_b, x, y);
                else
                    temp = white.makeMove(tab, t_a, t_b, x, y);
				//if move was legal, it is done, and we have to
				//udpate our view for player
                if(temp!=null){
                    for(int i=0; i<9;i++){
                        for(int j=0; j<9;j++){
                            tab[i][j] = temp[i][j];
                        }
                    }
                    update_view();
                    //it is  our opponent's turn
                    counter++;
                    counter%=2;
                    //we have to check, if one of the players won
                    if(checkWin(0)){
                        System.out.println("White won!");
                        white.actualizeQueue();
                    } else if(checkWin(1)){
                        System.out.println("Black won!");
                        black.actualizeQueue();
                    }
                    ai_turn = true;
                }
                mouse_full=false;
            }
	}
	public void init()
	{
		for(int i=0;i<9;i++)
		{
			for(int j=0;j<9;j++)
			{
				tab[i][j]=new Point(i,j);
				//set Corners
				if((i==0||i==8)&&(j==0||j==8))
					tab[i][j].isCorner=true;
				//set Middle and King
				if(i==4&&j==4)
				{
					tab[i][j].isCastle=true;
					tab[i][j].isPawn=3;
				}
				//set Blacks
				if(((i>2&&i<6||j>2&&j<6)&&(i==0||j==0||i==8||j==8))||((i==1||j==1||i==7||j==7)&&(i==4||j==4)))
				{
					
					tab[i][j].isPawn=1;
				}
				//set Whites
				if(((j==4&&i>1&&i<7)||(i==4&&j>1&&j<7))&&(j!=4||i!=4))
				{
					
					tab[i][j].isPawn=2;
				}
			}
		}
		
	}
	//this method udpates our frame to look like our board.
	public void update_view()
	{
            for(int i=0;i<9;i++)
            {
                for(int j=0;j<9;j++)
                {
                    int p_p=tab[i][j].getP();
                    switch(p_p)
                    {
                        case 1:
                            v.setBlack(i,j);
                            break;
                        case 2:
                            v.setWhite(i,j);
                            break;
                        case 3:
                            v.setKing(i,j);
                            break;
                    }
                    //when no pawn, then colour empty field
                    if(p_p==0)
                    {
                        if((i==0||i==8)&&(j==0||j==8))
                            v.setGray(i,j);
                        else
                            v.setOrange(i,j);
                    }
                }
            }
				
				
	}
	//this method sets all Listeners
	public void setListeners()
	{
		for(int i=0;i<9;i++)
		{
			for(int j=0;j<9;j++)
			{
				v.jb[i][j].addMouseListener(new MouseListener()
					{
                                                @Override
						public void mouseClicked(MouseEvent e) 
						{
							int button = e.getButton();
							if (button==MouseEvent.BUTTON1 && !win && !ai_turn){
								// if left click, then do method "click"
								click((MyButton)e.getSource());
							}
						}
						@Override
						public void mouseEntered(MouseEvent e) {}

						@Override
						public void mouseExited(MouseEvent e) {}

						@Override
						public void mousePressed(MouseEvent e) {}

						@Override
						public void mouseReleased(MouseEvent e) {}
					});
			}
		}
	}
	//metoda rozpoczynaj¹ca akcjê gry	
	public void play()
	{
		setListeners(); //tworzenie s³uchaczy
		update_view(); //GUI
		v.show_view(); //utworzenie widoku ramki
                while(!win){
                    System.out.print("");
                    if(ai_turn){
                        
                        Point[][] temp;
                        //AI is going to move
                        if(counter==0)
                            temp = black.makeMove(tab, -1, -1, -1, -1);
                        else
                            temp = white.makeMove(tab, -1, -1, -1, -1);
                                        //if move was legal, it is done, and we have to
                                        //udpate our view for player
                        if(temp!=null){
                            for(int i=0; i<9;i++){
                                for(int j=0; j<9;j++){
                                    tab[i][j] = temp[i][j];
                                }
                            }
                            update_view();
                            //it is  our opponent's turn
                            counter++;
                            counter%=2;
                            //we have to check, if one of the players won
                            if(checkWin(0)){
                                System.out.println("White won!");
                                white.actualizeQueue();
                            } else if(checkWin(1)){
                                System.out.println("Black won!");
                                black.actualizeQueue();
                            }
                            
                            if(counter == 0){
                                ai_turn = black.isAI();
                            } else {
                                ai_turn = white.isAI();
                            }
                        }
                        
                    }
                }
	}
}
		