package org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.premises;

import org.metaborg.meta.lang.dynsem.interpreter.PremiseFailure;
import org.metaborg.meta.lang.dynsem.interpreter.utils.SourceSectionUtil;
import org.spoofax.interpreter.core.Tools;
import org.spoofax.interpreter.terms.IStrategoAppl;

import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.source.SourceSection;

public class FailsPremise extends Premise {

	@Child protected Premise premise;

	public FailsPremise(SourceSection source, Premise premise) {
		super(source);
		this.premise = premise;
	}

	@Override
	public void execute(VirtualFrame frame) {

		boolean premiseSucceeds = true;
		try {
			premise.execute(frame);
		} catch (PremiseFailure pf) {
			premiseSucceeds = false;
		}

		if (premiseSucceeds) {
			throw PremiseFailure.INSTANCE;
		}

	}

	public static FailsPremise create(IStrategoAppl t, FrameDescriptor fd) {
		assert Tools.hasConstructor(t, "Fails", 1);
		return new FailsPremise(SourceSectionUtil.fromStrategoTerm(t), Premise.create(Tools.termAt(t, 0), fd));
	}
}
