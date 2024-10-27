package tiger.ui;

import tiger.ui.TigerChangeEvent;

public interface TigerChangeListener<T> {
    public void stateChanged(TigerChangeEvent<T> event);
}
