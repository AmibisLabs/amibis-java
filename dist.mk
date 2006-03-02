


DISTON=${USER}@oberon:/var/www/release
BASEJAR=omiscid.jar
CLASSDIR=bin

dist: prepare
	scp $(BASEJAR) $(DISTON)
	mv $(BASEJAR) ${HOME}/tmp

prepare:
	rm -rf ,,build
	cp -r $(CLASSDIR) ,,build
	cd ,,build && jar cvf ../$(BASEJAR) .

