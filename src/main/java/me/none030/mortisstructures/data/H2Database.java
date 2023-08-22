package me.none030.mortisstructures.data;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class H2Database extends Database {

    private Connection connection;
    private final File file;

    public H2Database(File file, String username, String password) {
        super(null, 0, null, username, password);
        this.file = file;
    }

    public Connection getConnection() {
        if (connection != null) {
            return connection;
        }
        try {
            Class.forName("org.h2.Driver");
            this.connection = DriverManager.getConnection("jdbc:h2:file:" + file.getAbsolutePath(), getUsername(), getPassword());
            return connection;
        }catch (SQLException | ClassNotFoundException exp) {
            exp.printStackTrace();
        }
        return null;
    }
}
