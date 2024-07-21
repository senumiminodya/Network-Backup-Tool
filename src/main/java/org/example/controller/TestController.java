package org.example.controller;

import com.jfoenix.controls.JFXButton;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import org.example.db.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class TestController {
    @FXML
    private JFXButton automaticalBackupBtn;

    @FXML
    private JFXButton backupBtn;

    @FXML
    private JFXButton saveBtn;

    @FXML
    private TextField txtAddress;

    @FXML
    private TextField txtDate;

    @FXML
    private TextField txtName;

    @FXML
    private TextField txtTime;

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    @FXML
    void btnAutomaticalBackupOnAction(ActionEvent event) {
        try {
            String date = txtDate.getText();
            String time = txtTime.getText();
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
            LocalDateTime dateTime = LocalDateTime.parse(date + "T" + time, DateTimeFormatter.ISO_LOCAL_DATE_TIME);

            scheduleBackup(dateTime);
        } catch (Exception e) {
            System.out.println("Invalid date/time format. Please use 'yyyy-mm-dd' for date and 'HH:mm' for time.");
        }
    }


    @FXML
    void btnBackupOnAction(ActionEvent event) {
        backupData();
    }

    private void backupData() {
        String selectSQL = "SELECT * FROM test1";
        String insertSQL = "INSERT INTO backup_test1 (name, address) VALUES (?, ?)";
        String deleteSQL = "DELETE FROM test1";

        try (Connection connection = DBConnection.getConnection();
             Statement selectStmt = connection.createStatement();
             ResultSet resultSet = selectStmt.executeQuery(selectSQL);
             PreparedStatement insertStmt = connection.prepareStatement(insertSQL)) {

            // Begin transaction
            connection.setAutoCommit(false);

            // Insert data into the backup table
            while (resultSet.next()) {
                String name = resultSet.getString("name");
                String address = resultSet.getString("address");

                insertStmt.setString(1, name);
                insertStmt.setString(2, address);
                insertStmt.addBatch();
            }

            insertStmt.executeBatch();

            // Clear data from the original table
            try (Statement deleteStmt = connection.createStatement()) {
                deleteStmt.executeUpdate(deleteSQL);
            }

            // Commit transaction
            connection.commit();

            System.out.println("Data backed up and cleared successfully.");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void scheduleBackup(LocalDateTime dateTime) {
        long delay = LocalDateTime.now().until(dateTime, ChronoUnit.SECONDS);
        if (delay > 0) {
            scheduler.schedule(this::backupData, delay, TimeUnit.SECONDS);
            System.out.println("Backup scheduled for: " + dateTime);
        } else {
            System.out.println("Scheduled time is in the past. Please choose a future time.");
        }
    }

    @FXML
    void btnSaveOnAction(ActionEvent event) {
        String name = txtName.getText();
        String address = txtAddress.getText();
        saveToDatabase(name, address);
        txtName.clear();
        txtAddress.clear();

    }

    private void saveToDatabase(String name, String address) {
        String sql = "INSERT INTO test1 (name, address) VALUES (?, ?)";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setString(1, name);
            preparedStatement.setString(2, address);

            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Data saved successfully.");
            } else {
                System.out.println("Failed to save data.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }


    }

}
