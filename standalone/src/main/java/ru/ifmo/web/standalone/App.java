package ru.ifmo.web.standalone;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import ru.ifmo.web.database.dao.MenagerieDAO;
import ru.ifmo.web.service.MenagerieService;

import javax.sql.DataSource;
import javax.xml.ws.Endpoint;
import java.io.InputStream;
import java.util.Properties;

@Slf4j
public class App {
    public static void main(String... args) {
        String url = "http://0.0.0.0:8081/menagerie";

        DataSource dataSource = initDataSource();
        System.setProperty("com.sun.xml.ws.fault.SOAPFaultBuilder.disableCaptureStackTrace",
                "false");
        Endpoint.publish(url, new MenagerieService(new MenagerieDAO(dataSource)));
        log.info("Application started");
    }

    @SneakyThrows
    private static DataSource initDataSource() {
        InputStream dsPropsStream = App.class.getClassLoader().getResourceAsStream("datasource.properties");
        Properties dsProps = new Properties();
        dsProps.load(dsPropsStream);
        HikariConfig hikariConfig = new HikariConfig(dsProps);
        HikariDataSource dataSource = new HikariDataSource(hikariConfig);
        return dataSource;
    }
}
