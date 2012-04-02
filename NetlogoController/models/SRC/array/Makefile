## Makefile template for NetLogo Extensions
## 

# NETLOGO is the directory containing the NetLogo install we are
# building against.  if not specified, it defaults to two dirs up from
# this directory
ifeq ($(origin NETLOGO), undefined)
     NETLOGO =../..
endif

# JAVA_HOME is the base of the java installation we will build with
ifeq ($(origin JAVA_HOME), undefined)
  $(error JAVA_HOME must be defined.)
endif



JAVA = $(JAVA_HOME)/bin/java
JAVAC = $(JAVA_HOME)/bin/javac
JAVACARGS=-g -deprecation -Xlint:all -Xlint:-serial -Xlint:-fallthrough -Xlint:-path -encoding us-ascii -source 1.5 -target 1.5
JAR = $(JAVA_HOME)/bin/jar
SLASH = /
COLON = :


OS = $(shell uname)

ifneq (,$(findstring CYGWIN,$(OS)))
SLASH = \\
COLON = \;
endif

SRCDIR = src
CLASSDIR = classes
LIBDIR = lib
DOCDIR = doc

## Include the configuration file
include config.mk

JAVAFILES = $(wildcard $(SRCDIR)/*.java)
CLASSFILES = $(patsubst $(SRCDIR)/%.java,$(CLASSDIR)/%.class,$(JAVAFILES))

empty=
space:=$(empty) $(empty)
NLJAR=$(NETLOGO)$(SLASH)NetLogo.jar
CLASSPATH=$(NLJAR)$(COLON)$(subst $(space),$(COLON),$(EXTERNAL_JARS))$(COLON)$(USERCLASSPATH)

BUILDPREFIX=build
BUILDDIR=$(BUILDPREFIX)/$(EXTENSION)
INSTALL=install


manifest.txt.versioned: $(NLJAR) manifest.txt
	rm -f $@
	cp manifest.txt $@
	/bin/echo -n "NetLogo-Extension-API-Version: " >> $@
	# In the following line, you might wonder -- 
	#   What is the 'cat' for?  
	#   i.e. Why do " | cat >> @$" instead of just ">> $@"? 
	# Well, the problem is that there is a stupid 
	# cygwin bug (that seems to be basically described here
	# http://www.cygwin.com/ml/cygwin/2007-07/msg00589.html, 
	# and the messages responding to this one. ).  Basically, the
	# bug causes the >> append-to-file redirection to overwrite part
	# of the file, instead of append, in some situations (at least
	# from System.out in Java?) So piping through cat seemed
	# to be the simplest work-around.  ~Forrest (12/13/2007)    
	$(JAVA) -cp $(NLJAR) org.nlogo.headless.Main --extension-api-version | /bin/cat >> $@

$(EXTENSION).jar: $(JAVAFILES) $(NLJAR) $(EXTERNAL_JARS) manifest.txt.versioned Makefile
	mkdir -p $(CLASSDIR)
	$(JAVAC) $(JAVACARGS) -classpath $(CLASSPATH) -d $(CLASSDIR) $(JAVAFILES)

	$(JAR) cmf manifest.txt.versioned $(EXTENSION).jar -C $(CLASSDIR) .

# cleaning
.PHONY: clean
clean:
	rm -rf $(CLASSDIR)
	rm -f $(EXTENSION).jar
	rm -rf $(BUILDPREFIX)
	rm -f manifest.txt.versioned
	rm -f $(EXTENSION).tgz


# REAL cleaning, as in scrubbing, to get the mildew out of your dirs
.PHONY: realclean
realclean: clean
	find . -name "\#*" -print0 | xargs -0 rm -rf
	find . -name "\.\#*" -print0 | xargs -0 rm -rf
	find . -name "*~" -print0 | xargs -0 rm -rf
	find . -name TAGS -print0 | xargs -0 rm -rf

docs:

lib:

.PHONY: prerelease
prerelease: $(EXTENSION).jar docs lib
	mkdir -p $(BUILDDIR)
	$(foreach jar, $(EXTERNAL_JARS), cp $(jar) $(BUILDDIR);)
	$(foreach distfile, $(DISTFILES), cp $(distfile) $(BUILDDIR);)
	$(foreach distdir, $(DISTDIRS), cp -r $(distdir) $(BUILDDIR);)
	if [ -d $(DOCDIR) ]; then cp -r $(DOCDIR) $(BUILDDIR); fi
	if [ -d $(LIBDIR) ]; then cp -r $(LIBDIR) $(BUILDDIR); fi
	if [ -d $(SRCDIR) ]; then cp -r $(SRCDIR) $(BUILDDIR); fi
	if [ -f README.txt ]; then cp -r README.txt $(BUILDDIR); fi
	cp $(EXTENSION).jar $(BUILDDIR)
	cp Makefile config.mk manifest.txt $(BUILDDIR)
	if test -n "$$(find . -maxdepth 1 -name '*.nlogo' -print -quit)" ; then cp *.nlogo $(BUILDDIR); fi
	(cd $(BUILDPREFIX); /usr/bin/find . -name CVS -print0 | xargs -0 rm -rf)
	(cd $(BUILDPREFIX); /usr/bin/find . -name .svn -print0 | xargs -0 rm -rf)

$(EXTENSION).tgz: prerelease
	tar czfC $@ $(BUILDPREFIX) $(EXTENSION)

$(EXTENSION).zip: prerelease
	cd $(BUILDPREFIX); zip -r ../$(EXTENSION).zip $(EXTENSION)
