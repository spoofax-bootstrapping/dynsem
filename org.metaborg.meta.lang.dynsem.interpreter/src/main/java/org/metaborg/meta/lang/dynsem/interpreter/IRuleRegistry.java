package org.metaborg.meta.lang.dynsem.interpreter;

import org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.Rule;

public interface IRuleRegistry {

	public Rule lookupRule(String name, int arity);

	public int ruleCount();
}