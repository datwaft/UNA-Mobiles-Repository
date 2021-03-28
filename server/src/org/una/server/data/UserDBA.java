package org.una.server.data;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;

public class UserDBA {
    private static UserDBA instance = null;

    private Connection connection = DatabaseConnection.getInstance().getConnection();

    public Boolean canLogIn(String username, String password) throws SQLException {
        CallableStatement query = connection.prepareCall("{ ? = call can_log_in( ?, ? ) }");
        query.registerOutParameter(1, Types.BOOLEAN);
        query.setString(2, username);
        query.setString(3, password);
        query.execute();
        return query.getBoolean(1);
    }

    public static UserDBA getInstance() {
        if (instance == null) instance = new UserDBA();
        return instance;
    }
}
