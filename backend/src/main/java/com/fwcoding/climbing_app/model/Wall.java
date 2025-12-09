 package com.fwcoding.climbing_app.model;

import com.fwcoding.climbing_app.enums.WallType;

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
@Table(name = "wall")
public class Wall {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name="gid", nullable = false)
    private Gym gym;

    @ManyToOne
    @JoinColumn(name = "uid", nullable = false)
    private User user;

    private String name;
    private String photoUrl;

    @Enumerated(EnumType.STRING)
    private WallType defaultType;
}
