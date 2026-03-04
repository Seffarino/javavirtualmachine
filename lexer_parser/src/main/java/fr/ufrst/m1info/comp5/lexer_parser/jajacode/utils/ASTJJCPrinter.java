package fr.ufrst.m1info.comp5.lexer_parser.jajacode.utils;


import fr.ufrst.m1info.comp5.jajacode.*;

public class ASTJJCPrinter implements JajaCodeAnalyserVisitor {
    private int indentation = 0;
    @Override
    public Object visit(SimpleNode node, Object data) {
        StringBuilder output = new StringBuilder();
        printOpeningTag(node, output);
        visitChildren(node, output);
        printClosingTag(node, output);
        return output.toString();
    }

    @Override
    public Object visit(ASTJJCinstrs node, Object data) {
        return null;
    }

    @Override
    public Object visit(ASTJJCinstr node, Object data) {
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
        String nodeName = node.getClass().getSimpleName().replace("ASTJJC", "");
        appendIndentedLine(output, "<" + nodeName + ">");
    }

    private void printClosingTag(SimpleNode node, StringBuilder output) {
        String nodeName = node.getClass().getSimpleName().replace("ASTJJC", "");
        appendIndentedLine(output, "</" + nodeName + ">");
    }

    private void appendIndentedLine(StringBuilder output, String text) {
        for (int i = 0; i < indentation; i++) {
            output.append("  ");
        }
        output.append(text).append("\n");
    }
    @Override
    public Object visit(ASTJJCStart node, Object data) {
        StringBuilder output = new StringBuilder();
        printOpeningTag(node, output);
        visitChildren(node, output);
        printClosingTag(node, output);
        return output.toString();
    }


    @Override
    public Object visit(ASTJJCinit node, Object data) {
        return null;
    }

    @Override
    public Object visit(ASTJJCswap node, Object data) {
        return null;
    }

    @Override
    public Object visit(ASTJJCNew node, Object data) {
        return null;
    }

    @Override
    public Object visit(ASTJJCnewarray node, Object data) {
        return null;
    }

    @Override
    public Object visit(ASTJJCinvoke node, Object data) {
        return null;
    }

    @Override
    public Object visit(ASTJJCReturn node, Object data) {
        return null;
    }

    @Override
    public Object visit(ASTJJCpush node, Object data) {
        return null;
    }

    @Override
    public Object visit(ASTJJCpop node, Object data) {
        return null;
    }

    @Override
    public Object visit(ASTJJCload node, Object data) {
        return null;
    }

    @Override
    public Object visit(ASTJJCaload node, Object data) {
        return null;
    }

    @Override
    public Object visit(ASTJJCstore node, Object data) {
        return null;
    }

    @Override
    public Object visit(ASTJJCastore node, Object data) {
        return null;
    }

    @Override
    public Object visit(ASTJJCwrite node, Object data) {
        return null;
    }

    @Override
    public Object visit(ASTJJCwriteln node, Object data) {
        return null;
    }

    @Override
    public Object visit(ASTJJClength node, Object data) {
        return null;
    }

    @Override
    public Object visit(ASTJJCIf node, Object data) {
        return null;
    }

    @Override
    public Object visit(ASTJJCGoto node, Object data) {
        return null;
    }

    @Override
    public Object visit(ASTJJCinc node, Object data) {
        return null;
    }

    @Override
    public Object visit(ASTJJCainc node, Object data) {
        return null;
    }

    @Override
    public Object visit(ASTJJCoper node, Object data) {
        return null;
    }

    @Override
    public Object visit(ASTJJCnop node, Object data) {
        return null;
    }

    @Override
    public Object visit(ASTJJCjcstop node, Object data) {
        return null;
    }

    @Override
    public Object visit(ASTJJCjcident node, Object data) {
        return null;
    }

    @Override
    public Object visit(ASTJJCjcstring node, Object data) {
        return null;
    }

    @Override
    public Object visit(ASTJJCjcvrai node, Object data) {
        return null;
    }

    @Override
    public Object visit(ASTJJCjcfaux node, Object data) {
        return null;
    }

    @Override
    public Object visit(ASTJJCjcadress node, Object data) {
        return null;
    }

    @Override
    public Object visit(ASTJJCjcnbre node, Object data) {
        return null;
    }

    @Override
    public Object visit(ASTJJCneg node, Object data) {
        return null;
    }

    @Override
    public Object visit(ASTJJCnot node, Object data) {
        return null;
    }

    @Override
    public Object visit(ASTJJCadd node, Object data) {
        return null;
    }

    @Override
    public Object visit(ASTJJCsub node, Object data) {
        return null;
    }

    @Override
    public Object visit(ASTJJCmul node, Object data) {
        return null;
    }

    @Override
    public Object visit(ASTJJCdiv node, Object data) {
        return null;
    }

    @Override
    public Object visit(ASTJJCcmp node, Object data) {
        return null;
    }

    @Override
    public Object visit(ASTJJCsup node, Object data) {
        return null;
    }

    @Override
    public Object visit(ASTJJCor node, Object data) {
        return null;
    }

    @Override
    public Object visit(ASTJJCand node, Object data) {
        return null;
    }

    @Override
    public Object visit(ASTJJCjcsorte node, Object data) {
        return null;
    }

    @Override
    public Object visit(ASTJJCjctype node, Object data) {
        return null;
    }

    @Override
    public Object visit(ASTJJCjcnil node, Object data) {
        return null;
    }

}
