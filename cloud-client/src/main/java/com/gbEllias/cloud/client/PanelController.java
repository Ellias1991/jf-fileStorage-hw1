package com.gbEllias.cloud.client;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;

import java.io.IOException;
import java.net.URL;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class PanelController implements Initializable {

    @FXML
    TableView<FileInfo> filesTable;

    @FXML
    ComboBox<String>discsBox;
    @FXML
    TextField PathField;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        TableColumn<FileInfo, String> fileTypeColumn = new TableColumn<>();//для каждого элемента таблицы создаем собственный столбец
        fileTypeColumn.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getType().getName()));//как создать столбец
        fileTypeColumn.setPrefWidth(24);

        TableColumn<FileInfo, String> fileNameColumn = new TableColumn<>("Имя");//для каждого элемента таблицы создаем собственный столбец

        fileNameColumn.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getFileName()));//как создать столбец
        fileNameColumn.setPrefWidth(240);

        TableColumn<FileInfo, Long> fileSizeColumn = new TableColumn<>("Размер");
        fileSizeColumn.setCellValueFactory(param -> new SimpleObjectProperty<>(param.getValue().getSize()));
        fileSizeColumn.setPrefWidth(240);


        fileSizeColumn.setCellFactory(column -> {
            return new TableCell<FileInfo, Long>() {//как выглядит ячейка в столбце
                @Override
                protected void updateItem(Long item, boolean empty) {
                    super.updateItem(item, empty);
                    if (item == null || empty) {
                        setText(null);
                        setStyle("");
                    } else {
                        String text = String.format("%,d bytes", item);
                        if (item == -1L) {
                            text = "[DIR]";
                        }
                        setText(text);
                    }
                }
            };
        });
        fileSizeColumn.setPrefWidth(120);

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        TableColumn<FileInfo, String> fileDateColumn = new TableColumn<>("Дата изменения");
        fileDateColumn.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getLastModified().format(dtf)));
        fileDateColumn.setPrefWidth(120);
        filesTable.getColumns().addAll(fileTypeColumn, fileNameColumn, fileSizeColumn, fileDateColumn);//добавить столбец в таблицу
        filesTable.getSortOrder().add(fileTypeColumn);

        discsBox.getItems().clear();//очищаем комбобокс
        for (Path p: FileSystems.getDefault().getRootDirectories()){//предоставление информации о файловой системе и запрашиваем список корневых диреткорий(диск С,D)
            discsBox.getItems().add(p.toString());
        }
          discsBox.getSelectionModel().select(0);//отображается диск первый по умолчанию
        updateList(Paths.get("."));//способ в Java NIO создать пути

          filesTable.setOnMouseClicked(new EventHandler<MouseEvent>() {//кликаем мышкой по файлам и папкам
              @Override
              public void handle(MouseEvent event) {
                  if(event.getClickCount()==2){
                     Path path=Paths.get(PathField.getText()).resolve(filesTable.getSelectionModel().getSelectedItem().getFileName());
                  if(Files.isDirectory(path)){
                      updateList(path);
                  }
                  }
              }
          });
    }

    public void updateList(Path path) {//метод который умеет из какого -то пути  по любому пути собрать список файлов

        try {
            PathField.setText(path.normalize().toAbsolutePath().toString());//метод setText позволяет в текстовое поле что-то написать
            filesTable.getItems().clear();//запрос списка элементов,которые лежат в таблице
            filesTable.getItems().addAll(Files.list(path).map(FileInfo::new).collect(Collectors.toList()));
            filesTable.sort();//таблица сортируется по умолчанию
        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Не удалось обновить список файлов", ButtonType.OK);
            alert.showAndWait();
        }

    }

    public void bthPathUpAction(ActionEvent actionEvent) {//работа кнопки Вверх
        Path upperPath=Paths.get(PathField.getText()).getParent();
        if(upperPath!= null){
            updateList(upperPath);
        }
    }

    public void SelectDiskAction(ActionEvent actionEvent) {//метод,который позволяет при выборе диска попадать на этот диск
        ComboBox<String> element=((ComboBox<String>)actionEvent.getSource());
        updateList(Paths.get(element.getSelectionModel().getSelectedItem()));

    }
    public String getSelectedFileName(){//какой файл выбран
        if(filesTable.isFocused()){
            return null;
        }
        return filesTable.getSelectionModel().getSelectedItem().getFileName();
    }
    public String getCurrentPath(){//куда указывает данная папка
        return PathField.getText();

    }


}
