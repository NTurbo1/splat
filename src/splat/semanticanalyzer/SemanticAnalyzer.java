package splat.semanticanalyzer;

import java.util.HashSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import splat.parser.elements.Declaration;
import splat.parser.elements.FunctionDecl;
import splat.parser.elements.FuncParamDecl;
import splat.parser.elements.ProgramAST;
import splat.parser.elements.Statement;
import splat.parser.elements.ReturnStatement;
import splat.parser.elements.IfElseStatement;
import splat.parser.elements.Type;
import splat.parser.elements.VariableDecl;

public class SemanticAnalyzer {

	private ProgramAST progAST;
	
	private Map<String, FunctionDecl> funcMap = new HashMap<>();
	private Map<String, Type> progVarMap = new HashMap<>();
	
	public SemanticAnalyzer(ProgramAST progAST) {
		this.progAST = progAST;
	}

	public void analyze() throws SemanticAnalysisException {
		
		// Checks to make sure we don't use the same labels more than once
		// for our program functions and variables 
		checkNoDuplicateProgLabels();
		
		// This sets the maps that will be needed later when we need to
		// typecheck variable references and function calls in the 
		// program body
		setProgVarAndFuncMaps();
		
		// Perform semantic analysis on the functions
		for (FunctionDecl funcDecl : funcMap.values()) {	
			analyzeFuncDecl(funcDecl);
		}
		
		// Perform semantic analysis on the program body
		for (Statement stmt : progAST.getStmts()) {
			stmt.analyze(funcMap, progVarMap);
		}
		
	}

	private void analyzeFuncDecl(FunctionDecl funcDecl) throws SemanticAnalysisException {
		
		// Checks to make sure we don't use the same labels more than once
		// among our function parameters, local variables, and function names
		checkNoDuplicateFuncLabels(funcDecl);
		
		// Get the types of the parameters and local variables
		Map<String, Type> varAndParamMap = getVarAndParamMap(funcDecl);
		
		// Perform semantic analysis on the function body
		for (Statement stmt : funcDecl.getStmts()) {
			stmt.analyze(funcMap, varAndParamMap);
		}

        if (funcDecl.getReturnType() != Type.VOID) {
            int numStmts = funcDecl.getStmts().size();
            if (numStmts == 0) {
                throw new SemanticAnalysisException(
                        "A function with a non void return type can't have an empty body!", funcDecl);
            }

            Statement lastStmt = funcDecl.getStmts().get(numStmts - 1);
            if (!(lastStmt instanceof ReturnStatement)) {
                if (lastStmt instanceof IfElseStatement) {
                    IfElseStatement lastIfElseStmt = (IfElseStatement) lastStmt;
                    if (!lastIfElseStmt.returns()) {
                        throw new SemanticAnalysisException(
                                "Missing a return statement in the if-else construct!", lastIfElseStmt);
                    }
                } else {
                    // TODO: You may want to check if the last statement is a while-loop statement and
                    // that it's condition expression is always true.
                    throw new SemanticAnalysisException("Missing a return statement at the end of the " +
                            "funciton body.", lastStmt);
                }
            }
        }
	}
	
	
    /*
     * Returns a map of function parameters and local variables of the function.
     */
	private Map<String, Type> getVarAndParamMap(FunctionDecl funcDecl) {
		
        Map<String, Type> vpMap = new HashMap<>();
        for (FuncParamDecl param : funcDecl.getParams()) {
            vpMap.put(param.getLabel(), param.getType());
        }

        for (VariableDecl varDecl : funcDecl.getLocalVarDecls()) {
            vpMap.put(varDecl.getLabel(), varDecl.getType());
        }

		return vpMap;
	}

	private void checkNoDuplicateFuncLabels(FunctionDecl funcDecl) throws SemanticAnalysisException 
    {
        Set<String> labels = new HashSet<>(funcMap.keySet());

        for (FuncParamDecl param : funcDecl.getParams()) {
            String label = param.getLabel();
            if (labels.contains(label)) {
                throw new SemanticAnalysisException(
                        "Cannot have duplicate label '" + label + "' in a function", funcDecl);
            }
            labels.add(label);
        }

        for (VariableDecl varDecl : funcDecl.getLocalVarDecls()) {
            String label = varDecl.getLabel();
            if (labels.contains(label)) {
                throw new SemanticAnalysisException(
                        "Cannot have duplicate label '" + label + "' in a function.", funcDecl);
            }
            labels.add(label);
        }
	}
	
	private void checkNoDuplicateProgLabels() throws SemanticAnalysisException {
		
		Set<String> labels = new HashSet<String>();
		
 		for (Declaration decl : progAST.getDecls()) {
 			String label = decl.getLabel().toString();
 			
			if (labels.contains(label)) {
				throw new SemanticAnalysisException("Cannot have duplicate label '"
						+ label + "' in program", decl);
			} else {
				labels.add(label);
			}
			
		}
	}
	
	private void setProgVarAndFuncMaps() {
		
		for (Declaration decl : progAST.getDecls()) {

			String label = decl.getLabel().toString();
			
			if (decl instanceof FunctionDecl) {
				FunctionDecl funcDecl = (FunctionDecl)decl;
				funcMap.put(label, funcDecl);
				
			} else if (decl instanceof VariableDecl) {
				VariableDecl varDecl = (VariableDecl)decl;
				progVarMap.put(label, varDecl.getType());
			}
		}
	}
}
