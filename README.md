# ANE-Wizard #
***
#####This is a fork of repo by [freshplanet](https://github.com/freshplanet/ANE-Wizard) with some CHANGES.
#####Now an input for the extension package.
***
 






This wizard is a set of tools to help Air Mobile developers build ANE (Air Native Extensions) faster.

**What does it do exactly?**

The wizard is composed of 4 tools:

- a Python script that will create the appropriate folders hierarchy for your ANE, and setup an Ant script to let you compile the ANE quickly
- an updated version of Divij Kumar's Xcode template (https://github.com/divijkumar/xcode-template-ane) that let you quickly setup the iOS part of your ANE
- an Eclipse "New ANE Project" wizard that let you quickly setup the Android part of your ANE
- a Flash Builder "New ANE Project" wizard that let you quickly setup the Actionscript part of your ANE

**Installation**

Note: the path below might change depending on your system. I assume you are on OSX.

1. Unzip _ANE-Wizard-Xcode.zip_ in the following directory: _~/Library/Developer/Xcode/Templates/Project Templates_ (then restart Xcode)
2. Copy _ANE-Wizard-Eclipse.jar_ in the following directory: _/Applications/eclipse/plugins_ (then restart Eclipse)
3. Copy _ANE-Wizard-FlashBuilder.jar_ in the following directory: _/Applications/Adobe Flash Builder 4.6/eclipse/plugins_ (then restart Flash Builder)

**Creating a new ANE**

Let's say you want to create an extension named MyExtension at the path ~/Projects/MyExtension

1. In the Terminal, go to the ANE-Wizard folder.
2. Run _./newANE.py MyExtension ~/Projects/MyExtensions_ (at this point you should see new folders at ~/Projects/MyExtension)
3. Create a new Xcode project: File -> New -> Project -> Air Native Extension -> Air Native Extension for iOS. When prompted to choose a folder, choose ~/Projects/MyExtension/NativeIOS
4. Create a new Eclipse project: File -> New -> Project -> Air Native Extension (ANE) -> ANE Project. When prompted to choose a folder, choose ~/Projects/MyExtension/NativeAndroid
5. Create a new Flash Builder project: File -> New -> Project -> Air Native Extension (ANE) -> ANE Project. When prompted to choose a folder, choose ~/Projects/MyExtension/AS
6. In the Terminal, go to ~/Projects/MyExtension.
7. Run _ant_ (at this point your ANE is at ~/Projects/MyExtension/Binaries/MyExtension.ane)

**Work in progress**

This wizard is a work in progress. We use it at FreshPlanet to speed up the development of our own ANEs and thought it might be useful to other developers. We haven't taken the time to make it
completely independent, so you might want to update some of the auto-generated files.

For example, the Eclipse and Flash Builder wizards use the com.freshplanet.ane package. You might want to replace it with your own. Also, the _build.properties_ and _NativeAndroid/.classpath_
files link to a Air SDK path that might be different on your machine.

**Problems / Feedback**

If you encounter an issue using this wizard, you can create a new Issue on Github. We'll do our best to help you, but don't guarantee we'll have time to do it promptly.

If you need more information about ANEs, we recommand you read the following articles:

- http://www.adobe.com/devnet/air/native-extensions-for-air.html
- http://www.adobe.com/devnet/air/articles/transferring-data-ane-ios-pt1.html
- http://www.adobe.com/devnet/air/articles/extending-air.html
