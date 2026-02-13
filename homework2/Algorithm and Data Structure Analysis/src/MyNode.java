public class MyNode<T>{

    //fields
    private T item;
    private MyNode<T> left;
    private MyNode<T> right;
    private MyNode<T> parent;

    //constructor
    public MyNode(T item) {
        this.item = item;
        this.left = null;
        this.right = null;
        this.parent = null;
    }

    //getters
    public T getItem(){
        return this.item;
    }

    public MyNode<T> getLeft(){
        return this.left;
    }

    public MyNode<T> getRight(){
        return this.right;
    }

    public MyNode<T> getParent(){
        return this.parent;
    }

    //setters

    public void setLeft(MyNode<T> input){
        this.left = input;
    }

    public void setRight(MyNode<T> input){
        this.right = input;
    }

    public void setParent(MyNode<T> input){
        this.parent = input;
    }

}
