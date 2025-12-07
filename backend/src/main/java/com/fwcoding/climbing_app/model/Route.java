package com.fwcoding.climbing_app.model;

import com.fwcoding.climbing_app.model.enums.RouteStatus;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name="route")
public class Route {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long rid;

    @ManyToOne
    @JoinColumn(name = "uid", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "wid", nullable = false)
    private Wall wall;

    private String grade;
    private String color;

    @Enumerated(EnumType.STRING)
    private RouteStatus status; // ACTIVE, COMPLETED, ARCHIVED

    private String note;

    private String photoUrl;

}
