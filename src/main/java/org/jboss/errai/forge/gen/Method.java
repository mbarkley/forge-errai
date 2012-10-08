package org.jboss.errai.forge.gen;

import japa.parser.ast.body.Parameter;
import japa.parser.ast.expr.AnnotationExpr;
import japa.parser.ast.expr.NameExpr;
import japa.parser.ast.stmt.BlockStmt;
import japa.parser.ast.type.Type;

import java.util.List;

public class Method {
	
	List<AnnotationExpr> annotations;
	int beginLine;
	int endLine;
	BlockStmt body;
	int modifiers;
	String name;
	List<Parameter> parameters;
	Type type;
	List<NameExpr> throwz;
	String parametersAsString;
	
	
	public Method(List<AnnotationExpr> annotations, int beginLine, int endLine,
			BlockStmt body, int modifiers, String name, List<Parameter> parameters, Type type,
			List<NameExpr> throwz) {
		super();
		this.annotations = annotations;
		this.beginLine = beginLine;
		this.endLine = endLine;
		this.body = body;
		this.name = name;
		this.parameters = parameters;
		this.type = type;
		this.throwz = throwz;
		this.modifiers = modifiers;
	}
	
	public List<AnnotationExpr> getAnnotations() {
		return annotations;
	}
	public void setAnnotations(List<AnnotationExpr> annotations) {
		this.annotations = annotations;
	}
	public int getBeginLine() {
		return beginLine;
	}
	public void setBeginLine(int beginLine) {
		this.beginLine = beginLine;
	}
	public int getEndLine() {
		return endLine;
	}
	public void setEndLine(int endLine) {
		this.endLine = endLine;
	}
	public BlockStmt getBody() {
		return body;
	}
	public void setBody(BlockStmt body) {
		this.body = body;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public List<Parameter> getParameters() {
		return parameters;
	}
	public void setParameters(List<Parameter> parameters) {
		this.parameters = parameters;
	}
	public Type getType() {
		return type;
	}
	public void setType(Type type) {
		this.type = type;
	}
	public List<NameExpr> getThrowz() {
		return throwz;
	}
	public void setThrowz(List<NameExpr> throwz) {
		this.throwz = throwz;
	}

	@Override
	public String toString() {
		return "Method [annotations=" + annotations + ", beginLine="
				+ beginLine + ", endLine=" + endLine + ", body=" + body
				+ ", modifiers=" + modifiers + ", name=" + name + ", parameters=" + parameters + ", type="
				+ type + ", throwz=" + throwz + "]";
	}

	public int getModifiers() {
		return modifiers;
	}

	public void setModifiers(int modifiers) {
		this.modifiers = modifiers;
	}

	public String getParametersAsString() {
		return parameters == null ? "" : 
			this.getParameters().toString().substring(1, this.getParameters().toString().length() -1);
	}

	public void setParametersAsString(String parametersAsString) {
		this.parametersAsString = parametersAsString;
	}
	

}
