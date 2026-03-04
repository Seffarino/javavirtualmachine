package fr.ufrst.m1info.comp5.gui;

import fr.ufrst.m1info.comp5.compiler.*;
import fr.ufrst.m1info.comp5.interpreters.MemoryAtMoment;
import fr.ufrst.m1info.comp5.interpreters.jajacode.*;
import fr.ufrst.m1info.comp5.interpreters.jajacode.InterpreterJjcVisitor;
import fr.ufrst.m1info.comp5.interpreters.minijaja.DebugMiniJaja;
import fr.ufrst.m1info.comp5.interpreters.minijaja.InterpreterError.InterpreterError;
import fr.ufrst.m1info.comp5.interpreters.minijaja.InterpreterVisitor;
import fr.ufrst.m1info.comp5.jajacode.ASTJJCStart;
import fr.ufrst.m1info.comp5.jajacode.JajaCodeAnalyser;
import fr.ufrst.m1info.comp5.memory.Memory;
import fr.ufrst.m1info.comp5.memory.SymbolTable;
import fr.ufrst.m1info.comp5.memory.TasException;
import fr.ufrst.m1info.comp5.minijaja.ASTMjjStart;
import fr.ufrst.m1info.comp5.minijaja.MiniJajaAnalyser;
import fr.ufrst.m1info.comp5.type_checker.TypeCheckerVisitor;
import fr.ufrst.m1info.comp5.type_checker.TypeError.TypeCheckerError;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.scene.input.KeyEvent;

import java.io.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

public class GuiController {

    @FXML
    private BorderPane primaryStage;

    @FXML
    private TextArea codeInput;

    @FXML
    private TextArea jajacodeInput;
    @FXML
    private TextArea logError;
    @FXML
    private TextArea lineCount;
    @FXML
    private TextArea lineCountjjc;

    private ArrayList<Integer> breakpoint;
    private ArrayList<Integer> breakpointjjc;
    @FXML
    private Label ligne;
    @FXML
    private Label colonne;

    @FXML
    private TextArea pile;
    @FXML
    private TextArea tas;
    private Memory memory;
    private List<InterpreterError> errors;
    private SymbolTable s;
    private List<InterpreterError> errorsI;
    private List<TypeCheckerError> errorsT;
    private LinkedList<String> jjcStack;

    private final DebugMiniJaja d = new DebugMiniJaja();
    private final DebugJajaCode j = new DebugJajaCode();

    private ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;


    @FXML
    public void initialize() {
        breakpoint = new ArrayList<>();
        breakpointjjc = new ArrayList<>();
        try {
            memory=new Memory();
        } catch (TasException e) {
            throw new RuntimeException(e);
        }
        errors = new ArrayList<>();
        s = memory.getSymbolTable();
        errorsI = new ArrayList<>();
        errorsT = new ArrayList<>();
        jjcStack = new LinkedList<>();

        lineCount.addEventFilter(KeyEvent.KEY_PRESSED, KeyEvent::consume);
        lineCount.addEventFilter(KeyEvent.KEY_RELEASED, KeyEvent::consume);
        lineCount.addEventFilter(KeyEvent.KEY_TYPED, KeyEvent::consume);

        lineCount.addEventFilter(KeyEvent.KEY_PRESSED, KeyEvent::consume);
        lineCount.addEventFilter(KeyEvent.KEY_RELEASED, KeyEvent::consume);
        lineCount.addEventFilter(KeyEvent.KEY_TYPED, KeyEvent::consume);

        lineCountjjc.addEventFilter(KeyEvent.KEY_PRESSED, KeyEvent::consume);
        lineCountjjc.addEventFilter(KeyEvent.KEY_RELEASED, KeyEvent::consume);
        lineCountjjc.addEventFilter(KeyEvent.KEY_TYPED, KeyEvent::consume);

        jajacodeInput.addEventFilter(KeyEvent.KEY_PRESSED, KeyEvent::consume);
        jajacodeInput.addEventFilter(KeyEvent.KEY_RELEASED, KeyEvent::consume);
        jajacodeInput.addEventFilter(KeyEvent.KEY_TYPED, KeyEvent::consume);

        pile.addEventFilter(KeyEvent.KEY_PRESSED, KeyEvent::consume);
        pile.addEventFilter(KeyEvent.KEY_RELEASED, KeyEvent::consume);
        pile.addEventFilter(KeyEvent.KEY_TYPED, KeyEvent::consume);

        tas.addEventFilter(KeyEvent.KEY_PRESSED, KeyEvent::consume);
        tas.addEventFilter(KeyEvent.KEY_RELEASED, KeyEvent::consume);
        tas.addEventFilter(KeyEvent.KEY_TYPED, KeyEvent::consume);
    }

    private void printDebugInfoJJC(){
        jajacodeInput.deselect();
        jajacodeInput.setEditable(false);

        int nbLine = j.getLine()-1;

        String[] lines = jajacodeInput.getText().split("\n", -1);
        for (int i = 0; i < lines.length; ++i) {
            if(i == nbLine) {
                int startOfLine = getStartIndexOfLine(lines, i);
                int endOfLine = getEndIndexOfLine(lines, i);


                // Sélectionner la ligne spécifique
                jajacodeInput.selectRange(startOfLine, endOfLine);
            }
        }

        MemoryAtMoment m = j.resumeThread();
        pile.setText(m.getSymbolTable());
        System.out.println(m.getSymbolTable());
        System.out.print(m.getTas());
        tas.setText(m.getTas());
    }
    /**
     * Highlight the line of the code we're debuging
     * Print the stack and the memory
     */
    private void printDebugInfo() {
        codeInput.deselect();
        codeInput.setEditable(false);

        int nbLine = d.getLine()-1;

        String[] lines = codeInput.getText().split("\n", -1);
        for (int i = 0; i < lines.length; ++i) {
            if(i == nbLine) {
                int startOfLine = getStartIndexOfLine(lines, i);
                int endOfLine = getEndIndexOfLine(lines, i);

                // Sélectionner la ligne spécifique
                codeInput.selectRange(startOfLine, endOfLine);
            }
        }

        MemoryAtMoment m = d.resumeThread();
        pile.setText(m.getSymbolTable());
        tas.setText(m.getTas());
    }

    @FXML
    protected void onNextBrekpointClick() {
        d.setJumpBreakpoint(true);
        printDebugInfo();
    }

    /**
     * Bouton breakpoint
     */
    @FXML
    protected void onCompileButtonClick() {
        compile();
    }

    private void initDebugModeJJC(){
        pile.setText("");
        tas.setText("");
        j.init();
        j.setBreakpoint(breakpointjjc);
        j.setJumpBreakpoint(true); // pas dérangeant comme en pas a pas le tableau est null
        Task<Void> debugTask = new Task<>() {
            @Override
            protected Void call() {
                j.setInput(jajacodeInput.getText());
                j.start();
                try {
                    j.join();  // Wait for the background thread to finish
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return null;
            }
        };
        debugTask.setOnSucceeded(event -> {
            // Update UI after the background task is completed
            updateUIAfterBackgroundTaskJJC();
        });

        Thread backgroundThread = new Thread(debugTask);
        backgroundThread.start();
    }



    private void initDebugMode() {
        pile.setText("");
        tas.setText("");
        d.init();
        d.setBreakpoint(breakpoint);
        d.setJumpBreakpoint(true); // pas dérangeant comme en pas a pas le tableau est null
        Task<Void> debugTask = new Task<>() {
            @Override
            protected Void call() {
                d.setInput(codeInput.getText());
                d.start();
                try {
                    d.join();  // Wait for the background thread to finish
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return null;
            }
        };

        debugTask.setOnSucceeded(event -> {
            // Update UI after the background task is completed
            updateUIAfterBackgroundTask();
        });

        Thread backgroundThread = new Thread(debugTask);
        backgroundThread.start();
    }

    /**
     * Boutons init step by step
     */
    @FXML
    protected void onDebugButtonClick() {
        initDebugMode();
    }

    @FXML
    protected void onDebugBreakPointButtonClickJjc() {
        j.setJumpBreakpoint(true);
        initDebugModeJJC();
    }

    /**
     * Button that init debug by breakpoint
     */
    @FXML
    protected void onDebugBreakPointButtonClick() {
        d.setJumpBreakpoint(true);
        initDebugMode();
    }

    @FXML
    protected void onDebugJajaButtonClick() {
        initDebugModeJJC();
    }
    protected void updateUIAfterBackgroundTaskJJC() {
        Platform.runLater(() -> {
            compileJJC();
        });
    }
    protected void updateUIAfterBackgroundTask() {
        Platform.runLater(() -> {
           compile();
        });

    }


    private int getStartIndexOfLine(String[] lines, int lineNumber) {
        if (lineNumber >= 0 && lineNumber < lines.length) {
            int startIndex = 0;
            for (int i = 0; i < lineNumber; i++) {
                startIndex += lines[i].length() + 1; // +1 for the newline character
            }
            return startIndex;
        }
        return -1;
    }

    private int getEndIndexOfLine(String[] lines, int lineNumber) {
        if (lineNumber >= 0 && lineNumber < lines.length) {
            return getStartIndexOfLine(lines, lineNumber) + lines[lineNumber].length();
        }
        return -1;
    }

    /**
     * Bouton pas mjj suivant
     */
    @FXML
    protected void onNextDebugButtonClick() {
        printDebugInfo();
    }

    /**
     * Bouton pas à pas jjc
     */
    @FXML
    protected void onNextJajaDebugButtonClick() {
        printDebugInfoJJC();
    }
    private void compileJJC() {
        try {
            ASTJJCStart astJJc = JajaCodeAnalyser.parseFromString(jajacodeInput.getText());
            System.out.println("jjc:");
            astJJc.jjtAccept(new InterpreterJjcVisitor(new Memory(), errors, false,null), null);
            if (!errors.isEmpty()) {
                for (InterpreterError e : errors) {
                    logError.appendText(e.toString() + "\n");
                }
                throw new RuntimeException("ErrorJJC");

            }

        } catch (Exception exception) {
            try {
                memory = new Memory();
            } catch (TasException e) {
                throw new RuntimeException(e);
            }
        }

    }
    private void compile() {
        errorsT.clear();
        errors.clear();
        errorsI.clear();

        logError.setText("");
        pile.setText("");
        tas.setText("");
        jajacodeInput.setText("");
        jjcStack.clear();

        try {
            memory = new Memory();
        } catch (TasException e) {
            logError.setText(e.getMessage());
        }

        try {
            System.out.println(codeInput.getText());
            ASTMjjStart ast = MiniJajaAnalyser.parseFromString(codeInput.getText());
            ast.jjtAccept(new TypeCheckerVisitor(TypeCheckerVisitor.FIRST_PASS,memory, errorsT),null);
            ast.jjtAccept(new TypeCheckerVisitor(TypeCheckerVisitor.SECOND_PASS,memory, errorsT),null);
            if(!errorsT.isEmpty()){
                for ( TypeCheckerError e: errorsT ) {
                    logError.appendText(e.toString()+"\n");
                }
                throw new RuntimeException("ErrorT");

            }
            System.out.println("mjj:");
            ast.jjtAccept(new InterpreterVisitor(memory, errorsI, false, null),null);
            if(!errorsI.isEmpty()){
                for (InterpreterError e: errorsI ) {
                    logError.appendText(e.toString()+"\n");
                }
                throw new RuntimeException("ErrorI");

            }
            ast.jjtAccept(new CompilerVisitor(jjcStack), null);
            CompilerPrinter printer = new CompilerPrinter(jjcStack);

            jajacodeInput.setText(printer.printJjcStack());
            onCountLineJjc();

           // pile.setText(s.toString());
           // tas.setText(memory.getTas().toString());
            memory = new Memory();
        } catch (Exception exception) {
            logError.appendText(exception.getMessage());
            try {
                memory = new Memory();
            } catch (TasException e) {
                throw new RuntimeException(e);
            }
        }
    }


    public void setUpStreams() {
        System.setOut(new PrintStream(outContent));
    }
    public void restoreStreams() {
        System.setOut(originalOut);
    }

    @FXML
    protected void onExecuteMJJButtonClick() {
        logError.setText("Resultat de l'éxecution MiniJaja :"+"\n");
        outContent = new ByteArrayOutputStream();
        errorsT.clear();
        errors.clear();
        errorsI.clear();
        try {
            memory = new Memory();
        } catch (TasException e) {
            logError.setText(e.getMessage());
        }
        jjcStack.clear();
        try {
            ASTMjjStart ast = MiniJajaAnalyser.parseFromString(codeInput.getText());
            ast.jjtAccept(new TypeCheckerVisitor(TypeCheckerVisitor.FIRST_PASS, memory, errorsT), null);
            ast.jjtAccept(new TypeCheckerVisitor(TypeCheckerVisitor.SECOND_PASS, memory, errorsT), null);
            if (!errorsT.isEmpty()) {
                for (TypeCheckerError e : errorsT) {
                    logError.appendText(e.toString() + "\n");
                }
                throw new RuntimeException("ErrorT");

            }
            setUpStreams();
            ast.jjtAccept(new InterpreterVisitor(memory, errorsI, false, null), null);
            if (!errorsI.isEmpty()) {
                for (InterpreterError e : errorsI) {
                    logError.appendText(e.toString() + "\n");
                }
                throw new RuntimeException("ErrorI");
            }

            logError.appendText(outContent.toString());
            restoreStreams();

            //pile.setText(s.toString());
            //tas.setText(memory.getTas().toString());

        } catch (Exception exception) {
            logError.setText(exception.getMessage());
            try {
                memory = new Memory();
            } catch (TasException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @FXML
    protected void onExecuteJJCButtonClick() {
        logError.setText("Resultat de l'éxecution JajaCode :"+"\n");
        try {
            memory = new Memory();
        } catch (TasException e) {
            throw new RuntimeException(e);
        }
        outContent = new ByteArrayOutputStream();
        errorsT.clear();
        errors.clear();
        errorsI.clear();
        jjcStack.clear();

        try {
            ASTMjjStart ast = MiniJajaAnalyser.parseFromString(codeInput.getText());
            ast.jjtAccept(new TypeCheckerVisitor(TypeCheckerVisitor.FIRST_PASS, memory, errorsT), null);
            ast.jjtAccept(new TypeCheckerVisitor(TypeCheckerVisitor.SECOND_PASS, memory, errorsT), null);
            if (!errorsT.isEmpty()) {
                for (TypeCheckerError e : errorsT) {
                    logError.appendText(e.toString() + "\n");
                }
                throw new RuntimeException("ErrorT");

            }

            ast.jjtAccept(new InterpreterVisitor(memory, errorsI, false, null), null);
            if (!errorsI.isEmpty()) {
                for (InterpreterError e : errorsI) {
                    logError.appendText(e.toString() + "\n");
                }
                throw new RuntimeException("ErrorI");

            }
            ast.jjtAccept(new CompilerVisitor(jjcStack), null);
            CompilerPrinter printer = new CompilerPrinter(jjcStack);

            jajacodeInput.setText(printer.printJjcStack());
            onCountLineJjc();

            setUpStreams();
            ASTJJCStart astJJc = JajaCodeAnalyser.parseFromString(printer.printJjcStack());


            astJJc.jjtAccept(new InterpreterJjcVisitor(new Memory(), errors, false,null), null);
            if (!errors.isEmpty()) {
                for (InterpreterError e : errors) {
                    logError.appendText(e.toString() + "\n");
                }
                throw new RuntimeException("ErrorJJC");
            }

            logError.appendText(outContent.toString());
            restoreStreams();
            //pile.setText(s.toString());
            //tas.setText(memory.getTas().toString());

        } catch (Exception exception) {
            logError.appendText(exception.getMessage());
            try {
                memory = new Memory();
            } catch (TasException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @FXML
    protected void onOpenButtonClick() {
        Stage stage = (Stage) primaryStage.getScene().getWindow();

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Ouvrir un fichier");

        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("MiniJaja", "*.mjj")
        );

        File selectedFile = fileChooser.showOpenDialog(stage);

        if (selectedFile != null) {
            codeInput.setText("");
            try {
                Scanner scanner = new Scanner(selectedFile);
                while (scanner.hasNextLine()) {
                    codeInput.appendText(scanner.nextLine() + "\n");
                }
                //Création des lignes
                onCountLine();
            } catch (FileNotFoundException e) {
                System.out.println(e);
            }
        }
    }

    @FXML
    protected void onSaveButtonClick() {
        Stage stage = (Stage) primaryStage.getScene().getWindow();

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Enregistrer un fichier");

        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("MiniJaja", "*.mjj")
        );

        File selectedFile = fileChooser.showSaveDialog(stage);
        String fileName = selectedFile.toString();
        if (!fileName.endsWith(".mjj")) {
            fileName += ".mjj";
            selectedFile = new File(fileName);
        }
        if (selectedFile != null) {
            try {
                FileWriter fileWriter;

                fileWriter = new FileWriter(selectedFile);
                fileWriter.write(codeInput.getText());
                fileWriter.close();
            } catch (IOException ex) {

            }
        }
    }

    @FXML
    protected void onCountLineJjc() {
        //crée les lignes
        lineCountjjc.clear();
        String[] lines = jajacodeInput.getText().split("\n", -1);
        for (int i = 0; i < lines.length; ++i) {
            lineCountjjc.appendText(i + 1 + "\n");
        }
    }

    @FXML
    protected void onCountLine() {
        //crée les lignes
        lineCount.clear();
        String[] lines = codeInput.getText().split("\n", -1);
        for (int i = 0; i < lines.length; ++i) {
                lineCount.appendText(i + 1 + "\n");
        }

        int caretPosition = codeInput.getCaretPosition();
        int lineNumber = 1;
        int columnNumber = 0;

        for (int i = 0; i < caretPosition; i++) {
            if (codeInput.getText().charAt(i) == '\n') {
                lineNumber++;
                columnNumber = 0;
            } else {
                columnNumber++;
            }
        }

        ligne.setText("Ligne : " + lineNumber);
        colonne.setText("Colonne : " + columnNumber);

    }

    @FXML
    protected void onDebugPoint(MouseEvent event) {
        int lineIndex = getLineIndex(event.getY());
        String currentText = lineCount.getText();
        String[] lines = currentText.split("\n");

        if (lineIndex >= 0 && lineIndex < lines.length) {
            String line = lines[lineIndex];

            // Vérifiez si la ligne contient déjà un "X"
            if (line.endsWith("•")) {
                // Si oui, retirez le "X"
                lines[lineIndex] = line.substring(0, line.length() - 1);
                Integer i = Integer.parseInt(line.substring(0, line.length() - 1));
                breakpoint.remove(i);
            } else {
                // Sinon, ajoutez le "X"
                lines[lineIndex] = line + "•";
                breakpoint.add(Integer.parseInt(line));
            }

            //Initialise le debugeur
            d.setBreakpoint(breakpoint);

            // Reconstruisez le texte avec la modification
            StringBuilder newText = new StringBuilder();
            for (String updatedLine : lines) {
                newText.append(updatedLine).append("\n");
            }
            // Mettez à jour le texte dans le TextArea
            lineCount.setText(newText.toString());
        }
    }

    @FXML
    protected void onDebugPointJjc(MouseEvent event) {
        int lineIndex = getLineIndexJJC(event.getY());
        String currentText = lineCountjjc.getText();
        String[] lines = currentText.split("\n");

        if (lineIndex >= 0 && lineIndex < lines.length) {
            String line = lines[lineIndex];
            // Vérifiez si la ligne contient déjà un "X"
            if (line.endsWith("•")) {
                // Si oui, retirez le "X"
                lines[lineIndex] = line.substring(0, line.length() - 1);
                Integer i = Integer.parseInt(line.substring(0, line.length() - 1));
                breakpointjjc.remove(i);
            } else {
                // Sinon, ajoutez le "X"
                lines[lineIndex] = line + "•";
                breakpointjjc.add(Integer.parseInt(line));
            }

            //Initialise le debugeur
            j.setBreakpoint(breakpointjjc);

            // Reconstruisez le texte avec la modification
            StringBuilder newText = new StringBuilder();
            for (String updatedLine : lines) {
                newText.append(updatedLine).append("\n");
            }
            // Mettez à jour le texte dans le TextArea
            lineCountjjc.setText(newText.toString());
        }
    }


    private int getLineIndex(double y) {
        int approxLineIndex = (int) (y / estimateLineHeight());
        return Math.min(approxLineIndex, lineCount.getParagraphs().size() - 1);
    }

    private double estimateLineHeight() {
        Text text = new Text();
        text.setFont(lineCount.getFont());
        text.setText("X"); // Single character for estimation
        return text.getLayoutBounds().getHeight();
    }
    private int getLineIndexJJC(double y) {
        int approxLineIndex = (int) (y / estimateLineHeightJJC());
        return Math.min(approxLineIndex, lineCountjjc.getParagraphs().size() - 1);
    }
    private double estimateLineHeightJJC(){
        Text text = new Text();
        text.setFont(lineCountjjc.getFont());
        text.setText("X"); // Single character for estimation
        return text.getLayoutBounds().getHeight();
    }

    @FXML
    public void onNextBreakpointClickJJC() {
        j.setJumpBreakpoint(true);
        printDebugInfoJJC();
    }
}