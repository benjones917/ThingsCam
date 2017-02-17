package com.ben.thingscam;

public class ThingsCamImages {
    String image;
    String label;

    public ThingsCamImages() {
    }

    public ThingsCamImages(String image, String label) {
        this.image = image;
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public String getImage() {
        return image;
    }
}
