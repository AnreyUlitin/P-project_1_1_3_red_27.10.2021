package jm.task.core.jdbc.dao;
import java.sql.*;
import java.util.*;
import jm.task.core.jdbc.model.*;
import jm.task.core.jdbc.util.*;

public class UserDaoJDBCImpl implements UserDao {
    private Connection connection;

    public UserDaoJDBCImpl() {
        //пустой конструктор
    }


    public void createUsersTable() {
        try (Connection connection = Util.getConnection();
             Statement statement = connection.createStatement()) {
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS usersdao " +
                    "(id BIGINT PRIMARY KEY AUTO_INCREMENT, " +
                    "name VARCHAR(46) NOT NULL, " +
                    "lastName VARCHAR(64) NOT NULL, " +
                    "age TINYINT NOT NULL)");
            System.out.println("Создана таблица userdao");
            statement.close();
            connection.commit();
        } catch (SQLException e) {
            e.printStackTrace();
            try {
                connection.rollback();
            } catch (SQLException e1) {
                e1.printStackTrace();
            } finally {
                try {
                    connection.close();
                } catch (SQLException e2) {
                    e2.printStackTrace();
                }
            }
        }
    }


    public void dropUsersTable() {
        try (Connection connection = Util.getConnection();
             Statement statement = connection.createStatement()) {
            statement.executeUpdate("DROP TABLE IF EXISTS userdao");
            System.out.println("Drop user table");
            statement.close();
            connection.commit();
        } catch (SQLException e) {
            e.printStackTrace();
            try {
                connection.rollback();
            } catch (SQLException e1) {
                e1.printStackTrace();
            } finally {
                try {
                    connection.close();
                } catch (SQLException e2) {
                    e2.printStackTrace();
                }
            }
        }
    }


    public void saveUser(String name, String lastName, byte age) {
        PreparedStatement preparedStatement = null;
        try {
            connection = Util.getConnection();
            preparedStatement = connection.prepareStatement("INSERT INTO usersdao (name, lastname, age) VALUES (?, ?, ?)");
            preparedStatement.setString(1, name);
            preparedStatement.setString(2, lastName);
            preparedStatement.setByte(3, age);
            preparedStatement.executeUpdate();
            System.out.println("User с имя: " + name + " фамилия: " + lastName + " возраст: " + age + " добавлен в таблицу");
            connection.commit();
            preparedStatement.close();
        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
            e.printStackTrace();
        } finally {
            try {
                if (connection != null)
                    connection.close();
            } catch (SQLException e2) {
                e2.printStackTrace();
            }
        }
    }


    public void removeUserById(long id) {
        try {
            Connection connection = Util.getConnection();
            Statement statement = connection.createStatement();
            long byId = statement.executeUpdate("DELETE FROM usersdao WHERE id = " + id);
            System.out.println("Удалён пользователь с id = " + byId);
            connection.commit();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                try {
                    connection.rollback();
                } catch (SQLException e1) {
                    e1.printStackTrace();
                }
                connection.close();
            } catch (SQLException e2) {
                e2.printStackTrace();
            }
        }
    }


    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        try (Statement statement = connection.createStatement()) {
            ResultSet resultSet = statement.executeQuery("SELECT * FROM usersdao");
            while (resultSet.next()) {
                User user = new User();
                user.setId(resultSet.getLong("id"));
                user.setName(resultSet.getString("name"));
                user.setLastName(resultSet.getString("lastname"));
                user.setAge(resultSet.getByte("age"));
                users.add(user);
            }
            connection.commit();
            statement.close();
            resultSet.close();
        } catch (SQLException e) {
            e.printStackTrace();
            try {
                connection.close();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
        }
        System.out.println("Get all users");
        return users;
    }


    public void cleanUsersTable() {
        PreparedStatement statement = null;
        try {
            Connection connection = Util.getConnection();
            statement = connection.prepareStatement("TRUNCATE TABLE usersdao");
            statement.executeUpdate();
            System.out.println("Clean Users Table");
            connection.commit();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            connection.rollback();
        } catch (SQLException e1) {
            e1.printStackTrace();
        } finally {
            try {
                assert statement != null;
                statement.close();
            } catch (SQLException e2) {
                e2.printStackTrace();
            }
            try {
                connection.close();
            } catch (SQLException e3) {
                e3.printStackTrace();
            }
        }
    }
}
