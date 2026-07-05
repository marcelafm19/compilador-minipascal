package br.com.comcet.tp7;

import br.com.comcet.tp1.ast.*;
import org.objectweb.asm.*;

import java.util.HashMap;
import java.util.Map;

public class CodeGenerator implements Opcodes {
    private ClassWriter cw;
    private MethodVisitor mv;
    private String className;
    private Map<String, Integer> varMap;
    private int nextVarIndex;

    public byte[] generate(Program program, String fileName) {
        this.className = fileName;
        cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
        // Cria a estrutura da classe
        cw.visit(V17, ACC_PUBLIC, className, null, "java/lang/Object", null);

        // Gera o construtor padrão obrigatório
        MethodVisitor init = cw.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null);
        init.visitCode();
        init.visitVarInsn(ALOAD, 0);
        init.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false);
        init.visitInsn(RETURN);
        init.visitMaxs(0, 0);
        init.visitEnd();

        // 1. Gera os métodos de funções secundárias, se existirem
        if (program.functions != null) {
            for (FunctionDecl func : program.functions) {
                visitFunctionDecl(func);
            }
        }

        // 2. Cria o método public static void main(String[] args)
        mv = cw.visitMethod(ACC_PUBLIC | ACC_STATIC, "main", "([Ljava/lang/String;)V", null, null);
        mv.visitCode();

        varMap = new HashMap<>();
        nextVarIndex = 1; // Mapeamento da JVM: índice 0 é do argumento args[] do main

        // Mapeia as variáveis globais
        if (program.variables != null) {
            for (VarDecl var : program.variables.declarations) {
                varMap.put(var.name, nextVarIndex++);
            }
        }

        // Executa as instruções do bloco principal
        for (Command cmd : program.commands) {
            visitCommand(cmd);
        }

        mv.visitInsn(RETURN);
        mv.visitMaxs(0, 0);
        mv.visitEnd();
        cw.visitEnd();

        return cw.toByteArray();
    }

    private void visitFunctionDecl(FunctionDecl func) {
        // Resolve a assinatura da função (Ex: assumimos (I)I para Inteiro -> Inteiro)
        StringBuilder sig = new StringBuilder("(");
        for (@SuppressWarnings("unused") VarDecl param : func.parameters) {
            sig.append("I");
        }
        sig.append(")I");

        MethodVisitor oldMv = mv;
        Map<String, Integer> oldVarMap = varMap;
        int oldNextIndex = nextVarIndex;

        mv = cw.visitMethod(ACC_PUBLIC | ACC_STATIC, func.name, sig.toString(), null, null);
        mv.visitCode();
        varMap = new HashMap<>();
        nextVarIndex = 0; // Para funções estáticas nossas, o índice 0 é o primeiro parâmetro

        // Adiciona os parâmetros na tabela
        for (VarDecl param : func.parameters) {
            varMap.put(param.name, nextVarIndex++);
        }
        // Adiciona as variáveis locais da função na tabela
        if (func.variables != null) {
            for (VarDecl var : func.variables.declarations) {
                varMap.put(var.name, nextVarIndex++);
            }
        }

        if (func.body != null) {
            for (Command cmd : func.body.commands) {
                visitCommand(cmd);
            }
        }

        // Adiciona um retorno padrão para a máquina não quebrar em caso de procedimentos sem returns
        mv.visitInsn(ICONST_0);
        mv.visitInsn(IRETURN);
        mv.visitMaxs(0, 0);
        mv.visitEnd();

        // Devolve o contexto do fluxo principal
        mv = oldMv;
        varMap = oldVarMap;
        nextVarIndex = oldNextIndex;
    }

    private void visitCommand(Command cmd) {
        if (cmd instanceof AssignmentCommand) {
            AssignmentCommand assign = (AssignmentCommand) cmd;
            visitExpression(assign.expr);
            int index = varMap.get(assign.id.name);
            mv.visitVarInsn(ISTORE, index); // Salva da pilha na variável

        } else if (cmd instanceof PrintCommand) {
            PrintCommand print = (PrintCommand) cmd;
            mv.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
            visitExpression(print.expression);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(I)V", false);

        } else if (cmd instanceof BlockCommand) {
            BlockCommand block = (BlockCommand) cmd;
            for (Command c : block.commands) {
                visitCommand(c);
            }

        } else if (cmd instanceof IfCommand) {
            IfCommand ifCmd = (IfCommand) cmd;
            Label lElse = new Label();
            Label lEnd = new Label();

            visitExpression(ifCmd.condition);
            mv.visitJumpInsn(IFEQ, lElse); // Se for falso (0) vai pro Else

            visitCommand(ifCmd.thenBranch);
            mv.visitJumpInsn(GOTO, lEnd);

            mv.visitLabel(lElse);
            if (ifCmd.elseBranch != null) {
                visitCommand(ifCmd.elseBranch);
            }
            mv.visitLabel(lEnd);

        } else if (cmd instanceof WhileCommand) {
            WhileCommand whileCmd = (WhileCommand) cmd;
            Label lStart = new Label();
            Label lEnd = new Label();

            mv.visitLabel(lStart);
            visitExpression(whileCmd.condition);
            mv.visitJumpInsn(IFEQ, lEnd); // Se for falso finaliza

            visitCommand(whileCmd.body);
            mv.visitJumpInsn(GOTO, lStart); // Volta para a verificação inicial

            mv.visitLabel(lEnd);

        } else if (cmd instanceof RepeatCommand) {
            // repeat ... until <cond>
            // Executa o corpo pelo menos uma vez e repete enquanto a condição for FALSA
            RepeatCommand repeat = (RepeatCommand) cmd;
            Label lStart = new Label();

            mv.visitLabel(lStart);
            visitCommand(repeat.body);        // executa o corpo
            visitExpression(repeat.condition); // avalia a condição
            mv.visitJumpInsn(IFEQ, lStart);   // se for 0 (falso), volta ao início

        } else if (cmd instanceof ReadCommand) {
            // readln(x): lê um inteiro de System.in e salva na variável
            ReadCommand read = (ReadCommand) cmd;
            int index = varMap.get(read.identifier.name);

            // new Scanner(System.in)
            mv.visitTypeInsn(NEW, "java/util/Scanner");
            mv.visitInsn(DUP);
            mv.visitFieldInsn(GETSTATIC, "java/lang/System", "in", "Ljava/io/InputStream;");
            mv.visitMethodInsn(INVOKESPECIAL, "java/util/Scanner", "<init>",
                    "(Ljava/io/InputStream;)V", false);

            // scanner.nextInt()
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/util/Scanner", "nextInt", "()I", false);
            mv.visitVarInsn(ISTORE, index);
        }
    }

    private void visitExpression(Expression expr) {
        if (expr instanceof Literal) {
            Literal lit = (Literal) expr;
            if (lit.value instanceof Integer) {
                mv.visitLdcInsn(lit.value);
            } else if (lit.value instanceof Boolean) {
                boolean val = (Boolean) lit.value;
                mv.visitInsn(val ? ICONST_1 : ICONST_0);
            }

        } else if (expr instanceof Identifier) {
            Identifier id = (Identifier) expr;
            int index = varMap.get(id.name);
            mv.visitVarInsn(ILOAD, index); // Carrega a variável na pilha

        } else if (expr instanceof UnaryExpression) {
            // Suporte ao operador 'not' booleano
            UnaryExpression unary = (UnaryExpression) expr;
            visitExpression(unary.expr);
            if (unary.operator.equalsIgnoreCase("not")) {
                // Inverte 0 ↔ 1 via XOR com 1
                mv.visitInsn(ICONST_1);
                mv.visitInsn(IXOR);
            }

        } else if (expr instanceof BinaryExpression) {
            BinaryExpression bin = (BinaryExpression) expr;
            visitExpression(bin.left);
            visitExpression(bin.right);

            switch (bin.operator) {
                // Operadores aritméticos
                case "+": mv.visitInsn(IADD); break;
                case "-": mv.visitInsn(ISUB); break;
                case "*": mv.visitInsn(IMUL); break;
                case "/": mv.visitInsn(IDIV); break;

                // Operadores lógicos bit-a-bit (operandos são 0/1)
                case "and": mv.visitInsn(IAND); break;
                case "or":  mv.visitInsn(IOR);  break;

                // Operadores relacionais — produzem 0 (falso) ou 1 (verdadeiro) na pilha
                case ">":
                case "<":
                case "=":   // Pascal usa '=' para igualdade
                case "<>":  // Pascal usa '<>' para diferente
                case ">=":
                case "<=":
                case "==":  // mantido para compatibilidade retroativa
                case "!=": {
                    Label lTrue = new Label();
                    Label lEnd  = new Label();

                    int opcode;
                    switch (bin.operator) {
                        case ">":  opcode = IF_ICMPGT; break;
                        case "<":  opcode = IF_ICMPLT; break;
                        case "=":
                        case "==": opcode = IF_ICMPEQ; break;
                        case "<>":
                        case "!=": opcode = IF_ICMPNE; break;
                        case ">=": opcode = IF_ICMPGE; break;
                        case "<=": opcode = IF_ICMPLE; break;
                        default:   opcode = IF_ICMPEQ; break;
                    }

                    mv.visitJumpInsn(opcode, lTrue);
                    mv.visitInsn(ICONST_0);      // Falso
                    mv.visitJumpInsn(GOTO, lEnd);
                    mv.visitLabel(lTrue);
                    mv.visitInsn(ICONST_1);      // Verdadeiro
                    mv.visitLabel(lEnd);
                    break;
                }
            }

        } else if (expr instanceof FunctionCall) {
            FunctionCall call = (FunctionCall) expr;
            for (Expression arg : call.arguments) {
                visitExpression(arg); // Empilha os argumentos
            }
            StringBuilder sig = new StringBuilder("(");
            for (int i = 0; i < call.arguments.size(); i++) {
                sig.append("I");
            }
            sig.append(")I");
            mv.visitMethodInsn(INVOKESTATIC, className, call.functionName, sig.toString(), false);
        }
    }
}