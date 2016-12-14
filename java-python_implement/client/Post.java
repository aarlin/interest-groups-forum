/*
 * for saving information on all of the posts that the user has read
 */
package client;

/**
 *
 * @author anniecourtney
 */
public class Post {
    String postID;
    
    Post next;
    
    public Post(String post){
        postID=post;
    }
    @Override
    public String toString(){
        return postID;
    }
    
    public void setNext(Post next){
        this.next=next;
    }
    
    public Post getNext(){
        return next;
    }
    
    public boolean hasNext(){
        return next!=null;
    }
    
}
