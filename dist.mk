


dist-on-venus:
	rm -rf ,,build
	cp -r bin ,,build
	#cp -r ../ServicesJava/bin/* ,,build
	#rm -rf ,,build/src
	#cd ,,build && jar xvf ../../ServicesJava/archive/external/dns_sd.jar
	#echo "Main-Class: fr.prima.bipgui.BipTools" > ,,build/MANIFEST.MF
	#echo "Class-Path: dns_sd.jar bipProject.jar" >> ,,build/MANIFEST.MF
	#cd ,,build && jar mcvf MANIFEST.MF ../bipTools.jar .
	cd ,,build && jar cvf ../bipProject.jar .
	scp bipProject.jar ${USER}@venus:/var/www/Prima/prima/people/${USER}/release
	mv bipProject.jar ${HOME}/tmp
