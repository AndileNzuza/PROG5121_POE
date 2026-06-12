/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.mycompany.prog5121_poe;

import java.util.ArrayList;
import java.util.Scanner;

import java.io.FileReader;
import java.io.BufferedReader;
import java.util.Date;
import java.text.SimpleDateFormat;

import java.io.FileWriter;

import java.io.IOException;

import java.util.regex.Pattern;

/**
 *
 * @author Andile Nzuza
 */
    
    //Declaration
class Message{
    String ID;
    String hash;
    String recipient;
    String content;
    String timestamp;
    String status;
    
    public Message(String ID, String hash, String recipient, String content, String timestamp, String status){
        this.ID = ID;
        this.hash = hash;
        this.recipient = recipient;
        this.content = content;
        this.timestamp = timestamp;
        this.status = status;
    }
}

public class PROG5121_POE {
    static ArrayList<String> sentMessages = new ArrayList<>();
    static ArrayList<String> disregardMessages = new ArrayList<>();
    static ArrayList<String> storedMessages = new ArrayList<>();
    static ArrayList<String> messageHashes = new ArrayList<>();
    static ArrayList<String> messageIDs = new ArrayList<>();

    static ArrayList<Message> messages = new ArrayList<>();

    static Scanner input = new Scanner(System.in);
    static int sentCount = 0;
    static int messageLimit = 0;
    static int messageCounter = 0;

    static final String JSON_FILE = "messages.json";
    static Object validateNumber;

    //Check if username contains underscore and is max 5 characters
    public static boolean checkUserName(String username){
        return username.contains("_")&& username.length() <=5;
    }

    //Check password has 8+ characters, 1 uppercase, 1 special character
    public static boolean checkPasswordComplexity(String password){
        String regex = "^(?=.*[A-Z])(?=.*\\d)(?=.*[@#$%^&+=!]).{8,}$";
        return Pattern.matches(regex, password);
    }

    //Check if cell phone number is valid (+27 followed by 9 digits)
    public static boolean checkCellPhoneNumber(String number){
        String regex = "\\+27\\d{9}$";
        return Pattern.matches(regex, number);
    }

    //Validate registration credentials and return feedback message
    public static String registerUser(String username, String password){
        if (!checkUserName(username)){
            return "Username is not correct. Must have _ and max 5 characters.";}
        if (!checkPasswordComplexity(password)){
            return "Password is not correct. Must have 8+ characters, capital letter, number, and special character";
        }else{
            return"";
        }
    }

    //Check login credentials agaisnt stored credentials
    public static boolean loginUser(String username, String password,String storedUsername, String storedPassword){
        return username.equals(storedUsername)&& password.equals(storedPassword);
    }

    //Show logic message
    public static String returnLoginStatus(boolean status){
        if (status){
            return "Login succesful! Welcome back!";
        }else { 
            return "Username or password incorrect, please try again.";
        }
    }

    //Validate recipient phone number
    public static String validateNumber(String  num){
        if (num == null) 
            return "incorrect";
        if (!num.startsWith("+27"))
            return "incorrect: Recipient number must start with +27";
        if (num.length()!= 12)
            return "incorrect: Recipient number must be 12 digits (+27XXXXXXXXX)";
        if (!num.substring(3).matches("\\d{9}"))
            return "incorrect: only digits allowed after +27";
            return "Recipient number entered successfully";
    }

    //Generate message hash from ID, sequence number, and firts/last word of message
    public static String createMessageHash(String ID, int num, String message){
        String [] words = message.trim().split("\\s+");
        String first = words.length > 0 ? words[0]: "Message";
        String last = words.length > 1 ? words[words.length - 1]: words[0];
        return (ID.substring(0, 2)+ ":" + num + ":" + first + last).toUpperCase();
    }

    public static void main(String[] args){
        String[] credentials = registerUser();
        messageLimit = getMessageLimit();

        boolean running = true;
        while (running){
            displayMenu();
            String choice = input.nextLine().trim();
            switch (choice){
                case "1": sendMessage(); break;
                case "2": showMessage();break;
                case "3": discardLastMessage();break;
                case "4": storedMessage();break;
                case "5": saveAndExit(); running = false; break;
                default:  System.out.println("Invalid option. Choose 1-5"); 
            }
        }
}
           
/**
* Adds one message to ALL parallel arrays and the master messages list.
* This keeps every array in sync at all times.
* @param ID 10-digit message ID string
* @param recipient phone number or developer ID
* @param content message body text
* @param status "Sent", "Stored", or "Disregard"
*/
            
public static void addMessageToArrays(String ID, String recipient, String content, String status){
    String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
    String hash = createMessageHash(ID, messages.size(), content);
    
    Message message = new Message(ID, hash, recipient, content, timestamp, status);
    messages.add(message);
    messageIDs.add(ID);
    messageHashes.add(hash);
    
if (status.equals("Sent")){
    sentMessages.add(content);
    sentCount++;
}else if (status.equals("Stored")){
    storedMessages.add(content);
}else if (status.equals("Disregard")){
    disregardMessages.add(content);
    System.out.println("Unkown status '" + status + "' for message:" + ID);
}
}

/**
 * Removes a message from the master list AND all relevant parallel arrays.
 * Ensures no orphaned data remains in any array.
 * @param m the Message object remove
 */

public static void removeMessageFromArrays(Message m){
    messages.remove(m);
    messageIDs.remove(m.ID);
    messageHashes.remove(m.hash);
    
    if (m.status.equals("Sent")){
        sentMessages.remove(m.content);
        sentCount--;
    }else if (m.status.equals("Stored")){
         storedMessages.remove(m.content);
    }else if (m.status.equals("Disregard")){
         disregardMessages.remove(m.content);
    }
}

//Method to register a new user and return their credentials
public static String[] registerUser(){
    System.out.println("Registation   ");
    
    String username;
    while (true){
        System.out.print("Enter Username (must contain '_' nad max 5 characters):");
        username = input.nextLine();
        if (checkUserName(username)) break;
        System.out.println("Invalid username.Example: user_");
    }
    String password;
    while (true){
        System.out.print("Enter Passwoprd (8+ characters, 1 capital letter, 1 number, 1 special character):");
        password = input.nextLine();
        if (checkPasswordComplexity(password))break;
        System.out.println("Invalid password.Example: Password5!");
    }
    String cellPhone;
    while (true){
        System.out.print("Enter Cell Phone(+27 followed by 9 digits):");
        cellPhone = input.nextLine();
        if (checkCellPhoneNumber(cellPhone)){
           System.out.println("Cell phone number successfully added.");
           break;
        }
        System.out.println("Invalid number. Must start with +27 and have 9 digiots. Example: +27123456789");
    }
    
//Display registration message and return credentials

System.out.println(registerUser(username, password));
    return new  String[]{username, password};
    
}

//User login method
public static  void login(String[] credentials){
    
    System.out.println("    Login");
    while (true){
        System.out.print("Enter Username: ");
        String loginUser = input.nextLine();
        System.out.print("Enter Password: ");
        String loginPass = input.nextLine();
        if (loginUser(loginUser, loginPass, credentials[0], credentials[1])){
           System.out.println("Welcome back" + loginUser + ", grat to see you again.");
           System.out.println("\n");
           System.out.println("    WELCOME TO QUIKCHAT   ");
           break; //Break loop if login is successful
        }
        System.out.println("Invalid credentials. Try again");
    }
}

//Message limit metyhod
public static int getMessageLimit(){
    while (true){
        try{
            System.out.print("How many messages do you want to send?");
            int limit = Integer.parseInt(input.nextLine().trim());
            if (limit <= 0){
                System.out.println("Please enter a number greater than 0.");
            }else {
                return limit;
            }
        }catch(NumberFormatException e)
        {
            System.out.println("Please enter a whole number.");
        }
    }
}

//Method to display the menu 
public static void displayMenu(){
    System.out.println("QUICKCHAT MENU ");
    System.out.println("1. Send Message");
    System.out.println("2. Show recently sent messages");
    System.out.println("3. Discard Last Message");
    System.out.println("4. Stored Messages");
    System.out.println("5. Quit");
    System.out.print("Choose an option (1-5):");
}

//Method for the message composition
public static void composeMessage(){
    messageCounter++;
}

//Method for sending message
public static void sendMessage(){
    String usernameUser;
    String status;
    if(sentCount >= messageLimit){
        System.out.println("[WARNIG] Message limit reached!");
        return;
    }
    
    composeMessage();
    System.out.println("\n~~~~ Message No." + messageCounter + "~~~~");
    System.out.print("Enter Recipient Number (+27XXXXXXXXX):");
    String rec = input.nextLine().trim();
    String validation = validateNumber(rec);
    
    if(!validation.equals("Recipient number entered successfully")){
        System.out.println("ERROR-" + validation);
        return;
    }
    while(true){
        System.out.print("Enter Recipient Username(must contain '_' and max 5 characters):");
        usernameUser = input.nextLine();
        if(checkUserName(usernameUser))break;
        System.out.println("Invalid Rcipient username. Example: user_");
    }
    
    System.out.print("Enter you message(max 250 characters):");
    String message = input.nextLine();
    if(message.length()> 250){
        int excessCharacters = message.length()- 250;
        int excessWords = message.substring(250).split("\\s+").length;
        System.out.println("Message exceeds 250 characters by" + excessCharacters + "characters("+ excessWords +"words).,[Please reduce the size]");
        return;
    }
    System.out.println("\nWhat would you like to do with message?");
    System.out.println("1. Send Message");
    System.out.println("2. Store Message");
    System.out.println("3. Disregard Message");
    System.out.println("Choose an option (1-3):");
    String statusChoice = input.nextLine().trim();
    
    if(statusChoice.equals("1")){
        status = "Sent";
    }else if(statusChoice.equals("2")){
        status = "Stored";
    }else if(statusChoice.equals("3")){
        status = "Disregard";
    }else{
        System.out.println("Invalid choice. Defaulting to Sent.");
        status = "Sent";
    }
    String ID = String.format("%010d", (long)(Math.random()*10000000000L));
    
    addMessageToArrays(ID, rec, message, status);
    
    Message added = messages.get(messages.size()-1);
    System.out.println("MESSAGE ID   :" + added.ID);
    System.out.println("HAS   :" + added.hash);
    System.out.println("RECIPIENT NUMBER  :" + rec);
    System.out.println("RECIPIENT USERNAME: "+ usernameUser);
    System.out.println("MESSAGE  :" + message);
    System.out.println("STATUS  :" + status);
    System.out.println("TIME  :" + added.timestamp);
    
    System.out.println("Message successfully" + status + ".");
}

//Method for discarding the last message
public static void discardLastMessage() {
    if (messages.isEmpty()) {
        System.out.println("No messages to discard.");
        return;
    }

    Message lastMessage = messages.get(messages.size() - 1);

    removeMessageFromArrays(lastMessage);

    System.out.println("Message discarded: " + lastMessage.content);
}

//Method to show message; prints full details of every message in the master list
public static void showMessage(){
     System.out.println("ALL MESSAGE");
     if(messages.isEmpty()){
          System.out.println("No messsages found.");
          return;
     }
     int sentLocalCount = 0;
     int storedLocalCount = 0;
     int disregardedLocalCount =0;
     
     for(int i = 0; i < messages.size(); i++){
         Message message = messages.get(i);
         
         switch(message.status.toLowerCase()){
             case "sent": sentLocalCount++; break;
             case "stored": storedLocalCount++; break;
             case "disregard": disregardedLocalCount++; break;
         }
          System.out.println("ID  :" + message.ID);
          System.out.println("HASH  :" + message.hash);
          System.out.println("RECIPIENT:" + message.recipient);
          System.out.println("MESSAGE :" + message.content);
          System.out.println("STATUS " + message.status);
          System.out.println("TIME :" + message.timestamp);
     }
     
      System.out.println("\nTotal:" + messages.size()
        + "I Sent:" + sentLocalCount
        + "I Stored:" + storedLocalCount
        + "I Disregard:" + disregardedLocalCount);
} 

//Prompts user to search by Message ID or Recipient Number
public static void searchMessage(){
     System.out.println("SEARCH");
     System.out.println("1. By Message ID");
     System.out.println("2. By Recipient Number");
     System.out.println("Choose:");
     String sc = input.nextLine().trim();
     ArrayList<Message> results = new ArrayList<>();
     
     if(sc.equals("1")){
          System.out.println("Enter Message ID: ");
          String sid = input.nextLine().trim();
          for(Message m : messages){
              if(m.ID.equalsIgnoreCase(sid))
                  results.add(m);
          }
     }else if(sc.equals("2")){
          System.out.print("Enter Recipient Number:");
          String srec = input.nextLine().trim();
          for(Message m : messages){
              if(m.recipient.equalsIgnoreCase(srec))
                  results.add(m);
          }
     }if(results.isEmpty()){
    System.out.println("No matching messages found.");
}else{
    System.out.println("Results (" + results.size() + "):");

    for(Message m : results){
        System.out.println("[" + m.status + "] "
                + m.recipient + " - "
                + m.content);
    }
}
}

//Prompts for a message ID, confirms with the user, then deletes it
public static void deleteMessageByld(){
    if(messages.isEmpty()){
         System.out.println("INFO No messages ot delete.");
         return;
    }
     System.out.print("Enter Message ID to delete:");
     String did = input.nextLine().trim();
     Message target = findById(did);
     if(target == null){
          System.out.println("[ERROR] No message found with ID:" + did);
          return;
     }
      System.out.println("'Found: [" + target.status +"]" + target.content);
      System.out.print("Confirm delete? (yes/no):");
      if(input.nextLine().trim().equalsIgnoreCase("yes")){
          removeMessageFromArrays(target);
          System.out.println("SUCCESS Message deleted.");
      }else{
          System.out.println("Deletion cancelled.");
      }
}

//Search for a message by its ID field
public static Message findById(String ID){
    for(Message m : messages){
        if(m.ID.equalsIgnoreCase(ID))
            return m;
    }
    return null;
}

//Search for a message by its hash field
public static Message findByHash(String hash){
    for(Message m : messages){
        if(m.hash.equalsIgnoreCase(hash)) 
            return m;
    }
    return null;
}

//Displays the Stored Messages submenu and handles option selection
public static void storedMessage(){
    boolean inSubMenu = true;
    while(inSubMenu){
        
         System.out.println("\n.......STORED MESSAGES MENU.......");
          System.out.println("a. Display sender and recipient of all stored message");
          System.out.println("b. Display the longest stored message");
          System.out.println("c. Search for a message by message ID");
          System.out.println("d. Search message by recipient number");
          System.out.println("e. Delete a message by using message hash");
          System.out.println("f. Display full message report");
          System.out.println("o. Back to main menu");
          
          System.out.print("Choose (a-f or o):  ");
          String ch = input.nextLine().trim().toLowerCase();
          switch(ch){
              case "a": displayStoredSenderRecipient(); break;
              case "b": displayLongestMessageID(); break;
              case "c": searchByMessageID(); break;
              case "d": searchByRecipient(); break;
              case "e": deleteByHash(); break;
              case "f": displayFullReport(); break;
              case "o": inSubMenu = false; break;
              default: System.out.println("Invalid option.");
          }
    }
}

public static void displayStoredSenderRecipient(){
    System.out.println("\n~~~~Stored Messages: Recipient and Preview~~~~");
    boolean found = false;
    for(Message m: messages){
        if(m.status.equals("Stored")){
            String preview = m.content.length()> 40
                    ? m.content.substring(0, 40) + "...": m.content;
            System.out.println("Recipient:" + m.recipient + "I Message:" + preview);
            found = true;
        }
    }
    
    if(!found){
        System.out.println("No stored messages found.");
    }
}

public static String displayLongestMessageID(){
    String longest = "";
    for(Message m : messages){
        if(m.content.length()> longest.length()){
            longest = m.content;
        }
    }
    if(longest.isEmpty()){
       System.out.println("No messages available."); 
    }else{
        System.out.println("\n~~~ Longest Message ~~~");
        System.out.println(longest);
    }
    return longest;
}

public static String searchByMessageID(){
    System.out.print("Enter Message ID or Developer Number to search:");
    return searchByMessageIDLogic(input.nextLine().trim());
}

public static String searchByMessageIDLogic(String query){
    for(Message m : messages){
        if(m.ID.equalsIgnoreCase(query)|| m.recipient.equalsIgnoreCase(query)){
           System.out.println("Recipient:" + m.recipient);
           System.out.println("Message :" + m.content);
           return m.content;
        }
    }
    System.out.println("No message found for:" + query);
    return "";
}

public static ArrayList<String> searchByRecipient(){
    System.out.print("Enter Recipient Number:");
    return searchByRecipientLogic(input.nextLine().trim());
}

public static ArrayList<String> searchByRecipientLogic(String recipient){
    ArrayList<String> results = new
        ArrayList<>();
    System.out.println("\n~~~~ Messages for recipient:" + recipient + "~~~~~");
    for(Message m : messages){
        if(m.recipient.equalsIgnoreCase(recipient)){
            System.out.println("[" + m.status +"]" + m.content);
            results.add(m.content);
        }
    }
    if(results.isEmpty()){
        System.out.println("No messages found for this recipient.");
    }
    return results;
}

public static boolean deleteByHash(){
    System.out.print("Enter Message Hash to delete:");
    return deleteByHashLogic(input.nextLine().trim());
}

public static boolean deleteByHashLogic(String hash){
    Message target = findByHash(hash);
    if(target == null){
      System.out.println("No message found with hash:" + hash);
      return false;
    }
    System.out.println("Deleting:\""+ target.content + "\"");
    removeMessageFromArrays(target);
    System.out.println("Message \"" + target.content + "\" successfully deleted.");
    return true;
}

public static void displayFullReport(){
    System.out.println("\n....... FULL MESSAGE REPORT ........");
    if(messages.isEmpty()){
        System.out.println("No messages available.");
        return;
    }
    System.out.printf("%-20s %-16s %-10s %s%n", "Message Hash", "Recipient", "Status", "Message");
    for(Message m : messages){
        String preview = m.content.length()> 35
                ? m.content.substring(0, 35) + "...": m.content;
        System.out.printf("%-20s %-16s %-10s %s%n", m.hash, m.content, m.status, preview);
    }
    System.out.println("Sent:" + sentMessages.size() + "I Stored:" + storedMessages.size() + "I Disregarded:" + disregardMessages.size());
}

//Into the storedMessages parallel array. No exteranl JSON library is needed.
public static void readJsonIntoArray(){
    System.out.println("\n~~~~~Reading JSON file into array~~~~~");
    ArrayList<String> loadedFromFile = new ArrayList<>();
    try(BufferedReader reader = new BufferedReader(new FileReader(JSON_FILE))){
        String line;
        String currentMessage = null;
        
        while((line = reader.readLine())!= null){
            line = line.trim();
            
            if(line.startsWith("\"message\"")){
                int colonlndex = line.indexOf(":");
                if(colonlndex != -1){
                   String valueSection = line.substring(colonlndex + 1).trim();
                    
                   if(valueSection.startsWith("\"")){
                    valueSection = valueSection.substring(1);
                }
                   if(valueSection.endsWith("\",")){
                      valueSection = valueSection.substring(0, valueSection.length() - 2); 
                   }else if(valueSection.endsWith("\"")){
                    valueSection = valueSection.substring(0, valueSection.length() - 1);
                }
                   currentMessage = valueSection;
            }
        }
            
            if(line.startsWith("}")&& currentMessage != null){
                loadedFromFile.add(currentMessage);
                if(! storedMessages.contains(currentMessage)){
                    
                    storedMessages.add(currentMessage);
                }
                currentMessage = null;
            }
    }
        if(loadedFromFile.isEmpty()){
            System.out.println("JSON file is empty or contains no messages.");
        }else{
            System.out.println("SUCCESS" + loadedFromFile.size() + "message(s) loaded from" + JSON_FILE + ":");
            for(int i = 0; i < loadedFromFile.size(); i++){
                System.out.println("[" + (i + 1) + "]" + loadedFromFile.get(i));
            }
        }
}catch(IOException e){
    System.out.println("Could not read'" + JSON_FILE + "'. Please save messages first(option 5 -> yes).");
    
}
}

//So that readJsonIntoArray() can correctly parse the file back
public static void storeMessage(){
    if(messages.isEmpty()){
        System.out.println("[INFO] No messages to save.");
        return;
    }
    try(FileWriter fw = new FileWriter(JSON_FILE)){
        fw.write("[\n");
        for(int i = 0; i < messages.size(); i++){
            Message m = messages.get(i);
            fw.write("{\n");
            fw.write("  \"ID\":\""  + m.ID + "\",\n");
            fw.write("  \"hash\":\""  + m.hash  +"\",\n");
            fw.write("  \"recipient\":\"" + m.recipient + "\",\n");
            fw.write("  \"message\":\"" + m.content + "\",\n");
            fw.write("  \"status\":\"" + m.status + "\",\n");
            fw.write("  \"time\":\"" + m.timestamp + "\"\n");
            fw.write("}" + (i < messages.size() - 1 ? ",":"") + "\n");
        }
        fw.write("]");
        System.out.println("SUCCESS" + messages.size() + "message(s) saved to" + JSON_FILE + ".");
    }catch(IOException e){
        System.out.println("Could not write file:" + e.getMessage());
    }
}

//Method to save and exit 
public static void saveAndExit(){
    String save;
    while(true){
        System.out.print("Save before exit? (yes/no):"); 
        
        save = input.nextLine();
        
        if(save.equalsIgnoreCase("yes")){
            
            storeMessage();
            
            System.out.println("SUCCESS Messages saved before exit.");
            break;
        }else if(save.equalsIgnoreCase("no")){
            break;
        }else{
            System.out.println("Invalid input! Please type 'yes' or 'no'.");
        }
    }
    System.out.println("Total messages sent:" + sentCount);
    
    System.out.println("Goodbye");
}

}
            
            


 