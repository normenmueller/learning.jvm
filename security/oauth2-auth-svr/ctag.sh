#!/bin/bash
rm tags
rm -rf .libs
sbt unpackJars
ctags -R .
ctags -R -a ~/Sources/3rd/jvm/scala/src/library
