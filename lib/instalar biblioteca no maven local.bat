@echo off
start mvn install:install-file "-Dfile=.\FirstApp-Preloader.jar" "-DgroupId=firstapp.preloader" "-DartifactId=FirstApp-Preloader" "-Dversion=1.0.0" "-Dpackaging=jar" 
