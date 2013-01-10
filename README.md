# ANE-Wizard #
***
#####This is a extended version of a repo by [freshplanet](https://github.com/freshplanet/ANE-Wizard).

***
 
**ANE-Wizard**

This wizard is a set of tools to help Air Mobile developers build ANE (Air Native Extensions) faster.

**What does it do exactly?**

The wizard is composed of 4 tools:

- an updated version of Divij Kumar's Xcode template (https://github.com/divijkumar/xcode-template-ane) that let you quickly setup the iOS part of your ANE
- an Eclipse "New ANE Project" wizard that let you quickly setup the Android part of your ANE

- a Flash Builder "New ANE Main Project" wizard that let you quickly setup two projects with a correct folders, the build script for the ANE and a second Actionscript project for the ANE
- a Flash Builder "New ANE Project" wizard that let you quickly setup the Actionscript part of your ANE
- 
**Installation**

Note: the path below might change depending on your system. I assume you are on OSX.

1. Unzip _ANE-Wizard-Xcode.zip_ in the following directory: _~/Library/Developer/Xcode/Templates/Project Templates_ (then restart Xcode)
2. Copy _ANE-Wizard-Eclipse.jar_  in the eclipse directory: _/Applications/eclipse/plugins_ (then restart Eclipse)
3. Copy _ANE-Wizard-FlashBuilder.jar_ and _ANE-Wizard-MainProject.jar_ in the adobe Flash builder directory: _/Applications/Adobe Flash Builder 4.6/eclipse/plugins_ (then restart Flash Builder)


**Creating a new ANE**

Let's say you want to create an extension named MyExtension at the path ~/Projects/MyExtension

1. Create a new Flash builder Main Project: File -> New -> Air Native Extension (ANE) -> ANE Main Project. When promped specyfy the Air sdk path and path of the project
2. Create a new Xcode project: File -> New -> Project -> Air Native Extension -> Air Native Extension for iOS. When prompted to choose a folder, choose ~/Projects/MyExtension/NativeIOS
3. Create a new Eclipse project: File -> New -> Project -> Air Native Extension (ANE) -> ANE Project. When prompted to choose a folder, choose ~/Projects/MyExtension/NativeAndroid
4. Run _ant_ (at this point your ANE is at ~/Projects/MyExtension/Binaries/MyExtension.ane)

**Work in progress**

This wizard is a work in progress. FreshPlanet was using it to speed up the development of thair own ANEs and I thought it might be useful to other developers to evolve it a little.

**Problems / Feedback**

If you encounter an issue using this wizard, you can create a new Issue on Github. We'll do our best to help you, but don't guarantee we'll have time to do it promptly.

If you need more information about ANEs, we recommand you read the following articles:

- http://www.adobe.com/devnet/air/native-extensions-for-air.html
- http://www.adobe.com/devnet/air/articles/transferring-data-ane-ios-pt1.html
- http://www.adobe.com/devnet/air/articles/extending-air.html