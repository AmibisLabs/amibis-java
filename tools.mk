
include release.conf

BASEJAR=omiscid.jar
INTERFACEJAR=omiscidService.jar
CLASSDIR=bin

dist: prepare
# builds the omiscid jar (BASEJAR) file using 'prepare'

# cleans the concerned destinations
	ssh $(release-server) rm -rf $(release-server-path)/$(release-version)
	ssh $(release-server) mkdir -p $(release-server-path)/$(release-version)
	rm -rf ,,rel
	mkdir  ,,rel
	rm -rf ,,rel-bundle
	mkdir  ,,rel-bundle

# builds the gui and replicates it
	make -f tools.mk gui
	scp ,,rel/$(release-omiscid-gui-jar) $(release-diston)/$(release-version)/
# copies other distribution files both locally and remotely
	scp $(BASEJAR) ,,rel/$(release-omiscid-jar)
	scp $(BASEJAR) $(release-diston)/$(release-version)/$(release-omiscid-jar)
	scp licences $(patsubst %,lib/%,$(release-jars)) $(patsubst %,lib/%,$(release-libs)) ,,rel/
	scp licences $(patsubst %,lib/%,$(release-jars)) $(patsubst %,lib/%,$(release-libs)) $(release-diston)/$(release-version)/
# builds the tgz version and replicates it remotely
	cd ,,rel/ && tar cvfz ../$(release-all-tgz) *
	scp $(release-all-tgz) $(release-diston)/

# builds the bundle tgz
	make -f tools.mk bundle
	cd ,,rel-bundle && tar cvfz ../$(release-bundle-tgz) *
	scp $(release-bundle-tgz) $(release-diston)/
# copy the interface jar
	scp $(INTERFACEJAR) $(release-diston)/

# displays a warning if domain is wrong
	make -f tools.mk checkdomain

gui:
	make -C ../jOMiSCIDGui -f dist.mk BASEJAR=../jOMiSCID/,,rel/$(release-omiscid-gui-jar) prepare

bundle:
# regenerate the libs used by the bundle
	rm -rf ../jOMiSCIDBundle/embeddedlibs/*
	rm -rf ../jOMiSCIDBundle/native/*
	scp ,,rel/$(release-omiscid-jar) $(patsubst %,lib/%,$(release-bundle-jars)) ../jOMiSCIDBundle/embeddedlibs/
	scp $(patsubst %,lib/%,$(release-libs)) ../jOMiSCIDBundle/native/
# clean the build dir
	rm -rf ../jOMiSCIDBundle/build
# patch manifest file
	cat ../jOMiSCIDBundle/manifest/manifest.mf | awk "/^Bundle-Version:/ {print \"Bundle-Version: $(release-bundle-version)\" ; next} /^Bundle-ClassPath:/ {print \"Bundle-ClassPath: $(release-bundle-classpath)\" ; next} /^Bundle-NativeCode:/ {print \"Bundle-NativeCode: $(release-bundle-native-code)\" ; next} // {print}" > ,,.mf
	cp ,,.mf ../jOMiSCIDBundle/manifest/manifest.mf
# call ant
	cd ../jOMiSCIDBundle/ && ant -f build.publish.xml -Dosgi.publish.dir=../jOMiSCID/,,rel-bundle/

checkdomain:
	@find src -name \*.java -exec egrep -e '_bip.+\._tcp' {} \; -exec echo "!!!!!!!!!!!!!!!!!!!!" \; -exec echo "!! DOMAIN WARNING !!" \; -exec echo "!!!!!!!!!!!!!!!!!!!!" \;

prepare:
	rm -rf ,,build
	cp -r $(CLASSDIR) ,,build
	cp CHANGES ,,build/
	cd src            && find . -name \*.java -exec cp {} ../,,build/{} \;
	cd dnssd-src      && find . -name \*.java -exec cp {} ../,,build/{} \;
	cd generated-src  && find . -name \*.java -exec cp {} ../,,build/{} \;
	cd user-level-src && find . -name \*.java -exec cp {} ../,,build/{} \;
	cd ,,build && jar cvf ../$(BASEJAR) .
	cd ,,build && ( \
		rm -r fr/prima/omiscid/com ;\
		rm -r fr/prima/omiscid/control ;\
		rm -r fr/prima/omiscid/dnssd ;\
		rm -r fr/prima/omiscid/user/*/impl ;\
		rm -r fr/prima/omiscid/test ;\
		rm -r org/ ;\
		)
	cd ,,build && jar cvf ../$(INTERFACEJAR) .

headers:
	find src -name \*.java -exec ./set-header.sh {} \;
	find dnssd-src -name \*.java -exec ./set-header.sh {} \;
	find user-level-src -name \*.java -exec ./set-header.sh {} \;
	find test -name \*.java -exec ./set-header.sh {} \;
