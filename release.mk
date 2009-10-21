
include release.conf

BASEJAR=target/OMiSCID-$(release-version).jar
PATCHEDBASEJAR=OMiSCID-$(release-version).jar
INTERFACEJAR=omiscidService.jar
m=make -f release.mk

remote-dist:
	$m ssh-test

	$m omiscid-jar
	$m clean-package-build
	$m release-package
	$m clean-destination

# displays a warning if default domain is wrong
	$m checkdomain


ssh-test:
	@ssh $(release-server) echo SSH OK

checkdomain:
	@find src -name \*.java -exec egrep -e '_bip.+\._tcp' {} \; -exec echo "!!!!!!!!!!!!!!!!!!!!" \; -exec echo "!! DOMAIN WARNING !!" \; -exec echo "!!!!!!!!!!!!!!!!!!!!" \;

release-package-upload:
	scp $(release-all-zip) $(release-diston)/

clean-package-build:
	rm -rf ,,rel
	mkdir  ,,rel

clean-destination:
# cleans the concerned destinations
	ssh $(release-server) rm -rf $(release-server-path)/$(release-version)
	ssh $(release-server) mkdir -p $(release-server-path)/$(release-version)

release-package:
	cp $(PATCHEDBASEJAR) ,,rel/$(release-omiscid-jar)
	cp licences $(patsubst %,lib/%,$(release-jars)) $(patsubst %,lib/%,$(release-libs)) ,,rel/
	mv ,,rel/ $(release-all-folder-name) && zip -r $(release-all-zip) $(release-all-folder-name)/ && mv $(release-all-folder-name)/ ,,rel

omiscid-jar:
	rm -rf ,,build
	mvn -Dmaven.test.skip=true install
	mkdir ,,build
	cd ,,build && jar xf ../$(BASEJAR)
	cp CHANGES ,,build/
	cd src/main/java                && find . -name \*.java -exec cp {} ../../../,,build/{} \;
	cd src/main/dnssd-src      && find . -name \*.java -exec cp {} ../../../,,build/{} \;
#cd generated-src  && find . -name \*.java -exec cp {} ../,,build/{} \;
	cd src/main/user-level-src && find . -name \*.java -exec cp {} ../../../,,build/{} \;
	cd ,,build && jar cvf ../$(PATCHEDBASEJAR) .
	cd ,,build && ( \
		rm -r fr/prima/omiscid/com ;\
		rm -r fr/prima/omiscid/control ;\
		rm -r fr/prima/omiscid/dnssd ;\
		rm -r fr/prima/omiscid/user/*/impl ;\
		rm -r org/ ;\
		)
	cd ,,build && jar cvf ../$(INTERFACEJAR) .

