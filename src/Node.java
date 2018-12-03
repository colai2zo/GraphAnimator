import javafx.scene.Cursor;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;

public class Node extends Circle{

    private double originalX;
    private double originalY;
    private int hashCode;

    public Node(double x, double y){
        super(x, y, 5);
        setFill(Color.BLACK);
        setStroke(Color.BLACK);
    }

    @Override
    public boolean equals(Object other){
        if(other instanceof Node)
            return ((Node)other).getCenterX() == this.getCenterX() && ((Node)other).getCenterY() == this.getCenterY();
        return false;
    }

    @Override
    public int hashCode(){
        if(hashCode == 0){
            hashCode = (hashCode * 397) ^ (int) this.getCenterX();
            hashCode = (hashCode * 397) ^ (int) this.getCenterY();
        }
        return hashCode;
    }

    public boolean overlaps(Node other){
        double distX = this.getCenterX() - other.getCenterX();
        double distY = this.getCenterY() - other.getCenterY();
        double rSum = this.getRadius() + other.getRadius();
        return Math.hypot(distX, distY) < rSum * 2;
    }

    public boolean linearOverlaps(Node other, double spacing){
        return Math.abs(this.getCenterX() + this.getRadius() - (other.getCenterX() - other.getRadius())) < spacing ||
                Math.abs(this.getCenterX() - this.getRadius() - (other.getCenterX() - other.getRadius())) < spacing ||
                Math.abs(this.getCenterY() + this.getRadius() - (other.getCenterY() - other.getRadius())) < spacing ||
                Math.abs(this.getCenterY() - this.getRadius() - (other.getCenterY() - other.getRadius())) < spacing;
    }

    public double distanceTo(Node other){
        return Math.sqrt(Math.pow(this.getCenterX() - other.getCenterX(), 2) + Math.pow(this.getCenterY() - other.getCenterY(), 2));
    }

    public String toString(){
        return "(" + this.getCenterX() + "," + this.getCenterY() + ")";
    }



}
