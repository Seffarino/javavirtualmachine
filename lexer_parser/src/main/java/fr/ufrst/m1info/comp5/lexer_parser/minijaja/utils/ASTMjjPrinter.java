package fr.ufrst.m1info.comp5.lexer_parser.minijaja.utils;

import fr.ufrst.m1info.comp5.minijaja.*;

public class ASTMjjPrinter implements MiniJajaAnalyserVisitor {
    private int indentation = 0;

    @Override
    public Object visit(ASTMjjStart node, Object data) {
        StringBuilder output = new StringBuilder();
        printOpeningTag(node, output);
        visitChildren(node, output);
        printClosingTag(node, output);
        return output.toString();
    }

    @Override
    public Object visit(ASTMjjclasse node, Object data) {
        return null;
    }

    @Override
    public Object visit(ASTMjjident node, Object data) {
        return null;
    }

    private void visitChildren(SimpleNode node, StringBuilder output) {
        indentation++;
        for (int i = 0; i < node.jjtGetNumChildren(); i++) {
            SimpleNode child = (SimpleNode) node.jjtGetChild(i);

            // Check if the child has neither children nor a value
            boolean hasNoChildrenOrValue = (child.jjtGetNumChildren() == 0 && child.jjtGetValue() == null);

            if (hasNoChildrenOrValue) {
                appendIndentedLine(output, "<" + child.toString() + "/>");
            } else {
                printOpeningTag(child, output);
                if (child.jjtGetValue() != null) {
                    indentation++;
                    appendIndentedLine(output, child.jjtGetValue().toString());
                    indentation--;
                }
                visitChildren(child, output);
                printClosingTag(child, output);
            }
        }
        indentation--;
    }
    private void printOpeningTag(SimpleNode node, StringBuilder output) {
        String nodeName = node.getClass().getSimpleName().replace("ASTMjj", "");
        appendIndentedLine(output, "<" + nodeName + ">");
    }

    private void printClosingTag(SimpleNode node, StringBuilder output) {
        String nodeName = node.getClass().getSimpleName().replace("ASTMjj", "");
        appendIndentedLine(output, "</" + nodeName + ">");
    }

    private void appendIndentedLine(StringBuilder output, String text) {
        for (int i = 0; i < indentation; i++) {
            output.append("  "); // Utilisez deux espaces pour chaque niveau d'indentation
        }
        output.append(text).append("\n");
    }

    @Override
    public Object visit(SimpleNode node, Object data) {
        return null;
    }

    @Override
    public Object visit(ASTMjjdecls node, Object data) {
        return null;
    }

    @Override
    public Object visit(ASTMjjvnil node, Object data) {
        return null;
    }

    @Override
    public Object visit(ASTMjjmethode node, Object data) {
        return null;
    }

    @Override
    public Object visit(ASTMjjtableau node, Object data) {
        return null;
    }

    @Override
    public Object visit(ASTMjjvar node, Object data) {
        return null;
    }

    @Override
    public Object visit(ASTMjjcst node, Object data) {
        return null;
    }

    @Override
    public Object visit(ASTMjjvars node, Object data) {
        return null;
    }

    @Override
    public Object visit(ASTMjjomega node, Object data) {
        return null;
    }

    @Override
    public Object visit(ASTMjjmain node, Object data) {
        return null;
    }

    @Override
    public Object visit(ASTMjjentetes node, Object data) {
        return null;
    }

    @Override
    public Object visit(ASTMjjenil node, Object data) {
        return null;
    }

    @Override
    public Object visit(ASTMjjentete node, Object data) {
        return null;
    }

    @Override
    public Object visit(ASTMjjinstrs node, Object data) {
        return null;
    }

    @Override
    public Object visit(ASTMjjinil node, Object data) {
        return null;
    }

    @Override
    public Object visit(ASTMjjaffectation node, Object data) {
        return null;
    }

    @Override
    public Object visit(ASTMjjsomme node, Object data) {
        return null;
    }

    @Override
    public Object visit(ASTMjjincrement node, Object data) {
        return null;
    }

    @Override
    public Object visit(ASTMjjappelI node, Object data) {
        return null;
    }

    @Override
    public Object visit(ASTMjjretour node, Object data) {
        return null;
    }

    @Override
    public Object visit(ASTMjjecrire node, Object data) {
        return null;
    }

    @Override
    public Object visit(ASTMjjecrireln node, Object data) {
        return null;
    }

    @Override
    public Object visit(ASTMjjsi node, Object data) {
        return null;
    }

    @Override
    public Object visit(ASTMjjtantque node, Object data) {
        return null;
    }

    @Override
    public Object visit(ASTMjjchaine node, Object data) {
        return null;
    }

    @Override
    public Object visit(ASTMjjlistexp node, Object data) {
        return null;
    }

    @Override
    public Object visit(ASTMjjexnil node, Object data) {
        return null;
    }

    @Override
    public Object visit(ASTMjjnon node, Object data) {
        return null;
    }

    @Override
    public Object visit(ASTMjjet node, Object data) {
        return null;
    }

    @Override
    public Object visit(ASTMjjou node, Object data) {
        return null;
    }

    @Override
    public Object visit(ASTMjjegal node, Object data) {
        return null;
    }

    @Override
    public Object visit(ASTMjjsup node, Object data) {
        return null;
    }

    @Override
    public Object visit(ASTMjjmoins node, Object data) {
        return null;
    }

    @Override
    public Object visit(ASTMjjaddition node, Object data) {
        return null;
    }

    @Override
    public Object visit(ASTMjjsoustraction node, Object data) {
        return null;
    }

    @Override
    public Object visit(ASTMjjmult node, Object data) {
        return null;
    }

    @Override
    public Object visit(ASTMjjdiv node, Object data) {
        return null;
    }

    @Override
    public Object visit(ASTMjjappelE node, Object data) {
        return null;
    }

    @Override
    public Object visit(ASTMjjtab node, Object data) {
        return null;
    }

    @Override
    public Object visit(ASTMjjlongueur node, Object data) {
        return null;
    }

    @Override
    public Object visit(ASTMjjvrai node, Object data) {
        return null;
    }

    @Override
    public Object visit(ASTMjjfaux node, Object data) {
        return null;
    }

    @Override
    public Object visit(ASTMjjrien node, Object data) {
        return null;
    }

    @Override
    public Object visit(ASTMjjentier node, Object data) {
        return null;
    }

    @Override
    public Object visit(ASTMjjbool node, Object data) {
        return null;
    }

    @Override
    public Object visit(ASTMjjnbre node, Object data) {
        return null;
    }

}
