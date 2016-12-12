/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package client;

/**
 *
 * @author anniecourtney
 */
public class Post {
    String post;
    
    Post next;
    
    public Post(String post){
        this.post=post;
    }
    @Override
    public String toString(){
        return post;
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
