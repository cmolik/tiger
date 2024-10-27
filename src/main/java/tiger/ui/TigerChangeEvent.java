package tiger.ui;

public class TigerChangeEvent<T> {
    private T source;
    public TigerChangeEvent(T source) {
        this.source = source;
    }
    public T getSource() {
        return source;
    }
}
