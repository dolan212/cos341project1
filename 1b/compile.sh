#!/bin/bash
java Lexer $1
java Parser output.txt
java Pruner temp.txt
