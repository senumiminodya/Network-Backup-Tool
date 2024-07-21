package org.example.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import org.example.db.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class TestController {
    @FXML
    private Button backupBtn;

    @FXML
    private Button saveBtn;

    @FXML
    private TextField txtAddress;

    @FXML
    private TextField txtName;

    @FXML
    void btnBackupOnAction(ActionEvent event) {
        
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
