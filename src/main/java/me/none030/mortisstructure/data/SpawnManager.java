package me.none030.mortisstructure.data;

import me.none030.mortisstructure.MortisStructure;
import me.none030.mortisstructure.structure.StructureManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;

public class SpawnManager {

    private final StructureManager manager;
    private final Connection connection;

    public SpawnManager(StructureManager manager) {
        this.manager = manager;
        connection = manager.getConnection();
        initializeDatabase();
        load();
    }

    private void initializeDatabase() {
        String sql = "CREATE TABLE IF NOT EXISTS MortisStructures(id varchar(100), time varchar(100));";
        try {
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.executeUpdate();
        } catch (SQLException exp) {
            exp.printStackTrace();
        }
    }

    public void load() {
        try {
            PreparedStatement stmt = connection.prepareStatement("SELECT * FROM MortisStructures;");
            ResultSet resultSet = stmt.executeQuery();
            if (resultSet.next()) {
                while (resultSet.next()) {
                    String id = resultSet.getString("id");
                    LocalDateTime time = LocalDateTime.parse(resultSet.getString("time"));
                    manager.getTimeById().put(id, time);
                }
            }
        }catch (SQLException exp) {
            exp.printStackTrace();
        }
    }

    private LocalDateTime getTime(String id) {
        try {
            String sql = "SELECT * FROM MortisStructures WHERE id = ?;";
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setString(1, id);
            ResultSet resultSet = stmt.executeQuery();
            if (!resultSet.next()) {
                return null;
            }
            return LocalDateTime.parse(resultSet.getString("time"));
        } catch (SQLException exp) {
            exp.printStackTrace();
        }
        return null;
    }

    private void updateTime(String id, LocalDateTime time) {
        try {
            String sql = "UPDATE MortisStructures SET time = ? WHERE id = ?";
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setString(1, time.toString());
            stmt.setString(2, id);
            stmt.executeUpdate();
        } catch (SQLException exp) {
            exp.printStackTrace();
        }
    }

    private void addTime(String id, LocalDateTime time) {
        try {
            String sql = "INSERT INTO MortisStructures(id, time) VALUES(?, ?);";
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setString(1, id);
            stmt.setString(2, time.toString());
            stmt.execute();
        } catch (SQLException exp) {
            exp.printStackTrace();
        }
    }

    public void saveTime(String id, LocalDateTime time) {
        if (getTime(id) == null) {
            addTime(id, time);
        }else {
            updateTime(id, time);
        }
        manager.getTimeById().put(id, time);
    }
}
