import java.util.*;

public class EvaluadorExpresiones {
    
    public static final char NOT = '~';
    public static final char AND = '^';
    public static final char OR = 'v';
    public static final char IMPLICATION = '→';
    public static final char BICONDITIONAL = '↔';
    
    public static boolean evaluate(ExpressionNode node, Map<Character, Boolean> variableValues) {
        if (node == null) {
            throw new IllegalArgumentException("expresion no puede estar nula");
        }
        
        if (node.type == NodeType.VARIABLE) {
            if (!variableValues.containsKey(node.value)) {
                throw new IllegalArgumentException("No hay valor asociado: " + node.value);
            }
            return variableValues.get(node.value);
        }
        
        switch (node.value) {
            case NOT:
                return !evaluate(node.right, variableValues);
                
            case AND:
                return evaluate(node.left, variableValues) && evaluate(node.right, variableValues);
                
            case OR:
                return evaluate(node.left, variableValues) || evaluate(node.right, variableValues);
                
            case IMPLICATION:
                boolean leftValue = evaluate(node.left, variableValues);
                boolean rightValue = evaluate(node.right, variableValues);
                return !leftValue || rightValue; 
                
            case BICONDITIONAL:
                leftValue = evaluate(node.left, variableValues);
                rightValue = evaluate(node.right, variableValues);
                return leftValue == rightValue; 
                
            default:
                throw new IllegalArgumentException("operador desconocido: " + node.value);
        }
    }
     
    public static ExpressionNode parse(String expression) {
        if (expression == null || expression.isEmpty()) {
            throw new IllegalArgumentException("Expresion no puede estar vacia");
        }
        
        String postfix = ExpressionConverter.infixToPostfix(expression);
        
        Stack<ExpressionNode> stack = new Stack<>();
        
        for (char c : postfix.toCharArray()) {
            if (isVariable(c)) {
                stack.push(new ExpressionNode(c, NodeType.VARIABLE));
            } else if (isOperator(c)) {
                ExpressionNode node = new ExpressionNode(c, NodeType.OPERATOR);
                
                if (c == NOT) {
                    if (stack.isEmpty()) {
                        throw new IllegalArgumentException("Expresion invalida");
                    }
                    node.right = stack.pop();
                } else {
                    if (stack.size() < 2) {
                        throw new IllegalArgumentException("Expresion invalida");
                    }
                    node.right = stack.pop();
                    node.left = stack.pop();
                }
                
                stack.push(node);
            }
        }
        
        if (stack.size() != 1) {
            throw new IllegalArgumentException("Expresion invalida");
        }
        
        return stack.pop();
    }
    
    public static boolean isVariable(char c) {
        return c >= 'p' && c <= 't';
    }
    
   
    public static boolean isOperator(char c) {
        return c == NOT || c == AND || c == OR || c == IMPLICATION || c == BICONDITIONAL;
    }
}

enum NodeType {
    VARIABLE, OPERATOR
}

class ExpressionNode {
    char value;          
    NodeType type;        
    ExpressionNode left;  
    ExpressionNode right; 
    
    public ExpressionNode(char value, NodeType type) {
        this.value = value;
        this.type = type;
        this.left = null;
        this.right = null;
    }
}

class ExpressionConverter {
    
    public static String infixToPostfix(String infix) {
        StringBuilder postfix = new StringBuilder();
        Stack<Character> stack = new Stack<>();
        
        for (char c : infix.toCharArray()) {
            if (EvaluadorExpresiones.isVariable(c)) {
                postfix.append(c);
            } else if (c == '(') {
                stack.push(c);
            } else if (c == ')') {
                while (!stack.isEmpty() && stack.peek() != '(') {
                    postfix.append(stack.pop());
                }
                if (!stack.isEmpty() && stack.peek() == '(') {
                    stack.pop(); // Remove the '('
                }
            } else if (EvaluadorExpresiones.isOperator(c)) {
                while (!stack.isEmpty() && precedence(stack.peek()) >= precedence(c)) {
                    postfix.append(stack.pop());
                }
                stack.push(c);
            }
        }
        
        while (!stack.isEmpty()) {
            postfix.append(stack.pop());
        }
        
        return postfix.toString();
    }
    
    private static int precedence(char operator) {
        switch (operator) {
            case '(': return 0;
            case EvaluadorExpresiones.BICONDITIONAL: return 1;
            case EvaluadorExpresiones.IMPLICATION: return 2;
            case EvaluadorExpresiones.OR: return 3;
            case EvaluadorExpresiones.AND: return 4;
            case EvaluadorExpresiones.NOT: return 5;
            default: return -1;
        }
    }
}

class ExpressionEvaluationException extends RuntimeException {
    public ExpressionEvaluationException(String message) {
        super(message);
    }
}
