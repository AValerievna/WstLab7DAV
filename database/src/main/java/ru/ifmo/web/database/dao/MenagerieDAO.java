package ru.ifmo.web.database.dao;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.ifmo.web.database.entity.Menagerie;

import javax.sql.DataSource;
import javax.xml.datatype.XMLGregorianCalendar;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

@RequiredArgsConstructor
@Slf4j
public class MenagerieDAO {
    private final DataSource dataSource;

    private final String TABLE_NAME = "menagerie";
    private final String ID = "id";
    private final String ANIMAL = "animal";
    private final String NAME = "name";
    private final String BREED = "breed";
    private final String HEALTH = "health";
    private final String ARRIVAL = "arrival";

    private final List<String> columnNames = Arrays.asList(ID, ANIMAL, NAME, BREED, HEALTH, ARRIVAL);

    public List<Menagerie> findAll() throws SQLException {
        log.info("Find all");
        try (Connection connection = dataSource.getConnection()) {
            java.sql.Statement statement = connection.createStatement();
            StringBuilder query = new StringBuilder();
            statement.execute(query.append("SELECT ").append(String.join(", ", columnNames)).append(" FROM ").append(TABLE_NAME).toString());
            List<Menagerie> result = resultSetToList(statement.getResultSet());
            return result;
        }
    }

    public List<Menagerie> findWithFilters(Long id, String animal, String name, String breed, String health, Date arrival) throws SQLException {
        log.debug("Find with filters: {} {} {} {} {} {}", id, animal, name, breed, health, arrival);
        if (Stream.of(id, animal, name, breed, health, arrival).allMatch(Objects::isNull)) {
            return findAll();
        }

        StringBuilder query = new StringBuilder();
        query.append("SELECT ").append(String.join(",", columnNames)).append(" FROM ").append(TABLE_NAME).append(" WHERE ");
        int i = 1;
        List<Statement> statements = new ArrayList<>();
        if (id != null) {
            query.append(ID).append("= ?");
            statements.add(new Statement(i, id, getSqlType(Long.class)));
            i++;
        }
        if (animal != null) {
            if (!statements.isEmpty()) {
                query.append(" AND ");
            }
            query.append(ANIMAL).append("= ?");
            statements.add(new Statement(i, animal, getSqlType(String.class)));
            i++;
        }
        if (name != null) {
            if (!statements.isEmpty()) {
                query.append(" AND ");
            }
            query.append(NAME).append("= ?");
            statements.add(new Statement(i, name, getSqlType(String.class)));
            i++;
        }
        if (breed != null) {
            if (!statements.isEmpty()) {
                query.append(" AND ");
            }
            query.append(BREED).append("= ?");
            statements.add(new Statement(i, breed, getSqlType(String.class)));
            i++;
        }
        if (health != null) {
            if (!statements.isEmpty()) {
                query.append(" AND ");
            }
            query.append(HEALTH).append("= ?");
            statements.add(new Statement(i, health, getSqlType(String.class)));
            i++;
        }
        if (arrival != null) {
            if (!statements.isEmpty()) {
                query.append(" AND ");
            }
            query.append(ARRIVAL).append("= ?");
            statements.add(new Statement(i, arrival, getSqlType(Date.class)));
        }

        log.debug("Query string {}", query.toString());
        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement ps = connection.prepareStatement(query.toString());
            fillPreparedStatement(ps, statements);
            ResultSet rs = ps.executeQuery();
            return resultSetToList(rs);
        }

    }

    public Long create(String animal, String name, String breed, String health, Date arrival) throws SQLException {
        log.debug("Create with params {} {} {} {} {}", animal, name, breed, health, arrival);
        try (Connection connection = dataSource.getConnection()) {
            StringBuilder query = new StringBuilder();
            query.append("INSERT INTO ").append(TABLE_NAME).append("(").append(String.join(",", columnNames)).append(") VALUES(?,?,?,?,?,?)");
            connection.setAutoCommit(false);
            long newId;
            try (java.sql.Statement idStatement = connection.createStatement()) {
                idStatement.execute("SELECT nextval('menagerie_id_seq') nextval");
                try (ResultSet rs = idStatement.getResultSet()) {
                    rs.next();
                    newId = rs.getLong("nextval");
                }

            }
            try (PreparedStatement stmnt = connection.prepareStatement(query.toString())) {
                stmnt.setLong(1, newId);
                stmnt.setString(2, animal);
                stmnt.setString(3, name);
                stmnt.setString(4, breed);
                stmnt.setString(5, health);
                stmnt.setDate(6, new java.sql.Date(arrival.getTime()));
                int count = stmnt.executeUpdate();
                if (count == 0) {
                    throw new RuntimeException("Could not execute query");
                }
            }
            connection.commit();
            connection.setAutoCommit(true);
            return newId;
        }
    }

    public int delete(long id) throws SQLException {
        log.debug("Delete with id {}", id);
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(true);
            try (PreparedStatement ps = connection.prepareStatement("DELETE FROM " + TABLE_NAME + " WHERE id = ?")) {
                ps.setLong(1, id);
                return ps.executeUpdate();
            }
        }
    }

    public int update(long id, String animal, String name, String breed, String health, Date arrival) throws SQLException {
        log.debug("Update id {} and new values {} {} {} {} {}", id, animal, name, breed, health, arrival);
        try (Connection conn = dataSource.getConnection()) {
            conn.setAutoCommit(true);
            StringBuilder query = new StringBuilder("UPDATE " + TABLE_NAME + " SET ");
            int i = 1;
            List<Statement> statements = new ArrayList<>();
            if (animal != null) {
                query.append(ANIMAL).append("= ?");
                statements.add(new Statement(i, animal, getSqlType(String.class)));
                i++;
            }
            if (name != null) {
                if (!statements.isEmpty()) {
                    query.append(", ");
                }
                query.append(NAME).append("= ?");
                statements.add(new Statement(i, name, getSqlType(String.class)));
                i++;
            }
            if (breed != null) {
                if (!statements.isEmpty()) {
                    query.append(", ");
                }
                query.append(BREED).append("= ?");
                statements.add(new Statement(i, breed, getSqlType(String.class)));
                i++;
            }
            if (health != null) {
                if (!statements.isEmpty()) {
                    query.append(", ");
                }
                query.append(HEALTH).append("= ?");
                statements.add(new Statement(i, health, getSqlType(String.class)));
                i++;
            }
            if (arrival != null) {
                if (!statements.isEmpty()) {
                    query.append(", ");
                }
                query.append(ARRIVAL).append("= ?");
                statements.add(new Statement(i, arrival, getSqlType(Date.class)));
                i++;
            }

            statements.add(new Statement(i, id, getSqlType(Long.class)));
            query.append(" WHERE id = ?");
            try (PreparedStatement ps = conn.prepareStatement(query.toString())) {
                fillPreparedStatement(ps, statements);
                int updated = ps.executeUpdate();
                return updated;
            }
        }
    }

    private List<Menagerie> resultSetToList(ResultSet rs) throws SQLException {
        List<Menagerie> result = new ArrayList<>();
        while (rs.next()) {
            result.add(resultSetToEntity(rs));
        }
        return result;
    }

    private Menagerie resultSetToEntity(ResultSet rs) throws SQLException {
        long id = rs.getLong(ID);
        String animal = rs.getString(ANIMAL);
        String name = rs.getString(NAME);
        String breed = rs.getString(BREED);
        String health = rs.getString(HEALTH);
        Date arrival = rs.getDate(ARRIVAL);
        return new Menagerie(id, animal, name, breed, health, arrival);
    }

    private void fillPreparedStatement(PreparedStatement ps, List<Statement> statements) throws SQLException {
        for (Statement statement : statements) {
            if (statement.getValue() == null) {
                ps.setNull(statement.number, statement.sqlType);
            } else {
                switch (statement.getSqlType()) {
                    case Types.BIGINT:
                        ps.setLong(statement.number, (Long) statement.getValue());
                        break;
                    case Types.VARCHAR:
                        ps.setString(statement.number, (String) statement.getValue());
                        break;
                    case Types.TIMESTAMP:
                        ps.setDate(statement.number, (java.sql.Date) statement.getValue());
                        break;
                    default:
                        throw new RuntimeException(statement.toString());
                }
            }
        }
    }

    private int getSqlType(Class<?> clazz) {
        if (clazz == Long.class) {
            return Types.BIGINT;
        } else if (clazz == String.class) {
            return Types.VARCHAR;
        } else if (clazz == Date.class) {
            return Types.TIMESTAMP;
        }
        throw new IllegalArgumentException(clazz.getName());
    }

    @Data
    @AllArgsConstructor
    private static class Statement {
        private int number;
        private Object value;
        private int sqlType;
    }

}
