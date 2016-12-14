/**
 * for saving information on all of the subscriptions the user has and all of the posts they have read for that subscription
 */
package client;

import client.Post;

/**
 *
 * @author anniecourtney
 */
public class Subscription {

    String groupName; //the postID
    
    Subscription next;//another subscription or null if there are no more
    
    Post post= null;//a read post for this subscription or null if none have been read
    
    
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
    /**
     * appends a newly read post onto the list of read posts for this subscription group
     * @param post a new post that has now been read 
     */
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
    /**
     * 
     * @return the number of posts read in this subscription
     */
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
    public Post getPost(){
        return post;
    }
    
    public boolean hasPosts(){
        return post !=null;
    }
    /**
     * 
     * @return the last subscription in the list
     */
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
