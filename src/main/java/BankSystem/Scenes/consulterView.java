package BankSystem;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.control.Button;
import javafx.geometry.Insets;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.effect.DropShadow;
import javafx.scene.paint.Color;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.TextField;

public class consulterView extends StackPane {

    public consulterView(Stage primaryStage) {
        this();
        this.prefWidthProperty().bind(primaryStage.widthProperty());
        this.prefHeightProperty().bind(primaryStage.heightProperty());

    }

    public consulterView() {
        // declaration des elemts
        TextField nameAccountTF = new TextField("Entrez le nom du compte");
        TextField passwordAccountTF = new TextField("Entrez le mot de passe");
        Label soldeTF = new Label("");
        Button ValiderButton = new Button("Valider");
        Button retourButton = new Button("Retour");

        // centrer le texte dans les textField
        nameAccountTF.setAlignment(Pos.CENTER);
        passwordAccountTF.setAlignment(Pos.CENTER);
        soldeTF.setAlignment(Pos.CENTER);

        // positionement des elements
        nameAccountTF.setTranslateY(-200);
        passwordAccountTF.setTranslateY(-150);
        soldeTF.setTranslateY(-100);
        retourButton.setTranslateY(100);

        // ajout de la classe
        nameAccountTF.getStyleClass().add("ViewClassLabel");
        passwordAccountTF.getStyleClass().add("ViewClassLabel");
        soldeTF.getStyleClass().add("ViewClassLabel");
        ValiderButton.getStyleClass().add("ViewClassButton");
        ValiderButton.getStyleClass().add("validerButton");
        retourButton.getStyleClass().add("ViewClassButton");
        retourButton.getStyleClass().add("retourButton");

        // ajout des ecouteur d'evenements
        ValiderButton.addEventHandler(ActionEvent.ACTION, e -> {
            // code pour consulter le solde
            String nameAccount = nameAccountTF.getText().trim(); // retire les espaces
            String passwordAccount = passwordAccountTF.getText().trim(); // retire les espaces
            if (dataBaseManager.getSolde(nameAccount, passwordAccount) != -1) {
                soldeTF.setText("Le solde de votre compte est de : "
                        + dataBaseManager.getSolde(nameAccount, passwordAccount) + " FCFA");
            }
        });

        retourButton.addEventHandler(ActionEvent.ACTION, e -> {
            managerView.showView(retourButton, new mainView(managerView.getPrimaryStage(), 50));
        });

        this.getChildren().addAll(nameAccountTF, passwordAccountTF, soldeTF, ValiderButton, retourButton);

    }

    // ajouter les comptes de depot avec les validations
}