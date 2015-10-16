package org.webbuilder.utils.storage.event;

/**
 * Created by 浩 on 2015-08-27 0027.
 */
public abstract class KeyFilter<K> {
    private boolean isOver = false;

    public abstract boolean each(K key);

    public void over() {
        isOver = true;
    }

    public boolean isOver() {
        return isOver;
    }

    public String pattern() {
        return null;
    }
}
