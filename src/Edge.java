import javafx.scene.shape.Line;
public class Edge extends Line{

    private Node endPoint1;
    private Node endPoint2;

    public Edge(Node node1, Node node2){
        super(node1.getCenterX(), node1.getCenterY(), node2.getCenterX(), node2.getCenterY());
        endPoint1 = node1;
        endPoint2 = node2;
        this.startXProperty().bindBidirectional(endPoint1.centerXProperty());
        this.endXProperty().bindBidirectional(endPoint2.centerXProperty());
        this.startYProperty().bindBidirectional(endPoint1.centerYProperty());
        this.endYProperty().bindBidirectional(endPoint2.centerYProperty());
    }

    public Node getEndPoint1(){
        return endPoint1;
    }

    public Node getEndPoint2(){
        return endPoint2;
    }

    public double length(){
        return Math.sqrt(Math.pow(endPoint1.getCenterX() - endPoint2.getCenterX(), 2) + Math.pow(endPoint1.getCenterY() - endPoint2.getCenterY(), 2));
    }

    public boolean equals(Object other){
        if(other instanceof Edge){
            return ((Edge) other).getEndPoint1().equals(endPoint1) && ((Edge) other).getEndPoint2().equals(endPoint2) ||
                    ((Edge) other).getEndPoint1().equals(endPoint2) && ((Edge) other).getEndPoint2().equals(endPoint1);
        }
        return false;
    }

    @Override
    public int hashCode(){
        int hashCode = (int) this.endPoint1.hashCode();
        hashCode = hashCode ^ (int) this.endPoint2.hashCode();
        return hashCode;
    }
}