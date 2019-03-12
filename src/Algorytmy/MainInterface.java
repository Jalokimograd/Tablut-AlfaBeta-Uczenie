package Algorytmy;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.LinkedList;



/*
 * To jest klasa zbiorcza dla metod publicznych potrzebnych w innych miejscach programu
 * Ca�y zbi�r pakietu "Alorytmy" mo�na traktowa� jak czarn� skrzynk� do kt�rej co� wrzucamy 
 * za pomoc� poni�szych metod. albo co� dostajemy. Wszystkie inne metody publiczne nie
 * wymienione tutaj s�u�� do cal�w diagnostycznych.
 * 
 * 
 * public MainInterface() - konstruktor naszej klasy. Wczytuje on baz� danych z pliku lub tworzy now� w przypadku problemu z odczytem;
 * public void add(int[][] state) - dodanie stanu do aktualnej �cie�ki. Trzeba zaznaczy� �e jeszcze nie jest dodawany do samej struktury
 * 									drzewiastej, to robi nast�pna metoda. Je�eli dany stan ju� jest w bazie to nie jest wstawiany, jednak 
 * 									i tak traktowany jest jako aktualny w kontek�cie pozosta�ych metod;
 * public void QueueActualization() - zapisanie  aktualnej �cie�ki stan�w do struktury drzewiastej (w�a�ciwej bazy danych). Wykonuje si� to
 * 										tylko w sytuacji wygrania lub przegrania (kiedy nasza �cie�ka rozgrywki ko�czy si� w naturalny spos�b)
 * 										Domy�lnie moja cz� kodu nie wykrywa wygranej/przegranej wi�c trzeba to wywo�a� z zewn�trz. 
 * 										Dodatkowo podczas aktualizacji nasza baza jest zapisywana do Pliku. 
 * public int getActualStateValue() - zwraca liczb� przej�� przez dany stan (ile razy w historii programu gra znajdowa�a si� w danym stanie);
 * public double getActualStateWinProbabylity() - zwraca prawdopodobie�stwo z jakim rozgrywki przechodz�ce przez dany stan ko�czy�y si� zwyci�stwem;
 * public int[] getActualStateId() - zwraca id w postaci tablicy int[4], kt�ra jednoznacznie identyfikuje dany stan;
 * public int[][] getActualBoard() - Zwraca stan w postaci tablicy 9x9;
 * public LinkedList<Node> getActualChildren() - zwraca list� stan�w nast�puj�cych po danym;
 * public Node getBestChild() - Zwraca najlepsze dziecko danego stanu;
 * 

 */
public class MainInterface {
	
	BinaryTree tree = null;
	
	public MainInterface() {
		loadFile();
	}
	
	public boolean saveFile() {
		try (ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream("StatesGraph.bin"))) {
    	    outputStream.writeObject(tree);
    	    System.out.println("Poprawne zapisanie bazy danych");
    	    return true;
    	} 
		catch (IOException e) {
    		System.out.println("B��d zapisu bazy danych");
    		return false;
    	}
		
	}
	
	public boolean loadFile() {
		try (ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream("StatesGraph.bin"))) {
    	    tree = (BinaryTree) inputStream.readObject();
    	    System.out.println("Poprawne wczytanie bazy danych");
    	    System.out.println("Rozmiar Drzewa " + tree.getSize());
    	    return true;
    	}
    	catch (IOException | ClassNotFoundException e) {
    		System.out.println("B��d wczytywania bazy danych");
    		tree = new BinaryTree();
    		return false;
    	}
	}
	
	public void add(int[][] state) {
		tree.add(state);
	}
	public void QueueActualization() {
		tree.QueueActualization();
		saveFile();
	}
	public int getActualStateValue() {
		if(tree.getActualState()!=null)
			return tree.getActualState().getAllNumber();
		else return(-1);
	}
	public double getActualStateWinProbabylity() {
		if(tree.getActualState()!=null)
			return tree.getActualState().getWinProbability();
		else return(-1);
	}
	public int[] getActualStateId() {
		if(tree.getActualState()!=null)
			return tree.getActualState().getId();
		else return(null);
	}
	public int[][] getActualBoard(){
		if(tree.getActualState()!=null)
			return tree.getActualState().getBoard();
		else return(null);
	}
	public LinkedList<Node> getActualChildren(){
		if(tree.getActualState()!=null)
			return(tree.getActualState().getChildren());
		else return(null);
    }
	public int getActualQueueSize(){
		return tree.getActualQueue().size();
    }
	public Node getBestChild() {
		if(tree.getActualState()!=null)
			return(tree.getActualState().getBestChild());
		else return(null);
	}
	public Node getActualState() {
		return(tree.getActualState());
	}
	
	
    public static void main(String[] args) {
    		
    	MainInterface m = new MainInterface();
    	
    	//m.loadFile();
    	System.out.println("Czy drzewo jest puste: " + m.tree.isEmpty());
    	
    	System.out.println("Rozmiar Drzewa " + m.tree.getSize());

    	int[][] state1 = new int[][] {
	    	{0, 1, 0, 1, 0, 0, 0, 0, 0}, 
	    	{0, 0, 0, 0, 0, 0, 0, 0, 0},
	    	{0, 0, 0, 0, 2, 2, 2, 2, 2},
	    	{2, 2, 2, 2, 2, 2, 2, 2, 2},
	    	{2, 2, 2, 2, 0, 2, 2, 0, 0},
	    	{0, 0, 0, 0, 0, 0, 0, 0, 0},
	    	{0, 0, 0, 0, 0, 0, 1, 0, 2},
	    	{0, 0, 0, 2, 0, 0, 0, 0, 0},
	    	{0, 0, 0, 0, 2, 0, 0, 0, 0}};
    	
    	int[][] state2 = new int[][] {
        	{1, 1, 0, 0, 0, 0, 0, 0, 0}, 
        	{0, 0, 0, 0, 0, 0, 0, 0, 0},
        	{0, 0, 0, 0, 2, 2, 2, 2, 2},
        	{2, 2, 2, 2, 2, 2, 2, 2, 2},
        	{2, 2, 2, 2, 0, 2, 2, 0, 0},
        	{0, 0, 0, 0, 0, 0, 0, 0, 0},
        	{0, 0, 1, 1, 1, 0, 0, 0, 2},
        	{0, 0, 0, 0, 0, 0, 0, 0, 0},
        	{0, 0, 0, 0, 0, 0, 0, 0, 0}};
        	
    	int[][] state3 = new int[][] {
        	{0, 1, 1, 0, 0, 0, 0, 0, 0}, 
        	{0, 0, 0, 0, 0, 0, 0, 0, 0},
        	{0, 0, 0, 0, 2, 2, 2, 2, 2},
        	{2, 2, 2, 2, 2, 2, 2, 2, 2},
        	{2, 2, 2, 2, 0, 2, 2, 0, 0},
        	{0, 0, 0, 0, 2, 0, 0, 0, 0},
        	{0, 3, 0, 0, 0, 0, 0, 0, 2},
        	{0, 0, 1, 0, 0, 0, 0, 0, 0},
        	{0, 0, 0, 2, 0, 0, 0, 0, 0}};
        	
    	int[][] state4 = new int[][] {
        	{0, 1, 1, 0, 0, 0, 0, 0, 0}, 
        	{0, 0, 0, 0, 0, 0, 0, 0, 0},
        	{0, 2, 0, 0, 2, 2, 2, 2, 2},
        	{2, 2, 2, 2, 2, 2, 2, 2, 2},
        	{2, 2, 2, 2, 0, 2, 2, 0, 0},
        	{0, 0, 0, 0, 2, 0, 0, 0, 0},
        	{0, 3, 0, 0, 0, 1, 1, 0, 2},
        	{0, 0, 1, 0, 0, 0, 0, 0, 0},
        	{0, 0, 0, 2, 0, 0, 0, 0, 0}};
        	
    	int[][] state5 = new int[][] {
        	{1, 1, 0, 0, 0, 0, 0, 0, 0}, 
        	{0, 0, 2, 0, 0, 0, 0, 0, 0},
        	{0, 0, 0, 0, 2, 2, 2, 2, 2},
        	{2, 2, 2, 2, 2, 2, 2, 2, 2},
        	{2, 2, 2, 2, 0, 2, 2, 0, 0},
        	{0, 0, 0, 0, 0, 0, 0, 0, 0},
        	{0, 0, 1, 1, 1, 0, 0, 0, 2},
        	{0, 0, 0, 0, 0, 0, 0, 0, 0},
        	{0, 0, 0, 0, 0, 0, 0, 0, 0}};
        	
    	int[][] state6 = new int[][] {
        	{0, 1, 1, 2, 0, 0, 0, 0, 0}, 
        	{0, 2, 0, 0, 0, 0, 0, 0, 0},
        	{0, 0, 0, 0, 2, 2, 2, 2, 2},
        	{2, 2, 2, 2, 2, 2, 2, 2, 2},
        	{2, 2, 2, 2, 0, 2, 2, 0, 0},
        	{0, 0, 0, 0, 2, 0, 0, 0, 0},
        	{0, 3, 0, 0, 0, 0, 0, 0, 2},
        	{0, 0, 1, 0, 0, 0, 0, 0, 0},
        	{0, 0, 0, 2, 0, 0, 0, 0, 0}};
        	
    	int[][] state7 = new int[][] {
        	{1, 1, 1, 0, 0, 0, 0, 0, 0}, 
        	{2, 0, 0, 0, 0, 0, 0, 0, 0},
        	{0, 0, 0, 0, 2, 2, 2, 2, 2},
        	{2, 2, 2, 2, 2, 2, 2, 2, 2},
        	{2, 2, 2, 2, 0, 2, 2, 0, 0},
        	{0, 0, 0, 0, 2, 0, 0, 0, 0},
        	{0, 3, 0, 0, 0, 1, 1, 0, 2},
        	{0, 0, 1, 0, 0, 0, 0, 0, 0},
        	{0, 0, 0, 2, 0, 0, 0, 0, 0}};
           
    	int[][] state8 = new int[][] {
    		{0, 1, 1, 0, 0, 0, 0, 0, 0}, 
        	{0, 2, 1, 0, 0, 0, 0, 0, 0},
        	{0, 0, 0, 0, 2, 2, 2, 2, 2},
        	{2, 2, 2, 2, 2, 2, 2, 2, 2},
        	{2, 2, 2, 2, 0, 2, 2, 0, 0},
        	{0, 0, 0, 0, 2, 0, 0, 0, 0},
        	{0, 3, 0, 0, 0, 0, 0, 0, 2},
        	{0, 0, 1, 0, 0, 0, 0, 0, 0},
        	{0, 0, 0, 2, 0, 0, 0, 0, 0}};
        	
        	
        System.out.println(m.tree.getActualQueue().size());
        System.out.println("Dodanie 1. elementu");
    	m.add(state1);
    	System.out.println("ilo�� przej�� przez 1. element: " + m.getActualStateValue());
    	System.out.println("ilo�� wygra� przez 1. element:  " + m.tree.getActualState().getWinNumber());
    	System.out.println("ilo��dzieci 1. elementu:        " + m.getActualChildren().size());
    	if(m.getBestChild()!=null)
    		System.out.println("Najlepsze dziecko:              " + m.getBestChild().getWinProbability() + " / " + m.getBestChild().getAllNumber());
    		
    	System.out.println(m.tree.getActualQueue().size());
    	System.out.println("Dodanie 2. elementu");
    	m.add(state2);
    	System.out.println("ilo�� przej�� przez 2. element: " + m.getActualStateValue());
    	System.out.println("ilo�� wygra� przez 2. element:  " + m.tree.getActualState().getWinNumber());
    	System.out.println("ilo��dzieci 2. elementu:        " + m.getActualChildren().size());
    	if(m.getBestChild()!=null)
    		System.out.println("Najlepsze dziecko:              " + m.getBestChild().getWinProbability() + " / " + m.getBestChild().getAllNumber());

    	System.out.println(m.tree.getActualQueue().size());
    	System.out.println("Dodanie 3. elementu");
    	m.add(state3);
    	System.out.println("ilo�� przej�� przez 3. element: " + m.getActualStateValue());
    	System.out.println("ilo�� wygra� przez 3. element:  " + m.tree.getActualState().getWinNumber());
    	System.out.println("ilo��dzieci 3. elementu:        " + m.getActualChildren().size());
    	if(m.getBestChild()!=null)
    		System.out.println("Najlepsze dziecko:              " + m.getBestChild().getWinProbability() + " / " + m.getBestChild().getAllNumber());

    	System.out.println(m.tree.getActualQueue().size());
    	System.out.println("Dodanie 4. elementu");
    	m.add(state4);
    	System.out.println("ilo�� przej�� przez 4. element: " + m.getActualStateValue());
    	System.out.println("ilo�� wygra� przez 4. element:  " + m.tree.getActualState().getWinNumber());
    	System.out.println("ilo��dzieci 4. elementu:        " + m.getActualChildren().size());
    	if(m.getBestChild()!=null)
    		System.out.println("Najlepsze dziecko:              " + m.getBestChild().getWinNumber() + " / " + m.getBestChild().getAllNumber());

    	System.out.println(m.tree.getActualQueue().size());
    	System.out.println("Dodanie 5. elementu");
    	m.add(state5);
    	System.out.println("ilo�� przej�� przez 5. element: " + m.getActualStateValue());
    	System.out.println("ilo�� wygra� przez 5. element:  " + m.tree.getActualState().getWinNumber());
    	System.out.println("ilo��dzieci 5. elementu:        " + m.getActualChildren().size());
    	if(m.getBestChild()!=null)
    		System.out.println("Najlepsze dziecko:              " + m.getBestChild().getWinNumber() + " / " + m.getBestChild().getAllNumber());
    	
    	System.out.println(m.tree.getActualQueue().size());
    	System.out.println("Dodanie 6. elementu");
    	m.add(state6);
    	System.out.println("ilo�� przej�� przez 6. element: " + m.getActualStateValue());
    	System.out.println("ilo�� wygra� przez 6. element:  " + m.tree.getActualState().getWinNumber());
    	System.out.println("ilo��dzieci 6. elementu:        " + m.getActualChildren().size());
    	if(m.getBestChild()!=null)
    		System.out.println("Najlepsze dziecko:              " + m.getBestChild().getWinNumber() + " / " + m.getBestChild().getAllNumber());

    	System.out.println(m.tree.getActualQueue().size());
    	System.out.println("Dodanie 7. elementu");
    	m.add(state7);
    	System.out.println("ilo�� przej�� przez 7. element: " + m.getActualStateValue());
    	System.out.println("ilo�� wygra� przez 7. element:  " + m.tree.getActualState().getWinNumber());
    	System.out.println("ilo��dzieci 7. elementu:        " + m.getActualChildren().size());
    	if(m.getBestChild()!=null)
    		System.out.println("Najlepsze dziecko:              " + m.getBestChild().getWinNumber() + " / " + m.getBestChild().getAllNumber());
    	
    	System.out.println(m.tree.getActualQueue().size());
    	System.out.println("Dodanie 8. elementu");
    	m.add(state8);
    	System.out.println("ilo�� przej�� przez 8. element: " + m.getActualStateValue());
    	System.out.println("ilo�� wygra� przez 8. element:  " + m.tree.getActualState().getWinNumber());
    	System.out.println("ilo��dzieci 8. elementu:        " + m.getActualChildren().size());
    	if(m.getBestChild()!=null)
    		System.out.println("Najlepsze dziecko:              " + m.getBestChild().getWinNumber());
		
    	System.out.println(m.tree.getActualQueue().size());
    	
    	System.out.println("Czy drzewo jest puste: " + m.tree.isEmpty());
    	//System.out.println("Rozmiar Drzewa " + m.tree.getSize());
    	
    	
    	
    	System.out.println("Aktualizacja drzewa");
    	m.QueueActualization();
    	
    	
    	

	}
}
