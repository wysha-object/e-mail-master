package cn.com.wysha.e_mail_master;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

public class JavaFXWebApplication extends Application {

    @Override
    public void start(Stage primaryStage) {
        WebView webView = new WebView();
        webView.getEngine().load("http://localhost:8080");

        primaryStage.setTitle("E-Mail Master");
        primaryStage.setScene(new Scene(webView, 1920, 1080));
        primaryStage.show();

    }

    @Override
    public void stop() {
        if (EMailMasterApplication.context != null) EMailMasterApplication.context.close();
    }
}