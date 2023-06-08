package ru.job4j.quartz;

import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

import java.io.FileInputStream;
import java.io.IOException;

import static org.quartz.JobBuilder.newJob;
import static org.quartz.SimpleScheduleBuilder.simpleSchedule;
import static org.quartz.TriggerBuilder.newTrigger;

public class AlertRabbit {
    public static void main(String[] args) {
        try {
            Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
            scheduler.start();
            JobDetail job = newJob(Rabbit.class).build();
            SimpleScheduleBuilder times = simpleSchedule()
                    .withIntervalInSeconds(Rabbit.properties())
                    .repeatForever();
            Trigger trigger = newTrigger()
                    .startNow()
                    .withSchedule(times)
                    .build();
            scheduler.scheduleJob(job, trigger);
        } catch (SchedulerException se) {
            se.printStackTrace();
        }
    }

    public static class Rabbit implements Job {
        @Override
        public void execute(JobExecutionContext context) {
            System.out.println("Rabbit runs here ...");
        }

        public static int properties() {
            try (FileInputStream in = new FileInputStream("C:\\Users\\Ivan_Kozhevnikov"
                    + "\\IdeaProjects\\job4j_grabber\\src\\main\\resources\\rabbit.properties")) {
                StringBuilder text = new StringBuilder();
                int read;
                while ((read = in.read()) != -1) {
                    text.append((char) read);
                }
                String[] split = text.toString().split("=");
                return Integer.parseInt(split[1].trim());
            } catch (IOException e) {
                e.printStackTrace();
            }
            return 0;
        }
    }
}