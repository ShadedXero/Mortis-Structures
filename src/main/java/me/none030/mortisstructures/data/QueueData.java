package me.none030.mortisstructures.data;

import java.sql.ResultSet;

public class QueueData {

    private final String statement;
    private final QueueType type;
    private final Object[] objects;

    public QueueData(String statement, QueueType type, Object... objects) {
        this.statement = statement;
        this.type = type;
        this.objects = objects;
    }

    public ResultSet run(Database database) {
        switch (type) {
            case EXECUTE:
                if (objects != null) {
                    database.execute(statement, objects);
                }else {
                    database.execute(statement);
                }
                return null;
            case UPDATE:
                if (objects != null) {
                    database.update(statement, objects);
                }else {
                    database.update(statement);
                }
                return null;
            case QUERY:
                if (objects != null) {
                    return database.query(statement, objects);
                }else {
                    return database.query(statement);
                }
        }
        return null;
    }

    public String getStatement() {
        return statement;
    }

    public QueueType getType() {
        return type;
    }

    public Object[] getObjects() {
        return objects;
    }
}
