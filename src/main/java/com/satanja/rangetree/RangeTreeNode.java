package com.satanja.rangetree;

import java.util.List;

class RangeTreeNode {

    private final double position;
    private final FractionalCascading associatedTree;

    private RangeTreeNode leftSubtree;
    private RangeTreeNode rightSubtree;

    RangeTreeNode(final double position, final FractionalCascading associatedTree) {
        this.position = position;
        this.associatedTree = associatedTree;
    }

    void setLeftSubtree(final RangeTreeNode leftSubtree) {
        this.leftSubtree = leftSubtree;
    }

    void setRightSubtree(final RangeTreeNode rightSubtree) {
        this.rightSubtree = rightSubtree;
    }

    RangeTreeNode getLeftSubtree() {
        return leftSubtree;
    }

    RangeTreeNode getRightSubtree() {
        return rightSubtree;
    }

    double getPosition() {
        return position;
    }

    FractionalCascadingKey rootSearch(final double min) {
        return associatedTree.rootSearch(min);
    }

    int compareTo(final double min, final double max) {
        if (min <= position && position <= max) {
            return 0;
        }

        if (position < min) {
            return 1;
        }

        return -1;
    }

    boolean isLeafNode() {
        return leftSubtree == null && rightSubtree == null;
    }

    List<Point> report(final FractionalCascadingKey fractionalCascadingKey, final Window window) {
        return associatedTree.report(fractionalCascadingKey, window);
    }
}
