package melardev.launchers;

import com.mongodb.*;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Sorts;
import com.mongodb.client.model.Updates;
import org.bson.Document;
import org.h2.tools.Server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.*;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.Consumer;

public class JdbcLauncher {


    public static void main(String[] args) {
        //createDatabaseAndTable();
        //insertAndUpdateDemo();
        //savePointDemo();
        //navigateResultSetDemo();
        //preparedStatementsDemo();
        //readFromDbDemo();
        //navigateResultSetDemo();
        //textBasedFileDemo();

        //readFromDbDemo();
        //saveFileDemo();
        h2Demo();
        //mongoDemo();
        //derbyEmbeddedDemo();
    }

    private static void mongoDemo() {
        MongoClient mongoClient = null;
        try {

            // Connect

            //MongoCredential credential = MongoCredential.createCredential("", "", "".toCharArray());
            // MongoClient mongoClient = new MongoClient(new ServerAddress("localhost", 27017), Arrays.asList(credential));
            // Connect to mongodb server on localhost
            mongoClient = new MongoClient("localhost", 27017);
            MongoDatabase db = mongoClient.getDatabase("mongo_jdbc");
            // Insert

            MongoCollection<Document> usersCollection = db.getCollection("users");
            //usersCollection.createIndex(Indexes.ascending("first_name"));
            Document doc1 = new Document("first_name", "Colleen")
                    .append("last_name", "Evans")
                    .append("age", 21);

            usersCollection.insertOne(doc1);

            Document doc2 = new Document("first_name", "Carla")
                    .append("last_name", "Masson")
                    .append("age", 29)
                    .append("social", new BasicDBObject("email", "carla@email.com")
                            .append("twitter", "@carlalikesmongol"));

            //usersCollection.insertOne(doc2);

            Document doc3 = new Document().append("first_name", "Melar")
                    .append("last_name", "Dev")
                    .append("age", 25)
                    .append("social", new Document("facebook", "Melar Dev")
                            .append("twitter", "@melardev")
                            .append("youtube", "Melardev"));

            BasicDBList socialList = new BasicDBList();
            socialList
                    .addAll(Arrays.asList(
                            new Document("name", "Blog")
                                    .append("official_url", "http://melardev.com/eng")
                                    .append("user_url", "")
                                    .append("username", "Melardev"),
                            new Document("name", "facebook")
                                    .append("official_url", "https://www.facebook.com")
                                    .append("user_url", "https://www.facebook.com/melardev")
                                    .append("username", "Melar Dev"),
                            new Document("name", "youtube")
                                    .append("official_url", "https://www.youtube.com")
                                    .append("user_url", "https://www.youtube.com/melardev")
                                    .append("username", "Melardev"),
                            new Document("name", "instagram")
                                    .append("official_url", "https://www.instagram.com")
                                    .append("user_url", "https://www.instagram.com/melar_dev/")
                                    .append("username", "melar_dev")));

            Document doc4 = new Document().append("first_name", "Melar")
                    .append("last_name", "Dev")
                    .append("social", socialList);

            usersCollection.insertMany(Arrays.asList(doc2, doc3, doc4));

            // Array: Approach 2
            List<Document> usersList = new ArrayList<>();
            usersList.add(new Document("facebook", "Melar Dev"));
            usersList.add(new Document("youtube", "Melardev"));
            usersCollection.insertOne(new Document("social", usersList));

            // Update
            Object _id = usersCollection.find().first().get("_id");
            usersCollection.updateOne(Filters.eq("_id", _id), Updates.combine(Updates.set("first_name", "")));

            // Read
            FindIterable<Document> cursor = usersCollection.find();
            MongoCursor<Document> iterator = cursor.iterator();
            while (iterator.hasNext()) {
                Document document = iterator.next();
                System.out.println(document);
            }

            //Document first = collection.find().first();
            usersCollection.find().forEach(new Block<Document>() {
                @Override
                public void apply(Document document) {
                    System.out.println(document.toJson());
                }
            });
            usersCollection.find().forEach((Block<? super Document>) d -> System.out.println(d.toJson()));

            System.out.println();
            System.out.println("Find >= 21 yo; $gte");
            usersCollection.find(new Document("age", new Document("$gte", 21))).forEach((Block<? super Document>) d -> System.out.println(d.toJson()));

            System.out.println();
            System.out.println("Find >= 25yo; Filters");
            usersCollection.find(Filters.gte("age", 25)).forEach((Block<? super Document>) d -> System.out.println(d.toJson()));

            System.out.println();
            System.out.println("Find >= 18 && < 26 yo; chaining filters and sorting");
            usersCollection.find(Filters.and(Filters.gt("age", 18), new Document("age",
                    new Document("$lt", 26)) //, Filters.lt("age", 26)
            ))
                    .sort(Sorts.ascending("age"))
                    .forEach((Consumer<? super Document>) d -> System.out.println(d.toJson()));

            // Delete
            usersCollection.deleteOne(Filters.eq("_id", _id));

            // Delte collection
            usersCollection.drop();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (mongoClient != null)
                mongoClient.close();
        }
    }

    public static void h2Demo() {
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        try {
            /*
            JdbcConnectionPool cp = JdbcConnectionPool.create("jdbc:h2:file:D:/h2", "sa", "");
            conn = cp.getConnection();
            conn.close();
            cp.dispose();
             */

            /*
            JdbcDataSource ds = new JdbcDataSource();
            ds.setURL("");
            ds.setUser("sa");
            ds.setPassword("");
            conn = ds.getConnection();
            */

            Class.forName("org.h2.Driver");
            //conn = DriverManager.getConnection("jdbc:h2:file:C:\\Users\\melardev\\AppData\\Local\\Temp\\h2\\mydb2.db", "sa", "");
            //conn = DriverManager.getConnection("jdbc:h2:tcp://localhost/D:/h2/mydb.db", "sa", "");
            conn = DriverManager.getConnection("jdbc:h2:tcp://localhost/D:/h2/mydb_server.db", "sa", "");

            //jdbc:h2:file:D:/h2/mydb.db
            //jdbc:h2:tcp://localhost/D:/h2/mydb.db;DB_CLOSE_DELAY=-1
            //jdbc:h2:mem:test;DB_CLOSE_DELAY=-1
            stmt = conn.createStatement();
            String sql = "DROP SCHEMA IF EXISTS jdbc_db";
            stmt.executeUpdate(sql);
            sql = "CREATE SCHEMA jdbc_db";
            stmt.execute(sql);
            System.out.println("Database created");
            stmt.execute("CREATE TABLE IF NOT EXISTS `jdbc_db`.`employees`" + "( `id` INT NOT NULL AUTO_INCREMENT ,"
                    + "`first_name` VARCHAR(100) NOT NULL ," + "`last_name` VARCHAR(100) NOT NULL ,"
                    + "`age` INT NOT NULL ," + "PRIMARY KEY (`id`))");
            System.out.println("Table created");


            sql = "INSERT INTO JDBC_DB.EMPLOYEES(first_name, last_name, age)VALUES('Jan', 'Dallas', 21)";//, ('Jessie', 'Day', 18)
            //+ " ('Tammy', 'Phelps', 32), ('Hannah', 'Cole', 28), ('Philip', 'Jensen', 26)";

            int insertedRows = stmt.executeUpdate(sql);
            System.out.println("Rows inserted: " + insertedRows);

            rs = stmt.getGeneratedKeys();
            while (rs.next())
                System.out.println("Inserted row with Id: " + rs.getInt(1));

            sql = "UPDATE JDBC_DB.employees SET first_name='John' WHERE id=1";
            stmt.executeUpdate(sql);

            sql = "SELECT * FROM JDBC_DB.employees";
            rs = stmt.executeQuery(sql);
            while (rs.next()) {
                int id = rs.getInt(rs.findColumn("id"));
                String firstName = rs.getString("first_name");
                String lastName = rs.getString("last_name");
                int age = rs.getInt("age");
                System.out.printf("%d: %s %s, %d years old\n", id, firstName, lastName, age);

            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            try {
                if (stmt != null)
                    stmt.close();
                if (rs != null)
                    rs.close();
                if (conn != null)
                    conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static void h2ServerDemo() {
        Server server = null;
        try {
            server = Server.createTcpServer("-tcpPort", "9000", "-tcpAllowOthers").start();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (server != null)
                server.stop();
        }
    }

    public static void derbyClientServerDemo() {
        try {//dblook -d 'jdbc:derby:c:\temp\db\FAQ\db' -z myschema -o lars.sql
            Class.forName("org.apache.derby.jdbc.ClientDriver").newInstance();

            Connection connect = DriverManager
                    .getConnection("jdbc:derby://localhost/c:/temp/db/FAQ/db");
            PreparedStatement statement = connect
                    .prepareStatement("SELECT * from USERS");

            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                String user = resultSet.getString("name");
                String number = resultSet.getString("number");
                System.out.println("User: " + user);
                System.out.println("ID: " + number);
            }
        } catch (InstantiationException e1) {
            e1.printStackTrace();
        } catch (IllegalAccessException e1) {
            e1.printStackTrace();
        } catch (ClassNotFoundException e1) {
            e1.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        /*
            String dbURL = "jdbc:derby://localhost/webdb";
            Properties properties = new Properties();
            properties.put("user", "");
            properties.put("password", "");
            properties.put("create", "true");

            conn = DriverManager.getConnection(dbURL, properties);
*/
    }

    public static void derbyEmbeddedDemo() {

        /* Derby in embedded mode runs inside the JVM process, no one other than this App can access the database

            jdbc:derby:your/path/folder/database;create=true

            jdbc:derby:C:/Users/melardev/AppData/Local/Temp/jdbc_db;create=true

            memory database:
            jdbc:derby:memory:jdbc_db;create=true
            delete after the app exit
            jdbc:derby:memory:jdbc_db;drop=true

            Connect to a database presents in the classpath:
            jdbc:derby:classpath:jdbc_db

            Connect to jdbc_db database inside a jar(should be inside classpath)
            jdbc:derby:jar:webdb

            ...(not in the classpath)
            jdbc:derby:jar:(C:/path/to/my.jar)webdb

            Shutdown the current database:
            jdbc:derby:;shutdown=true
             */

        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        try {
            try {
                // Class.forName("org.apache.derby.jdbc.EmbeddedDriver");
                DriverManager.registerDriver(new org.apache.derby.jdbc.EmbeddedDriver());
            } catch (SQLException e) {
                e.printStackTrace();
            }
            try {
                conn = DriverManager.getConnection("jdbc:derby:memory:jdbc_db;create=true");
                stmt = conn.createStatement();
            } catch (SQLException e) {
                e.printStackTrace();
            }

            String sql = "DROP TABLE employees";
            try {
                stmt.executeUpdate(sql);
            } catch (SQLException e) {
                if (((SQLSyntaxErrorException) e).getSQLState().equals("42Y55"))
                    System.out.println("Database Doesn't exist");
            }

            try {
                stmt.execute("CREATE TABLE employees(id INT PRIMARY KEY ,"
                        + "first_name VARCHAR(100) NOT NULL ,last_name VARCHAR(100) NOT NULL ,"
                        + "age INT NOT NULL)");
            } catch (SQLException e) {
                e.printStackTrace();
            }

            System.out.println("Table created");
            sql = "INSERT INTO employees(id, first_name, last_name, age) VALUES (1, 'Jan', 'Dallas', 21),(2, 'Jessie', 'Day', 18)," +
                    "(3, 'Tammy', 'Phelps', 32), (4, 'Hannah', 'Cole', 28), (5, 'Philip', 'Jensen', 26)";

            int insertedRows = 0;
            try {
                insertedRows = stmt.executeUpdate(sql, Statement.RETURN_GENERATED_KEYS);
            } catch (SQLException e) {
                e.printStackTrace();
            }
            System.out.println("Rows inserted: " + insertedRows);

            try {
                rs = stmt.getGeneratedKeys();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            try {
                if (rs != null) {
                    while (rs.next())
                        System.out.println("Inserted row with Id: " + rs.getInt(1));
                }
                sql = "UPDATE employees SET first_name='John' WHERE id=1";
                stmt.executeUpdate(sql);

                sql = "SELECT * FROM employees";
                rs = stmt.executeQuery(sql);
                while (rs.next()) {
                    int id = rs.getInt(rs.findColumn("id"));
                    String firstName = rs.getString("first_name");
                    String lastName = rs.getString("last_name");
                    int age = rs.getInt("age");
                    System.out.printf("%d: %s %s, %d years old\n", id, firstName, lastName, age);

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } finally {
            try {
                if (stmt != null)
                    stmt.close();
                if (rs != null)
                    rs.close();

                //DriverManager.getConnection("jdbc:derby:memory:jdbc_db;shutdown=true");
                if (conn != null)
                    conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }


    public static void createDatabaseAndTable() {
        Connection conn = null;
        Statement stmt = null;
        try {
            Class.forName("com.mysql.jdbc.Driver");

            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306", "root", "root");
            //DriverManager.getConnection("jdbc:mysql://localhost/?user=root&password=root");
            stmt = conn.createStatement();

            String sql = "DROP DATABASE IF EXISTS jdbc_db";
            stmt.executeUpdate(sql);

            sql = "CREATE DATABASE jdbc_db";
            stmt.execute(sql);
            System.out.println("Database created");
            stmt.execute("CREATE TABLE IF NOT EXISTS `jdbc_db`.`employees`" + "( `id` INT NOT NULL AUTO_INCREMENT ,"
                    + "`first_name` VARCHAR(100) NOT NULL ," + "`last_name` VARCHAR(100) NOT NULL ,"
                    + "`age` INT NOT NULL ," + "PRIMARY KEY (`id`)) " + "ENGINE = InnoDB;");

            //stmt.execute("use jdbc_db");
            stmt.execute("CREATE PROCEDURE jdbc_db.`GetEmployeeById`(IN `id` INT) NO SQL " + "BEGIN "
                    + "SELECT * FROM employees WHERE employees.id = id;\n" + "END" + "");

            stmt.execute("CREATE PROCEDURE jdbc_db.`GetEmployeesCount`(OUT `result` INT) NO SQL\n" + "BEGIN\n"
                    + "SELECT COUNT(*) FROM employees INTO result;\n" + "END");

            System.out.println("Table created");
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            // Driver not available, make sure you installed it
            e.printStackTrace();

        } finally {
            try {
                if (stmt != null)
                    stmt.close();

                if (conn != null)
                    conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static void insertAndUpdateDemo() {
        //Class.forName("com.mysql.cj.jdbc.Driver"); This is the new driver, and we don't have to register it, it is done automatically for us

        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        try {
            // look at the conn type, what if we have multiple JDBC Drivers? how to use one or the other?
            // the DriverManager knows which one to use from the connection string you are passing.
            conn = DriverManager.getConnection("jdbc:mysql://localhost/jdbc_db?&user=root&password=root");
            stmt = conn.createStatement();

            String sql = "INSERT INTO employees(first_name, last_name, age) VALUES('Jan', 'Dallas', 21), ('Jessie', 'Day', 18),"
                    + " ('Tammy', 'Phelps', 32), ('Hannah', 'Cole', 28), ('Philip', 'Jensen', 26)";

            int insertedRows = stmt.executeUpdate(sql, Statement.RETURN_GENERATED_KEYS);
            System.out.println("Rows inserted: " + insertedRows);

            rs = stmt.getGeneratedKeys();
            while (rs.next())
                System.out.println("Inserted row with Id: " + rs.getInt(1));

            sql = "UPDATE employees SET first_name='John' WHERE id=1";
            stmt.executeUpdate(sql);

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (stmt != null)
                    conn.close();
                if (rs != null)
                    conn.close();
                if (conn != null)
                    conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static void readFromDbDemo() {
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        // Prepared statement: better performance, used when you use the same query multiple times, also MORE SECURE
        String sql = "SELECT * FROM employees";
        try {
            conn = DriverManager.getConnection("jdbc:mysql://localhost/jdbc_db", "root", "root");
            stmt = conn.createStatement();
            sql = "SHOW DATABASES";
            rs = stmt.executeQuery(sql);


            while (rs.next()) {
                int id = rs.getInt(rs.findColumn("id"));
                String firstName = rs.getString("first_name");
                String lastName = rs.getString("last_name");
                int age = rs.getInt("age");

                System.out.printf("%d: %s %s, %d years old\n", id, firstName, lastName, age);
                if (true)
                    System.exit(0);
            }
        } catch (SQLException e) {

            e.printStackTrace();
        } finally {
            try {

                if (stmt != null)
                    stmt.close();
                if (rs != null)
                    rs.close();
                if (conn != null)
                    conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

    }

    public static void preparedStatementsDemo() {

        Connection conn = null;
        ResultSet rs = null;
        PreparedStatement pstmt = null;
        // Prepared statement: better performance, used when you use the same query multiple times, also MORE SECURE
        String sql = "INSERT INTO employees(first_name, last_name, age)VALUES(?,?,?)";
        try {
            conn = DriverManager.getConnection("jdbc:mysql://localhost/jdbc_db", "root", "root");
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, "Erika"); // set first_name
            pstmt.setString(2, "Cain"); // set last_name
            pstmt.setInt(3, 22); // set last_name

            pstmt.executeUpdate();

            sql = "SELECT id, first_name, last_name, age FROM employees";
            rs = pstmt.executeQuery(sql);

            while (rs.next()) {
                int id = rs.getInt("id");
                String firstName = rs.getString("first_name");
                String lastName = rs.getString("last_name");
                int age = rs.getInt("age");

                System.out.printf("%d: %s %s, %d years old\n", id, firstName, lastName, age);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {

                if (pstmt != null)
                    pstmt.close();
                if (rs != null)
                    rs.close();
                if (conn != null)
                    conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

    }

    public static void storedProceduresDemo() {
        try {
            // not needed to register the driver
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e1) {
            e1.printStackTrace();
        }

        // CallableStatement: used to call stored procedures
        // {} is called SQL Escape syntax
        String sql = "{call GetEmployeesCount (?)}";

        Connection conn = null;
        CallableStatement cstmt = null;
        try {
            conn = DriverManager.getConnection("jdbc:mysql://localhost/jdbc_db", "root", "root");
            cstmt = conn.prepareCall(sql);
            //register the second arg since it is OUT
            cstmt.registerOutParameter(1, java.sql.Types.INTEGER);
            cstmt.execute();
            System.out.printf("There are %d rows actually\n", cstmt.getInt("result"));

            cstmt.close();
            cstmt = conn.prepareCall("{call GetEmployeeById(?)}");

            cstmt.setInt(1, 2);
            ResultSet rs = cstmt.executeQuery();
            if (rs.first()) {
                int id = rs.getInt(rs.findColumn("id"));
                String firstName = rs.getString("first_name");
                String lastName = rs.getString("last_name");
                int age = rs.getInt("age");

                System.out.printf("%d: %s %s, %d years old\n", id, firstName, lastName, age);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (conn != null)
                    conn.close();
                if (cstmt != null)
                    cstmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static void navigateResultSetDemo() {

        // Statement:
        Connection conn = null;

        try {
            conn = DriverManager.getConnection("jdbc:mysql://localhost/jdbc_db", "root", "root");
            String sql = "SELECT * FROM employees ";
            Statement stmt = conn.createStatement(ResultSet.TYPE_FORWARD_ONLY,
                    // you can go backwards, and it is a full copy of the data
                    // so if anyone else makes a change, it does not change this resultset
                    ResultSet.CONCUR_READ_ONLY); // you can not change the result set

            ResultSet rs = stmt.executeQuery(sql);
            System.out.println("Last");
            rs.last();
            dumpRs(rs);
            System.out.println("First");
            rs.first();
            dumpRs(rs);
            System.out.println("Absolute 6");
            rs.absolute(6);
            dumpRs(rs);
            System.out.println("relative 2");
            rs.relative(-2);
            dumpRs(rs);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void dumpRs(ResultSet rs) throws SQLException {

        if (rs.isAfterLast())
            return;

        if (rs.isBeforeFirst())
            rs.next();

        do {
            int id = rs.getInt(rs.findColumn("id"));
            String firstName = rs.getString("first_name");
            String lastName = rs.getString("last_name");
            int age = rs.getInt("age");

            System.out.printf("%d: %s %s, %d years old\n", id, firstName, lastName, age);
        } while (rs.next());

    }

    public static void updatingResultSet() {
        try { // Remember, this is optional in the newer mysql connector versions
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }

        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        try {
            conn = DriverManager.getConnection("jdbc:mysql://localhost/jdbc_db", "root", "root");
            stmt = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE // you can navigate it and is not aware of changes
                    // made by others to the underlying database
                    , ResultSet.CONCUR_UPDATABLE);
            String sql = "SELECT id, first_name, last_name, age FROM employees";
            rs = stmt.executeQuery(sql);

            //Update
            System.out.println("Before update");
            dumpRs(rs);
            rs.last();
            String oldLastName = rs.getString("last_name");
            rs.updateString("last_name", "Montana");
            rs.updateRow();
            System.out.println("After updateRow");
            dumpRs(rs);

            // Reupdate
            rs.last();
            rs.updateString("last_name", oldLastName);
            rs.updateRow();
            System.out.println("Reset");
            dumpRs(rs);

            System.out.println("Insert new row");
            rs.moveToInsertRow();
            rs.updateString("first_name", "Pamela");
            rs.updateString("last_name", "Miles");
            rs.updateInt("age", 18);
            rs.insertRow(); //it is an insertion, so always at the end.
            rs.first();
            dumpRs(rs);

            // Delete first row
            rs.absolute(1);
            System.out.println("Delete first row");

            rs.deleteRow();
            rs.beforeFirst();
            dumpRs(rs);

            rs.close();
            stmt.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (conn != null)
                    conn.close();

                if (stmt != null)
                    stmt.close();

                if (rs != null)
                    rs.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static void JdbcDateDemo() {

        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        try {

            conn = DriverManager.getConnection("jdbc:mysql://localhost/jdbc_db", "root", "root");
            stmt = conn.createStatement();
            rs = stmt.executeQuery("SELECT DATE(NOW()) as date, TIME(NOW()) as time, TIMESTAMP(NOW()) as timestamp");
            if (rs.first()) {
                Date date = rs.getObject("date", Date.class);
                System.out.println("Date: " + date.toString());

                Time time = rs.getObject("time", Time.class);
                System.out.println("Time: " + time.toString());

                Timestamp timestamp = rs.getObject("timestamp", Timestamp.class);
                System.out.println("timestamp: " + timestamp.toString());

                Timestamp ts = rs.getTimestamp("timestamp");
                Calendar calendar = Calendar.getInstance(Locale.getDefault());
                calendar.setTimeInMillis(ts.getTime());
                String dateString = new SimpleDateFormat("HH:mm:ss dd-MM-yyyy").format(calendar.getTime());
                System.out.println(dateString);

            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {

                if (stmt != null)
                    stmt.close();

                if (rs != null)
                    rs.close();
                if (conn != null)
                    conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

    }

    public static void transactionDemo() {
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        try {
            conn = DriverManager.getConnection("jdbc:mysql://localhost/jdbc_db", "root", "root");
            conn.setAutoCommit(false);

            stmt = conn.createStatement();

            //stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);

            String sql = "INSERT INTO employees(first_name, last_name, age) VALUES('Arturo', 'Willis', 52)";
            stmt.executeUpdate(sql);

            sql = "INSERT INTO employees(first_name, last_name, age) VALUES('Dianna', 'Simmons', 47)";
            stmt.executeUpdate(sql);

            System.out.println("Never reached");
            conn.commit();

        } catch (SQLException e) {
            e.printStackTrace();
            try {
                if (conn != null)
                    conn.rollback();
                System.out.println("rolled back");

            } catch (SQLException e2) {
                e2.printStackTrace();
            }

        } catch (Exception e) {

            e.printStackTrace();
        } finally {

            try {
                if (stmt != null)
                    stmt.close();

                if (rs != null)
                    rs.close();

                if (conn != null)
                    conn.close();
            } catch (SQLException se) {
                se.printStackTrace();
            }
        }

    }

    public static void savePointDemo() {
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        Savepoint savePoint1 = null;
        Savepoint savePoint2 = null;
        try {
            conn = DriverManager.getConnection("jdbc:mysql://localhost/jdbc_db", "root", "root");
            conn.setAutoCommit(false);

            stmt = conn.createStatement();

            //stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);

            String sql = "INSERT INTO employees(first_name, last_name, age) VALUES('Arturo', 'Willis', 52)";
            stmt.executeUpdate(sql);
            savePoint1 = conn.setSavepoint("savepoint1");

            sql = "adasdINSERT INTO employees(first_name, last_name, age) VALUES('Dianna', 'Simmons', 47)";
            stmt.executeUpdate(sql);
            savePoint2 = conn.setSavepoint("savepoint2");

            sql = "This sql statement goes really wrong";
            stmt.executeUpdate(sql);

            System.out.println("Never reached");
            conn.commit();

        } catch (SQLException e) {

            e.printStackTrace();

            try {
                if (conn == null)
                    return;
                if (savePoint2 != null) {
                    conn.rollback(savePoint2);
                    System.out.println("rolled back to savePoint2");
                } else if (savePoint1 != null) {
                    conn.rollback(savePoint1);
                    System.out.println("rolled back to savePoint1");
                } else if (conn != null) {
                    conn.rollback();
                    System.out.println("rolled back");
                }

            } catch (SQLException e2) {
                e2.printStackTrace();
            }

        } catch (Exception e) {

            e.printStackTrace();
        } finally {

            try {
                if (stmt != null)
                    stmt.close();

                if (rs != null)
                    rs.close();

                if (conn != null)
                    conn.close();
            } catch (SQLException se) {
                se.printStackTrace();
            }
        }
    }

    public static void batchDemo() {
        /**
         * For each operation on the database, the database has to be hit, what if we
         * have to make multiple operations such as insert 5000 rows at once? there is
         * too much overhead, we can overcome this issue by adding operations to a
         * batch(group) and then submitted that group to the database once and all of
         * them will be executed in one go, usually you don't want to run partially your
         * queries meaning that you want to rollback if one of your queries fail
         */
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        try {
            conn = DriverManager.getConnection("jdbc:mysql://localhost/jdbc_db", "root", "root");
            stmt = conn.createStatement();
            String[] queries = {"INSERT INTO employees(first_name, last_name, age) VALUES('Dianna', 'Simmons', 47)",
                    "INSERT INTO employees(first_name, last_name, age) VALUES('Oliver', 'Yol', 32)",
                    "INSERT INTO employees(first_name, last_name, age) VALUES('Moz', 'tasse', 65)",
                    "INSERT INTO employees(first_name, last_name, age) VALUES('Omar', 'Benali', 67)",
                    "--INSERT INTO employees(first_name, last_name, age) VALUES('Alejandra', 'Mars', 29)",
                    "INSERT INTO employees(first_name, last_name, age) VALUES('Dominique', 'Saint Pierre', 38)",
                    "INSERT INTO employees(first_name, last_name, age) VALUES('Tom', 'Gates', 43)"};

            conn.setAutoCommit(false);

            for (String query : queries)
                stmt.addBatch(query);

            int[] count = stmt.executeBatch();
            System.out.println("We have " + count.length);

            conn.commit();

            rs = stmt.executeQuery("select * from employees");
            dumpRs(rs);

        } catch (SQLException e) {
            e.printStackTrace();
            try {
                conn.rollback();
                rs = stmt.executeQuery("select * from employees");
                dumpRs(rs);
            } catch (SQLException e1) {

                e1.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (stmt != null)
                    stmt.close();

                if (rs != null)
                    rs.close();

                if (conn != null)
                    conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static void saveFileDemo() {
        Connection conn = null;
        PreparedStatement pstmt = null;
        Statement stmt = null;
        ResultSet rs = null;


        try {
            conn = DriverManager.getConnection("jdbc:mysql://localhost/jdbc_db", "root", "root");

            stmt = conn.createStatement();

            stmt.executeUpdate("DROP TABLE IF EXISTS files");
            String sql = "CREATE TABLE `files` (`id` int(11) NOT NULL AUTO_INCREMENT,"
                    + "`text` text NOT NULL, `binary` LONGBLOB NOT NULL, PRIMARY KEY (`id`))";

            stmt.executeUpdate(sql);
            InputStream isText = JdbcLauncher.class.getResourceAsStream("/data.json");
            InputStream isBin = JdbcLauncher.class.getResourceAsStream("/curl.exe");
            int fileSize = isText.available();
            //Create PreparedStatement and stream data
            sql = "INSERT INTO files(`text`, `binary`) VALUES (?, ?)";
            pstmt = conn.prepareStatement(sql);
            //pstmt.setAsciiStream(1, isText, (int) fileSize);
            pstmt.setCharacterStream(1, new InputStreamReader(isText));
            pstmt.setBinaryStream(2, isBin);
            pstmt.execute();

            isText.close();
            isBin.close();

            sql = "SELECT * FROM files";
            rs = stmt.executeQuery(sql);

            if (rs.next()) {
                InputStream xmlInputStream = rs.getAsciiStream("text");
                BufferedReader reader = new BufferedReader(new InputStreamReader(xmlInputStream));
                String line;
                while ((line = reader.readLine()) != null)
                    System.out.println(line);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (stmt != null)
                    stmt.close();

                if (rs != null)
                    rs.close();

                if (conn != null)
                    conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
