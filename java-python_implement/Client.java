/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package client;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;



public class Client {
     
        String state = ""; //for changing state/ accesses the appropriate subcommands

        boolean loggedIn = false;//certain functionality is only available once they are logged in
        
        String userID = null;
        
        String allGroups;
        
        Subscription subscriptions = new Subscription("");
        
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
           if(subscriptions!=null){
           Subscription point = subscriptions;
            while (point.hasNext()){
                point=point.getNext();
                System.out.println(point.toString());
                //System.out.println(point.getName());
            }
           }
           
          
            
            
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

                   //bufferedWriter.write("comp.programming\n{\n}\ncomp.lang.python\n{\n}\n");
                
                bufferedWriter.close();
       
            }catch(Exception e){
                System.out.println(e.toString());
            }
            
            
        }
        
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
            }else if(state.equals("ag")){
                System.out.println("s INTEGER : subscribes the user to the group in the list that corresponds with the given integer"
                        + "\nu INTEGER : unsubscribes the user from the group in the list that corresponds with the given integer"
                        + "\nn INTEGER : lists the next INTEGER groups or quits the ag state if all groups are displayed"
                        + "\nq : exits from the ag command");
            }else if(state.equals("sg")){
                System.out.println("u INTEGER : unsubscribes the user from the group in the list that corresponds with the given integer"
                        + "\nn INTEGER : lists the next INTEGER groups or quits the ag state if all groups are displayed"
                        + "\nq : exits from the ag command");          
            }else if(state.equals("rg")){
                System.out.println("[id] : an integer between 1 and N that chooses which post to display"
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
            Socket clientSocket = new Socket("allv25.all.cs.stonybrook.edu", 12007); //create client socket, use hostname and port number to connect to server
                          
            DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream()); //create output stream attached to socket
            BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));//create input stream attached to socket

            System.out.println("connected");

            String command = null;
            Scanner s = new Scanner(System.in);
            while (!"exit".equals(command)){
                command = s.nextLine(); 
//            System.out.println("c: "+command);
//LOGIN
                if(command.startsWith("login")){
                //c.login(s.next());
                    c.login(command.substring(6));//calls login with username
                }
//LOGOUT
                if(command.startsWith("logout")){
                //logout - tells server to close connection, closes connection, saves user history, terminates
                
                //outToServer.writeUTF("logout");
                
                //System.out.println("from server:"+inFromServer.readLine()+".");
                
                //clientSocket.close();
                
//                //TEST
//                Post p = new Post("NEW");
//                c.getSubscriptions().getNext().addPost(p);

                
                    c.save();//saves user data locally
                    System.exit(0);
                }
//AG
                if(command.startsWith("ag")){
                //All Groups
                    System.out.println("ag command entered");
                    c.setState("ag");
                    if(command.length()>2){ //if number is specified
                        int x = Integer.parseInt(command.substring(3));
                        outToServer.writeBytes("ag "+x+"\n");
                    }else{    
                        outToServer.writeBytes("ag\n");
                    }
                    
                    String allGroupsIn = inFromServer.readLine(); //recieves string of all groups separated by \n
                    System.out.println("");
                    String[] allGroups = allGroupsIn.split("&"); // allGroups is now an array of all the existing groups 

//TEST
//                String[] allGroups= {"comp.programming","comp.os.threads","comp.lang.c"};
//TEST
                    int i=1;
                    for(String g: allGroups){ //prints the groups and whether or not the user is subscribed
//                        System.out.println("1");//test
                        Subscription z = c.getSubscriptions(); //pointer to iterate through subscriptions list
                        boolean sub=false; //if user is subscribed to the group this becomes true
                        while(z.hasNext()){ //checking if user is subscribed to the group g
//                            System.out.println("2");//test
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
                        System.out.println(g);
                        i++; //increase so that the list is printed with numbering
                    }
                    
                    c.setAllGroups(allGroupsIn);  //save the string off all the groups so server doesnt have to send it again for the n subcommand     
                } 
//SG
                if(command.startsWith("sg")){
                //Subscriptions
                    c.setState("sg");
                    int count = 5;//Default is print 5 groups
                    if(command.length()>2){//if they specify a number of posts count is changed to that number
                        count = Integer.parseInt(command.substring(3));
                    }
                
                    outToServer.writeBytes("sg \n"); //ask server for the total number of posts for each group
                
                    String totalPostsIn = inFromServer.readLine();//wait for response (groupName*numberOfPosts&groupName*numberOfPosts\n)
                    
                    //System.out.println("sg: "+totalPostsIn);
                    
                    String[] totalPosts = totalPostsIn.split("&");//split into an array of groupName*numberOfPosts

                    Subscription temp = c.getSubscriptions();//pointer for iterating through subscriptions
                    int r = 1;//for numbering the printed list 
                    while(temp.hasNext()&&r<count){//go through the subscriptions until there are none left or you go over the count
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
     
            }
//RG
            if(command.equals("rg")){
                //Read Group
                c.setState("rg");
            }
            if(command.equals("help")){
                System.out.println("help entered");
                c.help();
            }
//SUBCOMMANDS

//subscribe
            if(command.startsWith("s")){
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
                        System.out.println(i);
                        String group = allGroups[Integer.parseInt(i)-1];//the name of the group to unsubscribe from
                        Subscription p = c.getSubscriptions(); //pointer to iterate through subscriptions to find the one to delete
                        while(p.hasNext()){        
                            Subscription n =p.getNext();
                            if(n.getName().equals(group)){
                                if(n.hasNext()){
                                    p.setNext(n.getNext());
                                }else{
                                    p.setNext(null);
                                }
                            }
                            p=p.getNext();
                        }
                        
                    } 
                }
            }
            
//next
            if(command.startsWith("n")){
                if(c.getState().equals("ag")){
                    System.out.println("N entered");
                
                    int i = Integer.parseInt(s.next());
              
                    outToServer.writeBytes("n "+i+"\n"); 
              
                    c.setAllGroups(c.getAllGroups()+inFromServer.readLine());  
             
             
                    String[] allGroups = c.getAllGroups().split("&"); // allGroups is now an array of all the existing groups
//                for(String g: allGroups){
//                    System.out.println(g);
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
                }
            }
          
            if(command.equals("q")){
                if(c.getState().equals("ag")|c.getState().equals("sg")|c.getState().equals("rg")){
                    outToServer.writeBytes("q\n");                   
                    System.out.println(inFromServer.readLine());     
                    c.setState("");
                }else if(c.getState().equals("id")){
                    c.setState("rg");
                }
                
            }
//            
//            if(command.equals("r")){
//                
//            }
//            
            if(command.equals("p")){//new post command
                if(c.getState().equals("rg")){//we are in the read group state
                    
                    //prompt user for post title/subject
                    
                    //prompt user for post content ("\n.\n" a dot on its own line ends the post)
                    
                    //submit the post to the server
                    
                   // list posts of group again with the new post as unread
                }
                
            }
            
//            try{
//                int x = Integer.parseInt(s.next());//if the command is just a number in that one case
//                if(c.getState().equals("rg")){
//                    c.setState("id");
//                }
//            }catch(Exception e){
//                
//            }
      }
            
            //System.out.println(command); //for testing
//        }
            System.out.println("out of while");
            //}
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








