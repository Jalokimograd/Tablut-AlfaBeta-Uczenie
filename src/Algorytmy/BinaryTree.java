package Algorytmy;


import java.awt.EventQueue;
import java.util.LinkedList;
import java.util.Queue;
import java.lang.Math.*;
import java.io.*;

public class BinaryTree implements Serializable {

    private Node root;
    
    private int size;
    
    private LinkedList<Node> actualQueue;	//œcie¿ka aktualnej rozgrywki
    
    
    public LinkedList<Node> getActualQueue() {
    	return(actualQueue);
    }
    public BinaryTree() {
    	actualQueue = new LinkedList<Node>();
    }
    
    public Node getActualState(){
    	if(actualQueue.size()>0)
    		return(actualQueue.getFirst());
    	else return(null);
    }

    public void QueueActualization() {
    	
    	Node actual, prev;
    	boolean o=true;
    	actual = actualQueue.removeFirst();
    	
    	while(actualQueue.size() > 0) {
    		if(o==true) {o=false; actual.actualizationPositiv();}
    		else {o=true; actual.actualizationNegativ();}
    	
    		prev = actualQueue.removeFirst();
    		actual.addParent(prev);
    		prev.addChild(actual);
    		actual = prev;
    	}
    	
    	if(o==true) actual.actualizationPositiv();
    }
    public void add(int[][] state) {
    	addNoRecursive(tableTranslate(state), state);
    }
    
    //Alternatywne wstawianie wêz³ów do drzewa dla poprzedniej wersji rekurencyjnej
    public void addNoRecursive(int[] id, int[][] state) {
    	Node current = root;
    	Node prev = null;
    	if(root==null) {
    		root = new Node(id, state);
    	}
    	else
    		while(true)
    			for(int i=0; i<4; ++i) {
    				if(id[i] > current.id[i]) {
        				prev = current;
        				current = current.right;
        				if(current==null) {
        					current = new Node(id, state); 
        					++size;
            	            current.parent = prev;
            	            prev.right = current;
            	            rebalance(current);
            	            QueueAdd(current);
            	            System.out.println("nowy");
            	            return;
        				}         
        				break;
        			}
    				else if(id[i] < current.id[i]) {
    					prev = current;
        				current = current.left;
        				if(current==null) {
        					current = new Node(id, state); 
        					++size;
            	            current.parent = prev;
            	            prev.left = current;
            	            rebalance(current);
            	            QueueAdd(current);
            	            System.out.println("nowy");
            	            return;
        				}
        				break;
    				}
    				else if(id[i] == current.id[i]) {
    					if(i==3) {
    						QueueAdd(current);
    						System.out.println("plagiat"); 
    						return;
    					}
    					continue;
    				}	
    			}
    }
    
    //______________________________________________________________________WYWA¯ANIE_DRZEWA________________________
    
    private void rebalance(Node n) {
        setBalance(n);
 
        if (n.balance == -2) {
            if (height(n.left.left) >= height(n.left.right))
                n = rotateRight(n);
            else
                n = rotateLeftThenRight(n);
 
        } else if (n.balance == 2) {
            if (height(n.right.right) >= height(n.right.left))
                n = rotateLeft(n);
            else
                n = rotateRightThenLeft(n);
        }
 
        if (n.parent != null) {
            rebalance(n.parent);
        } else {
            root = n;
        }
    }
    
    private void setBalance(Node... nodes) {
        for (Node n : nodes) {
            reheight(n);
            n.balance = height(n.right) - height(n.left);
        }
    }
    
    private void reheight(Node node) {
        if (node != null) {
            node.height = 1 + Math.max(height(node.left), height(node.right));
        }
    }
    
    private int height(Node n) {
        if (n == null)
            return -1;
        return n.height;
    }
    
    private Node rotateLeftThenRight(Node n) {
        n.left = rotateLeft(n.left);
        return rotateRight(n);
    }
 
    private Node rotateRightThenLeft(Node n) {
        n.right = rotateRight(n.right);
        return rotateLeft(n);
    }
    
    private Node rotateLeft(Node a) {
    	 
        Node b = a.right;
        b.parent = a.parent;
 
        a.right = b.left;
 
        if (a.right != null)
            a.right.parent = a;
 
        b.left = a;
        a.parent = b;
 
        if (b.parent != null) {
            if (b.parent.right == a) {
                b.parent.right = b;
            } else {
                b.parent.left = b;
            }
        }
 
        setBalance(a, b);
 
        return b;
    }
 
    private Node rotateRight(Node a) {
 
        Node b = a.left;
        b.parent = a.parent;
 
        a.left = b.right;
 
        if (a.left != null)
            a.left.parent = a;
 
        b.right = a;
        a.parent = b;
 
        if (b.parent != null) {
            if (b.parent.right == a) {
                b.parent.right = b;
            } else {
                b.parent.left = b;
            }
        }
 
        setBalance(a, b);
 
        return b;
    }
    
    //______________________________________________________________________________________________________

    //wywo³ywane zawsze przy próbie dodania Node do drzewa
    private void QueueAdd(Node current) {	
    	actualQueue.addFirst(current);
    }

    public boolean isEmpty() {
        return root == null;
    }

    public int getSize() {
        return size;
    }

    private int getSizeRecursive(Node current) {
        return current == null ? 0 : getSizeRecursive(current.left) + 1 + getSizeRecursive(current.right);
    }
 
    public boolean containsNode(int[] id) {
        return containsNodeRecursive(root, id);
    }

    private boolean containsNodeRecursive(Node current, int[] id) {
        if (current == null) {
            return false;
        }

        for(int i=0; i<4; ++i) {
        	if (id[i] < current.id[i]) {
                return containsNodeRecursive(current.left, id);
            } 
            else if (id[i] > current.id[i]) {
                return containsNodeRecursive(current.right, id);
            }       
            else if (id[i] == current.id[i]) {
            	if(i==3) return true;
            	continue;
            }
        }
        return false;
    }
    
    
    public void delete(int[][] state) {
        root = deleteRecursive(root, tableTranslate(state));
    }

    private Node deleteRecursive(Node current, int[] id) {
        if (current == null) {
            return null;
        }

        for(int i=0; i<4; ++i) {
        	if (id[i] < current.id[i]) {
                current.left = deleteRecursive(current.left, id);
                return current;
            } 
            else if (id[i] > current.id[i]) {
                current.right = deleteRecursive(current.right, id);
                return current;
            }       
            else if (id[i] == current.id[i]) {
            	if(i==3) {
                    if (current.left == null && current.right == null) {
                        return null;
                    }
                    
                    
                    if (current.right == null) {
                        return current.left;
                    }

                    if (current.left == null) {
                        return current.right;
                    }
                    
                    
                    
                    int[] smallestId = findSmallestId(current.right);
                    current.id = smallestId;
                    current.right = deleteRecursive(current.right, smallestId);
                    return current;
            	}
            	continue;
            }
        }
        return null;

    }
    
    private int[] findSmallestId(Node root) {
        return root.left == null ? root.getId() : findSmallestId(root.left);
    }
    
    public void traverseInOrder(Node node) {
        if (node != null) {
            traverseInOrder(node.left);
            System.out.print(" " + node.getId()[0] + ", " + node.getId()[1] + ", " + node.getId()[2] + ", " + node.getId()[3]);
            traverseInOrder(node.right);
        }
    }

    public void traversePreOrder(Node node) {
        if (node != null) {
        	System.out.print(" " + node.getId()[0] + ", " + node.getId()[1] + ", " + node.getId()[2] + ", " + node.getId()[3]);
            traversePreOrder(node.left);
            traversePreOrder(node.right);
        }
    }

    public void traversePostOrder(Node node) {
        if (node != null) {
            traversePostOrder(node.left);
            traversePostOrder(node.right);
            System.out.print(" " + node.getId()[0] + ", " + node.getId()[1] + ", " + node.getId()[2] + ", " + node.getId()[3]);
        }
    }

    public void traverseLevelOrder() {
        if (root == null) {
            return;
        }

        Queue<Node> nodes = new LinkedList<>();
        nodes.add(root);

        while (!nodes.isEmpty()) {

            Node node = nodes.remove();

            System.out.print(" " + node.id[0]);

            if (node.left != null) {
                nodes.add(node.left);
            }

            if (node.left != null) {
                nodes.add(node.right);
            }
        }
    }
    
    private int[] tableTranslate(int[][] statePos) {
    	int[] id = new int[4];
    	int[][] state = new int[9][9];
    	
    	id[0] = id[1] = id[2] = id[3] = -2147483648;
    	
    	int x=4, y=4;
    	
 
    	//zapisujemy po³o¿enie króla
    	for(int i=0; i<9; ++i) {  		
    		for(int j=0; j<9; ++j) {
    			state[i][j] = statePos[i][j];
    			if(state[i][j] == 3) {
    				state[i][j] = 1;
    				y = i;	
    				x = j;
    				break;
    			}
    		}
    	}  
    	
    	//translacja tablicy na identyfikator liczbowy
    	for(int j=1; j<8; ++j) {
    		id[0] = (int) (id[0] + state[0][j] * Math.pow(3, (j-1)));
    	}
    	for(int j=0; j<9; ++j) {
    		id[0] = (int) (id[0] + state[1][j] * Math.pow(3, (j+7)));
    	}
    	for(int j=0; j<4; ++j) {
    		id[0] = (int) (id[0] + state[2][j] * Math.pow(3, (j+16)));
    	}
    	for(int j=4; j<9; ++j) {
    		id[1] = (int) (id[1] + state[2][j] * Math.pow(3, (j-4)));
    	}
    	for(int j=0; j<9; ++j) {
    		id[1] = (int) (id[1] + state[3][j] * Math.pow(3, (j+5)));
    	}
    	for(int j=0; j<4; ++j) {
    		id[1] = (int) (id[1] + state[4][j] * Math.pow(3, (j+14)));
    	}
    	for(int j=5; j<7; ++j) {
    		id[1] = (int) (id[1] + state[4][j] * Math.pow(3, (j+13)));
    	}
    	for(int j=7; j<9; ++j) {
    		id[2] = (int) (id[2] + state[4][j] * Math.pow(3, (j-7)));
    	}
    	for(int j=0; j<9; ++j) {
    		id[2] = (int) (id[2] + state[5][j] * Math.pow(3, (j+2)));
    	}
    	for(int j=0; j<9; ++j) {
    		id[2] = (int) (id[2] + state[6][j] * Math.pow(3, (j+11)));
    	}
    	for(int j=0; j<9; ++j) {
    		id[3] = (int) (id[3] + state[7][j] * Math.pow(3, j));
    	}
    	for(int j=1; j<8; ++j) {
    		id[3] = (int) (id[3] + state[8][j] * Math.pow(3, (j+8)));
    	}
    	
    	//zapisywanie wspó³rzêdnych króla
    	id[3] = (int) (id[3] + (y%3) * Math.pow(3, (16)));
    	id[3] = (int) (id[3] + (y/3) * Math.pow(3, (17)));
    	
    	id[3] = (int) (id[3] + (x%3) * Math.pow(3, (18)));
    	id[3] = (int) (id[3] + (x/3) * Math.pow(3, (19)));
    	
    	return(id);
    }
    /*
    
    main jest do testowania
    Kod jest trochê nieuporz¹dkowany wiêc sprecyzujê jak wygl¹da interfejs tej "czarnej skrzynki"
    metody interfejsu:
    
    public void add(int[][]) - Dodanie elementu do struktury danych przez podanie tablicy 9x9, tej o której pisa³em na grupie
    
    public void QueueActualization() - wywo³ujemy kiedy gra siê zakoñczy³a. Drzewo jest wtedy aktualizowane o nowo wprowadzone stany,
    								   gdy¿ wczeœniej nie wiedzieliœmy jak je traktowaæ. Mogliœmy wygraæ albo przegraæ, ale kiedy 
    								   poinformujemy program ¿e ju¿ siê zakoñczy³o to on sobie podsumuje które prowadz¹ do wygrania, 
    								   a które nie
    
    public Node getActualState() - zwraca w³aœnie dodany element dla którego mo¿emy wywo³ywaæ:
    			public int[] getId() - zwraca id
			    public int getWinNumber() - zwraca liczbê zwyciêstw z tego stanu
			    public int getAllNumber() - zwraca liczbê przejœæ przez ten stan
			    public double getWinProbability() - zwraca odsetek zwyciêstw
    
    inne publiczne metody mo¿na u¿ywaæ ale powsta³y g³ównie do testowania poprawnoœci struktury
    
    
    
    */
}


