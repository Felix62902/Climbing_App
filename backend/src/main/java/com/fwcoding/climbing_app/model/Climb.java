package com.fwcoding.climbing_app.model;

import jakarta.persistence.Column;
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
@Table(name="climb")
public class Climb {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long cid;

    @ManyToOne
    @JoinColumn(name="sid", nullable = false)
    private Session session;

    @ManyToOne
    @JoinColumn(name="wid", nullable = false)
    private Wall wall;

    @ManyToOne
    @JoinColumn(name="pid")
    private Project project;

    @Column(nullable = false)
    private String grade;

    @Column(nullable = false)
    private String color;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ClimbStatus status; // flashed, attempted, sent

    private Integer attempts;
    private Integer score;

    private String note;
}
