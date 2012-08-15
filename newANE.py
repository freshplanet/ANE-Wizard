#!/usr/bin/python
import sys, os, shutil

def createANE(name, path):
	if not os.path.exists(path): os.makedirs(path)

	for folder in ["AS", "NativeIOS", "NativeAndroid"]:
		subpath = os.path.join(path, folder)
		if not os.path.exists(subpath): os.makedirs(subpath)

	buildProperties = "PROJECT_NAME = %s\n\n%s" % (name, open('resources/build.properties').read())
	buildPropertiesFile = open(os.path.join(path, "build.properties"), "w")
	buildPropertiesFile.write(buildProperties)
	buildPropertiesFile.close()

	shutil.copy('resources/build.xml', path)


def main():
	if len(sys.argv) < 3:
		raise Exception("One argument (ANE name and path) expected!")
	else:
		createANE(sys.argv[1], sys.argv[2])


if __name__ == "__main__":
	main()