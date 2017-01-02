package org.overminds.joinLog;

import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.UUID;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

import com.mysql.jdbc.Connection;
// import com.mysql.jdbc.PreparedStatement;

public class JoinLog extends JavaPlugin {
	 
	//Load config:
	FileConfiguration config = getConfig();
	
    //Connection vars
    static Connection myConnection; //This is the variable we will use to connect to database
	
	
    // Fired when plugin is first enabled
    @Override
    public void onEnable() {
    	
    	
        // Register our command "kit" (set an instance of your command class as executor)
        this.getCommand("joinlog").setExecutor(new CommandJoinlist());
        
    	// Initialize default config variables
    	config.addDefault("SqlHostname", "localhost:3306");
    	config.addDefault("SqlDatabase", "Minecraft");
    	config.addDefault("SqlUsername", "minecraft_web");
    	config.addDefault("SqlPassword", "1234");
    	config.addDefault("SqlSSL", false);
    	config.addDefault("Author", "WernerWaage");
        config.options().copyDefaults(true);
        saveConfig();

       // Enable our class to check for new players using onPlayerJoin()
       // getServer().getPluginManager().registerEvents(this, this);
    	
 
        try { // check for jdbc driver
            Class.forName("com.mysql.jdbc.Driver"); //this accesses Driver in jdbc.
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            System.err.println("jdbc driver unavailable!");
            return;
        }
        try { 
        	//Attempt SQL Connection:
            //DataBase vars, load from config and create connection string
 
            final String username=config.getString("SqlUsername"); //Enter in your db username
            final String password=config.getString("SqlPassword"); //Enter your password for the db
            final String hostname=config.getString("SqlHostname"); //Enter your password for the db
            final String database=config.getString("SqlDatabase"); //Enter your password for the db
            final String usessl=config.getString("SqlSSL"); //Enter your password for the db
            
            final String url = "jdbc:mysql://" + hostname + "/" + database + "?autoReconnect=true&useSSL=" + usessl + "";

            myConnection = (Connection) DriverManager.getConnection(url,username,password);
            System.out.println("[JoinLog -->] MySql Connection Successfull!");
            

            // Create default tables if they dont exist:
            String createTable = "CREATE TABLE IF NOT EXISTS `jl_users` (`UserID` INT NOT NULL AUTO_INCREMENT,`UUID` VARCHAR(64) NULL,`Name` VARCHAR(128) NULL,PRIMARY KEY (`UserID`));";
            String createLogTable = "CREATE TABLE `minecraft`.`jl_userlog` (`ID` INT NOT NULL AUTO_INCREMENT,`UUID` VARCHAR(64) NULL,`Date` DATETIME NULL,`Status` VARCHAR(45) NULL,PRIMARY KEY (`ID`));";
            
            
            // Requires MySQL Connector 5.1 or newer!
	        PreparedStatement myPreparedStatement = myConnection.prepareStatement(createTable);
	        myPreparedStatement.executeUpdate();
	            
	        PreparedStatement myPreparedStatement2 = myConnection.prepareStatement(createLogTable);
	        myPreparedStatement2.executeUpdate();
	        
	        

	       
            
        } catch (SQLException e) { //catching errors)
        	System.out.println("[JoinLog -->] MySql Connection Failed :(((!");
            e.printStackTrace(); //prints out SQLException errors to the console (if any)
        }
        
        
        
        
    }
    // Fired when plugin is disabled
    @Override
    public void onDisable() {
        try {
                if(myConnection!=null && !myConnection.isClosed()){
                	myConnection.close(); 
                }
        }catch(Exception e){
                        e.printStackTrace();
       
        }
    }
    
    
    
 // This method checks for incoming players and sends them a message
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        if (config.getBoolean("SqlSSL")) {
            player.sendMessage("SSL Enabled");
        } else {
            player.sendMessage("SSL Disabled");
        }
        
        UUID vPlayerUUID = player.getUniqueId();
        String vPlayername = player.getDisplayName();
        
        // Save join information to db:
        String sql = "INSERT INTO jl_users(name) VALUES ('"+ vPlayername+"');";
        PreparedStatement myPreparedStatementJoin;
		try {
			myPreparedStatementJoin = myConnection.prepareStatement(sql);
	        myPreparedStatementJoin.setString(1, "Something"); //I set the "?" to "Something"
	        myPreparedStatementJoin.executeUpdate();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

   
    }
    
    
    

}