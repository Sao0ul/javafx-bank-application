package BankSystem;

import java.net.URL;
import javafx.scene.image.ImageView;
import javafx.scene.control.ContentDisplay;
import javafx.scene.image.Image;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.control.Button;
import javafx.scene.effect.DropShadow;
import javafx.scene.paint.Color;

public class mainView extends VBox {

    public mainView(Stage primaryStage, double spacing) {

        super(spacing);
        this.prefWidthProperty().bind(primaryStage.widthProperty());
        this.prefHeightProperty().bind(primaryStage.heightProperty());

        this.setAlignment(Pos.CENTER);
        this.getStyleClass().add("mainSceneCss");

        // Label accueil
        Label label = new Label("Bienvenue dans le Système Bancaire");
        label.setAlignment(Pos.CENTER);
        label.getStyleClass().add("lbel");

        // Bouton Quitter initial
        Button quitButton = new Button("Quitter");
        quitButton.getStyleClass().add("my-button");

        // Image initiale
        URL imageUrlQuit = getClass().getClassLoader().getResource("images/shutdown.png");
        Image quitImg = new Image(imageUrlQuit.toExternalForm());
        ImageView quitImgView = new ImageView(quitImg);
        quitImgView.setFitWidth(30);
        quitImgView.setFitHeight(30);
        quitButton.setGraphic(quitImgView);
        quitButton.setContentDisplay(ContentDisplay.TOP);

        // Action fermeture
        quitButton.setOnAction(e -> {
            dataBaseManager.closeConnection(); // Ferme la connexion à la base de données
            primaryStage.close();
        });
        // Nouvelle image pour le survol
        URL newImageUrl = getClass().getClassLoader().getResource("images/new-shutdown.png");
        Image newImg = new Image(newImageUrl.toExternalForm());
        ImageView newImgView = new ImageView(newImg);
        newImgView.setFitWidth(60);
        newImgView.setFitHeight(60);

        // État initial du bouton pour restauration
        final String initialText = quitButton.getText();
        final ImageView initialImageView = quitImgView;
        final ContentDisplay initialContent = quitButton.getContentDisplay();
        final String initialStyle = quitButton.getStyle();

        // Survol
        quitButton.setOnMouseEntered(e -> {
            quitButton.setText(""); // supprime texte
            quitButton.setGraphic(newImgView); // nouvelle image
            quitButton.setContentDisplay(ContentDisplay.GRAPHIC_ONLY); // centre l'image
            quitButton.setStyle("-fx-background-color: red;"); // fond rouge
            quitButton.setEffect(new DropShadow(5, Color.DARKRED));
        });

        // Fin du survol
        quitButton.setOnMouseExited(e -> {
            quitButton.setText(initialText);
            quitButton.setGraphic(initialImageView);
            quitButton.setContentDisplay(initialContent);
            quitButton.setStyle(initialStyle);
            quitButton.setEffect(null);
        });

        // Boutons principaux
        mainButtonView root = new mainButtonView(primaryStage);

        // Layout principal
        this.getChildren().addAll(label, root, quitButton);
    }
}

// crer un projet rfx :mvn archetype:generate -D
// archetypeArtifactId=maven-archetype-quickstart

/*
 * javac -d name src/paiement/*.java
 * envoyer un les ficheirs .class dans un dossier nommé <name> qui sera cree par
 * javac
 */