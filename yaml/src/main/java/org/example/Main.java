package org.example;

import org.yaml.snakeyaml.Yaml;

public class Main {
    public static void main(String[] args) {
        System.out.println("Hello world!");
        Yaml yaml=new Yaml();
        yaml.load("payload");
    }



}