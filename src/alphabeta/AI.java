package alphabeta;


import Algorytmy.MainInterface;
import Gra.Point;
import static alphabeta.AI.pawnType.Black;
import static alphabeta.AI.pawnType.King;
import static alphabeta.AI.pawnType.None;
import static alphabeta.AI.pawnType.White;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;

public class AI {
    public enum pawnType {
        Black, White, King, None
    }
    protected int myId;
    protected int oppId;
    protected boolean isHuman;
    MainInterface mInt;
    
    public AI(int Id, boolean hum, MainInterface mInt){
        myId = Id;
        oppId = (myId+1)%2;
        isHuman = hum;
        this.mInt = mInt;
    }
    
    public void actualizeQueue(){
        mInt.QueueActualization();
    }

    //transform Point[][] to pawnType[][]
    pawnType[][] transformPointToPawnType(Point[][] foreignBoard){
        if(foreignBoard == null){
            return null;
        }
        pawnType[][] myBoard = new pawnType[9][9];
        
        for(int i=0; i<9; i++){
            for(int j=0; j<9; j++){
                switch(foreignBoard[i][j].getP()){
                case 0:
                    myBoard[i][j] = None;
                    break;
                case 1:
                    myBoard[i][j] = Black;
                    break;
                case 2:
                    myBoard[i][j] = White;
                    break;
                case 3:
                    myBoard[i][j] = King;
                    break;
                }
            }
        }
        
        return myBoard;
    }
    
    //transform int[][] to pawnType[][]
    pawnType[][] transformIntToPawnType(int[][] foreignBoard){
        if(foreignBoard == null){
            return null;
        }
        pawnType[][] myBoard = new pawnType[9][9];
        
        for(int i=0; i<9; i++){
            for(int j=0; j<9; j++){
                switch(foreignBoard[i][j]){
                case 0:
                    myBoard[i][j] = None;
                    break;
                case 2:
                    myBoard[i][j] = Black;
                    break;
                case 1:
                    myBoard[i][j] = White;
                    break;
                case 3:
                    myBoard[i][j] = King;
                    break;
                }
            }
        }
        
        return myBoard;
    }
    
    //transform pawnType[][] to Point[][]
    Point[][] transformToPoint(pawnType[][] myBoard){
        if(myBoard == null){
            return null;
        }
        Point[][] foreignBoard = new Point[9][9];
        
        for(int i=0; i<9; i++){
            for(int j=0; j<9; j++){
                foreignBoard[i][j] = new Point(i,j);
                switch(myBoard[i][j]){
                case None:
                    foreignBoard[i][j].setP(0);
                    break;
                case Black:
                    foreignBoard[i][j].setP(1);
                    break;
                case White:
                    foreignBoard[i][j].setP(2);
                    break;
                case King:
                    foreignBoard[i][j].setP(3);
                    break;
                }
            }
        }
        
        return foreignBoard;
    }
    
    //transform pawnType[][] to int[][]
    int[][] transformToDB(pawnType[][] myBoard){
        if(myBoard == null){
            return null;
        }
        int[][] foreignBoard = new int[9][9];
        
        for(int i=0; i<9; i++){
            for(int j=0; j<9; j++){
                switch(myBoard[i][j]){
                case None:
                    foreignBoard[i][j] = 0;
                    break;
                case Black:
                    foreignBoard[i][j] = 2;
                    break;
                case White:
                    foreignBoard[i][j] = 1;
                    break;
                case King:
                    foreignBoard[i][j] = 3;
                    break;
                }
            }
        }
        
        return foreignBoard;
    }
    
    public Point[][] makeMove(Point[][] foreignBoard, int fromX, int fromY, int toX, int toY){
        pawnType[][] myBoard = transformPointToPawnType(foreignBoard);     
        
        int[][] nextDatabaseBoard;
        int[][] nextDatabaseBoard2;
        if(!isHuman){
            if(mInt.getBestChild()!=null){
                double dbProb = mInt.getBestChild().getWinProbability();
                Random rand = new Random();
                if(rand.nextDouble()%100>dbProb){
                    int[] move = minmax(1, myId, myBoard, Integer.MIN_VALUE, Integer.MAX_VALUE);
                    System.out.println(move[0] + " : " + move[1] + "," + move[2] + " -> " + move[3] + "," + move[4]);
                    fromX = move[1]; fromY = move[2];
                    toX = move[3]; toY = move[4];
                } else {
                    nextDatabaseBoard = mInt.getBestChild().getBoard();
                    pawnType[][] nextBoard = transformIntToPawnType(nextDatabaseBoard);
                    Point[][] nextForeignBoard = transformToPoint(nextBoard);
                    return nextForeignBoard;
                }
            } else {
                int[] move = minmax(4, myId, myBoard, Integer.MIN_VALUE, Integer.MAX_VALUE);
                System.out.println(move[0] + " : " + move[1] + "," + move[2] + " -> " + move[3] + "," + move[4]);
                fromX = move[1]; fromY = move[2];
                toX = move[3]; toY = move[4];
            }
            
        }
        
        pawnType[][] nextBoard = movePawn(myBoard, myId, fromX, fromY, toX, toY);
        
        Point[][] nextForeignBoard = transformToPoint(nextBoard);
        nextDatabaseBoard = transformToDB(nextBoard);
        System.out.println(mInt.getActualQueueSize());
        if(nextDatabaseBoard != null){
            mInt.add(nextDatabaseBoard);
        }

        
        nextDatabaseBoard2 = mInt.getActualBoard();
        
        
        if(nextDatabaseBoard2!=null)
	        for(int i=0; i<9; ++i) {
	        	for(int j=0; j<9; ++j) {
	        		System.out.print(nextDatabaseBoard2[i][j]);
	        	}
	        	System.out.println();
	        }
        System.out.println("_____________________________________________________________");   
        
        return nextForeignBoard;
    }
    
    //find best move in board states tree
    int[] minmax(int depth, int playerID, pawnType[][] myBoard, int alpha, int beta){
        //coordinates of best move
        int fromXBest, fromYBest, toXBest, toYBest;
        fromXBest=fromYBest=toXBest=toYBest=-1; 
        
        int currentScore;
        
        //reached maximum depth, return weight
        if(depth==0){             
            return new int[]{weigthFunction(myBoard), -1, -1, -1, -1};
        }
        
        //one of the sides won
        if(hasWon(myBoard, (playerID+1)%2)){
            return new int[]{((playerID+1)%2==0 ? Integer.MAX_VALUE : Integer.MIN_VALUE), -1, -1, -1, -1};
        }
        
        
        int curX, curY;
        //interate through board, to move your pawns
        for(int i =0; i<9; i++){
            for(int j = 0; j<9; j++){
                //check if there is a pawn on tile
                if((playerID == 0 && (myBoard[i][j] == White || myBoard[i][j] == King)) || (playerID == 1 && myBoard[i][j] == Black)){
                    curX = i-1;
                    curY = j;
                    //for all available moves towards top
                    while(curX >= 0){
                        //check if further path is blocked
                        if(!isPathClear(myBoard, i, j, curX, curY)){
                            break;
                        }
                        //get board after currently checked move
                        pawnType[][] nextBoard = movePawn(myBoard, playerID, i, j, curX, curY);
                        //check if the move is valid
                        if(nextBoard != null){
                            //go deeper with current values and changed player
                            currentScore = minmax(depth-1, (playerID+1)%2, nextBoard, alpha, beta)[0];
                            
                            //check if new best move is found
                            if((playerID == 0 && currentScore > alpha) || (playerID == 1 && currentScore < beta)){
                                if(playerID == 0){
                                    alpha = currentScore;
                                } else {
                                    beta = currentScore;
                                }
                                fromXBest = i;
                                fromYBest = j;
                                toXBest = curX;
                                toYBest = curY;
                            } 
                            //if the new move has the same value, randomly assign the new value or not
                            else if((playerID == 0 && currentScore == alpha) || (playerID == 1 && currentScore == beta)){
                                Random rand = new Random();
                                if(rand.nextBoolean()){
                                    if(playerID == 0){
                                        alpha = currentScore;
                                    } else {
                                        beta = currentScore;
                                    }
                                    fromXBest = i;
                                    fromYBest = j;
                                    toXBest = curX;
                                    toYBest = curY;
                                }
                            }
                        }
                        //cut-off
                        if(alpha >= beta){
                            return new int[]{(playerID == 0) ? alpha : beta, fromXBest, fromYBest, toXBest, toYBest};
                        }
                        curX--;
                    }
                    
                    curX = i+1;
                    curY = j;
                    //for all available moves towards bottom
                    while(curX <= 8){
                        //check if further path is blocked
                        if(!isPathClear(myBoard, i, j, curX, curY)){
                            break;
                        }
                        //get board after currently checked move
                        pawnType[][] nextBoard = movePawn(myBoard, playerID, i, j, curX, curY);
                        //check if the move is valid
                        if(nextBoard != null){
                            //go deeper with current values and changed player
                            currentScore = minmax(depth-1, (playerID+1)%2, nextBoard, alpha, beta)[0];
                            
                            //check if new best move is found
                            if((playerID == 0 && currentScore > alpha) || (playerID == 1 && currentScore < beta)){
                                if(playerID == 0){
                                    alpha = currentScore;
                                } else {
                                    beta = currentScore;
                                }
                                fromXBest = i;
                                fromYBest = j;
                                toXBest = curX;
                                toYBest = curY;
                            } 
                            //if the new move has the same value, randomly assign the new value or not
                            else if((playerID == 0 && currentScore == alpha) || (playerID == 1 && currentScore == beta)){
                                Random rand = new Random();
                                if(rand.nextBoolean()){
                                    if(playerID == 0){
                                        alpha = currentScore;
                                    } else {
                                        beta = currentScore;
                                    }
                                    fromXBest = i;
                                    fromYBest = j;
                                    toXBest = curX;
                                    toYBest = curY;
                                }
                            }
                        }
                        //cut-off
                        if(alpha >= beta){
                            return new int[]{(playerID == 0) ? alpha : beta, fromXBest, fromYBest, toXBest, toYBest};
                            
                        }
                        
                        curX++;
                    }

                    curX = i;
                    curY = j-1;
                    //for all available moves towards left
                    while(curY >= 0){
                        //check if further path is blocked
                        if(!isPathClear(myBoard, i, j, curX, curY)){
                            break;
                        }
                        //get board after currently checked move
                        pawnType[][] nextBoard = movePawn(myBoard, playerID, i, j, curX, curY);
                        //check if the move is valid
                        if(nextBoard != null){
                            //go deeper with current values and changed player
                            currentScore = minmax(depth-1, (playerID+1)%2, nextBoard, alpha, beta)[0];
                            
                            //check if new best move is found
                            if((playerID == 0 && currentScore > alpha) || (playerID == 1 && currentScore < beta)){
                                if(playerID == 0){
                                    alpha = currentScore;
                                } else {
                                    beta = currentScore;
                                }
                                fromXBest = i;
                                fromYBest = j;
                                toXBest = curX;
                                toYBest = curY;
                            } 
                            //if the new move has the same value, randomly assign the new value or not
                            else if((playerID == 0 && currentScore == alpha) || (playerID == 1 && currentScore == beta)){
                                Random rand = new Random();
                                if(rand.nextBoolean()){
                                    if(playerID == 0){
                                        alpha = currentScore;
                                    } else {
                                        beta = currentScore;
                                    }
                                    fromXBest = i;
                                    fromYBest = j;
                                    toXBest = curX;
                                    toYBest = curY;
                                }
                            }
                        }
                        //cut-off
                        if(alpha >= beta){
                            return new int[]{(playerID == 0) ? alpha : beta, fromXBest, fromYBest, toXBest, toYBest};
                        }

                        curY--;
                    }

                    curX = i;
                    curY = j+1;
                    //for all available moves towards right
                    while(curY <= 8){
                        //check if further path is blocked
                        if(!isPathClear(myBoard, i, j, curX, curY)){
                            break;
                        }
                        //get board after currently checked move
                        pawnType[][] nextBoard = movePawn(myBoard, playerID, i, j, curX, curY);
                        //check if the move is valid
                        if(nextBoard != null){
                            //go deeper with current values and changed player
                            currentScore = minmax(depth-1, (playerID+1)%2, nextBoard, alpha, beta)[0];
                            
                            
                            //check if new best move is found
                            if((playerID == 0 && currentScore > alpha) || (playerID == 1 && currentScore < beta)){
                                if(playerID == 0){
                                    alpha = currentScore;
                                } else {
                                    beta = currentScore;
                                }
                                fromXBest = i;
                                fromYBest = j;
                                toXBest = curX;
                                toYBest = curY;
                            } 
                            //if the new move has the same value, randomly assign the new value or not
                            else if((playerID == 0 && currentScore == alpha) || (playerID == 1 && currentScore == beta)){
                                Random rand = new Random();
                                if(rand.nextBoolean()){
                                    if(playerID == 0){
                                        alpha = currentScore;
                                    } else {
                                        beta = currentScore;
                                    }
                                    fromXBest = i;
                                    fromYBest = j;
                                    toXBest = curX;
                                    toYBest = curY;
                                }
                            }
                        }
                        //cut-off
                        if(alpha >= beta){
                            return new int[]{(playerID == 0) ? alpha : beta, fromXBest, fromYBest, toXBest, toYBest};
                        }

                        curY++;
                    }
                }
            }
        }
        return new int[]{(playerID == 0) ? alpha : beta, fromXBest, fromYBest, toXBest, toYBest};
    }
    
    //check if path is clear between source tile and destination tile
    Boolean isPathClear(pawnType[][] myBoard, int fromX, int fromY, int toX, int toY){
        if(fromY == toY){
            if(fromX < toX){
                for(int i = fromX+1; i <= toX; i++){
                    if(myBoard[i][fromY] != None){
                        return false;
                    }
                }
            } else {
                for(int i = fromX-1; i >= toX; i--){
                    if(myBoard[i][fromY] != None){
                        return false;
                    }
                }
            }
        } else {
            if(fromY < toY){
                for(int i = fromY+1; i <= toY; i++){
                    if(myBoard[fromX][i] != None){
                        return false;
                    }
                }
            } else {
                for(int i = fromY-1; i >= toY; i--){
                    if(myBoard[fromX][i] != None){
                        return false;
                    }
                }
            }
        }
        return true;
    }

    //return boards state after pawn is moved from (fromX,fromY) to (toX,toY)
    pawnType[][] movePawn(pawnType[][] prevBoard, int id, int fromX, int fromY, int toX, int toY){
        //can the player move pawn from source tile?
        if((id == 0 && !(prevBoard[fromX][fromY] == White || prevBoard[fromX][fromY] == King)) ||
           (id == 1 && prevBoard[fromX][fromY] != Black)) {
            return null;
        }
        //not a move
        if(fromX == toX && fromY == toY){
            return null;
        }
        //illegal move (diagonal)
        if(fromX != toX && fromY != toY){
            return null;
        }        
        //check if castle is destination
        if(toX == 4 && toY == 4){
            return null;
        }
        //check if a pawn is the king and if not is a corner the destination
        if((prevBoard[fromX][fromY] != King) && (toX==0||toX==8) && (toY==0||toY==8)){
            return null;
        }
        //destination tile occupied
        if(prevBoard[toX][toY] != None){
            return null;
        }
        
        //create new board
        pawnType[][] myBoard = new pawnType[9][9];
        for(int i=0;i<9;i++){
            for(int j=0;j<9;j++){
                myBoard[i][j]=prevBoard[i][j];
            }
        }
        
        //make the move
        myBoard[toX][toY] = myBoard[fromX][fromY];
        myBoard[fromX][fromY] = None;
        
        boolean isBlack = (myBoard[toX][toY] == Black); 
        boolean isWhite = (myBoard[toX][toY] == White); 
        
        //check if any pawns are taken by the move
        //check above
        if(isValid(toX-2, toY) && (((myBoard[toX-2][toY] == White || myBoard[toX-2][toY] == King) && !isBlack) || (myBoard[toX-2][toY] == Black && isBlack))) {
            if((!isBlack && myBoard[toX-1][toY] == Black)||(isBlack && (myBoard[toX-1][toY] == White || myBoard[toX-1][toY] == King))) {
            	myBoard[toX-1][toY] = None;
            }
        }
        //check left
        if(isValid(toX, toY-2) && (((myBoard[toX][toY-2] == White || myBoard[toX][toY-2] == King) && !isBlack) || (myBoard[toX][toY-2] == Black && isBlack))) {
            if((!isBlack && myBoard[toX][toY-1] == Black)||(isBlack && (myBoard[toX][toY-1] == White || myBoard[toX][toY-1] == King))) {
            	myBoard[toX][toY-1] = None;
            }
        }
        //check below
        if(isValid(toX+2, toY) && (((myBoard[toX+2][toY] == White || myBoard[toX+2][toY] == King) && !isBlack) || (myBoard[toX+2][toY] == Black && isBlack))) {
            if((!isBlack && myBoard[toX+1][toY] == Black)||(isBlack && (myBoard[toX+1][toY] == White || myBoard[toX+1][toY] == King))) {
            	myBoard[toX+1][toY] = None;
            }
        }
        //check right
        if(isValid(toX, toY+2) && (((myBoard[toX][toY+2] == White || myBoard[toX][toY+2] == King) && !isBlack) || (myBoard[toX][toY+2] == Black && isBlack))) {
            if((!isBlack && myBoard[toX][toY+1] == Black)||(isBlack && (myBoard[toX][toY+1] == White || myBoard[toX][toY+1] == King))) {
            	myBoard[toX][toY+1] = None;
            }
        }
        //by the corners
        //upper left, upper bound
        if(toX == 0 && toY == 2){
            if(isBlack && myBoard[toX][toY-1]==White){
                myBoard[toX][toY-1] = None;
            }
            else if(isWhite && myBoard[toX][toY-1]==Black){
                myBoard[toX][toY-1] = None;
            }
        } 
        //upper left, left bound
        if(toX == 2 && toY == 0){
            if(isBlack && myBoard[toX-1][toY]==White){
                myBoard[toX-1][toY] = None;
            }
            else if(isWhite && myBoard[toX-1][toY]==Black){
                myBoard[toX-1][toY] = None;
            }
        } 
        //upper right, upper bound
        else if(toX == 0 && toY == 6){
            if(isBlack && myBoard[toX][toY+1]==White){
                myBoard[toX][toY+1] = None;
            }
            else if(isWhite && myBoard[toX][toY+1]==Black){
                myBoard[toX][toY+1] = None;
            }
        }
        //upper right, right bound
        else if(toX == 2 && toY == 8){
            if(isBlack && myBoard[toX-1][toY]==White){
                myBoard[toX-1][toY] = None;
            }
            else if(isWhite && myBoard[toX-1][toY]==Black){
                myBoard[toX-1][toY] = None;
            }
        }
        //lower left, lower bound
        if(toX == 8 && toY == 2){
            if(isBlack && myBoard[toX][toY-1]==White){
                myBoard[toX][toY-1] = None;
            }
            else if(isWhite && myBoard[toX][toY-1]==Black){
                myBoard[toX][toY-1] = None;
            }
        } 
        //lower left, left bound
        if(toX == 6 && toY == 0){
            if(isBlack && myBoard[toX+1][toY]==White){
                myBoard[toX+1][toY] = None;
            }
            else if(isWhite && myBoard[toX+1][toY]==Black){
                myBoard[toX+1][toY] = None;
            }
        } 
        //lower right, right bound
        else if(toX == 6 && toY == 8){
            if(isBlack && myBoard[toX+1][toY]==White){
                myBoard[toX+1][toY] = None;
            }
            else if(isWhite && myBoard[toX+1][toY]==Black){
                myBoard[toX+1][toY] = None;
            }
        }
        //lower right, lower bound
        else if(toX == 8 && toY == 6){
            if(isBlack && myBoard[toX][toY+1]==White){
                myBoard[toX][toY+1] = None;
            }
            else if(isWhite && myBoard[toX][toY+1]==Black){
                myBoard[toX][toY+1] = None;
            }
        }
        
        //by the castle
        //up
        if(toX==2 && toY==4){
            if(isBlack && myBoard[toX+1][toY]==White){
                myBoard[toX+1][toY] = None;
            }
            else if(isWhite && myBoard[toX+1][toY]==Black){
                myBoard[toX+1][toY] = None;
            }
        }
        //right
        if(toX==4 && toY==6){
            if(isBlack && myBoard[toX][toY-1]==White){
                myBoard[toX][toY-1] = None;
            }
            else if(isWhite && myBoard[toX][toY-1]==Black){
                myBoard[toX][toY-1] = None;
            }
        }
        //down
        if(toX==6 && toY==4){
            if(isBlack && myBoard[toX-1][toY]==White){
                myBoard[toX-1][toY] = None;
            }
            else if(isWhite && myBoard[toX-1][toY]==Black){
                myBoard[toX-1][toY] = None;
            }
        }
        //left
        if(toX==4 && toY==2){
            if(isBlack && myBoard[toX][toY+1]==White){
                myBoard[toX][toY+1] = None;
            }
            else if(isWhite && myBoard[toX][toY+1]==Black){
                myBoard[toX][toY+1] = None;
            }
        }
        
        return myBoard;
    }

    
    //return array of 2 ints representing king's position on current board
    int[] kingsPosition(pawnType[][] board){
        
        for(int i=0;i<9;i++){
            for(int j=0;j<9;j++){
                if(board[i][j]==King){
                    return new int[]{i,j};
                }
            }
        }
        return new int[]{-1,-1};
    }
    
    //check if player with given ID has won
    private boolean hasWon(pawnType[][] board, int Id) {
        int[] K = kingsPosition(board);
        int Kx = K[0];
        int Ky = K[1];
        
        if(!isValid(Ky, Ky) && Id==1){
            return true;
        } else if(!isValid(Ky, Ky) && Id==0){
            return false;
        }
        //white - check if the king is in the corner
        if(Id==0){
            return board[0][0] == King || board[0][8] == King || board[8][0] == King || board[8][8] == King;
        } 
        //black - check if king is surrounded
        else {
            //king is on border
            if(Kx==0||Kx==8){
                //by the ending tile
                if(Ky==1&&board[Kx][Ky+1]==Black){
                    return true;
                } else if(Ky==7&&board[Kx][Ky-1]==Black){
                    return true;
                } 
                //other tiles
                if(board[Kx][Ky-1] == Black && board[Kx][Ky+1] == Black){
                    return true;
                }
            } else if(Ky==0||Ky==8){
                //by the ending tile
                if(Kx==1&&board[Kx+1][Ky]==Black){
                    return true;
                } else if(Kx==7&&board[Kx-1][Ky]==Black){
                    return true;
                }
                //other tiles
                if(board[Kx-1][Ky] == Black && board[Kx+1][Ky] == Black){
                    return true;
                }
            }
            //King in the castle
            else if(Kx==4&&Ky==4){
                //surrounded from all sides
                if(board[Kx-1][Ky] == Black && board[Kx+1][Ky] == Black && board[Kx][Ky-1] == Black && board[Kx][Ky+1] == Black){
                    return true;
                } 
            } 
            //King by the castle -- surrounded from three sides
            else if(Kx==4 &&Ky==3){
                if(board[Kx-1][Ky] == Black && board[Kx+1][Ky] == Black && board[Kx][Ky-1] == Black){
                    return true;
                }
            } else if(Kx==4 &&Ky==5){
                if(board[Kx-1][Ky] == Black && board[Kx+1][Ky] == Black && board[Kx][Ky+1] == Black){
                    return true;
                }
            } else if(Kx==3 &&Ky==4){
                if(board[Kx-1][Ky] == Black && board[Kx][Ky-1] == Black && board[Kx][Ky+1] == Black){
                    return true;
                }
            } else if(Kx==5 &&Ky==4){
                if(board[Kx+1][Ky] == Black && board[Kx][Ky-1] == Black && board[Kx][Ky+1] == Black){
                    return true;
                }
            }
            //other cases
            else{
                if((board[Kx+1][Ky] == Black && board[Kx-1][Ky] == Black) || (board[Kx][Ky+1] == Black && board[Kx][Ky-1] == Black)){
                    return true;
                }
            }
            return false;
        }
        
    }
    
    
    //evaluate current board
    int weigthFunction(pawnType[][] myBoard){
        //win/lose        
        if(hasWon(myBoard, 0)){
            return Integer.MAX_VALUE;
        }
        if(hasWon(myBoard, 1)){
            return Integer.MIN_VALUE;
        }
        
        int value = 0;
        
        //how many jumps the King is from the corner
        int jumpValue = -10;
        value += countJumps(myBoard, jumpValue);
//        System.out.println("jumps: " + countJumps(myBoard, jumpValue));
        
        //pawn amount
        int pawnValue = 40;
        value += countPawns(myBoard, pawnValue);
//        System.out.println("pawns: " + countPawns(myBoard, pawnValue));
        
        //how many potential takes
        int takeValue = 10;
        value += countTakes(myBoard, takeValue);
//        System.out.println("takes: " + countTakes(myBoard, takeValue));
        
        //how many pawns are surrounded from how many directions
        int surroundValue = 2;
        value += countSurrounds(myBoard, surroundValue);
//        System.out.println("surrounds: " + countSurrounds(myBoard, surroundValue));
        
        return value;
    }
    
    //count how many jumps is the king from the nearest corner
    int countJumps(pawnType[][] myBoard, int jumpValue){
        int[] K = kingsPosition(myBoard);
        int Kx = K[0];
        int Ky = K[1];
        
        
        //create visited map, if !None then true else false
        boolean[][] visited = new boolean[9][9];
        for(int i =0; i<9; i++){
            for(int j = 0; j<9; j++){
                visited[i][j] = (myBoard[i][j] != None);
            }
        }
        visited[Kx][Ky] = true; 
        int rowNum[] = {-1, 0, 0, 1}; 
        int colNum[] = {0, -1, 1, 0}; 
        
        Queue<int[]> q = new LinkedList<>();

        int[] s = {Kx, Ky, 0};
        q.add(s);
        
        while(!q.isEmpty()){
            int[] cur = q.poll();
            //if reached corner
            if((cur[0] == 0 || cur[0] == 8) && (cur[1] == 0 || cur[1] == 8)){
                return (cur[2] > 5) ? 5*jumpValue : cur[2]*jumpValue;
            }
            for(int i=0; i<4; i++){
                int row = cur[0] + rowNum[i];
                int col = cur[1] + colNum[i];
                while(isValid(row,col) && myBoard[row][col] == None){
                    if(isValid(row,col) && !visited[row][col]){
                        visited[row][col] = true;
                        int[] tmp = {row, col, cur[2]+1};
                        q.add(tmp);
                    }
                    row+=rowNum[i];
                    col+=colNum[i];
                }
            }
        }
        
        return 5*jumpValue;
    }
    
    
    //is the tile within the board
    boolean isValid(int row, int col){
        return (row >= 0) && (row < 9) && 
                (col >= 0) && (col < 9); 
    }
    
    
    //count each player's amount of pawns
    int countPawns(pawnType[][] board, int pawnValue){
        int value = pawnValue;
        
        for(int i=0; i<9; i++){
            for(int j = 0; j< 8; j++){
                if(board[i][j] == Black){
                    value-=pawnValue;
                } else if(board[i][j] == White){
                    value+=pawnValue;
                }
            }
        }
        
        return value;
    }

   
    //check how many potential takes
    int countTakes(pawnType[][] board, int takeValue){
        int value = 0;
        for(int i=0; i<9; i++){
            for(int j = 0; j< 9; j++){
                if(board[i][j] == Black){
                    //check horizontal taking
                    //check if pawn is on border tile
                    if(i>0){
                        if(i<8){
                            //if not check if there is a possibility to be taken
                            if(board[i-1][j] == White){
                                //opposite pawn above
                                if(board[i+1][j] == None){
                                    //pawn can take from the other side
                                    //left
                                    int temp = j - 1;
                                    while(temp>=0){
                                        if(board[i+1][temp] == Black){
                                            break;
                                        } else if(board[i+1][temp] == White || board[i+1][temp] == King){
                                            value+=takeValue;
                                            break;
                                        }
                                        temp--;
                                    }
                                    //right
                                    temp = j + 1;
                                    while(temp<=8){
                                        if(board[i+1][temp] == Black){
                                            break;
                                        } else if(board[i+1][temp] == White || board[i+1][temp] == King){
                                            value+=takeValue;
                                            break;
                                        }
                                        temp++;
                                    }
                                    //down
                                    temp = i+2;
                                    while(temp<=8){
                                        if(board[temp][j] == Black){
                                            break;
                                        } else if(board[temp][j] == White || board[temp][j] == King){
                                            value+=takeValue;
                                            break;
                                        }
                                        temp++;
                                    }
                                }
                            } else if(board[i+1][j] == White){
                                //opposite below
                                if(board[i-1][j] == None){
                                    //pawn can take from the other side
                                    //left
                                    int temp = j - 1;
                                    while(temp>=0){
                                        if(board[i-1][temp] == Black){
                                            break;
                                        } else if(board[i-1][temp] == White || board[i-1][temp] == King){
                                            value+=takeValue;
                                            break;
                                        }
                                        temp--;
                                    }
                                    //right
                                    temp = j + 1;
                                    while(temp<=8){
                                        if(board[i-1][temp] == Black){
                                            break;
                                        } else if(board[i-1][temp] == White || board[i-1][temp] == King){
                                            value+=takeValue;
                                            break;
                                        }
                                        temp++;
                                    }
                                    //up
                                    temp = i-2;
                                    while(temp>=0){
                                        if(board[temp][j] == Black){
                                            break;
                                        } else if(board[temp][j] == White || board[temp][j] == King){
                                            value+=takeValue;
                                            break;
                                        }
                                        temp--;
                                    }
                                }
                            }
                        }
                    }
                    
                    //check vertical taking
                    //check if pawn is on border tile
                    if(j>0){
                        if(j<8){
                            //if not check if there is a possibility to be taken
                            if(board[i][j-1] == White){
                                //opposite pawn on the left
                                if(board[i][j+1] == None){
                                    //pawn can take from the other side
                                    //up
                                    int temp = i-1;
                                    while(temp>=0){
                                        if(board[temp][j+1] == Black){
                                            break;
                                        } else if(board[temp][j+1] == White || board[temp][j+1] == King){
                                            value+=takeValue;
                                            break;
                                        }
                                        temp--;
                                    }
                                    //right
                                    temp = j + 2;
                                    while(temp<=8){
                                        if(board[i][temp] == Black){
                                            break;
                                        } else if(board[i][temp] == White || board[i][temp] == King){
                                            value+=takeValue;
                                            break;
                                        }
                                        temp++;
                                    }
                                    //down
                                    temp = i+1;
                                    while(temp<=8){
                                        if(board[temp][j+1] == Black){
                                            break;
                                        } else if(board[temp][j+1] == White || board[temp][j+1] == King){
                                            value+=takeValue;
                                            break;
                                        }
                                        temp++;
                                    }
                                }
                            } else if(board[i][j+1] == White){
                                //opposite pawn on the right
                                if(board[i][j-1] == None){
                                    //pawn can take from the other side
                                    //up
                                    int temp = i-1;
                                    while(temp>=0){
                                        if(board[temp][j-1] == Black){
                                            break;
                                        } else if(board[temp][j-1] == White || board[temp][j-1] == King){
                                            value+=takeValue;
                                            break;
                                        }
                                        temp--;
                                    }
                                    //left
                                    temp = j - 2;
                                    while(temp>=0){
                                        if(board[i][temp] == Black){
                                            break;
                                        } else if(board[i][temp] == White || board[i][temp] == King){
                                            value+=takeValue;
                                            break;
                                        }
                                        temp--;
                                    }
                                    //down
                                    temp = i+1;
                                    while(temp<=8){
                                        if(board[temp][j-1] == Black){
                                            break;
                                        } else if(board[temp][j-1] == White || board[temp][j-1] == King){
                                            value+=takeValue;
                                            break;
                                        }
                                        temp++;
                                    }
                                }
                            }
                        }
                    }
                } else if(board[i][j] == White){
                    
                    //check horizontal taking
                    //check if pawn is on border tile
                    if(i>0){
                        if(i<8){
                            //if not check if there is a possibility to be taken
                            if(board[i-1][j] == Black){
                                //opposite pawn above
                                if(board[i+1][j] == None){
                                    //pawn can take from the other side
                                    //left
                                    int temp = j - 1;
                                    while(temp>=0){
                                        if(board[i+1][temp] == White || board[i+1][temp] == King){
                                            break;
                                        } else if(board[i+1][temp] == Black ){
                                            value-=takeValue;
                                            break;
                                        }
                                        temp--;
                                    }
                                    //right
                                    temp = j + 1;
                                    while(temp<=8){
                                        if(board[i+1][temp] == White || board[i+1][temp]  == King){
                                            break;
                                        } else if(board[i+1][temp] == Black){
                                            value-=takeValue;
                                            break;
                                        }
                                        temp++;
                                    }
                                    //down
                                    temp = i+2;
                                    while(temp<=8){
                                        if(board[temp][j] == White || board[temp][j] == King){
                                            break;
                                        } else if(board[temp][j] == Black){
                                            value-=takeValue;
                                            break;
                                        }
                                        temp++;
                                    }
                                }
                            } else if(board[i+1][j] == Black){
                                //opposite below
                                if(board[i-1][j] == None){
                                    //pawn can take from the other side
                                    //left
                                    int temp = j - 1;
                                    while(temp>=0){
                                        if(board[i-1][temp] == White || board[i-1][temp] == King){
                                            break;
                                        } else if(board[i-1][temp] == Black){
                                            value-=takeValue;
                                            break;
                                        }
                                        temp--;
                                    }
                                    //right
                                    temp = j + 1;
                                    while(temp<=8){
                                        if(board[i-1][temp] == White || board[i-1][temp] == King){
                                            break;
                                        } else if(board[i-1][temp] == Black){
                                            value-=takeValue;
                                            break;
                                        }
                                        temp++;
                                    }
                                    //up
                                    temp = i-2;
                                    while(temp>=0){
                                        if(board[temp][j] == White || board[temp][j] == King){
                                            break;
                                        } else if(board[temp][j] == Black){
                                            value-=takeValue;
                                            break;
                                        }
                                        temp--;
                                    }
                                }
                            }
                        }
                    }
                    
                    //check vertical taking
                    //check if pawn is on border tile
                    if(j>0){
                        if(j<8){
                            //if not check if there is a possibility to be taken
                            if(board[i][j-1] == Black){
                                //opposite pawn on the left
                                if(board[i][j+1] == None){
                                    //pawn can take from the other side
                                    //up
                                    int temp = i-1;
                                    while(temp>=0){
                                        if(board[temp][j+1] == White || board[temp][j+1] == King){
                                            break;
                                        } else if(board[temp][j+1] == Black){
                                            value-=takeValue;
                                            break;
                                        }
                                        temp--;
                                    }
                                    //right
                                    temp = j + 2;
                                    while(temp<=8){
                                        if(board[i][temp] == White || board[i][temp] == King){
                                            break;
                                        } else if(board[i][temp] == Black){
                                            value-=takeValue;
                                            break;
                                        }
                                        temp++;
                                    }
                                    //down
                                    temp = i+1;
                                    while(temp<=8){
                                        if(board[temp][j+1] == White || board[temp][j+1] == King){
                                            break;
                                        } else if(board[temp][j+1] == Black){
                                            value-=takeValue;
                                            break;
                                        }
                                        temp++;
                                    }
                                }
                            } else if(board[i][j+1] == Black){
                                //opposite pawn on the right
                                if(board[i][j-1] == None){
                                    //pawn can take from the other side
                                    //up
                                    int temp = i-1;
                                    while(temp>=0){
                                        if(board[temp][j-1] == White || board[temp][j-1] == King){
                                            break;
                                        } else if(board[temp][j-1] == Black){
                                            value-=takeValue;
                                            break;
                                        }
                                        temp--;
                                    }
                                    //left
                                    temp = j - 2;
                                    while(temp>=0){
                                        if(board[i][temp] == White || board[i][temp] == King){
                                            break;
                                        } else if(board[i][temp] == Black){
                                            value-=takeValue;
                                            break;
                                        }
                                        temp--;
                                    }
                                    //down
                                    temp = i+1;
                                    while(temp<=8){
                                        if(board[temp][j-1] == White || board[temp][j-1] == King){
                                            break;
                                        } else if(board[temp][j-1] == Black){
                                            value-=takeValue;
                                            break;
                                        }
                                        temp++;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return value;
    }
    
    
    //from how many sides is the pwan surrounded by opponent
    int surroundedBy(pawnType[][] board, int i, int j){
        int count = 0;
        if(board[i][j] == None){
            return count;
        }
        Boolean isBlack = board[i][j] == Black;
        
        if(isBlack){
            //border tiles
            if(i==0 || i == 8){
                //by an ending tile
                if(j==1 || j==7){
                    count++;
                } else {
                    if(board[i][j-1] == White || board[i][j-1] == King || board[i][j+1] == White || board[i][j+1] == King){
                        count++;
                    }
                }
                if(i==0){
                    if(board[i+1][j] == White || board[i+1][j] == King){
                        count++;
                    }
                } else if(i==8){
                    if(board[i-1][j] == White || board[i-1][j] == King){
                        count++;
                    }
                }
            }
            else if(j==0 || j == 8){
                //by an ending tile
                if(i==1 || i==7){
                    count++;
                } else {
                    if(board[i-1][j] == White || board[i-1][j] == King || board[i+1][j] == White || board[i+1][j] == King){
                        count++;
                    }
                }
                if(j==0){
                    if(board[i][j+1] == White || board[i][j+1] == King){
                        count++;
                    }
                } else if(j==8){
                    if(board[i][j-1] == White || board[i][j-1] == King){
                        count++;
                    }
                }
            }
            //by the castle
            else if(i == 4 && j == 3){
                count++;
                if(board[i][j-1] == White || board[i][j-1] == King){
                    count++;
                }
                if(board[i-1][j] == White || board[i-1][j] == King){
                    count++;
                }
                if(board[i+1][j] == White || board[i+1][j] == King){
                    count++;
                }
            } else if(i == 4 && j == 5){
                count++;
                if(board[i][j+1] == White || board[i][j+1] == King){
                    count++;
                }
                if(board[i-1][j] == White || board[i-1][j] == King){
                    count++;
                }
                if(board[i+1][j] == White || board[i+1][j] == King){
                    count++;
                }  
            } else if(i == 3 && j == 4){
                count++;
                if(board[i-1][j] == White || board[i-1][j] == King){
                    count++;
                }
                if(board[i][j+1] == White || board[i][j+1] == King){
                    count++;
                }
                if(board[i][j-1] == White || board[i][j-1] == King){
                    count++;
                }  
            } else if(i == 5 && j == 4){
                count++;
                if(board[i+1][j] == White || board[i+1][j] == King){
                    count++;
                }
                if(board[i][j+1] == White || board[i][j+1] == King){
                    count++;
                }
                if(board[i][j-1] == White || board[i][j-1] == King){
                    count++;
                }  
            }
            //other tiles
            else{
                if(board[i][j-1] == White || board[i][j-1] == King){
                    count++;
                }
                if(board[i][j+1] == White || board[i][j+1] == King){
                    count++;
                }
                if(board[i-1][j] == White || board[i-1][j] == King){
                    count++;
                }
                if(board[i+1][j] == White || board[i+1][j] == King){
                    count++;
                }
            }
        } else if(!isBlack){
            //border tiles
            if(i==0 || i == 8){
                //by an ending tile
                if(j==1 || j==7){
                    count++;
                } else {
                    if(board[i][j-1] == Black || board[i][j+1] == Black){
                        count++;
                    }
                }
                //??? we count all surrounding
                if(i==0){
                    if(board[i+1][j] == Black){
                        count++;
                    }
                } else if(i==8){
                    if(board[i-1][j] == Black){
                        count++;
                    }
                }
            }
            else if(j==0 || j == 8){
                //by an ending tile
                if(i==1 || i==7){
                    count++;
                } else {
                    if(board[i-1][j] == Black || board[i+1][j] == Black){
                        count++;
                    }
                }
                //??? we count all surrounding
                if(j==0){
                    if(board[i][j+1] == Black){
                        count++;
                    }
                } else if(j==8){
                    if(board[i][j-1] == Black){
                        count++;
                    }
                }
            }
            //by the castle
            else if(i == 4 && j == 3){
                count++;
                if(board[i][j-1] == Black){
                    count++;
                }
                if(board[i-1][j] == Black){
                    count++;
                }
                if(board[i+1][j] == Black){
                    count++;
                }
            } else if(i == 4 && j == 5){
                count++;
                if(board[i][j+1] == Black){
                    count++;
                }
                if(board[i-1][j] == Black){
                    count++;
                }
                if(board[i+1][j] == Black){
                    count++;
                }  
            } else if(i == 3 && j == 4){
                count++;
                if(board[i-1][j] == Black){
                    count++;
                }
                if(board[i][j+1] == Black){
                    count++;
                }
                if(board[i][j-1] == Black){
                    count++;
                }  
            } else if(i == 5 && j == 4){
                count++;
                if(board[i+1][j] == Black){
                    count++;
                }
                if(board[i][j+1] == Black){
                    count++;
                }
                if(board[i][j-1] == Black){
                    count++;
                }  
            }
            //other tiles
            else{
                if(board[i][j-1] == Black){
                    count++;
                }
                if(board[i][j+1] == Black){
                    count++;
                }
                if(board[i-1][j] == Black){
                    count++;
                }
                if(board[i+1][j] == Black){
                    count++;
                }
            }
        }
        
        return count;
    }
    
    
    //check how many pawns are surrounded from how many directions
    int countSurrounds(pawnType[][] board, int surroundValue){
        int value = 0;
        
        for(int i=0; i<9; i++){
            for(int j = 0; j< 8; j++){                
                if(board[i][j] == Black){
                    value += surroundedBy(board,i,j)*surroundValue;
                } else if(board[i][j] == White || board[i][j] == King){
                    value -= surroundedBy(board,i,j)*surroundValue;
                }
            }
        }
        
        
        return value;
    }
  
    public boolean isAI(){
        return !isHuman;
    }
    
    //print board, function for debugging 
    void printBoard(pawnType[][] myBoard){
        for(int i =0; i<9; i++){
            for(int j = 0; j<9; j++){
                switch (myBoard[i][j]) {
                    case None:
                        System.out.print("- ");
                        break;
                    case Black:
                        System.out.print("B ");
                        break;
                    case White:
                        System.out.print("W ");
                        break;
                    case King:
                        System.out.print("K ");
                        break;
                    default:
                        break;
                }
            }
            System.out.println();
        }
    }
}
