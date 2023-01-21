package me.lauriichan.build.builder.ui.window.ui.animation;

import me.lauriichan.build.builder.ui.util.TimeHelper;

public final class BlinkAnimation<E> implements IAnimation<E> {

    private E valueOn;
    private E valueOff;
    private E valueHidden;

    private E current;

    private double blinkOn = 0;
    private double blinkOff = 0;
    private double blinkRatio = 0;

    private double blinkTime = 0;
    private boolean stay = false;

    private boolean triggered = false;

    @Override
    public void setTriggered(final boolean triggered) {
        this.triggered = triggered;
    }

    @Override
    public boolean isTriggered() {
        return triggered;
    }

    @Override
    public IAnimator<E> getAnimator() {
        return null;
    }

    @Override
    public void tick(final long deltaTime) {
        if (!triggered) {
            if (current == valueHidden) {
                return;
            }
            current = valueHidden;
            blinkTime = 0;
            stay = false;
            return;
        }
        final double second = TimeHelper.nanoAsSecond(deltaTime);
        if (stay) {
            if (blinkOn == 0 && current != valueOn) {
                current = valueOn;
                blinkTime = 0;
            }
            if (blinkTime == blinkOn) {
                stay = false;
                return;
            }
            blinkTime += Math.min(blinkOn - blinkTime, second);
            current = valueOn;
            return;
        }
        if (blinkOff == 0 && current != valueOff) {
            current = valueOn;
            blinkTime = 0;
        }
        if (blinkTime == 0) {
            stay = true;
            return;
        }
        blinkTime -= Math.min(blinkTime, second / blinkRatio);
        current = valueOff;
    }

    @Override
    public E getValue() {
        return current;
    }

    public void setBlink(final double blinkOn, final double blinkOff) {
        this.blinkOn = Math.max(0, blinkOn);
        this.blinkOff = Math.max(0, blinkOff);
        updateBlink();
    }

    public void setBlinkOn(final double blinkOn) {
        this.blinkOn = Math.max(0, blinkOn);
        updateBlink();
    }

    public void setBlinkOff(final double blinkOff) {
        this.blinkOff = Math.max(0, blinkOff);
        updateBlink();
    }

    private void updateBlink() {
        if (blinkOn == 0) {
            blinkRatio = 1;
            return;
        }
        this.blinkRatio = blinkOff / blinkOn;
    }

    @Override
    public void setStart(final E value) {
        this.valueOn = value;
        if (current == null) {
            this.current = value;
        }
    }

    @Override
    public void setEnd(final E value) {
        this.valueOff = value;
    }

    public void setHidden(final E valueHidden) {
        this.valueHidden = valueHidden;
    }

}