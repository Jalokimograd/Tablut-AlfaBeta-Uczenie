package Gra;

public class Main
{
	public static void main(String[] args)
	{
		View v= new View();
                Game game;
                if(args.length != 0){
                    game = new Game(v,args[0]);
                } else {
                    game = new Game(v,"white");
                }
		game.play();
	}
}