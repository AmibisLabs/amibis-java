
include release.conf

BASEJAR=omiscid.jar
INTERFACEJAR=omiscidService.jar
m=make -f tools.mk

remote-dist: 
	$m ssh-test
	$m clean-package-build
	$m clean-maven-build
	#$m clean-bundle-build

	$m omiscid-jar
	#$m bundle-zip
	$m gui-jar
	$m release-package
	$m maven-build
	$m clean-bundle-build

	$m clean-destination

	#$m omiscid-upload
	#$m bundle-upload
	#$m release-package-upload
	#$m gui-upload

# displays a warning if default domain is wrong
	$m checkdomain


headers:
	find src -name \*.java -exec ./set-header.sh {} \;
	find dnssd-src -name \*.java -exec ./set-header.sh {} \;
	find user-level-src -name \*.java -exec ./set-header.sh {} \;
	find test -name \*.java -exec ./set-header.sh {} \;
	find other-test -name \*.java -exec ./set-header.sh {} \;



ssh-test:
	@ssh $(release-server) echo SSH OK

checkdomain:
	@find src -name \*.java -exec egrep -e '_bip.+\._tcp' {} \; -exec echo "!!!!!!!!!!!!!!!!!!!!" \; -exec echo "!! DOMAIN WARNING !!" \; -exec echo "!!!!!!!!!!!!!!!!!!!!" \;

omiscid-upload:
	scp $(BASEJAR) $(release-diston)/$(release-version)/$(release-omiscid-jar)
	scp licences $(patsubst %,lib/%,$(release-jars)) $(patsubst %,lib/%,$(release-libs)) $(release-diston)/$(release-version)/

bundle-upload:
	scp $(release-bundle-zip) $(release-diston)/
# copy the interface jar
	scp $(INTERFACEJAR) $(release-diston)/

release-package-upload:
	scp $(release-all-zip) $(release-diston)/

gui-upload:
	scp ,,rel/$(release-omiscid-gui-jar) $(release-diston)/$(release-version)/


clean-maven-build:
	rm -rf ,,rel-maven

clean-bundle-build:
	rm -rf ,,rel-bundle
	mkdir  ,,rel-bundle

clean-package-build:
	rm -rf ,,rel
	mkdir  ,,rel

clean-destination:
# cleans the concerned destinations
	ssh $(release-server) rm -rf $(release-server-path)/$(release-version)
	ssh $(release-server) mkdir -p $(release-server-path)/$(release-version)

maven-build:
	mkdir -p  ,,rel-maven
	cat pom.core.xml | sed 's/@@@v@@@/'$(release-version)'/g' > ,,rel-maven/pom.xml
	cp $(BASEJAR) ,,rel-maven/$(release-omiscid-jar)
	cd ,,rel-maven && mvn deploy:deploy-file -Dfile=$(release-omiscid-jar) -DpomFile=pom.xml -DrepositoryId=archiva.internal -Durl=dav:http://oberon:8080/archiva/repository/internal/ 

maven-build-bundle:
	mkdir -p  ,,rel-maven
	cat pom.bundle.xml | sed 's/@@@v@@@/'$(release-version)'/g' | sed 's/@@@bv@@@/'$(release-bundle-version)'/g' > ,,rel-maven/pom.xml
	cp metadata.xml ,,rel-maven
	cd ,,rel-maven && mvn package -DpomFile=pom.xml
	cd ,,rel-maven && mvn deploy:deploy-file -Dfile=target/jomiscid-bundle-$(release-bundle-version).jar -DpomFile=pom.xml -DrepositoryId=prima.obr -Durl=scpexe://oberon/local_home/local/obr/
	cd ,,rel-maven && mvn org.apache.felix:maven-bundle-plugin:1.4.1:deploy-file -DremoteOBR -Dfile=target/jomiscid-bundle-$(release-bundle-version).jar -DpomFile=pom.xml -DrepositoryId=prima.obr -Duserauth.publickey= -Durl=scpexe://oberon/local_home/local/obr/

release-package:
	cp $(BASEJAR) ,,rel/$(release-omiscid-jar)
	cp licences $(patsubst %,lib/%,$(release-jars)) $(patsubst %,lib/%,$(release-libs)) ,,rel/
	mv ,,rel/ omiscid && zip -r $(release-all-zip) omiscid/ && mv omiscid/ ,,rel

omiscid-jar:
	rm -rf ,,build
	ant -Dfile.encoding=utf-8 compile
	cp -r build/classes ,,build
	cp CHANGES ,,build/
	cd src            && find . -name \*.java -exec cp {} ../,,build/{} \;
	cd dnssd-src      && find . -name \*.java -exec cp {} ../,,build/{} \;
#cd generated-src  && find . -name \*.java -exec cp {} ../,,build/{} \;
	cd user-level-src && find . -name \*.java -exec cp {} ../,,build/{} \;
	cd ,,build && jar cvf ../$(BASEJAR) .
	cd ,,build && ( \
		rm -r fr/prima/omiscid/com ;\
		rm -r fr/prima/omiscid/control ;\
		rm -r fr/prima/omiscid/dnssd ;\
		rm -r fr/prima/omiscid/user/*/impl ;\
		rm -r org/ ;\
		)
	cd ,,build && jar cvf ../$(INTERFACEJAR) .

gui-jar:
	make -C ../jOMiSCIDGui -f dist.mk BASEJAR=../jOMiSCID/,,rel/$(release-omiscid-gui-jar) prepare

bundle-zip:
# regenerate the libs used by the bundle
	rm -rf ../jOMiSCIDBundle/embeddedlibs/*
	rm -rf ../jOMiSCIDBundle/native/*
	cp $(BASEJAR) ../jOMiSCIDBundle/embeddedlibs/$(release-omiscid-gui-jar)
	cp $(patsubst %,lib/%,$(release-bundle-jars)) ../jOMiSCIDBundle/embeddedlibs/
	cp $(patsubst %,lib/%,$(release-libs)) ../jOMiSCIDBundle/native/
# clean the build dir
	rm -rf ../jOMiSCIDBundle/build
# patch manifest file
	cat ../jOMiSCIDBundle/manifest/manifest.mf | awk "/^Bundle-Version:/ {print \"Bundle-Version: $(release-bundle-version)\" ; next} /^Bundle-ClassPath:/ {print \"Bundle-ClassPath: $(release-bundle-classpath)\" ; next} /^Bundle-NativeCode:/ {print \"Bundle-NativeCode: $(release-bundle-native-code)\" ; next} // {print}" > ,,.mf
	cp ,,.mf ../jOMiSCIDBundle/manifest/manifest.mf
# call ant
	cd ../jOMiSCIDBundle/ && ant -f build.publish.xml -Dosgi.publish.dir=../jOMiSCID/,,rel-bundle/
	cd ,,rel-bundle && zip -r ../$(release-bundle-zip) *


#just a bunch of commands used for tests refactoring (conversion to junit)
# do-replacement 's@public class I0@\nimport org.junit.Test;\nimport static org.junit.Assert.*;\n\npublic class I0@g' I00{0,1,2,3,4}*java
# do-replacement 's/( *)public static void main\([^)]*\)/$1\@Test\n$1public void doIt()/g' I00{0,1,2,3,4}*java
# do-replacement 's/ *System.exit\([^;]*;[\n]//gm' I00{0,1,2,3,4}*java
# do-replacement 's/\@Test()/\@Test(expected=TestPassedPseudoException.class)/g' *.java



from=1

tests:
	ant test | tee ,,tests | sed 's/Tests run: 1\, Failures: 0\, Errors: 0,/================================= OK with: /g' | egrep -e '(\[junit\] Testsuite: )|(Failures: )|(==== OK)'
#	ant test | egrep -e '(\[junit\] Testsuite: )|(Failures: )' | sed 's/Tests run: 1\, Failures: 0\, Errors: 0,/================================= OK with: /g'
#	ant -Dfile.encoding=utf-8 jar
#	@./tests.sh "$(patsubst %,lib/%,$(release-jars))" ${from}



#.m2/setting.xml
# <settings>
#     <mirrors>
# 	<mirror>
# 	    <id>archiva.default</id>
# 	    <url>http://oberon:8080/archiva/repository/internal/</url>
# 	    <mirrorOf>*</mirrorOf>
# 	</mirror>
#     </mirrors>
#     <servers>
# 	<server>
# 	    <id>archiva.internal</id>
# 	    <username>prima</username>
# 	    <password>prima1</password>
# 	</server>
# 	<server>
# 	    <id>archiva.snapshots</id>
# 	    <username>prima</username>
# 	    <password>prima1</password>
#       	</server>	
# 	<server>
# 	    <id>prima.obr</id>
# 	</server>
#     </servers>
# </settings>
