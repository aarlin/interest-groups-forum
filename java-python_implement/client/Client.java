/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package client;

import java.io.*;
import java.net.Socket;
import java.sql.Timestamp;
import java.util.Scanner;



public class Client {
     
        String state = ""; //for changing state/ accesses the appropriate subcommands

        boolean loggedIn = false;//certain functionality is only available once they are logged in
        
        String userID = null;
        
        String allGroups;
        
        String postCount;
        String postContent; //the content of the post we are reading so the n subcommand doesn't have to find it again
        String postsReading; //string of the posts for the group that we are reading so it only has to be read fromthe server once (not again for all the subcommands)
        
        Subscription subscriptions = new Subscription("");
        
        Subscription reading;//the group that a user is currently reading from
        public String getPostContent(){
            return postContent;
        }
        public void setPostContent(String postContent){
            this.postContent=postContent;
        }
        public String getUserID(){
            return userID;
        }
        public String getPostsReading(){
            return postsReading;
        }
        public void setPostsReading(String postsReading){
            this.postsReading = postsReading;
        }
        public Subscription getReading(){
            return reading;
        }
        public void setReading(Subscription reading){
            this.reading=reading;
        }
        public Subscription getSubscriptions(){
            return subscriptions;
        }
        public String getState(){
            return state;
        }
        public void setState(String state){
            this.state=state;
        }
        public void setAllGroups(String allGroups){
            this.allGroups=allGroups;
        }
        public String getAllGroups(){
            return allGroups;
        }
        public String getPostCount(){
            return postCount;
        }
        public void setPostCount(String postCount){
            this.postCount=postCount;
        }
        
        
        /**
         * 
         * this command takes one argument, your user ID. 
         * It is used by the application to determine which discussion groups 
         * you have subscribed to, and for each subscribed group, 
         * which posts you have read.
         * @param userID 
         */
        public void login(String userID){
            loggedIn = true;
            this.userID=userID;
            System.out.println(userID +" logged in");
            
           //save(); //for testing
           getHistory();
           
           //FOR TESTING: (prints the working data on the logged in user)
//           if(subscriptions!=null){
//           Subscription point = subscriptions;
//            while (point.hasNext()){
//                point=point.getNext();
//                System.out.println(point.toString());
//                //System.out.println(point.getName());
//            }
//           }
           
          
            
            
        }
        
        /**
         * reads in any information on the user's history that is saved locally
         * after this is called the subscriptions data field will hold
         * the names off all of the groups the user is subscribed to as well 
         * as the posts they have read
         */
        public void getHistory(){
            String file = /*"client/"+*/userID+".txt";//user history file to open
            String line = null;//to reference one line at a time
            Subscription group=null;
            int rp=0;
            try{
                FileReader fileReader = new FileReader(file);
                BufferedReader bufferedReader = new BufferedReader(fileReader);
                while((line=bufferedReader.readLine())!=null){
                    if(group==null&&rp==0){
                        Subscription s = new Subscription(line);
                        group=s;
                        if(subscriptions.getNext()==null){
                            subscriptions.setNext(s);
                        }else{
                            Subscription last=subscriptions;
                            while(last.hasNext()){
                                last=last.getNext();
                            }
                            last.setNext(s);
                        }
                    }else if(line.equals("{")){
                        rp=1;
                    }else if(line.equals("}")){
                        group=null;
                        rp=0;
                    }else if((group!=null)&&rp==1){
                        group.addPost(new Post(line));
                    }
                }

                bufferedReader.close();
            }catch(FileNotFoundException e){
                System.out.println("There is no save history for this user.");
            }catch(Exception e){
                System.out.println("Get History "+e);
            }
        }
        
        /**
         * this saves all the user data locally to a file 
         */
        public void save(){
            
            String file = userID+".txt";//user history file to save to
            try{
                FileWriter fileWriter = new FileWriter(file);
                BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
                Subscription p = subscriptions;
                while(p.hasNext()){
                    p=p.getNext();
                    bufferedWriter.write(p.toString()+"\n");
                }

//                   bufferedWriter.write("comp.programming\n{\n}\ncomp.lang.python\n{\n}\n");
                
                bufferedWriter.close();
       
            }catch(Exception e){
                System.out.println(e.toString());
            }
            
            
        }
        /**
         * prints the appropriate commands, depending on what state the user is in
         */
        public void help(){
            System.out.println("help requested");
            if (loggedIn==false){
                //if you are not logged in these are the commands available:
                System.out.println("login : use 'login YOUR_USERNAME' to login");
            }else if(state.equals("")){
                //if you are logged in, these are the commands available:
                System.out.println("ag : lists the names of all existing discussion groups"
                        + "\nag INTEGER : lists the names of existing discussion groups up to the specified integer"
                        + "\nsg : lists all the groups you are subscribed to"
                        + "\nsg INTEGER : lists the groups you are subscribed to up to the specified integer"
                        + "\nrg GROUP_NAME : displays all posts for the given group"
                        + "\nrg GROUP_NAME INTEGER : displays posts for the given group up to the specified integer"
                        + "\nlogout : logs out the current user and closes the program");   
            }else if(state.startsWith("ag")){
                System.out.println("s INTEGER : subscribes the user to the group in the list that corresponds with the given integer"
                        + "\nu INTEGER : unsubscribes the user from the group in the list that corresponds with the given integer"
                        + "\nn INTEGER : lists the next INTEGER groups or quits the ag state if all groups are displayed"
                        + "\nq : exits from the ag command");
            }else if(state.startsWith("sg")){
                System.out.println("u INTEGER : unsubscribes the user from the group in the list that corresponds with the given integer"
                        + "\nn INTEGER : lists the next INTEGER groups or quits the ag state if all groups are displayed"
                        + "\nq : exits from the ag command");          
            }else if(state.startsWith("rg")){
                System.out.println("[ID] : ID is an integer between 1 and N that chooses which post to display"
                        + "\nr INTEGER/INTEGER_RANGE : marks the given number(ed) post(s) as read"
                        + "\nn INTEGER : lists the next posts up to the specified integer"
                        + "\np : create a new post"
                        + "\nq : exits the rg command");
            }else if (state.equals("id")){
                //options available when the user is reading a post
                System.out.println("n INTEGER : list the next lines of the post up to the specified integer"
                        + "\nq : quit displaying the post content");
            }
        }
        
        
      
        
        
    public static void main(String[] args) {
        Client c = new Client();
        try{ 
            Scanner s = new Scanner(System.in);
            
            String serverName = s.next();
            int portNumber = s.nextInt();
            
            Socket clientSocket = new Socket(serverName, portNumber); //create client socket, use hostname and port number to connect to server
                //allv25.all.cs.stonybrook.edu
            DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream()); //create output stream attached to socket
            BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));//create input stream attached to socket

            System.out.println("connected");
            String command = null;
            while (!"exit".equals(command)){
                command = s.nextLine(); 
//LOGIN
                if(command.startsWith("login")){
                    c.login(command.substring(6));//calls login with username
                }
//LOGOUT
                if(command.startsWith("logout")){
                //logout - saves user history, terminates
                    c.save();//saves user data locally
                    System.exit(0);
                }
//AG
                if(command.startsWith("ag")){
                //All Groups - displays 5 groups, or if a number of groups is specified it displays that many groups
                    c.setState("ag");
                    if(command.length()>2){ //if number is specified
                        int x = Integer.parseInt(command.substring(3));//get that number and make sure to send it to the server             
                        outToServer.writeBytes("ag "+x+"\n");//tell the server to send x groups        
                    }else{              
                        outToServer.writeBytes("ag\n");//tell the server to send 5 groups          
                    }        
                    String allGroupsIn = inFromServer.readLine(); //recieves string of all groups separated by \n
                    System.out.println("");
                    String[] allGroups = allGroupsIn.split("&"); // allGroups is now an array of all the existing groups 

                    int i=1;
                    for(String g: allGroups){ //prints the groups and whether or not the user is subscribed
                        Subscription z = c.getSubscriptions(); //pointer to iterate through subscriptions list
                        boolean sub=false; //if user is subscribed to the group this becomes true
                        while(z.hasNext()){ //checking if user is subscribed to the group g
                            z=z.getNext();
                            if(g.equals(z.getName())){
                                sub = true;
                            }
                        }
                        if(sub){//if the user is subscribed print with s
                            System.out.print(i+" (s) ");
                        }else{//if user is not subscribed print without s
                            System.out.print(i+" ( ) ");
                        }
                        System.out.println(g);//print the group
                        i++; //increase so that the list is printed with numbering
                    }
                    
                    c.setAllGroups(allGroupsIn);  //save the string off all the groups so server doesnt have to send it again for the n subcommand     
                } 
//SG
                if(command.startsWith("sg")){
                //Subscriptions - lists 5 subscriptions, or the amount specified by the user
                    c.setState(command);//saves the amount specified for the user (for use in the n subcommand)
                    if(command.length()==2){//if no number was specified
                        c.setState("sg 5");//so that the n subcommand can know that the default of 5 groups were shown
                    }
                    int count = 5;//Default is print 5 groups
                    if(command.length()>2){//if they specify a number of posts count is changed to that number
                        count = Integer.parseInt(command.substring(3));
                    }
                    outToServer.writeBytes("sg \n"); //ask server for the total number of posts for each group
                
                    String totalPostsIn = inFromServer.readLine();//wait for response (groupName*numberOfPosts&groupName*numberOfPosts\n)
                    c.setPostCount(totalPostsIn); //save this info for the n subcommand
                   
                    String[] totalPosts = totalPostsIn.split("&");//split into an array of groupName*numberOfPosts

                    Subscription temp = c.getSubscriptions();//pointer for iterating through subscriptions
                    int r = 1;//for numbering the printed list 
                    while(temp.hasNext()&&r<=count){//go through the subscriptions until there are none left or you go over the count
                        temp=temp.getNext();
                        int posts = 0;
                        for(int x=0; x<totalPosts.length; x++){ //go through the array of group names and post counts
                            if(totalPosts[x].startsWith(temp.getName())){//if the post ids match
                                posts = Integer.parseInt(totalPosts[x].substring(totalPosts[x].indexOf("*")+1));//get the number of unread posts
                            }
                        }
                        System.out.println(r+". "+(posts-temp.readPosts())+" "+temp.getName());
                        r++;
                    }
     
            }
//RG
            if(command.startsWith("rg")){
                //Read Group
               
                String[] request = command.split(" ");//so this is an array of rg, groupName, and possibly the number of posts desired
                int numberOfPosts = 5; //default displays 5 posts
                if(request.length==3){
                    numberOfPosts = Integer.parseInt(request[2]); //if a number of posts was specified
                }
                 c.setState("rg "+Integer.toString(numberOfPosts));//save how many posts were printed
                 
                outToServer.writeBytes(request[0]+" "+request[1]+"\n");//send "ag groupName\n" to server
                
                String p = inFromServer.readLine(); //recieve postID$time$subject&postID$time$subject etc
                
                c.setPostsReading(p);//saves this info from the server for subcommands
                
                String[] posts = p.split("&"); //an array of posts             
                Subscription group = c.getSubscriptions();//pointer to find the group in question so we can see what posts have been read in it
                while(group.hasNext()){
                    group=group.getNext();
                    if(group.getName().equals(request[1])){
                            c.setReading(group);
                            //System.out.println("group found");
                            break; //now group should be the subcription group in question
                    }
                }
                int x = 0;
                while(x<numberOfPosts&&x<posts.length){
                    String[] postInfo = posts[x].split("\\$"); //an array of postID, subject, content, username, timestamp for a single post
                    //check if the post has been read:
                    String read = "N";//indicates that this is a new post
                    if(group.hasPosts()){
                        Post p2 = group.getPost();//pointer to search for a matching post
                        while(p2!=null){        
                            if(p2.toString().equals(postInfo[0])){//if the postID matches a read postID
                                read=" ";//change read from "N" to " " to indicate it is not new
                                break;//stop looking for a match
                            }
                            p2=p2.getNext();
                        }
                    }       
                    System.out.println(postInfo[0]+"."+read+" "+postInfo[4]+" "+postInfo[1]);//print ID. read time subject
                    x++;
                }
            }
            if(command.equals("help")){
                System.out.println("help entered");
                c.help();
            }
//SUBCOMMANDS

//subscribe
            if(command.startsWith("s")&&!command.startsWith("sg")){
                if(c.getState().equals("ag")){
                    //read in the number or list of numbers
                    String numbers = command.substring(2);//substring of just the list of numbers of posts to subscribe to
                    String[] groups = numbers.split(" ");//array of the numbers of the posts to subscribe to 
                    
                    String[] allGroups = c.getAllGroups().split("&");//puts all the group in an array so that the index matches their number on the list displayed to the user
                    
                    for(String i : groups){//goes through the array of numbers to subscribe to and  subscribes
                        Subscription newSub = new Subscription(allGroups[Integer.parseInt(i)-1]);
                        c.getSubscriptions().getLast().setNext(newSub);  
                    }  
                } 
            }

//unsubscribe            
            if(command.startsWith("u")){
                if(c.getState().equals("ag")){
                    //read in the number or list of numbers
                    String numbers = command.substring(2);//substring of just the list of numbers of posts to unsubscribe from
                    String[] groups = numbers.split(" ");//array of the numbers of the posts to unsubscribe from 
                    
                    String[] allGroups = c.getAllGroups().split("&");//puts all the group in an array so that the index matches their number on the list displayed to the user
                    
                    for(String i : groups){
                        //System.out.println(i); TEST
                        String group = allGroups[Integer.parseInt(i)-1];//the name of the group to unsubscribe from
                        Subscription p = c.getSubscriptions(); //pointer to iterate through subscriptions to find the one to delete
                        while(p.hasNext()){        
                            Subscription n =p.getNext();
                            if(n.getName().equals(group)){
                                //System.out.println("found"); TEST
                                if(n.hasNext()){
                                    p.setNext(n.getNext());
                                }else{
                                    p.setNext(null);
                                }
                            }
                            if(p.hasNext()){
                                p=p.getNext();
                            }
                        }
                        
                    } 
                }else if(c.getState().startsWith("sg")){
                                        //read in the number or list of numbers
                    String numbers = command.substring(2);//substring of just the list of numbers of posts to unsubscribe from
                    String[] groups = numbers.split(" ");//array of the numbers of the posts to unsubscribe from 
                   
                        //System.out.println(i); TEST
                        Subscription p = c.getSubscriptions(); //pointer to iterate through subscriptions to find the one to delete
                        int count = 1;//to keep track of where we are in the subscription list
                        while(p.hasNext()){//to move through the subscription list    
                            Subscription n =p.getNext();
                            for(String i : groups){//to check to see if the current subscription is on the list to be removed we must go through the removed list
                                if(count==Integer.parseInt(i)){//if we are at a subscription that should be deleted
                                    //System.out.println("found"); TEST
                                    if(n.hasNext()){
                                        p.setNext(n.getNext());
                                    }else{
                                        p.setNext(null);
                                    }
                                }
                            }
                            if(p.hasNext()){
                                p=p.getNext();
                            }
                            count++;
                        }
                        
                    
                }
            }
            
//next
            if(command.startsWith("n")){
                if(c.getState().equals("ag")){
                    //System.out.println("N entered for ag");
                
                    int i = 2;//default is print two more groups
                    if(command.length()>2){
                           i= Integer.parseInt(/*s.next()*/command.substring(2));//i = how many more groups to print
                    }
         
                    outToServer.writeBytes("n "+i+"\n"); 
              
                    c.setAllGroups(c.getAllGroups()+inFromServer.readLine());  
          
             
                    String[] allGroups = c.getAllGroups().split("&"); // allGroups is now an array of all the existing groups
                    int y=1;
                    for(String g: allGroups){
                        if (g.equals("AG 500 CLOSE")){
                            System.out.println(g);
                            c.setState("");
                        }else{
                            Subscription z = c.getSubscriptions();
                            boolean sub=false;
                            while(z.hasNext()){
                                z=z.getNext();
                                if(g.equals(z.getName())){
                                    sub = true;
                                }
                            }
                            if(sub){
                                System.out.print(y+" (s) ");
                            }else{
                                System.out.print(y+" ( ) ");
                            }
                            System.out.println(g);
                            y++;
                        } 
                    }   
                }else if(c.getState().startsWith("sg")){
                    //System.out.println("n entered for sg");
                    int i = 2;//default is print 2 more groups
                    if(command.length()>2){
                        i=Integer.parseInt(command.substring(2))+Integer.parseInt(c.getState().substring(3)); //i = how many more groups to print + how many have already been printed
                    }
                    //System.out.println("i: "+i);
                    c.setState("sg "+i);//keeps track of how many have been printed in case n command is continuously entered
                    String[] totalPosts = c.getPostCount().split("&");
                    
                    Subscription temp = c.getSubscriptions();//pointer for iterating through subscriptions
                    int r = 1;//for numbering the printed list 
                    while(temp.hasNext()&&r<=i){//go through the subscriptions until there are none left or you go over the count
                        temp=temp.getNext();
                        int posts = 0;
                        //System.out.println("totalPosts.length: "+totalPosts.length);
                        for(int x=0; x<totalPosts.length; x++){ //go through the array of group names and post counts
                            if(totalPosts[x].startsWith(temp.getName())){
                                posts = Integer.parseInt(totalPosts[x].substring(totalPosts[x].indexOf("*")+1));
                            }
                        }
                        //System.out.println(posts);
                        System.out.println(r+". "+(posts-temp.readPosts())+" "+temp.getName());
                        r++;
                    }
                    if(r!=i+1){
                        System.out.println("quit from sg state");
                        c.setState("");
                    }
                    
                    
                }else if (c.getState().startsWith("id")){
                    int lines = 2;//default is display 2 more lines
                    if(command.length()>2){
                        lines = Integer.parseInt(command.substring(2)) /*+ Integer.parseInt(c.getState().substring(3))*/;//the number of new lines to display + the number of lines already displayed
                    }
                    String content = c.getPostContent();
                    for(int i=0; i<lines; i++){
                        if(content.length()!=0){
                            System.out.println(content.substring(0, Math.min(content.length(), 40)));
                            content = content.substring(Math.min(content.length(), 40));
                        }
                    } 
                    c.setPostContent(content);//take out what has been printed
                }else if(c.getState().startsWith("rg")){
                    int i = 2;//default is print 2 more posts
                    if(command.length()>2){
                       i= Integer.parseInt(command.substring(2));
                    }
                    int numberOfPosts = i+Integer.parseInt(c.getState().substring(3)); //i = how many more posts to print + how many have already been printed
                    //System.out.println("nOp: "+numberOfPosts); //TEST
                    String[] posts = c.getPostsReading().split("&");//an array of all the posts for this group
                    c.setState("rg "+Integer.toString(numberOfPosts));//update how many posts have been printed
                    Subscription group = c.getReading();
                    int x = 0;
                    while(x<numberOfPosts-1&&x<posts.length){
                        String[] postInfo = posts[x].split("\\$"); //an array of postID, subject, content, username, timestamp for a single post
                        //check if the post has been read:
                        String read = "N";
                        if(group.hasPosts()){
                            Post p2 = group.getPost();//pointer to search for a matching post
                            while(p2!=null){        
                                if(p2.toString().equals(postInfo[0])){//if the postID matches a read postID
                                    read=" ";//change read from "N" to " " to indicate it is not new
                                    break;//stop looking for a match
                                }
                                p2=p2.getNext();
                            }
                        }       
                        System.out.println(postInfo[0]+"."+read+" "+postInfo[4]+" "+postInfo[1]);//print ID. read time subject
                        x++;
                    }
                    
                }
            }
//quit          
            if(command.equals("q")){
                //quit - moves back one state
                if(c.getState().startsWith("ag")|c.getState().startsWith("sg")|c.getState().startsWith("rg")){ //if we are in one of these states     
                    outToServer.writeBytes("q\n");//tell the server we are quitting the state                   
                    System.out.println(inFromServer.readLine()); //print the response from the server
                    c.setState("");//change back to home state
                }else if(c.getState().equals("id")){//if we are in this state
                    c.setState("rg 5");//change back to the read group state
                    System.out.println("exited post reading state");
                }
                
            }
//mark as read            
            if(command.startsWith("r")&&!command.startsWith("rg")){
                //r - saves the post or posts in the range to the read posts list for the currrent group
                if(c.getState().startsWith("rg")){
                   String posts = command.substring(2); // this is a string of a single postID or a range of postIDs
                    //System.out.println("posts to be marked: "+posts);//test
                   String[] read;
                   if(posts.contains("-")){//if it is a range split it to just have the ids
                       String[] x = posts.split("-");//getting both numbers from the range
                       int numberToMark = ((Integer.parseInt(x[1])-Integer.parseInt(x[0]))/10)+1;//since the posts ids go up by 10 this is how we can get a count on how big the range is
                       read = new String[numberToMark];
                       for(int r =0; r<numberToMark; r++){
                           read[r]= Integer.toString(Integer.parseInt(x[0])+(r*10));//now read should be an array of all the ids to be marked read
                       } 
                   }else{
                      read = new String[1];//if it is not a range than read[0] is the only id to mark read
                      read[0]=posts;
                   }
                   
                   for(String r: read){
                       Post mark = new Post(r);//makes a new post with that id
                       
                       Post p = c.getReading().getPost();//pointer to iterate through post, so as not to count the same post as a read post twice
                       boolean alreadyRead = false;
                       while(p!=null){//move through the list of all the read posts for the current group
                           if(p.toString().equals(r)){//if the ids match (it was already marked as read)
                               alreadyRead=true;
                           }
                           p=p.getNext();
                       }
                       if(!alreadyRead){
                        c.getReading().addPost(mark);//adds it to the list of read posts for the current group we are reading from
                       }
                   } 
                }    
            }
      
//new post
            if(command.equals("p")){
            //new post command - prompt user for post details and sends it to the server to be saved
                if(c.getState().startsWith("rg")){//we are in the read group state
                    String content = "";
                    //prompt user for post title/subject
                    System.out.println("Enter post subject/title: ");
                    String subject= s.nextLine();
                    //prompt user for post content ("\n.\n" a dot on its own line ends the post)
                    System.out.println("Enter post content ('.' on a single line ends the post): ");
                    String end="";
                    while(!end.equals(".")){
                        content+=end;
                        end = s.nextLine();
                    }
                    //get the post id
                    String[] posts=c.getPostsReading().split("&"); 
                    String[] postInfo = posts[posts.length-1].split("\\$"); //locating the post id of the last post in this group
                    int postID = Integer.parseInt(postInfo[0])+10;//the post ids go up by 10 so this is the new post id for the new post
                    //get the post time
                    Timestamp time = new Timestamp(System.currentTimeMillis());
                    //submit the post to the server
                    String newPost = postID+"$"+subject+"$"+content+"$"+c.getUserID()+"$"+time.toString();               
                    //String sendingPost = "\""+postID+"\"$\""+subject+"\"$\""+content+"\"$\""+c.getUserID()+"\"$\""+time.toString()+"\"&";
                    outToServer.writeBytes("p\n");
                    outToServer.writeBytes(newPost+"\n");
                   // System.out.println("sent to server: "+newPost);   
                    // list posts of group again with the new post as unread
                    c.setPostsReading(c.getPostsReading()+"&"+newPost);
                }
                
            }
//ID (read post)            
            if(command.startsWith("[")){
            //read post - displays the first 2 lines (of 40 characters) of the post content that corresponds with the given post id
                c.setState("id");
                
//                String[] info = command.split(" ");//split on space because the second item is the po
                
                String postID = command.substring(1, command.length()-1);//this should be the id of the post to be opened
                c.getReading().addPost(new Post(postID));//marks/saves the post as read
                String[] posts = c.getPostsReading().split("&");//posts is an array of all the posts in the current group, we need to find the one that matched postID
                for(String x:posts){
                    String[] postInfo = x.split("\\$");//the different post information (id, subject, content, etc) is separated by $
                    if(postInfo[0].equals(postID)){
                        c.setPostContent(postInfo[2]);//this is the post content we want to display
                        System.out.println("Group: "+c.getReading().getName()//print post header
                                + "\nSubject: "+postInfo[1]
                                + "\nAuthor: "+postInfo[3]
                                + "\nDate: "+postInfo[4]+"\n");
                    }
                }
                int lines = 2; //default is display the first 2 lines of the post
                String content = c.getPostContent();
                for(int i=0; i<lines; i++){
                    if(content.length()!=0){
                        System.out.println(content.substring(0, Math.min(content.length(), 40)));
                        content = content.substring(Math.min(content.length(), 40));
                        //System.out.println(c.getPostContent().substring(i*40,Math.min(c.getPostContent().length(), 40)));//display lines of 40 characters
                    }
                }
                c.setPostContent(content);//take out what has been printed
            }
      }//end of while
            s.close();
            }catch(Exception e){
                System.out.println("E: "+e.toString());
            }  
    }
}


/**
 *so I remember how to run this on the command line:
 * 
 * go to the directory that Client.java is in (cd Java_Applications on my laptop)
 * javac Client.java
 * java Client TYPE_AN_IMPLEMENTED_COMMAND_HERE
 * 
 * 
 * 
 * 
 * 
 * multiple classes running from terminal (all the .java files are in Java_Applications.client.src.client)
 * 
 * go to src directory (Java_Applications, client, src)
 * 
 * 
 * compile everything (javac client/*.java)
 * 
 * 
 * run Client (java client.Client)
 * 
 * 
 * 
 */








