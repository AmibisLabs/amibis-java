

SERVER=oberon
SERVERPATH=/var/www/release
DISTON=${USER}@${SERVER}:${SERVERPATH}
BASEJAR=omiscid.jar
INTERFACEJAR=omiscid-use.jar
CLASSDIR=bin

dist: prepare
	scp $(BASEJAR) $(DISTON)
	mv $(BASEJAR) ${HOME}/tmp
	scp $(INTERFACEJAR) $(DISTON)
	mv $(INTERFACEJAR) ${HOME}/tmp
	make -f tools.mk checkdomain

checkdomain:
	@find src -name \*.java -exec egrep -e '_bip.+\._tcp' {} \; -exec echo "!!!!!!!!!!!!!!!!!!!!" \; -exec echo "!! DOMAIN WARNING !!" \; -exec echo "!!!!!!!!!!!!!!!!!!!!" \;

prepare:
	rm -rf ,,build
	cp -r $(CLASSDIR) ,,build
	cp CHANGES ,,build/
	cd src            && find . -name \*.java -exec cp {} ../,,build/{} \;
	cd generated-src  && find . -name \*.java -exec cp {} ../,,build/{} \;
	cd user-level-src && find . -name \*.java -exec cp {} ../,,build/{} \;
	cd ,,build && jar cvf ../$(BASEJAR) .
	cd ,,build && ( \
		rm cfg.properties ;\
		rm -r fr/prima/omiscid/com ;\
		rm -r fr/prima/omiscid/control ;\
		rm -r fr/prima/omiscid/dnssd ;\
		rm -r fr/prima/omiscid/user/*/impl ;\
		)
	cd ,,build && jar cvf ../$(INTERFACEJAR) .

fix-headers:
	find src -name \*.java -exec ./set-header.sh {} \;
	find user-level-src -name \*.java -exec ./set-header.sh {} \;
