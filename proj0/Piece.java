public class Piece{
	private boolean isFire;
	private String type;
	private boolean hasCaptured;
	private int piecex;
	private int piecey;
	private boolean isKing;
	private Board b;
	public Piece(boolean isFire, Board b, int x, int y, String type){
		this.isFire = isFire;
		this.type = type;
		this.piecex = x;
		this.piecey = y;
		this.b = b;
	}
	public boolean isFire(){
		return isFire;
	}
	public int side(){
		if (isFire){
			return 0;
		}
		return 1;
	}
	public boolean isKing(){
		return isKing;

	}
	public boolean isBomb(){
		return type.equals("bomb");
	}
	public boolean isShield(){
		return type.equals("shield");
	}
	public void move(int x, int y){
		//jump
		if (!type.equals("bomb") && Math.abs(x - piecex) == 2 && Math.abs(y - piecey) == 2){
            b.remove((x + piecex) / 2, (y + piecey) / 2); 
            b.remove(piecex, piecey);
            this.piecex = x;
	        this.piecey = y;
	        this.hasCaptured = true;
	        b.place(this, x, y);
            System.out.println("has captured " + hasCaptured);
            if ((isFire && piecey == 7) || (!isFire && piecey == 0)){
        		System.out.println("King Me");
	        	this.piecex = x;
	        	this.piecey = y;
	        	isKing = true;
	        }
        }
        //bomb jump
        else if (type.equals("bomb") && Math.abs(x - piecex) == 2 && Math.abs(y - piecey) == 2){
        	b.remove((x + piecex) / 2, (y + piecey) / 2);
        	b.remove(piecex, piecey);
        	b.place(this, x, y);
        	for (int i = (x - 1); i <= (x + 1); i++) {
                for (int j = (y - 1); j <= (y + 1); j++){
                	if (b.pieceAt(i, j) != null){
                		if (!b.pieceAt(i, j).isShield()){
                			b.remove(i, j);
                		}
                	}
                }
            }

        	
        }
        
        if (Math.abs(x - piecex) == 1 && Math.abs(y - piecey) == 1){
	        b.place(this, x, y);
	        b.remove(piecex, piecey);
	        this.piecex = x;
	        this.piecey = y;
	        if (isFire && y == 7 || !isFire && y == 0){
        		System.out.println("King Me");
	        	this.piecex = x;
	        	this.piecey = y;
	        	isKing = true;
	        }
	        
    	}

	}
	public boolean hasCaptured(){
		return hasCaptured;
	}
	public void doneCapturing(){
		hasCaptured = false;

	}
}