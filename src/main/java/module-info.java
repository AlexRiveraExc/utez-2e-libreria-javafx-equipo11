module utez.edu.mx.libreria {
    requires javafx.controls;
    requires javafx.fxml;

    opens utez.edu.mx.libreria            to javafx.fxml;
    opens utez.edu.mx.libreria.controller to javafx.fxml;
    opens utez.edu.mx.libreria.model      to javafx.fxml;

    exports utez.edu.mx.libreria;
    exports utez.edu.mx.libreria.controller;
    exports utez.edu.mx.libreria.model;
    exports utez.edu.mx.libreria.service;
}