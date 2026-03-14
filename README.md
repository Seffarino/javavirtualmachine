# JavaVirtualMachine

Java compiler/interpreter project built with Java and JavaCC.

## Overview

`JavaVirtualMachine` is an academic project focused on compiler and interpreter design.  
It includes parsing, syntax analysis, type checking, interpretation, and execution logic for a custom Java-like language.

The project demonstrates key concepts of language processing and software construction, including lexical analysis, parsing, semantic checks, and runtime execution.

## Features

- Lexical and syntax analysis
- JavaCC-based parser
- Type checking
- Interpretation and execution logic
- Compiler/interpreter project structure
- Java-based implementation

## Tech Stack

- Java
- JavaCC
- Maven
- JavaFX
- Shell

## Project Structure

compiler/       # Compilation logic
interpreters/   # Interpretation and execution logic
lexer_parser/   # Lexical and syntax analysis
type_checker/   # Semantic analysis and type checking
gui/            # Graphical interface
Memory/         # Memory-related components
Prerequisites

Make sure you have installed:

Java

Maven

JavaFX

Installation

Clone the repository:
``
git clone https://github.com/Seffarino/javavirtualmachine.git
cd javavirtualmachine
``
Build the project with Maven:
```
mvn clean install
```
Run

To launch the generated .jar file with JavaFX:
```
java --module-path /path/to/javafx/lib --add-modules javafx.controls,javafx.fxml -jar comp5.jar
```
Replace /path/to/javafx/lib with the path to your local JavaFX installation.

Purpose

This project was developed as part of a Master's degree curriculum and showcases practical work on compiler/interpreter construction, syntax analysis, and program execution.

Topics Covered

Compiler design

Interpreter design

Parsing

Syntax analysis

Type checking

Runtime execution
