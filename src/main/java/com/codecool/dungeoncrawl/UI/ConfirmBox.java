package com.codecool.dungeoncrawl.UI;

import javafx.stage.*;
import javafx.scene.*;
import javafx.scene.layout.*;
import javafx.scene.control.*;
import javafx.geometry.*;

public class ConfirmBox {
   static Boolean answer;

    public static Boolean display(String title, String msg){
        Stage window = new Stage();
        window.initModality(Modality.APPLICATION_MODAL);
        window.setTitle(title);
        window.setMinWidth(250);
        Label label = new Label();
        label.setText(msg);


        Button yesBtn = new Button("Yes");
        Button noBtn = new Button("No");

        yesBtn.setOnAction( e -> {
            answer = true;
            window.close();
        });

        noBtn.setOnAction( e -> {
            answer = false;
            window.close();
        });

        VBox layout = new VBox(10);
        layout.getChildren().addAll( label, yesBtn, noBtn);
        layout.setAlignment(Pos.CENTER);

        Scene scene = new Scene(layout);
        window.setScene(scene);
        window.showAndWait();

        return answer;


    }
}
