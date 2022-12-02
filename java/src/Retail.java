/*
 * Template JAVA User Interface
 * =============================
 *
 * Database Management Systems
 * Department of Computer Science &amp; Engineering
 * University of California - Riverside
 *
 * Target DBMS: 'Postgres'
 *
 */


import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.io.File;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;
import java.util.ArrayList;
import java.lang.Math;

/**
 * This class defines a simple embedded SQL utility class that is designed to
 * work with PostgreSQL JDBC drivers.
 *
 */
public class Retail {
   public String userId;
   // reference to physical database connection.
   private Connection _connection = null;

   // handling the keyboard inputs through a BufferedReader
   // This variable can be global for convenience.
   static BufferedReader in = new BufferedReader(
                                new InputStreamReader(System.in));

   /**
    * Creates a new instance of Retail shop
    *
    * @param hostname the MySQL or PostgreSQL server hostname
    * @param database the name of the database
    * @param username the user name used to login to the database
    * @param password the user login password
    * @throws java.sql.SQLException when failed to make a connection.
    */
   public Retail(String dbname, String dbport, String user, String passwd) throws SQLException {

      System.out.print("Connecting to database...");
      try{
         // constructs the connection URL
         String url = "jdbc:postgresql://localhost:" + dbport + "/" + dbname;
         System.out.println ("Connection URL: " + url + "\n");

         // obtain a physical connection
         this._connection = DriverManager.getConnection(url, user, passwd);
         System.out.println("Done");
      }catch (Exception e){
         System.err.println("Error - Unable to Connect to Database: " + e.getMessage() );
         System.out.println("Make sure you started postgres on this machine");
         System.exit(-1);
      }//end catch
   }//end Retail

   // Method to calculate euclidean distance between two latitude, longitude pairs. 
   public double calculateDistance (double lat1, double long1, double lat2, double long2){
      double t1 = (lat1 - lat2) * (lat1 - lat2);
      double t2 = (long1 - long2) * (long1 - long2);
      return Math.sqrt(t1 + t2); 
   }
   /**
    * Method to execute an update SQL statement.  Update SQL instructions
    * includes CREATE, INSERT, UPDATE, DELETE, and DROP.
    *
    * @param sql the input SQL string
    * @throws java.sql.SQLException when update failed
    */
   public void executeUpdate (String sql) throws SQLException {
      // creates a statement object
      Statement stmt = this._connection.createStatement ();

      // issues the update instruction
      stmt.executeUpdate (sql);

      // close the instruction
      stmt.close ();
   }//end executeUpdate

   /**
    * Method to execute an input query SQL instruction (i.e. SELECT).  This
    * method issues the query to the DBMS and outputs the results to
    * standard out.
    *
    * @param query the input query string
    * @return the number of rows returned
    * @throws java.sql.SQLException when failed to execute the query
    */
   public int executeQueryAndPrintResult (String query) throws SQLException {
      // creates a statement object
      Statement stmt = this._connection.createStatement ();

      // issues the query instruction
      ResultSet rs = stmt.executeQuery (query);

      /*
       ** obtains the metadata object for the returned result set.  The metadata
       ** contains row and column info.
       */
      ResultSetMetaData rsmd = rs.getMetaData ();
      int numCol = rsmd.getColumnCount ();
      int rowCount = 0;

      // iterates through the result set and output them to standard out.
      boolean outputHeader = true;
      while (rs.next()){
		 if(outputHeader){
			for(int i = 1; i <= numCol; i++){
			System.out.print(rsmd.getColumnName(i) + "\t");
			}
			System.out.println();
			outputHeader = false;
		 }
         for (int i=1; i<=numCol; ++i)
            System.out.print (rs.getString (i) + "\t");
         System.out.println ();
         ++rowCount;
      }//end while
      stmt.close ();
      return rowCount;
   }//end executeQuery

   /**
    * Method to execute an input query SQL instruction (i.e. SELECT).  This
    * method issues the query to the DBMS and returns the results as
    * a list of records. Each record in turn is a list of attribute values
    *
    * @param query the input query string
    * @return the query result as a list of records
    * @throws java.sql.SQLException when failed to execute the query
    */
   public List<List<String>> executeQueryAndReturnResult (String query) throws SQLException {
      // creates a statement object
      Statement stmt = this._connection.createStatement ();

      // issues the query instruction
      ResultSet rs = stmt.executeQuery (query);

      /*
       ** obtains the metadata object for the returned result set.  The metadata
       ** contains row and column info.
       */
      ResultSetMetaData rsmd = rs.getMetaData ();
      int numCol = rsmd.getColumnCount ();
      int rowCount = 0;

      // iterates through the result set and saves the data returned by the query.
      boolean outputHeader = false;
      List<List<String>> result  = new ArrayList<List<String>>();
      while (rs.next()){
        List<String> record = new ArrayList<String>();
		for (int i=1; i<=numCol; ++i)
			record.add(rs.getString (i));
        result.add(record);
      }//end while
      stmt.close ();
      return result;
   }//end executeQueryAndReturnResult

   /**
    * Method to execute an input query SQL instruction (i.e. SELECT).  This
    * method issues the query to the DBMS and returns the number of results
    *
    * @param query the input query string
    * @return the number of rows returned
    * @throws java.sql.SQLException when failed to execute the query
    */
   public int executeQuery (String query) throws SQLException {
       // creates a statement object
       Statement stmt = this._connection.createStatement ();

       // issues the query instruction
       ResultSet rs = stmt.executeQuery (query);

       int rowCount = 0;

       // iterates through the result set and count nuber of results.
       while (rs.next()){
          rowCount++;
       }//end while
       stmt.close ();
       return rowCount;
   }

   /**
    * Method to fetch the last value from sequence. This
    * method issues the query to the DBMS and returns the current
    * value of sequence used for autogenerated keys
    *
    * @param sequence name of the DB sequence
    * @return current value of a sequence
    * @throws java.sql.SQLException when failed to execute the query
    */
   public int getCurrSeqVal(String sequence) throws SQLException {
	Statement stmt = this._connection.createStatement ();

	ResultSet rs = stmt.executeQuery (String.format("Select currval('%s')", sequence));
	if (rs.next())
		return rs.getInt(1);
	return -1;
   }

   /**
    * Method to close the physical connection if it is open.
    */
   public void cleanup(){
      try{
         if (this._connection != null){
            this._connection.close ();
         }//end if
      }catch (SQLException e){
         // ignored.
      }//end try
   }//end cleanup

   /**
    * The main execution method
    *
    * @param args the command line arguments this inclues the <mysql|pgsql> <login file>
    */
   public static void main (String[] args) {
      if (args.length != 3) {
         System.err.println (
            "Usage: " +
            "java [-classpath <classpath>] " +
            Retail.class.getName () +
            " <dbname> <port> <user>");
         return;
      }//end if

      Greeting();
      Retail esql = null;
      try{
         // use postgres JDBC driver.
         Class.forName ("org.postgresql.Driver").newInstance ();
         // instantiate the Retail object and creates a physical
         // connection.
         String dbname = args[0];
         String dbport = args[1];
         String user = args[2];
         esql = new Retail (dbname, dbport, user, "");

         boolean keepon = true;
         while(keepon) {
            // These are sample SQL statements
            System.out.println("MAIN MENU");
            System.out.println("---------");
            System.out.println("1. Create user");
            System.out.println("2. Log in");
            System.out.println("9. < EXIT");
            String authorisedUser = null;
            switch (readChoice()){
               case 1: CreateUser(esql); break;
               case 2: authorisedUser = LogIn(esql); break;
               case 9: keepon = false; break;
               default : System.out.println("Unrecognized choice!"); break;
            }//end switch
            if (authorisedUser != null) {
              boolean usermenu = true;
              while(usermenu) {
                System.out.println('\n' + "MAIN MENU");
                System.out.println("---------");
                System.out.println("1. View Stores within 30 miles");
                System.out.println("2. View Product List");
                System.out.println("3. Place a Order");
                System.out.println("4. View 5 recent orders");

                //the following functionalities basically used by managers
                System.out.println("5. Update Product");
                System.out.println("6. View 5 recent Product Updates Info");
                System.out.println("7. View 5 Popular Items");
                System.out.println("8. View 5 Popular Customers");
                System.out.println("9. Place Product Supply Request to Warehouse");
                System.out.println("10. View All Order Information");
                System.out.println("11. View All Product Supply Requests");

                //the following functionalities basically used by admin
                System.out.println("12. View All User Information");
                System.out.println("13. View All Product Information");
                System.out.println("14. Update User Information");
                System.out.println("15. Update Product Information");

                System.out.println(".........................");
                System.out.println("20. Log out");
                switch (readChoice()){
                   case 1: viewStores(esql); break;
                   case 2: viewProducts(esql); break;
                   case 3: placeOrder(esql); break;
                   case 4: viewRecentOrders(esql); break;
                   case 5: updateProduct(esql); break;
                   case 6: viewRecentUpdates(esql); break;
                   case 7: viewPopularProducts(esql); break;
                   case 8: viewPopularCustomers(esql); break;
                   case 9: placeProductSupplyRequests(esql); break;
                   case 10: viewAllOrderInformation(esql); break;
                   case 11: viewAllProductSupplyRequests(esql); break;
                   case 12: viewAllUserInformation(esql); break;
                   case 13: viewAllProductInformation(esql); break;
                   case 14: updateUserInformation(esql); break;
                   case 15: updateProductInformation(esql); break;

                   case 20: usermenu = false; break;
                   default : System.out.println("Unrecognized choice!"); break;
                }
              }
            }
         }//end while
      }catch(Exception e) {
         System.err.println (e.getMessage ());
      }finally{
         // make sure to cleanup the created table and close the connection.
         try{
            if(esql != null) {
               System.out.print("Disconnecting from database...");
               esql.cleanup ();
               System.out.println("Done\n\nBye !");
            }//end if
         }catch (Exception e) {
            // ignored.
         }//end try
      }//end try
   }//end main

   public static void Greeting(){
      System.out.println(
         "\n\n*******************************************************\n" +
         "              User Interface      	               \n" +
         "*******************************************************\n");
   }//end Greeting

   /*
    * Reads the users choice given from the keyboard
    * @int
    **/
   public static int readChoice() {
      int input;
      // returns only if a correct value is given.
      do {
         System.out.print("Please make your choice: ");
         try { // read the integer, parse it and break.
            input = Integer.parseInt(in.readLine());
            break;
         }catch (Exception e) {
            System.out.println("Your input is invalid!");
            continue;
         }//end try
      }while (true);
      return input;
   }//end readChoice

   /*
    * Creates a new user
    **/
   public static void CreateUser(Retail esql){
      try{
         System.out.print("\tEnter name: ");
         String name = in.readLine();
         System.out.print("\tEnter password: ");
         String password = in.readLine();
         System.out.print("\tEnter latitude: ");   
         String latitude = in.readLine();       //enter lat value between [0.0, 100.0]
         System.out.print("\tEnter longitude: ");  //enter long value between [0.0, 100.0]
         String longitude = in.readLine();
         
         String type="Customer";

			String query = String.format("INSERT INTO USERS (name, password, latitude, longitude, type) VALUES ('%s','%s', %s, %s,'%s')", name, password, latitude, longitude, type);

         esql.executeUpdate(query);
         System.out.println ("User successfully created!");
      }catch(Exception e){
         System.err.println (e.getMessage ());
      }
   }//end CreateUser


   /*
    * Check log in credentials for an existing user
    * @return User login or null is the user does not exist
    **/
   public static String LogIn(Retail esql){
      try{
         System.out.print("\tEnter name: ");
         String name = in.readLine();
         System.out.print("\tEnter user id: ");
         String userID = in.readLine();
         System.out.print("\tEnter password: ");
         String password = in.readLine();

         esql.userId = userID;

         String query = String.format("SELECT * FROM USERS WHERE name = '%s' AND userID = '%s' AND password = '%s'", name, userID, password);
         int userNum = esql.executeQuery(query);

	 if (userNum > 0)
		return name;
         return null;
      }catch(Exception e){
         System.err.println (e.getMessage ());
         return null;
      }
   }//end

   public static String checkManager(Retail esql){
      try{
         System.out.print("Enter Manager ID: ");
         String managerID = in.readLine();

         String query = String.format("SELECT U.type FROM USERS U WHERE U.userID = '%s' AND U.type = 'manager' OR U.type = 'admin'", managerID);
         int userNum = esql.executeQuery(query);
	 if (userNum > 0)
		return managerID;
         return null;
      }catch(Exception e){
         System.out.print("\tERROR: Not A Manager ID");
         return null;
      }
   }//end

   public static String checkAdmin(Retail esql){
      try{
         System.out.print("Enter Admin ID: ");
         String adminID = in.readLine();

         String query = String.format("SELECT U.type FROM USERS U WHERE U.userID = '%s' AND U.type = 'admin'", adminID);
         int userNum = esql.executeQuery(query);
	 if (userNum > 0)
		return adminID;
         return null;
      }catch(Exception e){
         System.out.print("\tERROR: Not A Manager ID");
         return null;
      }
   }//end

   public static String store_belongs_manager(Retail esql){
      try{
         System.out.print("Enter Store ID: ");
         String storeID = in.readLine();

         String query = String.format("SELECT S.storeID FROM Store S WHERE S.storeID = '%s' AND S.managerID = '%s'", storeID, esql.userId);
         int userNum = esql.executeQuery(query);
	 if (userNum > 0)
		return storeID;
         return null;
      }catch(Exception e){
         System.out.print("\tERROR: Not A Manager ID");
         return null;
      }
   }//end

   public static String checkManager_Admin(Retail esql){
      try{
         String query = String.format("SELECT U.type FROM USERS U WHERE U.userID = '%s' AND U.type = 'manager' OR U.type = 'admin'", esql.userId);
         int userNum = esql.executeQuery(query);
	 if (userNum > 0)
		return esql.userId;
         return null;
      }catch(Exception e){
         System.out.print("\tERROR: Not A Manager ID");
         return null;
      }
   }//end

// Rest of the functions definition go in here

   public static void viewStores(Retail esql) {
      try{
      String query = String.format("select s.storeID, s.name, calculate_distance(u.latitude, u.longitude, s.latitude, s.longitude) as dist from users u, store s where u.userID = '%s' and calculate_distance(u.latitude, u.longitude, s.latitude, s.longitude) < 30", esql.userId);
      
      int rowCount = esql.executeQuery(query);
      esql.executeQueryAndPrintResult(query);
      System.out.println ("total row(s): " + rowCount);
      }
      catch(Exception e){
         System.err.println (e.getMessage ());
      }
   }
   public static void viewProducts(Retail esql) {
      try{
	      String query = "SELECT * FROM Product Where storeID = ";
         System.out.print("Enter Store ID: ");
         String input = in.readLine();
         query += input;

         int rowCount = esql.executeQuery(query);
         esql.executeQueryAndPrintResult(query);
         System.out.println ("total row(s): " + rowCount);
      }catch(Exception e){
      	System.err.println (e.getMessage());
      }
   }
   public static void placeOrder(Retail esql) {
      try{
         String authorisedUser = checkManager_Admin(esql);
         if(authorisedUser != null){
            System.out.println("Must be logged in as a customer!");
            return;
         }
         boolean found = false;
         System.out.print("\tEnter StoreID: ");
         String storeID = in.readLine();
         System.out.print("\tEnter Product Name: ");
         String proName = in.readLine();
         System.out.print("\tEnter # of Units: ");
         String unitSize = in.readLine();
         int uSize = Integer.parseInt(unitSize);
         int uID = Integer.parseInt(esql.userId);
         String query = String.format("select s.storeID, s.name, calculate_distance(u.latitude, u.longitude, s.latitude, s.longitude) as dist from users u, store s where u.userId = '%s' and calculate_distance(u.latitude, u.longitude, s.latitude, s.longitude) < 30", uID);
         List<List<String>> result = esql.executeQueryAndReturnResult(query);
         int size = result.size();
         for(int i =0; i < size;i++){
         if(result.get(i).contains(storeID)){
                int sID = Integer.parseInt(storeID);
                String query2 =  String.format("select numberOfUnits from product where storeId = '%d' AND productName = '%s'", sID,proName);
                List<List<String>> res = esql.executeQueryAndReturnResult(query2);
                int remain = Integer.parseInt(res.get(0).get(0)) - uSize;
                if(remain >= 0){
                query = String.format("INSERT INTO ORDERS (customerID, storeID, productName, unitsOrdered,orderTime) VALUES ('%d', '%d', '%s','%d',NOW())", uID,sID, proName,uSize);
                esql.executeUpdate(query);
                query = String.format("UPDATE PRODUCT SET numberOfUnits = '%d' WHERE productName = '%s' AND storeID = '%d'",remain,proName,sID);
                esql.executeUpdate(query);
                }
                else{
                System.out.print("Not enough inventory in store!");
                }
                found = true;
                break;
         }
        }
        if(!found){
                System.out.print("Store not in range");
        }
        }
        catch(Exception e){
                System.err.println (e.getMessage ());
        }
   }
   public static void viewRecentOrders(Retail esql) {
      try{
         String authorisedUser = checkManager_Admin(esql);
         if(authorisedUser != null){
            System.out.println("Must be logged in as a customer!");
            return;
         }
	      String query = String.format("SELECT O.storeID, S.name, O.productName, O.unitsOrdered, O.orderTime FROM Users U, Store S, Orders O WHERE U.userID= '%s' AND U.userID=O.customerID AND S.storeID=O.storeID ORDER BY O.orderTime DESC LIMIT 5", esql.userId);
         
         int rowCount = esql.executeQuery(query);
         esql.executeQueryAndPrintResult(query);
         System.out.println ("total row(s): " + rowCount);
      }catch(Exception e){
      	System.err.println (e.getMessage());
      }
   }
   public static void updateProduct(Retail esql) {
      try{    
               String authorisedUser = "";
               String storeID = "";
               System.out.println('\n' + "OPTIONS");
               System.out.println("-------");
               System.out.println("1. Manager");
               System.out.println("2. Admin");
               System.out.println("3. Cancel");
               switch (readChoice()){
                   case 1: authorisedUser = checkManager(esql);
                           if(authorisedUser == null){
                                    System.out.print("ERROR: Not A Manager ID\n\n");
                                    return;
                           }
                           if(!authorisedUser.equals(esql.userId)){
                                    System.out.print("ERROR: Not Correct Manager ID\n\n");
                                    return;
                           }
                           
                           storeID = store_belongs_manager(esql);
                           if(storeID == null){
                                    System.out.print("ERROR: Invalid Store ID\n\n");
                                    return;
                           }; 
                           break;
                   case 2: authorisedUser = checkAdmin(esql);
                           if(authorisedUser == null){
                              System.out.print("ERROR: Not An Admin ID\n\n");
                              return;
                           }
                           if(!authorisedUser.equals(esql.userId)){
                              System.out.print("ERROR: Not Correct Admin ID\n\n");
                              return;
                           }
                           System.out.print("\tEnter StoreID: ");
                           storeID = in.readLine();
                           break;
                   case 3: return;
                   default : System.out.println("Unrecognized choice!"); break;
                }
                
                System.out.print("\tEnter Product Name: ");
                String proName = in.readLine();
                System.out.print("\tEnter # of Units: ");
                String unitSize = in.readLine();
                System.out.print("\tEnter cost: ");
                String unitCost = in.readLine();
                int sID = Integer.parseInt(storeID);
                int mID = Integer.parseInt(authorisedUser);
                int uSize = Integer.parseInt(unitSize);
                int uCost = Integer.parseInt(unitCost);
                String query = String.format("UPDATE PRODUCT SET numberOfUnits = '%d', pricePerUnit = '%d' WHERE productName = '%s' AND storeID = '%d'",uSize,uCost,proName,sID); 
                esql.executeUpdate(query);
                String query2 = String.format("INSERT INTO PRODUCTUPDATES (managerID,storeID,productName,updatedOn) VALUES ('%d','%d','%s',NOW())",mID,sID,proName);                      
                esql.executeUpdate(query2);
        }
        catch(Exception e){
                System.err.println (e.getMessage ());
        }
   }
   public static void viewRecentUpdates(Retail esql) {
      try{
            String authorisedUser = "";
            String storeID = "";
            System.out.println('\n' + "OPTIONS");
            System.out.println("-------");
            System.out.println("1. Manager");
            System.out.println("2. Admin");
            System.out.println("3. Cancel");
            switch (readChoice()){
                  case 1: authorisedUser = checkManager(esql);
                        if(authorisedUser == null){
                                 System.out.print("ERROR: Not A Manager ID\n\n");
                                 return;
                        }
                        if(!authorisedUser.equals(esql.userId)){
                                 System.out.print("ERROR: Not Correct Manager ID\n\n");
                                 return;
                        }
                        
                        storeID = store_belongs_manager(esql);
                        if(storeID == null){
                                 System.out.print("ERROR: Invalid Store ID\n\n");
                                 return;
                        }; 
                        break;
                  case 2: authorisedUser = checkAdmin(esql);
                        if(authorisedUser == null){
                           System.out.print("ERROR: Not An Admin ID\n\n");
                           return;
                        }
                        if(!authorisedUser.equals(esql.userId)){
                           System.out.print("ERROR: Not Correct Admin ID\n\n");
                           return;
                        }
                        System.out.print("\tEnter StoreID: ");
                        storeID = in.readLine();
                        break;
                  case 3: return;
                  default : System.out.println("Unrecognized choice!"); break;
               }

	      String query = String.format("SELECT P.updateNumber, P.managerID, P.storeID, P.productName, P.updatedOn FROM ProductUpdates P, Users U WHERE U.userID=P.managerID AND P.storeID = '%s' ORDER BY P.updatedOn DESC LIMIT 5", storeID);
         
         int rowCount = esql.executeQuery(query);
         esql.executeQueryAndPrintResult(query);
         System.out.println ("total row(s): " + rowCount);
      }catch(Exception e){
      	System.err.println (e.getMessage());
      }
   }
   public static void viewPopularProducts(Retail esql) {
      try{
            String authorisedUser = "";
            String storeID = "";
            System.out.println('\n' + "OPTIONS");
            System.out.println("-------");
            System.out.println("1. Manager");
            System.out.println("2. Admin");
            System.out.println("3. Cancel");
            switch (readChoice()){
                  case 1: authorisedUser = checkManager(esql);
                        if(authorisedUser == null){
                                 System.out.print("ERROR: Not A Manager ID\n\n");
                                 return;
                        }
                        if(!authorisedUser.equals(esql.userId)){
                                 System.out.print("ERROR: Not Correct Manager ID\n\n");
                                 return;
                        }
                        
                        storeID = store_belongs_manager(esql);
                        if(storeID == null){
                                 System.out.print("ERROR: Invalid Store ID\n\n");
                                 return;
                        }; 
                        break;
                  case 2: authorisedUser = checkAdmin(esql);
                        if(authorisedUser == null){
                           System.out.print("ERROR: Not An Admin ID\n\n");
                           return;
                        }
                        if(!authorisedUser.equals(esql.userId)){
                           System.out.print("ERROR: Not Correct Admin ID\n\n");
                           return;
                        }
                        System.out.print("\tEnter StoreID: ");
                        storeID = in.readLine();
                        break;
                  case 3: return;
                  default : System.out.println("Unrecognized choice!"); break;
               }

		 String query = String.format("SELECT productName,COUNT(*) AS Orders_Made FROM ORDERS WHERE storeID = '%s' GROUP BY productName ORDER BY COUNT(*) DESC LIMIT 5", storeID);

       int rowCount = esql.executeQuery(query);
       esql.executeQueryAndPrintResult(query);
       System.out.println ("total row(s): " + rowCount);		
      }
      catch(Exception e){
         System.err.println (e.getMessage ());
      }
   }
   public static void viewPopularCustomers(Retail esql) {
      try{
            String authorisedUser = "";
            String storeID = "";
            System.out.println('\n' + "OPTIONS");
            System.out.println("-------");
            System.out.println("1. Manager");
            System.out.println("2. Admin");
            System.out.println("3. Cancel");
            switch (readChoice()){
                  case 1: authorisedUser = checkManager(esql);
                        if(authorisedUser == null){
                                 System.out.print("ERROR: Not A Manager ID\n\n");
                                 return;
                        }
                        if(!authorisedUser.equals(esql.userId)){
                                 System.out.print("ERROR: Not Correct Manager ID\n\n");
                                 return;
                        }
                        
                        storeID = store_belongs_manager(esql);
                        if(storeID == null){
                                 System.out.print("ERROR: Invalid Store ID\n\n");
                                 return;
                        }; 
                        break;
                  case 2: authorisedUser = checkAdmin(esql);
                        if(authorisedUser == null){
                           System.out.print("ERROR: Not An Admin ID\n\n");
                           return;
                        }
                        if(!authorisedUser.equals(esql.userId)){
                           System.out.print("ERROR: Not Correct Admin ID\n\n");
                           return;
                        }
                        System.out.print("\tEnter StoreID: ");
                        storeID = in.readLine();
                        break;
                  case 3: return;
                  default : System.out.println("Unrecognized choice!"); break;
               }

	      String query = String.format("SELECT O.storeID, U.name, O.customerID, COUNT(*) AS Orders_Made FROM Users U, Store S, Orders O WHERE U.userID=O.customerID AND S.storeID=O.storeID AND O.storeID = '%s' GROUP BY O.customerID, O.storeID, U.name ORDER BY COUNT(*) DESC LIMIT 5", storeID);
         
         int rowCount = esql.executeQuery(query);
         esql.executeQueryAndPrintResult(query);
         System.out.println ("total row(s): " + rowCount);
      }catch(Exception e){
      	System.err.println (e.getMessage());
      }
   }
   public static void placeProductSupplyRequests(Retail esql) {
      try{
               String authorisedUser = "";
               String storeID = "";
               System.out.println('\n' + "OPTIONS");
               System.out.println("-------");
               System.out.println("1. Manager");
               System.out.println("2. Admin");
               System.out.println("3. Cancel");
               switch (readChoice()){
                   case 1: authorisedUser = checkManager(esql);
                           if(authorisedUser == null){
                                    System.out.print("ERROR: Not A Manager ID\n\n");
                                    return;
                           }
                           if(!authorisedUser.equals(esql.userId)){
                                    System.out.print("ERROR: Not Correct Manager ID\n\n");
                                    return;
                           }
                           
                           storeID = store_belongs_manager(esql);
                           if(storeID == null){
                                    System.out.print("ERROR: Invalid Store ID\n\n");
                                    return;
                           }; 
                           break;
                   case 2: authorisedUser = checkAdmin(esql);
                           if(authorisedUser == null){
                              System.out.print("ERROR: Not An Admin ID\n\n");
                              return;
                           }
                           if(!authorisedUser.equals(esql.userId)){
                              System.out.print("ERROR: Not Correct Admin ID\n\n");
                              return;
                           }
                           System.out.print("\tEnter StoreID: ");
                           storeID = in.readLine();
                           break;
                   case 3: return;
                   default : System.out.println("Unrecognized choice!"); break;
                }
                int sID = Integer.parseInt(storeID);
                int mID = Integer.parseInt(authorisedUser);
                System.out.print("\tEnter Product Name: ");
                String proName = in.readLine();
                System.out.print("\tEnter # of Units: ");
                String unitSize = in.readLine();
                System.out.print("\tEnter Warehouse ID: ");
                String warehouseID = in.readLine();
                int wID = Integer.parseInt(warehouseID);
                String query2 =  String.format("select numberOfUnits from product where storeId = '%d' AND productName = '%s'", sID,proName);
                int uSize = Integer.parseInt(unitSize);
                List<List<String>> res = esql.executeQueryAndReturnResult(query2);
                int updateNum = Integer.parseInt(res.get(0).get(0)) + uSize;
                String query = String.format("UPDATE PRODUCT SET numberOfUnits = '%d' WHERE productName = '%s' AND storeID = '%d'",updateNum,proName,sID);
                esql.executeUpdate(query);
                query = String.format("INSERT INTO ProductSupplyRequests (managerID, warehouseID, storeID, productName, unitsRequested) VALUES ('%d','%d', '%d', '%s','%d')", mID, wID, sID, proName, uSize);
                esql.executeUpdate(query);
        }
        catch(Exception e){
                System.err.println (e.getMessage());

        }
   }
   public static void viewAllOrderInformation(Retail esql) {
      try{
            String authorisedUser = "";
            String storeID = "";
            System.out.println('\n' + "OPTIONS");
            System.out.println("-------");
            System.out.println("1. Manager");
            System.out.println("2. Admin");
            System.out.println("3. Cancel");
            switch (readChoice()){
                  case 1: authorisedUser = checkManager(esql);
                        if(authorisedUser == null){
                                 System.out.print("ERROR: Not A Manager ID\n\n");
                                 return;
                        }
                        if(!authorisedUser.equals(esql.userId)){
                                 System.out.print("ERROR: Not Correct Manager ID\n\n");
                                 return;
                        }
                        
                        storeID = store_belongs_manager(esql);
                        if(storeID == null){
                                 System.out.print("ERROR: Invalid Store ID\n\n");
                                 return;
                        }; 
                        break;
                  case 2: authorisedUser = checkAdmin(esql);
                        if(authorisedUser == null){
                           System.out.print("ERROR: Not An Admin ID\n\n");
                           return;
                        }
                        if(!authorisedUser.equals(esql.userId)){
                           System.out.print("ERROR: Not Correct Admin ID\n\n");
                           return;
                        }
                        System.out.print("\tEnter StoreID: ");
                        storeID = in.readLine();
                        break;
                  case 3: return;
                  default : System.out.println("Unrecognized choice!"); break;
               }

	      String query = String.format("SELECT O.orderNumber, U.name, O.storeID, O.productName, O.orderTime FROM Orders O, Users U WHERE O.customerID=U.userID AND O.storeID= '%s'", storeID);
         
         int rowCount = esql.executeQuery(query);
         esql.executeQueryAndPrintResult(query);
         System.out.println ("total row(s): " + rowCount);
      }catch(Exception e){
      	System.err.println (e.getMessage());
      }
   }
   public static void viewAllProductSupplyRequests(Retail esql){
      try{
            boolean admin = false;
            String query = "";
            String authorisedUser = "";
            String storeID = "";
            System.out.println('\n' + "OPTIONS");
            System.out.println("-------");
            System.out.println("1. Manager");
            System.out.println("2. Admin");
            System.out.println("3. Cancel");
            switch (readChoice()){
                  case 1: authorisedUser = checkManager(esql);
                        if(authorisedUser == null){
                                 System.out.print("ERROR: Not A Manager ID\n\n");
                                 return;
                        }
                        if(!authorisedUser.equals(esql.userId)){
                                 System.out.print("ERROR: Not Correct Manager ID\n\n");
                                 return;
                        }
                        
                        storeID = store_belongs_manager(esql);
                        if(storeID == null){
                                 System.out.print("ERROR: Invalid Store ID\n\n");
                                 return;
                        }; 
                        break;
                  case 2: authorisedUser = checkAdmin(esql);
                        if(authorisedUser == null){
                           System.out.print("ERROR: Not An Admin ID\n\n");
                           return;
                        }
                        if(!authorisedUser.equals(esql.userId)){
                           System.out.print("ERROR: Not Correct Admin ID\n\n");
                           return;
                        }
                        admin = true;
                        break;
                  case 3: return;
                  default : System.out.println("Unrecognized choice!"); break;
               }

         if(!admin){
            query = String.format("SELECT * FROM ProductSupplyRequests WHERE storeID = '%s'", storeID);
         }
         else{
            query = String.format("SELECT * FROM ProductSupplyRequests");
         }
         
         int rowCount = esql.executeQuery(query);
         esql.executeQueryAndPrintResult(query);
         System.out.println ("total row(s): " + rowCount);
      }catch(Exception e){
      	System.err.println (e.getMessage());
      }
   }
   public static void viewAllUserInformation(Retail esql) {
      try{
         String authorisedUser = checkAdmin(esql);
         if(authorisedUser == null){
            System.out.print("ERROR: Not An Admin ID\n\n");
            return;
         }
         if(!authorisedUser.equals(esql.userId)){
            System.out.print("ERROR: Not Correct Admin ID\n\n");
            return;
         }

	      String query = String.format("SELECT * FROM Users");
         
         int rowCount = esql.executeQuery(query);
         esql.executeQueryAndPrintResult(query);
         System.out.println ("total row(s): " + rowCount);
      }catch(Exception e){
      	System.err.println (e.getMessage());
      }
   }
   public static void viewAllProductInformation(Retail esql) {
      try{
         String authorisedUser = checkAdmin(esql);
         if(authorisedUser == null){
            System.out.print("ERROR: Not An Admin ID\n\n");
            return;
         }
         if(!authorisedUser.equals(esql.userId)){
            System.out.print("ERROR: Not Correct Admin ID\n\n");
            return;
         }

	      String query = String.format("SELECT * FROM Product");
         
         int rowCount = esql.executeQuery(query);
         esql.executeQueryAndPrintResult(query);
         System.out.println ("total row(s): " + rowCount);
      }catch(Exception e){
      	System.err.println (e.getMessage());
      }
   }
   
   public static void updateUserInformation(Retail esql) {
      try{
      String authorisedUser = checkAdmin(esql);
      String query = "";
      String userID = "";
      String uName = "";
      String uPass = "";
      String userLat = "";
      String userLong = "";
      String uType = "";
      int uID = 0;
      int uLat = 0;
      int uLong = 0;
      if(authorisedUser == null){
         System.out.print("ERROR: Not An Admin ID\n\n");
         return;
      }
      System.out.println('\n' + "OPTIONS");
      System.out.println("-------");
      System.out.println("1. Update User Info");
      System.out.println("2. Remove User");
      System.out.println("3. Cancel");
      switch (readChoice()){
         case 1:
         System.out.print("Input userID to update: ");
         userID = in.readLine();
         System.out.print("Input name: ");
         uName = in.readLine();
         System.out.print("Input password: ");
         uPass = in.readLine();
         System.out.print("Input latitude: ");
         userLat = in.readLine();
         System.out.print("Input longitude: ");
         userLong = in.readLine();
         System.out.print("Input type: ");
         uType = in.readLine();
         uID = Integer.parseInt(userID);
         uLat = Integer.parseInt(userLat);
         uLong = Integer.parseInt(userLong);
         query = String.format("UPDATE USERS SET name ='%s', password ='%s', latitude ='%d', longitude ='%d', type = '%s' WHERE userID ='%d'", uName, uPass, uLat, uLong, uType,uID);
         esql.executeUpdate(query);
            break;
         case 2:
            System.out.print("Input userID to delete: ");
            userID = in.readLine();
            uID = Integer.parseInt(userID);
            query = String.format("DELETE FROM orders WHERE customerID = '%d'",uID);;
            esql.executeUpdate(query);
            query = String.format("DELETE FROM users WHERE userID = '%d'",uID);
            esql.executeUpdate(query);
            break;
         case 3: return;
         default : System.out.println("Unrecognized choice!"); break;
      }
      //query = String.format("SELECT * FROM users");
      //esql.executeQueryAndPrintResult(query);
      }
      catch(Exception e){
      System.err.println (e.getMessage());
      }
   }
   public static void updateProductInformation(Retail esql) {
      try{
      String authorisedUser = checkAdmin(esql);
      String query = "";
      String proName = "";
      String nProName = "";
      String storeID = "";
      if(authorisedUser == null){
         System.out.print("ERROR: Not An Admin ID\n\n");
         return;
      }
      System.out.println('\n' + "OPTIONS");
      System.out.println("-------");
      System.out.println("1. Add Product");
      System.out.println("2. Remove Product");
      System.out.println("3. Cancel");
      switch (readChoice()){
         case 1:
            System.out.print("Input New Product Name: ");
            proName = in.readLine();
            System.out.print("Input StoreID: ");
            storeID = in.readLine();
            System.out.print("Input numberOfUnits: ");
            String numUnits = in.readLine();
            System.out.print("Input pricePerUnit: ");
            String price = in.readLine();
            int sID = Integer.parseInt(storeID);
            int nUnits = Integer.parseInt(numUnits);
            int pri = Integer.parseInt(price);

            query = String.format("INSERT INTO PRODUCT (storeID,productName,numberOfUnits,pricePerUnit) VALUES ('%d','%s','%d','%d')",sID,proName,nUnits,pri);
            esql.executeUpdate(query);
            break;
         case 2:
            System.out.print("Input Product name to delete: ");
            proName = in.readLine();
            query = String.format("DELETE FROM Orders WHERE productName = '%s'",proName);
            esql.executeUpdate(query);
            query = String.format("DELETE FROM ProductUpdates WHERE productName = '%s'",proName);
            esql.executeUpdate(query);
            query = String.format("DELETE FROM ProductSupplyRequests  WHERE productName = '%s'",proName);
            esql.executeUpdate(query);
            query = String.format("DELETE FROM Product WHERE productName = '%s'",proName);
            esql.executeUpdate(query);
            break;
         case 3: return;
         default : System.out.println("Unrecognized choice!"); break;
      }
      //query = String.format("SELECT * FROM users");
      //esql.executeQueryAndPrintResult(query);
      }
      catch(Exception e){
      System.err.println (e.getMessage());
      }
   }

}//end Retail

