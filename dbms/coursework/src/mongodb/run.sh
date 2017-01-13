#!/bin/sh

while : ; do
	node crud.js
	[ $? != 0 ] && break
done
