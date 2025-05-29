package org.example.transformerapp.transformer;

public class InterruptibleCharSequence implements CharSequence {

    private final CharSequence original;

    public InterruptibleCharSequence(CharSequence original) {
        this.original = original;
    }

    @Override
    public int length() {
        return original.length();
    }

    @Override
    public char charAt(int index) {
        checkInterruption();
        return original.charAt(index);
    }

    @Override
    public CharSequence subSequence(int start, int end) {
        checkInterruption();
        return new InterruptibleCharSequence(original.subSequence(start, end));
    }

    @Override
    public String toString() {
        return original.toString();
    }

    private void checkInterruption() {
        if (Thread.currentThread().isInterrupted()) {
            throw new RegexOperationInterruptedException("Regex operation was interrupted.");
        }
    }

}
