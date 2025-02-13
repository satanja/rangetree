package com.satanja.rangetree;

import lombok.Getter;

@Getter
class FractionalCascadingKey {

    private final Point point;
    private final int index;

    private FractionalCascadingKey leftChild;
    private FractionalCascadingKey rightChild;

    public FractionalCascadingKey(final Point point, final int index) {
        this.point = point;
        this.index = index;
    }

    public boolean hasLeftChild() {
        return leftChild != null;
    }

    public boolean hasRightChild() {
        return rightChild != null;
    }

    public void setLeft(final FractionalCascadingKey leftChild) {
        this.leftChild = leftChild;
    }

    public void setRight(final FractionalCascadingKey rightChild) {
        this.rightChild = rightChild;
    }
}
