

SERVER=oberon
SERVERPATH=/var/www/release
DISTON=${USER}@${SERVER}:${SERVERPATH}
BASEJAR=omiscid.jar
CLASSDIR=bin

dist: prepare
	scp $(BASEJAR) $(DISTON)
	mv $(BASEJAR) ${HOME}/tmp

prepare:
	rm -rf ,,build
	cp -r $(CLASSDIR) ,,build
	cd src && find . -name \*.java -exec cp {} ../,,build/{} \;
	cd ,,build && jar cvf ../$(BASEJAR) .

