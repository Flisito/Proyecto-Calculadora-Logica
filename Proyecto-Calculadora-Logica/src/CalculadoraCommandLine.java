import java.util.*;

public class CalculadoraCommandLine {
    private static final Scanner scanner = new Scanner(System.in);
    
    private static final char NOT = '~';       
    private static final char AND = '^';      
    private static final char OR = 'v';       
    private static final char IMPLICATION = '→'; 
    private static final char BICONDITIONAL = '↔'; 
    
    public static void main(String[] args) {
        System.out.println("Bienvenido a la Calculadora Lógica");
        
        while (true) {
            String expression = getExpressionFromUser();
            if (expression.equalsIgnoreCase("exit")) {
                break;
            }
            
            try {
                Set<Character> variables = extractVariables(expression);
                
                if (variables.size() < 2 || variables.size() > 5) {
                    System.out.println("Error: La expresión debe contener entre 2 y 5 variables distintas.");
                    continue;
                }
                
                System.out.println("\nExpresión: " + expression);
                System.out.println("Variables detectadas: " + variables);
                
                generateTruthTable(expression, variables);
                
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
            
            System.out.println("\n¿Desea continuar? (s/n)");
            String response = scanner.nextLine();
            if (response.equalsIgnoreCase("n")) {
                break;
            }
        }
        
        System.out.println("¡Gracias por usar la Calculadora Lógica!");
    }
    
    
    private static String getExpressionFromUser() {
        StringBuilder expression = new StringBuilder();
        
        System.out.println("\nConstruya su expresión lógica (escriba 'done' para finalizar, 'exit' para salir):");
        System.out.println("Variables disponibles: p, q, r, s, t");
        System.out.println("Operadores disponibles:");
        System.out.println("1. ~ (negación)");
        System.out.println("2. ^ (conjunción/AND)");
        System.out.println("3. v (disyunción/OR)");
        System.out.println("4. → (implicación)");
        System.out.println("5. ↔ (bicondicional)");
        System.out.println("6. ( (paréntesis izquierdo)");
        System.out.println("7. ) (paréntesis derecho)");
        
        while (true) {
            System.out.print("\nExpresión actual: " + expression.toString());
            System.out.print("\nAgregar elemento (o 'done'/'exit'): ");
            String input = scanner.nextLine().trim();
            
            if (input.equalsIgnoreCase("done")) {
                break;
            }
            
            if (input.equalsIgnoreCase("exit")) {
                return "exit";
            }
            
            if (input.length() == 1) {
                char c = input.charAt(0);
                if (isVariable(c) || isOperator(c) || c == '(' || c == ')') {
                    expression.append(c);
                    continue;
                }
            }
            
            try {
                int choice = Integer.parseInt(input);
                switch (choice) {
                    case 1: expression.append(NOT); break;
                    case 2: expression.append(AND); break;
                    case 3: expression.append(OR); break;
                    case 4: expression.append(IMPLICATION); break;
                    case 5: expression.append(BICONDITIONAL); break;
                    case 6: expression.append('('); break;
                    case 7: expression.append(')'); break;
                    default: System.out.println("Opción no válida");
                }
            } catch (NumberFormatException e) {
                System.out.println("Entrada no válida. Ingrese un número del 1-7, una variable (p-t), o 'done'/'exit'");
            }
        }
        
        return expression.toString();
    }
    
    
    private static Set<Character> extractVariables(String expression) {
        Set<Character> variables = new TreeSet<>();
        for (char c : expression.toCharArray()) {
            if (isVariable(c)) {
                variables.add(c);
            }
        }
        return variables;
    }
    
    
    private static boolean isVariable(char c) {
        return c == 'p' || c == 'q' || c == 'r' || c == 's' || c == 't';
    }
    
    
    private static boolean isOperator(char c) {
        return c == NOT || c == AND || c == OR || c == IMPLICATION || c == BICONDITIONAL;
    }
    
    private static void generateTruthTable(String expression, Set<Character> variables) {
        List<Character> varList = new ArrayList<>(variables);
        Collections.sort(varList);
        
        for (char var : varList) {
            System.out.print(var + " | ");
        }
        System.out.println(expression);
        
        for (int i = 0; i < varList.size(); i++) {
            System.out.print("--| ");
        }
        for (int i = 0; i < expression.length(); i++) {
            System.out.print("-");
        }
        System.out.println();
        
        int rows = (int) Math.pow(2, variables.size());
        for (int i = 0; i < rows; i++) {
            Map<Character, Boolean> assignments = new HashMap<>();
            
            for (int j = 0; j < varList.size(); j++) {
                boolean value = ((i >> (varList.size() - j - 1)) & 1) == 1;
                assignments.put(varList.get(j), value);
                System.out.print((value ? "V" : "F") + " | ");
            }
            
            boolean result = evaluateExpression(expression, assignments);
            System.out.println(result ? "V" : "F");
        }
    }
    
    private static boolean evaluateExpression(String expression, Map<Character, Boolean> assignments) {
        String postfix = infixToPostfix(expression);
        
        Stack<Boolean> stack = new Stack<>();
        
        for (char c : postfix.toCharArray()) {
            if (isVariable(c)) {
                stack.push(assignments.get(c));
            } else if (isOperator(c)) {
                switch (c) {
                    case NOT:
                        boolean operand = stack.pop();
                        stack.push(!operand);
                        break;
                    case AND:
                        boolean b = stack.pop();
                        boolean a = stack.pop();
                        stack.push(a && b);
                        break;
                    case OR:
                        b = stack.pop();
                        a = stack.pop();
                        stack.push(a || b);
                        break;
                    case IMPLICATION:
                        b = stack.pop();
                        a = stack.pop();
                        stack.push(!a || b); // p→q es equivalnete a ¬p∨q
                        break;
                    case BICONDITIONAL:
                        b = stack.pop();
                        a = stack.pop();
                        stack.push(a == b); // p↔q es cierto cuando p y q tienen el mismo valor
                        break;
                }
            }
        }
        
        return stack.pop();
    }
    
    private static String infixToPostfix(String infix) {
        StringBuilder postfix = new StringBuilder();
        Stack<Character> stack = new Stack<>();
        
        for (char c : infix.toCharArray()) {
            if (isVariable(c)) {
                postfix.append(c);
            } else if (c == '(') {
                stack.push(c);
            } else if (c == ')') {
                while (!stack.isEmpty() && stack.peek() != '(') {
                    postfix.append(stack.pop());
                }
                if (!stack.isEmpty() && stack.peek() == '(') {
                    stack.pop(); 
                }
            } else if (isOperator(c)) {
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
            case BICONDITIONAL: return 1;
            case IMPLICATION: return 2;
            case OR: return 3;
            case AND: return 4;
            case NOT: return 5;
            default: return -1;
        }
    }
}