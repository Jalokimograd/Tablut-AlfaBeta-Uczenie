package Algorytmy;

import java.io.Serializable;
import java.util.LinkedList;

public class Node implements Serializable {
	int[] id = new int[4];	//identyfikator w drzewie binarnym
	
	int[][] board = new int[9][9];
	int balance;
	int height;
	
	Node left;				//dzieci w drzewie binarnym
    Node right;
    Node parent;
	
    private LinkedList<Node> parents = new LinkedList<Node>();	//wskazania na stany poprzedzaj¹ce obecny
    private LinkedList<Node> children = new LinkedList<Node>();	//wskazania na stany nastêpuj¹ce po obecnym
	
	private int all;	//ile razy przechodzono przez ten wêze³
	private int win;	//ile z tych przejœæ koñczy³o siê wygran¹
	
    
    

    public Node(int[] id, int[][] board) {
    	
    	for(int i=0; i<4; ++i) {
    		this.id[i] = id[i];
    	}
    	for(int i=0; i<9; ++i)
    		for(int j=0; j<9; ++j)
    			this.board[i][j] = board[i][j];
    	
        all = 0;	//ile by³o wszystkich przejœæ przez ten stan
        win = 0;	//ile przejœæ przez ten stan koñczy³o siê wygran¹
        
        right = null;
        left = null;
        parent = null;
    }
    
    public int[] getId() {
    	return(id);
    }
    public int getWinNumber() {
    	return(win);
    }
    public int getAllNumber() {
    	return(all);
    }
    public double getWinProbability() {
    	return(((double)win/(double)all) * 100);
    }
	public void addParent(Node father) {
		if(parents.contains(father) == false) 
			parents.add(father);
    }
    public void addChild(Node child) {
		if(children.contains(child) == false) 
			children.add(child);
    }
    public void actualizationPositiv() {
    	win++; 
    	all++;
    	//System.out.println("aktualizacja pozytywnego stanu ");
    }
    public void actualizationNegativ() {
    	all++;
    }
    public int[][] getBoard(){
    	return(board);
    }
    public LinkedList<Node> getChildren(){
    	return(children);
    }
    public Node getBestChild(){
    	Node pr = null;
    	if(children.size()>0) pr = children.get(0);
    	
    	for(int i=1; i<children.size(); ++i) {
    		if(children.get(i).getWinProbability() > pr.getWinProbability()) pr = children.get(i);
    	}
    return(pr);
    }
}

