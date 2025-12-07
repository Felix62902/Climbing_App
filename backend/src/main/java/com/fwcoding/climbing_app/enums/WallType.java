package com.fwcoding.climbing_app.model.enums;

public enum WallType {
    SLAB, //Less thatn 90 degree
    VERTICAL, // Approx 90 degree
    OVERHANG, //Steep wall 45 degree
    ROOF,   //Parellel to Ground
    ARETE,  // Outward facing corner of the wall
    DIHEDRAL, //Inward facing Corner
    SPRAY_WALL // Dense training walls (MoonBoard, Kilter etc)
}
