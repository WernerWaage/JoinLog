package org.overminds.joinLog;

import java.sql.DriverManager;
import java.sql.SQLException;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

import com.mysql.jdbc.Connection;

public class JoinLog extends JavaPlugin {
	 
	//Load config:
	FileConfiguration config = getConfig();
	
    //DataBase vars.
    final String username="minecraft_web"; //Enter in your db username
    final String password=""; //Enter your password for the db
    final String url = "jdbc:mysql://localhost:3306/minecraft?autoReconnect=true&useSSL=false"; //Enter URL w/db name

    //Connection vars
    static Connection connection; //This is the variable we will use to connect to database
	
	
    // Fired when plugin is first enabled
    @Override
    public void onEnable() {
        // Register our command "kit" (set an instance of your command class as executor)
        this.getCommand("joinlog").setExecutor(new CommandJoinlist());
        
 
    	// Initialize default config variables
    	config.addDefault("SqlHostname", "localhost:3306");
    	config.addDefault("SqlDatabase", "Database");
    	config.addDefault("SqlUsername", "Username");
    	config.addDefault("SqlPassword", "1234");
    	config.addDefault("SqlSSL", false);
    	config.addDefault("Author", "WernerWaage");
        config.options().copyDefaults(true);
        
        saveConfig();

       // Enable our class to check for new players using onPlayerJoin()
       // getServer().getPluginManager().registerEvents(this, this);
    	
 
        try { //We use a try catch to avoid errors, hopefully we don't get any.
            Class.forName("com.mysql.jdbc.Driver"); //this accesses Driver in jdbc.
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            System.err.println("jdbc driver unavailable!");
            return;
        }
        try { //Another try catch to get any SQL errors (for example connections errors)
            connection = (Connection) DriverManager.getConnection(url,username,password);
            //with the method getConnection() from DriverManager, we're trying to set
            //the connection's url, username, password to the variables we made earlier and
            //trying to get a connection at the same time. JDBC allows us to do this.
        } catch (SQLException e) { //catching errors)
            e.printStackTrace(); //prints out SQLException errors to the console (if any)
        }
        
        
        
        
    }
    // Fired when plugin is disabled
    @Override
    public void onDisable() {
        // envoke on disable.
        try { //using a try catch to catch connection errors (like wrong sql password...)
                if(connection!=null && !connection.isClosed()){ //checking if connection isn't null to
                //avoid recieving a nullpointer
                        connection.close(); //closing the connection field variable.
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
        
        // Save join information to db:
       
        
        
        
    }
    
    
    

}