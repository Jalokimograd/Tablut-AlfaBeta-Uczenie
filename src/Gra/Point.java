package Gra;

//this is instance of our virtual field (counterpart of Button)
public class Point
{
	//It has fields, informing us, if this field has some Pawn (and what kind of)
	//and fields informing us if field is corner, or middle of our board
	int x,y;
	int isPawn; //0- brak piona, 1-czarny pion, 2-bia³y pion, 3-król
	boolean isCorner;
	boolean isCastle;
	public  final int getX(){
		return this.x;
	}
	public  final int getY(){
		return this.y;
	}
	public  final int getP(){
		return this.isPawn;
	}
	public  final boolean getCo(){
		return this.isCorner;
	}
	public  final boolean getCa(){
		return this.isCastle;
	}
        
        public void setP(int p){
            isPawn = p;
        }
        
	public Point(int x, int y)
	{
		this.x=x;
		this.y=y;
		this.isPawn=0;
		this.isCorner=false;
		this.isCastle=false;
	}
}
	