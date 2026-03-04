/*package fr.ufrst.m1info.comp5;

import fr.ufrst.m1info.comp5.minijaja.ASTMjjStart;
import fr.ufrst.m1info.comp5.minijaja.MiniJajaAnalyser;
import fr.ufrst.m1info.comp5.minijaja.ParseException;
import fr.ufrst.m1info.comp5.minijaja.utils.ASTMjjPrinter;

import java.io.*;

public class App
{
    public static void main(String[] args) {
        ASTMjjStart nodeStart = null;

        if (args.length == 2 && args[0].equals("-f")) {
            nodeStart = getASTFromFile(args);
        } else {
            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
                System.out.println("Please enter your miniJajaCode (CTRL+D for end) :");
                String line;
                StringBuilder totalStr = new StringBuilder();
                while ((line = reader.readLine()) != null) {
                    totalStr.append(line);
                }
                nodeStart = getASTFromText(String.valueOf(totalStr));
            } catch (IOException e) {
                System.err.println("Erreur lors de la lecture depuis stdin : " + e.getMessage());
                System.exit(1);
            }
        }

        System.out.println("FAIT");
        if(nodeStart != null){
            System.out.println(nodeStart.jjtAccept(new ASTMjjPrinter(), null));

        }
    }

    private static ASTMjjStart getASTFromFile(String[] args){
        ASTMjjStart nodeStart = null;
        try{
            nodeStart = MiniJajaAnalyser.parseFromFile(args[1]);
        } catch (FileNotFoundException e) {
            System.err.println("File not found.");
            System.exit(1);
        } catch (ParseException e) {
            System.err.println("Parser error. File is invalid.");
            System.err.println(e.getMessage());
        }
        return nodeStart;
    }

    private static ASTMjjStart getASTFromText(String str){
        ASTMjjStart nodeStart = null;
        try{
            nodeStart = MiniJajaAnalyser.parseFromString(str);
        } catch (ParseException e) {
            System.err.println("Parser error. File is invalid.");
            System.err.println(e.getMessage());
        }
        return nodeStart;
    }
}
*/