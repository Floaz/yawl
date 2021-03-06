package org.yawlfoundation.yawl.worklet.rdr;

/**
 * This class holds the last evaluated node, and the last node that evaluated to true,
 * as the result of a tree search
 *
 * @author Michael Adams
 * @date 19/09/2014
 */
public class RdrPair {

    private RdrNode lastTrue;
    private RdrNode lastEvaluated;

    public RdrPair() { }

    public RdrPair(RdrNode lTrue, RdrNode lEvaluated) {
        lastTrue = lTrue;
        lastEvaluated = lEvaluated;
    }


    public boolean isPairEqual() {
        return lastTrue != null && lastEvaluated != null && lastTrue == lastEvaluated;
    }


    public RdrNode getParentForNewNode() {
        return isPairEqual() ? getLastTrueNode() : getLastEvaluatedNode();
    }


    public RdrNode getLastTrueNode() { return lastTrue; }


    public RdrNode getLastEvaluatedNode() { return lastEvaluated; }


    public RdrConclusion getConclusion() {
        return lastTrue != null ? lastTrue.getConclusion() : null;
    }


    public boolean hasNullConclusion() {
        RdrConclusion conclusion = getConclusion();
        return conclusion == null || conclusion.isNullConclusion();
    }


    public String toString() {
        String trueString = lastTrue != null ? lastTrue.toXML() : "";
        String evalString = lastEvaluated != null ? lastEvaluated.toXML() : "";
        return trueString + ":::" + evalString;
    }
}
