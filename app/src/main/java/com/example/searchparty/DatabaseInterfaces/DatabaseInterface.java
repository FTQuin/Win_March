package com.example.searchparty.DatabaseInterfaces;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.TextView;

import com.example.searchparty.Models.Game;
import com.example.searchparty.Models.Prediction;
import com.example.searchparty.Models.Team;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DatabaseInterface extends SQLiteOpenHelper {
    /*
    methods to add:
    
        getFutureGames: list<Game>
        getPastGames: list<Game>
        getSavedGames: list<Game>
        
        setFavoriteTeam: void
        unsetFavoriteTeam: void
        followTeam: void
        unFollowTeam: void
        
        getFollowedTeams: list<Team>
        getFavoriteTeam: Team
        
        
        **possibly create UpcomingGame and FinishedGame classes**
        Game:
            - homeTeam
            - awayTeam
            - date
            - prediction
            - stats **figure out how to implement this**
            get/set Prediction
            get/set <various stats>
            
        Prediction:
            - stats **figure out how to implement this**
            get/set SliderValue
            
        Team:
            - Name
            - previousGames
            - futureGames
            - team stats
            - logo
    */
    
    
    private static final String TAG = "DatabaseInterface";
    private static final String DATABASE_NAME = "win_march.db";
    
    //lists of team, game, and prediction objects
    private static Map<String, Game> gameMap;
    private static Map<String, Prediction> predictionMap;
    private static Map<String, Team> teamMap;
    private static List<String> futureGameIDs;
    private static List<String> pastGameIDs;
    
    //strings for creating the databases
    //team attributes
    private static final String TEAM_TABLE_NAME = "TEAM_TABLE";
    private static final String[][] TEAM_TABLE_COLS = {{"ID", "TEXT"},
            {"NAME", "TEXT"}};
    
    //game attributes
    private static final String GAME_TABLE_NAME = "GAME_TABLE";
    private static final String[][] GAME_TABLE_COLS = {{"ID", "TEXT"},
            {"HOME_TEAM_ID", "TEXT"}, {"AWAY_TEAM_ID", "TEXT"}, {"START_TIME", "LONG"}, {"HPTS", "DOUBLE"},
            {"HFGM", "DOUBLE"}, {"H3PM", "DOUBLE"}, {"HAST", "DOUBLE"}, {"HTO", "DOUBLE"},
            {"APTS", "DOUBLE"}, {"AFGM", "DOUBLE"}, {"A3PM", "DOUBLE"}, {"AAST", "DOUBLE"}, {"ATO", "DOUBLE"}};
    
    //prediction attributes
    private static final String PREDICTION_TABLE_NAME = "PREDICTION_TABLE";
    private static final String[][] PREDICTION_TABLE_COLS = {{"ID", "TEXT"},
            {"PREDICTED_OUTCOME", "DOUBLE"},{"GAME_ID", "TEXT"}};
    
    public DatabaseInterface(Context context){
        super(context, DATABASE_NAME, null, 1);
    
        gameMap = new HashMap<>();
        predictionMap = new HashMap<>();
        teamMap = new HashMap<>();
        futureGameIDs = new ArrayList<>();
        pastGameIDs = new ArrayList<>();
    }
    
    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create databases
        // Create string for game table
        StringBuilder createGameTable = new StringBuilder("CREATE TABLE " + GAME_TABLE_NAME+"(");
        for (String[] col : GAME_TABLE_COLS)
            createGameTable.append(col[0]).append(" ").append(col[1]).append(", ");
        createGameTable.delete(createGameTable.lastIndexOf(","), createGameTable.length());
        createGameTable.append(")");
        //create game table
        db.execSQL(createGameTable.toString());
        Log.d(TAG, "Created game table");
        
        // Create string for prediction table
        StringBuilder createPredictionTable = new StringBuilder("CREATE TABLE " + PREDICTION_TABLE_NAME+"(");
        for (String[] col : PREDICTION_TABLE_COLS)
            createPredictionTable.append(col[0]).append(" ").append(col[1]).append(", ");
        createPredictionTable.delete(createPredictionTable.lastIndexOf(","), createPredictionTable.length());
        createPredictionTable.append(")");
        //create prediction table
        db.execSQL(createPredictionTable.toString());
        Log.d(TAG, "Created prediction table");
    
        // Create string for team table
        StringBuilder createTeamTable = new StringBuilder("CREATE TABLE " + TEAM_TABLE_NAME+"(");
        for (String[] col : TEAM_TABLE_COLS)
            createTeamTable.append(col[0]).append(" ").append(col[1]).append(", ");
        createTeamTable.delete(createTeamTable.lastIndexOf(","), createTeamTable.length());
        createTeamTable.append(")");
        //create team table
        db.execSQL(createTeamTable.toString());
        Log.d(TAG, "Created team table");
        
        
        Log.d(TAG, "Created databases");
    }
    
    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP IF TABLE EXISTS " + TEAM_TABLE_NAME);
        db.execSQL("DROP IF TABLE EXISTS " + GAME_TABLE_NAME);
        db.execSQL("DROP IF TABLE EXISTS " + PREDICTION_TABLE_NAME);
        onCreate(db);
    }
    
    // reference for inserting content values for team class
    // {{"ID", "STRING"},
    // {"NAME", "TEXT"}}
    public boolean addTeam(Team team) {
        SQLiteDatabase db = this.getWritableDatabase();
        
        
        if(teamMap.containsKey(team.getID())){
        
            String query = "UPDATE " + TEAM_TABLE_NAME + " SET "
                    + TEAM_TABLE_COLS[1][0] + " = '" + team.getName()
                    + "' WHERE " + TEAM_TABLE_COLS[0][0] + " = '" + team.getID() + "'";
        
            db.execSQL(query);
    
            db.close();
            return true;
        }
        else if(teamMap.containsValue(team)){

            String query = "UPDATE " + TEAM_TABLE_NAME + " SET "
                    + TEAM_TABLE_COLS[0][0] + " = '" + team.getID()
                    + "' WHERE " + TEAM_TABLE_COLS[1][0] + " = '" + team.getName() + "'";
            
            db.execSQL(query);
    
            db.close();
            //TODO: check if there's a better way than blindly returning true
            return true;
            
        }
        else {
            teamMap.put(team.getID(), team);
            ContentValues contentValues = new ContentValues();
    
            //ID
            contentValues.put(TEAM_TABLE_COLS[0][0], team.getID());
            //name
            contentValues.put(TEAM_TABLE_COLS[1][0], team.getName());
    
            Log.d(TAG, "addData: Adding items to " + TEAM_TABLE_NAME + ": " + team.getID());
    
            long result = db.insert(TEAM_TABLE_NAME, null, contentValues);
    
            db.close();
            //if data is inserted incorrectly it will return -1
            return result == -1;
        }
        
    }
    
    // reference for inserting content values for game class
    // {{"ID", "STRING"},
    // {"HOME_TEAM_ID", "TEXT"}, {"AWAY_TEAM_ID", "TEXT"}, {"START_TIME", "Long"}, {"PTS", "DOUBLE"},
    // {"FGM", "DOUBLE"}, {"3PM", "DOUBLE"}, {"AST", "DOUBLE"}, {"TOS", "DOUBLE"}};
    public boolean addGame(Game game) {
        SQLiteDatabase db = this.getWritableDatabase();
        
        if(gameMap.containsKey(game.getID())){
        
            String query = "UPDATE " + GAME_TABLE_NAME + " SET "
                    + GAME_TABLE_COLS[1][0] + " = '" + game.getHomeTeam().getID() + "', "
                    + GAME_TABLE_COLS[2][0] + " = '" + game.getAwayTeam().getID() + "', "
                    + GAME_TABLE_COLS[3][0] + " = '" + game.getStartTime().getTime() +"'";
            for (int i = 0; i < game.getStatsMap().values().size(); i++) {
                query +=  ", '" + GAME_TABLE_COLS[i+4][0] + "' = " + game.getStatsMap().get(GAME_TABLE_COLS[i+4][1]);
            }
            query += " WHERE " + GAME_TABLE_COLS[0][0] + " = '" + game.getID() + "'";
        
            db.execSQL(query);
    
            db.close();
            return true;
        
        }
        else if(gameMap.containsValue(game)){
    
            String query = "UPDATE " + GAME_TABLE_NAME + " SET "
                    + GAME_TABLE_COLS[0][0] + " = " + game.getID()
                    + " WHERE "
                    + GAME_TABLE_COLS[1][0] + " = '" + game.getHomeTeam().getID() + "', "
                    + GAME_TABLE_COLS[2][0] + " = '" + game.getAwayTeam().getID() + "'";
    
            db.execSQL(query);
    
            db.close();
            return true;
        } else {
            gameMap.put(game.getID(), game);
            ContentValues contentValues = new ContentValues();
    
            //id
            contentValues.put(GAME_TABLE_COLS[0][0], game.getID());
            //home team
            contentValues.put(GAME_TABLE_COLS[1][0], game.getHomeTeam().getID());
            //away team
            contentValues.put(GAME_TABLE_COLS[2][0], game.getAwayTeam().getID());
            //date
            contentValues.put(GAME_TABLE_COLS[3][0], game.getStartTime().getTime());
            
            //stats
            for (int i = 0; i < game.getStatsMap().size(); i++) {
                contentValues.put(GAME_TABLE_COLS[4+i][0], game.getStatsMap().get(GAME_TABLE_COLS[4+i][0]));
            }
    
            Log.d(TAG, "addData: Adding items to " + GAME_TABLE_NAME + ": " + game.getID());
    
            long result = db.insert(GAME_TABLE_NAME, null, contentValues);
    
            //if data is inserted incorrectly it will return -1
            db.close();
            return result == -1;
        }
    }
    
    // reference for inserting content values for prediction class
    // {{"ID", "STRING"},
    // {"PREDICTED_OUTCOME", "DOUBLE"},{"GAME_ID", "TEXT"}}
    public boolean addPrediction(Prediction prediction) {
        SQLiteDatabase db = this.getWritableDatabase();
    
        if (predictionMap.containsKey(prediction.getID())){
            String query = "UPDATE " + PREDICTION_TABLE_NAME + " SET "
                    + PREDICTION_TABLE_COLS[1][0] + " = '" + prediction.getPredictedOutcome() + "', "
                    + PREDICTION_TABLE_COLS[2][0] + " = '" + prediction.getGame().getID() + "' "
                    + " WHERE " + PREDICTION_TABLE_COLS[0][0] + " = '" + prediction.getID() + "'";
    
            db.execSQL(query);
    
            db.close();
            return true;
        }
        else if(predictionMap.containsValue(prediction)){
    
            String query = "UPDATE " + PREDICTION_TABLE_NAME + " SET "
                    + PREDICTION_TABLE_COLS[0][0] + " = '" + prediction.getID() + "' "
                    + " WHERE "
                    + PREDICTION_TABLE_COLS[2][0] + " = '" + prediction.getGame().getID() + "'";
    
            db.execSQL(query);
    
            db.close();
            return true;
        } else {
            predictionMap.put(prediction.getID(), prediction);
            ContentValues contentValues = new ContentValues();
    
            //id
            contentValues.put(PREDICTION_TABLE_COLS[0][0], prediction.getID());
            //outcome
            contentValues.put(PREDICTION_TABLE_COLS[1][0], prediction.getPredictedOutcome());
            //game
            contentValues.put(PREDICTION_TABLE_COLS[2][0], prediction.getGame().getID());
    
            Log.d(TAG, "addData: Adding items to " + PREDICTION_TABLE_NAME + ": " + prediction.getID());
    
            long result = db.insert(PREDICTION_TABLE_NAME, null, contentValues);
    
            //if data is inserted incorrectly it will return -1
            db.close();
            return result == -1;
        }
    }
    
    public boolean loadDataFromDB(){
        SQLiteDatabase db = this.getWritableDatabase();
        
        //load teams first
        // {{"ID", "STRING"},
        // {"NAME", "TEXT"}}
        String query = "SELECT * FROM " + TEAM_TABLE_NAME;
        Cursor data = db.rawQuery(query, null);
    
        while (data.moveToNext()){
            Team tempTeam = new Team(data.getString(1));
            tempTeam.setID(data.getString(0));
            
            if(!teamMap.containsValue(tempTeam))
                teamMap.put(tempTeam.getID(), tempTeam);
        }
        
        //load games
        // {{"ID", "STRING"},
        // {"HOME_TEAM_ID", "TEXT"}, {"AWAY_TEAM_ID", "TEXT"}, {"START_TIME", "Long"}}
        query = "SELECT * FROM " + GAME_TABLE_NAME;
        data = db.rawQuery(query, null);
        
        while (data.moveToNext()){
            if(teamMap.get(data.getString(1)) != null && teamMap.get(data.getString(2)) != null) {
                Game tempGame = new Game(teamMap.get(data.getString(1)),
                        teamMap.get(data.getString(2)));
                tempGame.setID(data.getString(0));
                tempGame.setStartTime(data.getLong(3));
    
                //stats
                Map<String, Double> tempMap = tempGame.getStatsMap();
                for (int i = 0; i < tempGame.getStatsMap().values().size(); i++) {
                    tempMap.put(GAME_TABLE_COLS[4+i][0], data.getDouble(4+i));
                }
                tempGame.setStatsMap(tempMap);
                
                //add game to its teams
                if (tempGame.getStartTime().getTime() > new Date().getTime()) {
                    tempGame.getHomeTeam().addFutureGame(tempGame);
                    tempGame.getAwayTeam().addFutureGame(tempGame);
                    futureGameIDs.add(tempGame.getID());
                } else {
                    tempGame.getHomeTeam().addPreviousGame(tempGame);
                    tempGame.getAwayTeam().addPreviousGame(tempGame);
                    pastGameIDs.add(tempGame.getID());
                }
    
                if(!gameMap.containsValue(tempGame))
                    gameMap.put(tempGame.getID(), tempGame);
            }
        }
        
        //load predictions
        // {{"ID", "STRING"},
        // {"PREDICTED_OUTCOME", "DOUBLE"},{"GAME_ID", "TEXT"}}
        query = "SELECT * FROM " + PREDICTION_TABLE_NAME;
        data = db.rawQuery(query, null);
        
        while (data.moveToNext()){
            if(gameMap.get(data.getString(2)) != null) {
                Prediction tempPrediction = new Prediction();
                tempPrediction.setGame(gameMap.get(data.getString(2)));
                tempPrediction.setID(data.getString(0));
                tempPrediction.setPredictedOutcome(data.getDouble(1));
                
                if(!predictionMap.containsValue(tempPrediction))
                    predictionMap.put(tempPrediction.getID(), tempPrediction);
            }
        }
        
        data.close();
        db.close();
        
        Log.d(TAG, "Loaded everything from database");
        
        //return true if no problems
        return true;
        
//        } catch (Exception e){
//            e.printStackTrace();
//            Log.d(TAG, "Didn't Load from database");
//            return false;
//        }
        
    }
    
    //returning a list of objects
    public List<Team> getTeams(){
        this.loadDataFromDB();
        return new ArrayList<>(teamMap.values());
    }
    public List<Game> getGames(){
        this.loadDataFromDB();
        return new ArrayList<>(gameMap.values());
    }
    public List<Prediction> getPredictions(){
        this.loadDataFromDB();
        return new ArrayList<>(predictionMap.values());
    }
    public List<Game> getFutureGames(){
        this.loadDataFromDB();
        
        List<Game> output = new ArrayList<>();
        for(Game game: gameMap.values())
            if(game.getStartTime().getTime() > new Date().getTime())
                output.add(game);
        
        return output;
    }
    public List<Game> getPastGames() {
        this.loadDataFromDB();
        
        List<Game> output = new ArrayList<>();
        for(Game game: gameMap.values())
            if(game.getStartTime().getTime() < new Date().getTime())
                output.add(game);
    
        return output;
    }
    
    public Team getTeam(String ID){
        this.loadDataFromDB();
        return teamMap.get(ID);
    }
    public Game getGame(String ID){
        this.loadDataFromDB();
        return gameMap.get(ID);
    }
    public Prediction getPrediction(String ID){
        this.loadDataFromDB();
        return predictionMap.get(ID);
    }
    // for future reference
//
//    /**
//     * Returns all the data from database
//     * @return
//     */
//    public Cursor getData(){
//        SQLiteDatabase db = this.getWritableDatabase();
//        String query = "SELECT * FROM " + TABLE_NAME;
//        Cursor data = db.rawQuery(query, null);
//        return data;
//    }
//
//    /**
//     * Returns only the ID that matches the name passed in
//     * @param name
//     * @return
//     */
//    public Cursor getItemID(String name){
//        SQLiteDatabase db = this.getWritableDatabase();
//        String query = "SELECT " + COL1 + " FROM " + TABLE_NAME +
//                " WHERE " + COL2 + " = '" + name + "'";
//        Cursor data = db.rawQuery(query, null);
//        return data;
//    }
//
//    /**
//     * Updates the name field
//     * @param newName
//     * @param id
//     * @param oldName
//     */
//    public void updateName(String newName, int id, String oldName){
//        SQLiteDatabase db = this.getWritableDatabase();
//        String query = "UPDATE " + TABLE_NAME + " SET " + COL2 +
//                " = '" + newName + "' WHERE " + COL1 + " = '" + id + "'" +
//                " AND " + COL2 + " = '" + oldName + "'";
//        Log.d(TAG, "updateName: query: " + query);
//        Log.d(TAG, "updateName: Setting name to " + newName);
//        db.execSQL(query);
//    }
//
//    /**
//     * Delete from database
//     * @param id
//     * @param name
//     */
//    public void deleteName(int id, String name){
//        SQLiteDatabase db = this.getWritableDatabase();
//        String query = "DELETE FROM " + TABLE_NAME + " WHERE "
//                + COL1 + " = '" + id + "'" +
//                " AND " + COL2 + " = '" + name + "'";
//        Log.d(TAG, "deleteName: query: " + query);
//        Log.d(TAG, "deleteName: Deleting " + name + " from database.");
//        db.execSQL(query);
//    }
}
