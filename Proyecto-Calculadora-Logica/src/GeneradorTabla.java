import java.util.*;

public class GeneradorTabla {
    private static final Scanner scanner = new Scanner(System.in);
    
    public static void main(String[] args) {
        System.out.println("==================================");
        System.out.println("  GENERADOR DE TABLAS DE VERDAD  ");
        System.out.println("==================================");
        
        while (true) {
            try {
                String expression = getExpression();
                if (expression.equalsIgnoreCase("exit")) {
                    break;
                }
                
                Set<Character> variables = extractVariables(expression);
                validateExpression(expression, variables);
                
                System.out.println("\nExpresión: " + expression);
                System.out.println("Variables: " + variables);
                System.out.println("\nGenerando tabla de verdad...\n");
                
                generateTruthTable(expression, variables);
                
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
            
            System.out.print("\n¿Continuar? (s/n): ");
            String response = scanner.nextLine().trim();
            if (response.equalsIgnoreCase("n")) {
                break;
            }
        }
        
        System.out.println("\n¡Gracias por usar el Generador de Tablas de Verdad!");
    }
    
    private static String getExpression() {
        printMenu();
        StringBuilder expression = new StringBuilder();
        
        while (true) {
            System.out.print("\nExpresión actual: " + expression);
            System.out.print("\nSeleccione opción (o 'done'/'exit'): ");
            String input = scanner.nextLine().trim();
            
            if (input.equalsIgnoreCase("done")) {
                break;
            }
            
            if (input.equalsIgnoreCase("exit")) {
                return "exit";
            }
            
            try {
                int option = Integer.parseInt(input);
                switch (option) {
                    case 1: expression.append('('); break;
                    case 2: expression.append(')'); break;
                    case 3: expression.append(EvaluadorExpresiones.NOT); break;
                    case 4: expression.append(EvaluadorExpresiones.AND); break;
                    case 5: expression.append(EvaluadorExpresiones.OR); break;
                    case 6: expression.append(EvaluadorExpresiones.IMPLICATION); break;
                    case 7: expression.append(EvaluadorExpresiones.BICONDITIONAL); break;
                    case 8: expression.append('p'); break;
                    case 9: expression.append('q'); break;
                    case 10: expression.append('r'); break;
                    case 11: expression.append('s'); break;
                    case 12: expression.append('t'); break;
                    case 13: 
                        if (expression.length() > 0) {
                            expression.deleteCharAt(expression.length() - 1);
                        }
                        break;
                    case 14: 
                        expression.setLength(0);
                        break;
                    default:
                        System.out.println("Opción no válida. Por favor, intente de nuevo.");
                }
            } catch (NumberFormatException e) {
                
                if (input.length() == 1) {
                    char c = input.charAt(0);
                    if (EvaluadorExpresiones.isVariable(c) || EvaluadorExpresiones.isOperator(c) || c == '(' || c == ')') {
                        expression.append(c);
                    } else {
                        System.out.println("Carácter no válido. Utilice el menú para agregar elementos.");
                    }
                } else {
                    System.out.println("Entrada no válida. Seleccione una opción del menú.");
                }
            }
        }
        
        return expression.toString();
    }
    
    private static void printMenu() {
        System.out.println("\nMenú de opciones:");
        System.out.println("1. ( (paréntesis izquierdo)");
        System.out.println("2. ) (paréntesis derecho)");
        System.out.println("3. ~ (negación)");
        System.out.println("4. ^ (conjunción/AND)");
        System.out.println("5. v (disyunción/OR)");
        System.out.println("6. → (implicación)");
        System.out.println("7. ↔ (bicondicional)");
        System.out.println("8. p (variable p)");
        System.out.println("9. q (variable q)");
        System.out.println("10. r (variable r)");
        System.out.println("11. s (variable s)");
        System.out.println("12. t (variable t)");
        System.out.println("13. DEL (borrar último carácter)");
        System.out.println("14. CLEAR (borrar todo)");
        System.out.println("'done' para finalizar la expresión");
        System.out.println("'exit' para salir del programa");
    }
    
    private static Set<Character> extractVariables(String expression) {
        Set<Character> variables = new TreeSet<>();
        for (char c : expression.toCharArray()) {
            if (EvaluadorExpresiones.isVariable(c)) {
                variables.add(c);
            }
        }
        return variables;
    }
    
    private static void validateExpression(String expression, Set<Character> variables) {
        if (expression.isEmpty()) {
            throw new IllegalArgumentException("La expresión no puede estar vacía");
        }
        
        if (variables.size() < 2) {
            throw new IllegalArgumentException("La expresión debe contener al menos 2 variables distintas");
        }
        
        if (variables.size() > 5) {
            throw new IllegalArgumentException("La expresión debe contener como máximo 5 variables distintas");
        }
        
        int balance = 0;
        for (char c : expression.toCharArray()) {
            if (c == '(') balance++;
            if (c == ')') balance--;
            if (balance < 0) {
                throw new IllegalArgumentException("Paréntesis desbalanceados: hay un paréntesis de cierre sin su correspondiente paréntesis de apertura");
            }
        }
        
        if (balance != 0) {
            throw new IllegalArgumentException("Paréntesis desbalanceados: hay paréntesis de apertura sin cerrar");
        }
    }
    
    private static void generateTruthTable(String expression, Set<Character> variables) {
        List<Character> varList = new ArrayList<>(variables);
        Collections.sort(varList);
        
        // Parse the expression
        ExpressionNode expressionTree;
        try {
            expressionTree = EvaluadorExpresiones.parse(expression);
        } catch (Exception e) {
            throw new IllegalArgumentException("Error al analizar la expresión: " + e.getMessage());
        }
        
        // ANSI color codes for console output
        final String RESET = "\u001B[0m";
        final String RED = "\u001B[31m";
        final String GREEN = "\u001B[32m";
        final String WHITE = "\u001B[37m";
        final String BG_DARK = "\u001B[40m";
        final String BG_DARKER = "\u001B[100m";
        
        // Calculate column widths
        int columnWidth = 20; // Default column width
        
        // Print table header with background
        System.out.print(BG_DARK + WHITE);
        for (char var : varList) {
            printCentered(String.valueOf(var), columnWidth);
            System.out.print("|");
        }
        printCentered(expression, columnWidth);
        System.out.println(RESET);
        
        // Generate all possible combinations of truth values
        int rows = (int) Math.pow(2, variables.size());
        for (int i = 0; i < rows; i++) {
            // Alternating row background
            System.out.print((i % 2 == 0) ? BG_DARKER : BG_DARKER);
            
            Map<Character, Boolean> variableValues = new HashMap<>();
            
            // Assign values to variables for this row
            for (int j = 0; j < varList.size(); j++) {
                // Use bit manipulation to generate all combinations
                boolean value = ((i >> (varList.size() - j - 1)) & 1) == 1;
                variableValues.put(varList.get(j), value);
                
                // Print truth value with appropriate color
                String valueTxt = value ? GREEN + "V" : RED + "F";
                printCentered(valueTxt, columnWidth);
                System.out.print(WHITE + "|");
            }
            
            // Evaluate the expression with these values
            try {
                boolean result = EvaluadorExpresiones.evaluate(expressionTree, variableValues);
                String resultTxt = result ? GREEN + "V" : RED + "F";
                printCentered(resultTxt, columnWidth);
            } catch (Exception e) {
                printCentered(RED + "ERROR", columnWidth);
            }
            System.out.println(RESET);
        }
    }
    
    /**
     * Helper method to print centered text in a column
     */
    private static void printCentered(String text, int width) {
        // Strip ANSI color codes for length calculation
        String plainText = text.replaceAll("\u001B\\[[;\\d]*m", "");
        int padding = width - plainText.length();
        int leftPadding = padding / 2;
        int rightPadding = padding - leftPadding;
        
        System.out.print(" ".repeat(leftPadding) + text + " ".repeat(rightPadding));
    }
    }