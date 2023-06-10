package ru.job4j.quartz;

import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

import java.io.InputStream;
import java.sql.*;
import java.util.Properties;

import static org.quartz.JobBuilder.newJob;
import static org.quartz.SimpleScheduleBuilder.simpleSchedule;
import static org.quartz.TriggerBuilder.newTrigger;

public class AlertRabbit {
    public static void main(String[] args) throws Exception {
        try {
            Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
            scheduler.start();
            JobDataMap data = new JobDataMap();
            data.put("connect", Rabbit.connection());
            JobDetail job = newJob(Rabbit.class)
                    .usingJobData(data)
                    .build();
            SimpleScheduleBuilder times = simpleSchedule()
                    .withIntervalInSeconds(Rabbit.propertiesInterval())
                    .repeatForever();
            Trigger trigger = newTrigger()
                    .startNow()
                    .withSchedule(times)
                    .build();
            scheduler.scheduleJob(job, trigger);
            Thread.sleep(10000);
            scheduler.shutdown();
        } catch (SchedulerException se) {
            se.printStackTrace();
        }
    }

    public static class Rabbit implements Job {

        private static Properties init() throws Exception {
            Properties properties = new Properties();
            try (InputStream in = Rabbit.class.getClassLoader()
                    .getResourceAsStream("rabbit.properties")) {
                properties.load(in);
            }
            return properties;
        }

        public static int propertiesInterval() throws Exception {
            return Integer.parseInt(init().getProperty("rabbit.interval"));
        }

        private static Connection connection() throws Exception {
            Properties properties = init();
            Class.forName(properties.getProperty("hibernate.connection.driver_class"));
            return DriverManager.getConnection(
                    properties.getProperty("hibernate.connection.url"),
                    properties.getProperty("hibernate.connection.username"),
                    properties.getProperty("hibernate.connection.password"));
        }

        @Override
        public void execute(JobExecutionContext context) {
            System.out.println("Rabbit runs here ...");
            Connection connection = (Connection) context
                    .getJobDetail()
                    .getJobDataMap()
                    .get("connect");
            try (PreparedStatement preparedStatement = connection
                    .prepareStatement("INSERT INTO rabbit(created_date) VALUES (?)")) {
                preparedStatement.setTimestamp(1,
                        new Timestamp(System.currentTimeMillis()));
                preparedStatement.execute();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
    }
}