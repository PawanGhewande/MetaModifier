/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package metamodifier;

import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXDatePicker;
import com.jfoenix.controls.JFXListView;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.controls.JFXTimePicker;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import static java.nio.file.LinkOption.NOFOLLOW_LINKS;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Random;
import java.util.ResourceBundle;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import javafx.util.StringConverter;

/**
 *
 * @author pawan
 */
public class MetaViewController implements Initializable {

    @FXML
    private TextField path;
    @FXML
    private JFXDatePicker createDate;
    @FXML
    private JFXTimePicker createTime;
    @FXML
    private JFXListView<String> listFiles;
    @FXML
    private JFXTextField created;
    @FXML
    private JFXTextField modified;
    @FXML
    private JFXTextField accessed;
    @FXML
    private JFXDatePicker modifiedDate;
    @FXML
    private JFXTimePicker modifiedTime;
    @FXML
    private JFXCheckBox random;
    private File[] FILES = null;
    private final Random rand = new Random();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        modifiedDate.setConverter(new StringConverter<LocalDate>() {
            String pattern = "dd-MMM-yyyy";
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern(pattern);

            {
                modifiedDate.setPromptText(pattern.toLowerCase());
            }

            @Override
            public String toString(LocalDate date) {
                if (date != null) {
                    return dateFormatter.format(date);
                } else {
                    return "";
                }
            }

            @Override
            public LocalDate fromString(String string) {
                if (string != null && !string.isEmpty()) {
                    return LocalDate.parse(string, dateFormatter);
                } else {
                    return null;
                }
            }
        });
        createDate.setConverter(new StringConverter<LocalDate>() {
            String pattern = "dd-MMM-yyyy";
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern(pattern);

            {
                createDate.setPromptText(pattern.toLowerCase());
            }

            @Override
            public String toString(LocalDate date) {
                if (date != null) {
                    return dateFormatter.format(date);
                } else {
                    return "";
                }
            }

            @Override
            public LocalDate fromString(String string) {
                if (string != null && !string.isEmpty()) {
                    return LocalDate.parse(string, dateFormatter);
                } else {
                    return null;
                }
            }
        });

//        random.setOnAction(event -> {
//            if (random.isSelected()) {
//                 long minute = (long) (rand.nextInt((int) 390E11)+ 8.4E10);
//                createTime.setValue(createTime.getValue().plusMinutes(minute));
//            }
//        });

        listFiles.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                try {
                    Path file = Paths.get(path.getText() + "/" + listFiles.getSelectionModel().getSelectedItem());

                    BasicFileAttributes attr = Files.readAttributes(file, BasicFileAttributes.class);

                    LocalDateTime createdFileTime = LocalDateTime.ofInstant(attr.creationTime().toInstant(), ZoneId.systemDefault());
                    LocalDateTime modifiedFileTime = LocalDateTime.ofInstant(attr.creationTime().toInstant(), ZoneId.systemDefault());
                    LocalDateTime accessedFileTime = LocalDateTime.ofInstant(attr.creationTime().toInstant(), ZoneId.systemDefault());

                    created.setText(createdFileTime.toLocalDate() + " " + createdFileTime.toLocalTime());
                    modified.setText(modifiedFileTime.toLocalDate() + " " + modifiedFileTime.toLocalTime());
                    accessed.setText(accessedFileTime.toLocalDate() + " " + accessedFileTime.toLocalTime());

                } catch (IOException ex) {
                    ex.printStackTrace();
                }

            }
        });
    }

    @FXML
    private void loadFrom() {
        listFiles.getItems().clear();
        path.setText(browse());
        prepareList();
    }

    private String browse() {
        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle("Select Folder");
        File fileLocation = chooser.showDialog(null);
        loadFiles(fileLocation.getAbsolutePath());
        return fileLocation.getAbsolutePath();
    }

    @FXML
    private void next() {
        listFiles.getSelectionModel().select(listFiles.getSelectionModel().getSelectedIndex() + 1);
    }

    private void loadFiles(String dir) {
        FILES = new File(dir).listFiles();
    }
    private ObservableList<String> listData = FXCollections.observableArrayList();

    private void prepareList() {
        Arrays.sort(FILES);
        for (File file : FILES) {
            listData.add(file.getName());
        }
        listFiles.setItems(listData);
    }

    @FXML
    private void change() throws IOException {
        if (path.getText().equals("") && listFiles.getItems().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Invalid Path");
            alert.setHeaderText(null);
            alert.setContentText("Bhai,\nDekh ke kar na sahi folder select kar na.\n Aise kaise chalega Bhaiya?");
            alert.showAndWait();
            path.requestFocus();
        } else if (createDate.getValue() == null || createTime.getValue() == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Invalid Date Or Time");
            alert.setHeaderText(null);
            alert.setContentText("Bhai,\nBina Date or Time ki file kaise banaunga.\n dekh k kam kar na.");
            alert.showAndWait();
            listFiles.requestFocus();
        } else {
            if (random.isSelected()) {
                long minute = (rand.nextInt(3)+6)+(rand.nextInt(5)+8);
                //long nano = rand.nextInt(5643)+2347;
                LocalTime plusMinutes = createTime.getValue().plusMinutes(minute);
                createTime.setValue(plusMinutes);
            }
            Path source = Paths.get(path.getText() + "/" + listFiles.getSelectionModel().getSelectedItem());
            LocalDateTime ldt = LocalDateTime.of(createDate.getValue(), createTime.getValue());
            FileTime fileTime = FileTime.fromMillis(ldt.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());
            Files.setAttribute(source, "basic:creationTime", fileTime, NOFOLLOW_LINKS);
            Files.setLastModifiedTime(source, fileTime);
            next();
        }
    }

}
