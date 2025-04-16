package com.esprit.Controllers;

import com.esprit.Entities.*;
import com.esprit.Main;
import com.esprit.Repository.*;
import com.esprit.utils.Modals;
import io.github.palexdev.materialfx.controls.*;
import io.github.palexdev.materialfx.dialogs.MFXGenericDialog;
import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.effect.BoxBlur;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

import static com.esprit.utils.Validator.isValidEmail;

public class UseraccountController implements Initializable {
    public Button Home_Btn, Users_Btn, Reclamations_Btn, Events_Btn, Logout_Btn;
    public Pane general_pane;
    public MFXButton Confirmer_AccountUser, Update_AccountUser, ConfirmerD0_AccountUser1;
    public MFXTextField tf_UserAccountNumero, tf_UserAccountEmail;
    public ImageView ImageviewUser;
    public MFXGenericDialog DialogConfirm_Delete, Dialog_UpdatePassword_User;
    public MFXButton AnnulerDelete1_User, ConfirmDelete1_User;
    public Label user_name;
    public MFXButton UpdatePassword_User, AnnulerUpdateP_User, ChangeImageBtn;
    private File selectedImageFile;
    public MFXTextField tf_UserOldPassword, tf_UserNewPassword;

    private final AuthRepository au = new AuthRepository();
    private final UserRepository ur = new UserRepository();
    private double xOffset = 0;
    private double yOffset = 0;
    final int x = 1315;
    final int y = 890;
    private Integer userId;
    String userName, userPrenom;
    UserRole userRole;

    private void setupButtonAnimation(MFXButton button) {
        ScaleTransition st = new ScaleTransition(Duration.millis(200), button);
        st.setFromX(1.0); st.setFromY(1.0);
        st.setToX(1.05); st.setToY(1.05);
        button.setUserData(st);
    }

    @FXML private void onButtonHoverEnter(MouseEvent e) {
        MFXButton b = (MFXButton) e.getSource();
        ((ScaleTransition) b.getUserData()).setRate(1.0); ((ScaleTransition) b.getUserData()).playFromStart();
    }

    @FXML private void onButtonHoverExit(MouseEvent e) {
        MFXButton b = (MFXButton) e.getSource();
        ((ScaleTransition) b.getUserData()).setRate(-1.0); ((ScaleTransition) b.getUserData()).play();
    }

    public void updateUser(ActionEvent e) {
        User u = ur.getUserById(userId);
        if (!isValidEmail(tf_UserAccountEmail.getText())) {
            Modals.displayError("Email invalide", "Veuillez entrer un email valide.");
            return;
        }
        u.setEmail(tf_UserAccountEmail.getText());
        u.setTelephone(Integer.parseInt(tf_UserAccountNumero.getText()));
        if (selectedImageFile != null) u.setPhotoUrl(selectedImageFile.getAbsolutePath());
        ur.updateEmailPhoneAndImage(u);
        Modals.displaySuccess("Succès", "Profil mis à jour.");
        afficherdetails();
    }

    @FXML private void changeImage(ActionEvent e) {
        FileChooser fc = new FileChooser();
        fc.setTitle("Choisir une image");
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg"));
        File f = fc.showOpenDialog(ChangeImageBtn.getScene().getWindow());
        if (f != null) {
            selectedImageFile = f;
            ImageviewUser.setImage(new Image(f.toURI().toString()));
        }
    }

    public void deleteUser(ActionEvent e) throws IOException {
        ur.deleteEntity(userId);
        Stage stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
        stage.setScene(new Scene(FXMLLoader.load(Main.class.getResource("/com/esprit/Views/mainLoginSignUp.fxml"))));
    }

    void afficherdetails() {
        User a = ur.getUserById(userId);  // 🔁 Meilleur choix que getUserByName()
        if (a == null) {
            System.out.println("Utilisateur non trouvé !");
            return;
        }

        // ✅ Mise à jour du nom dans l'interface
        user_name.setText("Bienvenue, " + a.getNom());

        // ✅ Chargement de l'image
        if (a.getPhotoUrl() != null) {
            try {
                Image image = new Image(a.getPhotoUrl());
                ImageviewUser.setImage(image);
            } catch (Exception ex) {
                System.out.println("Erreur de chargement d'image: " + ex.getMessage());
            }
        }

        tf_UserAccountEmail.setText(String.valueOf(a.getEmail()));
        tf_UserAccountNumero.setText(String.valueOf(a.getTelephone()));
    }


    private void getUserSession() {
        UserSession session = ApplicationContext.getInstance().getUserSession();
        if (session != null) {
            userId = session.getUserId();
            userName = session.getUserName();
            userPrenom = session.getPrenom();
            userRole = session.getRole();
        }
    }

    private void showAlert(String msg) {
        Platform.runLater(() -> new Alert(Alert.AlertType.INFORMATION, msg, ButtonType.OK).showAndWait());
    }
    public void onLogoutButtonClick(ActionEvent actionEvent) {
        try {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirmation Message");
            alert.setHeaderText(null);
            alert.setContentText("Are you sure you want to logout?");
            Optional<ButtonType> option = alert.showAndWait();
            if (option.get().equals(ButtonType.OK)) {
                Logout_Btn.getScene().getWindow().hide();
                Parent root = FXMLLoader.load(com.esprit.Main.class.getResource("/views/mainLoginSignUp.fxml"));
                Stage stage = new Stage();
                Scene scene = new Scene(root);
                root.setOnMousePressed((MouseEvent event) -> {
                    xOffset = event.getSceneX();
                    yOffset = event.getSceneY();
                });
                root.setOnMouseDragged((MouseEvent event) -> {
                    stage.setX(event.getScreenX() - xOffset);
                    stage.setY(event.getScreenY() - yOffset);
                    stage.setOpacity(.6);
                });

                root.setOnMouseReleased((MouseEvent event) -> {
                    stage.setOpacity(1);
                });
                stage.initStyle(StageStyle.TRANSPARENT);
                stage.setScene(scene);
                stage.show();
            }else return;

        }catch (Exception e) {e.printStackTrace();}
    }
    public void ondialogueupdatebtnClick(ActionEvent actionEvent) {
        Dialog_UpdatePassword_User.setVisible(false);
        general_pane.setEffect(null);
    }

    public void UpdatePassword(ActionEvent actionEvent) {
        if(actionEvent.getSource()==UpdatePassword_User)
        {
            if(au.validerPassword(userId,tf_UserOldPassword.getText()))
            {
                au.modifyPassword(userId,tf_UserNewPassword.getText());
            }
        }
    }
    public void afficherUpdate(ActionEvent actionEvent) {
        Platform.runLater(() -> {

            BoxBlur boxBlur = new BoxBlur(5, 5, 3);
            general_pane.setEffect(boxBlur);


            Dialog_UpdatePassword_User.setOpacity(0);
            Dialog_UpdatePassword_User.setVisible(true);

            FadeTransition fadeIn = new FadeTransition(Duration.seconds(0.5), Dialog_UpdatePassword_User);
            fadeIn.setToValue(1.0);
            fadeIn.setOnFinished(event -> general_pane.setEffect(boxBlur)); // Apply blur after fade-in
            fadeIn.play();
        });
    }
    public void afficherDeleteDialog(ActionEvent actionEvent) {
        DialogConfirm_Delete.setOpacity(0);
        DialogConfirm_Delete.setVisible(true);
        // Créer une transition de fondu pour simuler l'effet de coup d'éponge
        FadeTransition fadeIn = new FadeTransition(Duration.seconds(0.5), DialogConfirm_Delete);
        fadeIn.setToValue(1.0);
        fadeIn.play();

        BoxBlur boxBlur = new BoxBlur();
        boxBlur.setWidth(5);
        boxBlur.setHeight(5);
        boxBlur.setIterations(3);
        general_pane.setEffect(boxBlur);
    }
    public void closeDeleteD(ActionEvent actionEvent) {
        DialogConfirm_Delete.setVisible(false);
        general_pane.setEffect(null);
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        getUserSession();
        if (userId == null) {
            showAlert("Erreur : Session non trouvée. Veuillez vous connecter.");
            return;
        }
        setupButtonAnimation(Confirmer_AccountUser);
        setupButtonAnimation(Update_AccountUser);
        setupButtonAnimation(ConfirmerD0_AccountUser1);
        afficherdetails();

        // 🔗 Actions
        Confirmer_AccountUser.setOnAction(this::updateUser);
        ConfirmerD0_AccountUser1.setOnAction(e -> {
            try { deleteUser(e); } catch (IOException ex) { ex.printStackTrace(); }
        });
    }
}
