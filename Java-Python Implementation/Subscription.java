/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package client;

import client.Post;

/**
 *
 * @author anniecourtney
 */
public class Subscription {

    String groupName;
    
    Subscription next;
    
    Post post= null;
    
    
    public String getName(){
        return groupName;
    }
    public void setName(String groupName){
        this.groupName=groupName;
    }
    
    public Subscription(String groupName){
        this.groupName=groupName;
    }
    
    public void setNext(Subscription next){
        this.next=next;
    }
    public Subscription getNext(){
        return next;
    }
    
    public boolean hasNext(){
        return next != null;
    }
    
    public void addPost(Post post){
        if(this.post==null){
            this.post=post;
        }else{
            Post last=this.post;
            while(last.hasNext()){
                last=last.getNext();
            }
            last.setNext(post);
        }
    }
    
    public int readPosts(){
        if (post==null){
            return 0;
        }else{
            int i=1;
            while(post.hasNext()){
                i++;
            }
            return i;
        }
    }
    public Subscription getLast(){
        if(next==null){
            return this;
        }else{
            Subscription pointer = this;
            while(pointer.hasNext()){
                pointer=pointer.getNext();
            }
            return pointer;
        }
    }
    
    @Override
    public String toString(){
        String s = groupName+"\n{\n";
        Post p = post;
        if(p!=null){
            s=s+p.toString()+"\n";
        while(p.hasNext()){
            p=p.getNext();
            s=s+p.toString()+"\n";
        }
        }
        s=s+"}";
        return s;
    }
    
    
    
}
