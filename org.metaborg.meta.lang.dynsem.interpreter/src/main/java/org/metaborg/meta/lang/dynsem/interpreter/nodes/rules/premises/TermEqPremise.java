package org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.premises;

import org.metaborg.meta.lang.dynsem.interpreter.PremiseFailure;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.building.TermBuild;
import org.metaborg.meta.lang.dynsem.interpreter.terms.ITerm;
import org.metaborg.meta.lang.dynsem.interpreter.utils.SourceSectionUtil;
import org.spoofax.interpreter.core.Tools;
import org.spoofax.interpreter.terms.IStrategoAppl;

import com.github.krukow.clj_lang.PersistentList;
import com.oracle.truffle.api.CompilerAsserts;
import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.dsl.Cached;
import com.oracle.truffle.api.dsl.Fallback;
import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.NodeChildren;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.nodes.NodeUtil;
import com.oracle.truffle.api.profiles.ValueProfile;
import com.oracle.truffle.api.source.SourceSection;

@Deprecated
@NodeChildren({ @NodeChild(value = "left", type = TermBuild.class), @NodeChild(value = "right", type = TermBuild.class) })
public abstract class TermEqPremise extends Premise {

	public TermEqPremise(SourceSection source) {
		super(source);
	}

	public static TermEqPremise create(IStrategoAppl t, FrameDescriptor fd) {
		CompilerAsserts.neverPartOfCompilation();
		assert Tools.hasConstructor(t, "TermEq", 2);
		TermBuild lhs = TermBuild.create(Tools.applAt(t, 0), fd);
		TermBuild rhs = TermBuild.create(Tools.applAt(t, 1), fd);
		return TermEqPremiseNodeGen.create(SourceSectionUtil.fromStrategoTerm(t), lhs, rhs);
	}

	@Specialization
	public void doBoolean(boolean left, boolean right) {
		if (left != right) {
			throw PremiseFailure.INSTANCE;
		}
	}

	@Specialization
	public void doInt(int right, int left) {
		if (left != right) {
			throw PremiseFailure.INSTANCE;
		}
	}

	@Specialization(guards = { "left == cachedLeft", "right == cachedRight" })
	public void doString(String left, String right, @Cached("left") String cachedLeft,
			@Cached("right") String cachedRight, @Cached("doStringEq(cachedLeft, cachedRight)") boolean isEqual) {
		if (!isEqual) {
			throw PremiseFailure.INSTANCE;
		}
	}

	@TruffleBoundary
	protected boolean doStringEq(String s1, String s2) {
		return s1.equals(s2);
	}

	@Specialization(guards = { "left == cachedLeft", "right == cachedRight" })
	public void doITermDirect(ITerm left, ITerm right, @Cached("left") ITerm cachedLeft,
			@Cached("right") ITerm cachedRight, @Cached("cachedLeft.equals(cachedRight)") boolean isEqual) {
		if (!isEqual) {
			throw PremiseFailure.INSTANCE;
		}
	}

	@Specialization(contains = "doITermDirect")
	public void doITermIndirect(ITerm left, ITerm right, @Cached("getTypeProfile()") ValueProfile leftTypeProfile,
			@Cached("getTypeProfile()") ValueProfile rightTypeProfile) {
		if (!leftTypeProfile.profile(left).equals(rightTypeProfile.profile(right))) {
			throw PremiseFailure.INSTANCE;
		}
	}

	@SuppressWarnings("rawtypes")
	@Specialization(guards = { "left == cachedLeft", "right == cachedRight" })
	public void doListDirect(PersistentList left, PersistentList right, @Cached("left") PersistentList cachedLeft,
			@Cached("right") PersistentList cachedRight, @Cached("doListEq(cachedLeft, cachedRight)") boolean isEqual) {
		if (!isEqual) {
			throw PremiseFailure.INSTANCE;
		}
	}

	@TruffleBoundary
	@SuppressWarnings("rawtypes")
	protected boolean doListEq(PersistentList l1, PersistentList l2) {
		return l1.equals(l2);
	}

	protected ValueProfile getTypeProfile() {
		return ValueProfile.createClassProfile();
	}

	@Fallback
	@TruffleBoundary
	public void doObject(Object left, Object right) {
		if (!left.equals(right)) {
			throw PremiseFailure.INSTANCE;
		}
	}

	@Override
	@TruffleBoundary
	public String toString() {
		return NodeUtil.printCompactTreeToString(this);
	}
}
