# DeepClas4BioIJComparator

DeepClas4BioIJComparator is an ImageJ plugin that connects ImageJ with the [DeepClas4Bio API](https://github.com/adines/DeepClas4Bio).  This plugin allows ImageJ users to use deep learning techniques for object classification abstracting deep learning techniques details and compare different models and framewroks in an easy way. 

DeepClas4BioIJComparator has been built on top of [SciJava Common](https://imagej.net/SciJava_Common) using SciJava Commands.

## Requirements
To use this plugin is necessary to have installed ImageJ with Java 8 and the [DeepClas4Bio API](https://github.com/adines/DeepClas4Bio).

## Installation
This plugin is updated in the ImageJ Update site and the recommendable way to install it is download the plugin using the [ImageJ Update Site](http://sites.imagej.net/Adines/).

## Using the plugin
In this section, we will see an example of how to use this plugin. For this example we will classify a lion image using different frameworks and models to compare them. In addition, you can use the models and the frameworks that best suit to your problem. 
To use the plugin you must follow the following steps:

 1. Load the image that you want to classify
![Loading the image](docs/images/001.png)


 2. Run the plugin
 
 Go to Plugins-->DeepClas4BioIJComparator


 3. Select the frameworks and the models you want use
 ![Select framework and model](docs/images/003.png)

 
 4. Visualize the output
 
 ![Visualize the output](docs/images/004.png)
